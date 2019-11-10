package com.bivgroup.seaweedfs.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.bivgroup.seaweedfs.client.caching.LookupCache;
import java.util.ArrayList;
import java.util.List;

public class WeedFSClientBuilder {

    HttpClient httpClient;

    URL masterUrl;

    List<URL> masterUrls;

    LookupCache lookupCache;

    public WeedFSClientBuilder() {
        this.masterUrls = new ArrayList<>();
    }

    public static WeedFSClientBuilder createBuilder() {
        return new WeedFSClientBuilder();
    }

    public WeedFSClientBuilder setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public WeedFSClientBuilder setMasterUrl(URL masterUrl) {
        this.masterUrl = masterUrl;
        return this;
    }

    public WeedFSClientBuilder setMasterUrls(String masterUrl) throws MalformedURLException {
        String[] UrlList = masterUrl.split(";");
        for (String url : UrlList) {
            URL masterURL = new URL(url);
            masterUrls.add(masterURL);
        }
        if (masterUrls.size()> 0) {
            setMasterUrl(masterUrls.get(0));
        }
        
        return this;
    }

    public WeedFSClientBuilder setLookupCache(LookupCache lookupCache) {
        this.lookupCache = lookupCache;
        return this;
    }

    public WeedFSClient build() {
        if (masterUrl == null) {
            try {
                // default url for testing purpose
                masterUrl = new URL("http://localhost:9333");
            } catch (MalformedURLException e) {
                // This cannot happen by construction
                throw new Error(e);
            }
        }

        if (httpClient == null) {
            // minimal http client
            httpClient = HttpClientBuilder.create().build();
        }

        return new WeedFSClientImpl(masterUrl, masterUrls, httpClient, lookupCache);
    }

}
