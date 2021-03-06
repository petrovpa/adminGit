package com.bivgroup.seaweedfs.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bivgroup.seaweedfs.client.caching.LookupCache;
import com.bivgroup.seaweedfs.client.net.AssignResult;
import com.bivgroup.seaweedfs.client.net.LookupResult;
import com.bivgroup.seaweedfs.client.net.WriteResult;
import com.bivgroup.seaweedfs.client.status.MasterStatus;
import com.bivgroup.seaweedfs.client.status.VolumeStatus;
import java.net.MalformedURLException;
import java.util.Iterator;
import org.apache.http.HttpStatus;

class WeedFSClientImpl implements WeedFSClient {

    final URL masterURL;
    final HttpClient httpClient;
    final LookupCache lookupCache;
    final List<URL> masterUrls;

    WeedFSClientImpl(URL masterURL, List<URL> masterUrls, HttpClient httpClient, LookupCache lookupCache) {
        this.masterURL = masterURL;
        this.httpClient = httpClient;
        this.lookupCache = lookupCache;
        this.masterUrls = masterUrls;
    }

    private StringBuilder buildAssignUrl(URL mURL, AssignParams params) throws MalformedURLException {
        StringBuilder url = new StringBuilder(new URL(mURL, "/dir/assign").toExternalForm());
        url.append("?count=");
        url.append(params.versionCount);
        if (params.replicationStrategy != null) {
            url.append("&replication=");
            url.append(params.replicationStrategy.parameterValue);
        }

        if (params.collection != null) {
            url.append("&collection=");
            url.append(params.collection);
        }
        return url;
    }

