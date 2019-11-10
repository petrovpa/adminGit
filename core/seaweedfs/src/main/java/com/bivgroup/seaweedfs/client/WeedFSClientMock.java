
package com.bivgroup.seaweedfs.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bivgroup.seaweedfs.client.status.MasterStatus;
import com.bivgroup.seaweedfs.client.status.VolumeStatus;

public class WeedFSClientMock implements WeedFSClient {

    @Override
    public Assignation assign(AssignParams params) throws IOException, WeedFSException {
        return null;
    }

    @Override
    public int write(WeedFSFile weedFSFile, Location location, File file) throws IOException, WeedFSException {
        return 0;
    }

    @Override
    public int write(WeedFSFile file, Location location, byte[] dataToUpload, String fileName) throws IOException,
            WeedFSException {
        return 0;
    }

    @Override
    public int write(WeedFSFile file, Location location, InputStream inputToUpload, String fileName)
            throws IOException, WeedFSException {
        return 0;
    }

    @Override
    public void delete(WeedFSFile file, Location location) throws IOException, WeedFSException {
    }

    @Override
    public List<Location> lookup(long volumeId) throws IOException, WeedFSException {
        return null;
    }

    @Override
    public InputStream read(WeedFSFile file, Location location) throws IOException, WeedFSException {
        return null;
    }

    @Override
    public MasterStatus getMasterStatus() {
        return null;
    }

    @Override
    public VolumeStatus getVolumeStatus(Location location) {
        return null;
    }

}