    @Override
    public Assignation assign(AssignParams params) throws IOException, WeedFSException {
        StringBuilder url = buildAssignUrl(masterURL, params);

        HttpGet get = new HttpGet(url.toString());
        try {
            // Ищем живой сервер.
            HttpResponse response = httpClient.execute(get);
            if ((response.getStatusLine().getStatusCode() < 200) || (response.getStatusLine().getStatusCode() > 299)) {
                for (URL masterUrl : masterUrls) {
                    url = buildAssignUrl(masterUrl, params);
                    get = new HttpGet(url.toString());
                    response = httpClient.execute(get);
                    if ((response.getStatusLine().getStatusCode() >= 200) && (response.getStatusLine().getStatusCode() <= 299)) {
                        break;
                    }
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                AssignResult result = mapper.readValue(response.getEntity().getContent(), AssignResult.class);

                if (result.error != null) {
                    throw new WeedFSException(result.error);
                }

                return new Assignation(result);
            } catch (JsonMappingException | JsonParseException e) {
                throw new WeedFSException("Unable to parse JSON from weed-fs", e);
            }
        } finally {
            get.abort();
        }
    }

    @Override
    public void delete(WeedFSFile file, Location location) throws IOException, WeedFSException {
        StringBuilder url = new StringBuilder();
        if (!location.publicUrl.contains("http")) {
            url.append("http://");
        }
        url.append(location.publicUrl);
        url.append("/");
        url.append(file.fid);

        HttpDelete delete = new HttpDelete(url.toString());
        try {
            HttpResponse response = httpClient.execute(delete);

            StatusLine line = response.getStatusLine();
            if (line.getStatusCode() < 200 || line.getStatusCode() > 299) {
                throw new WeedFSException("Error deleting file " + file.fid + " on " + location.publicUrl + ": " + line.getStatusCode() + " "
                        + line.getReasonPhrase());
            }
        } finally {
            delete.abort();
        }
    }

    @Override
    public List<Location> lookup(long volumeId) throws IOException, WeedFSException {
        if (lookupCache != null) {
            List<Location> ret = lookupCache.lookup(volumeId);
            if (ret != null) {
                return ret;
            }
        }

        StringBuilder url = new StringBuilder(new URL(masterURL, "/dir/lookup").toExternalForm());
        url.append("?volumeId=");
        url.append(volumeId);

        HttpGet get = new HttpGet(url.toString());
        try {
            HttpResponse response = httpClient.execute(get);

            ObjectMapper mapper = new ObjectMapper();
            try {
                LookupResult result = mapper.readValue(response.getEntity().getContent(), LookupResult.class);

                if (result.error != null) {
                    throw new WeedFSException(result.error);
                }

                if (lookupCache != null) {
                    lookupCache.setLocation(volumeId, result.locations);
                }

                return result.locations;
            } catch (JsonMappingException | JsonParseException e) {
                throw new WeedFSException("Unable to parse JSON from weed-fs", e);
            }
        } finally {
            get.abort();
        }

    }

    @Override
    public int write(WeedFSFile file, Location location, File fileToUpload) throws IOException, WeedFSException {
        if (fileToUpload.length() == 0) {
            throw new WeedFSException("Cannot write a 0-length file");
        }
        return write(file, location, fileToUpload, null, null, null);
    }

    @Override
    public int write(WeedFSFile file, Location location, byte[] dataToUpload, String fileName) throws IOException, WeedFSException {
        if (dataToUpload.length == 0) {
            throw new WeedFSException("Cannot write a 0-length data");
        }
        return write(file, location, null, dataToUpload, null, fileName);
    }

    @Override
    public int write(WeedFSFile file, Location location, InputStream inputToUpload, String fileName) throws IOException, WeedFSException {
        return write(file, location, null, null, inputToUpload, fileName);
    }

    private String sanitizeFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "file";
        } else if (fileName.length() > 256) {
            return fileName.substring(0, 255);
        }
        return fileName;

    }

    private int write(WeedFSFile file, Location location, File fileToUpload, byte[] dataToUpload, InputStream inputToUpload, String fileName)
            throws IOException, WeedFSException {
        StringBuilder url = new StringBuilder();
        if (!location.publicUrl.contains("http")) {
            url.append("http://");
        }
        url.append(location.publicUrl);
        url.append('/');
        url.append(file.fid);

        if (file.version > 0) {
            url.append('_');
            url.append(file.version);
        }

        HttpPost post = new HttpPost(url.toString());

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        if (fileToUpload != null) {
            if (fileName == null) {
                fileName = fileToUpload.getName();
            }
            multipartEntityBuilder.addBinaryBody("file", fileToUpload, ContentType.APPLICATION_OCTET_STREAM, sanitizeFileName(fileName));
        } else if (dataToUpload != null) {
            multipartEntityBuilder.addBinaryBody("file", dataToUpload, ContentType.APPLICATION_OCTET_STREAM, sanitizeFileName(fileName));
        } else {
            multipartEntityBuilder.addBinaryBody("file", inputToUpload, ContentType.APPLICATION_OCTET_STREAM, sanitizeFileName(fileName));
        }
        post.setEntity(multipartEntityBuilder.build());

        try {
            HttpResponse response = httpClient.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            try {
                WriteResult result = mapper.readValue(response.getEntity().getContent(), WriteResult.class);

                if (result.error != null) {
                    throw new WeedFSException(result.error);
                }

                return result.size;
            } catch (JsonMappingException | JsonParseException e) {
                throw new WeedFSException("Unable to parse JSON from weed-fs", e);
            }
        } finally {
            post.abort();
        }
    }

    @Override
    public InputStream read(WeedFSFile file, Location location) throws IOException, WeedFSException, WeedFSFileNotFoundException {
        StringBuilder url = new StringBuilder();
        if (!location.publicUrl.contains("http")) {
            url.append("http://");
        }
        url.append(location.publicUrl);
        url.append('/');
        url.append(file.fid);

        if (file.version > 0) {
            url.append('_');
            url.append(file.version);
        }
        HttpGet get = new HttpGet(url.toString());
        HttpResponse response = httpClient.execute(get);
        StatusLine line = response.getStatusLine();
        if (line.getStatusCode() == 404) {
            get.abort();
            throw new WeedFSFileNotFoundException(file, location);
        }
        if (line.getStatusCode() != 200) {
            get.abort();
            throw new WeedFSException("Error reading file " + file.fid + " on " + location.publicUrl + ": " + line.getStatusCode() + " "
                    + line.getReasonPhrase());
        }
        return response.getEntity().getContent();
    }

    @Override
    public MasterStatus getMasterStatus() throws IOException {
        URL url = new URL(masterURL, "/dir/status");

        HttpGet get = new HttpGet(url.toString());

        try {
            HttpResponse response = httpClient.execute(get);
            StatusLine line = response.getStatusLine();

            if (line.getStatusCode() != 200) {
                throw new IOException("Not 200 status recieved for master status url: " + url.toExternalForm());
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(response.getEntity().getContent(), MasterStatus.class);

            } catch (JsonMappingException | JsonParseException e) {
                throw new WeedFSException("Unable to parse JSON from weed-fs", e);
            }
        } finally {
            get.abort();
        }
    }

    @Override
    public VolumeStatus getVolumeStatus(Location location) throws IOException {
        StringBuilder url = new StringBuilder();
        if (!location.publicUrl.contains("http")) {
            url.append("http://");
        }
        url.append(location.publicUrl);
        url.append("/status");

        HttpGet get = new HttpGet(url.toString());

        try {
            HttpResponse response = httpClient.execute(get);
            StatusLine line = response.getStatusLine();

            if (line.getStatusCode() != 200) {
                throw new IOException("Not 200 status recieved for master status url: " + url.toString());
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(response.getEntity().getContent(), VolumeStatus.class);

            } catch (JsonMappingException | JsonParseException e) {
                throw new WeedFSException("Unable to parse JSON from weed-fs", e);
            }
        } finally {
            get.abort();
        }
    }
}
