package com.bivgroup.seaweedfs.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bivgroup.seaweedfs.client.status.MasterStatus;
import com.bivgroup.seaweedfs.client.status.VolumeStatus;

public interface WeedFSClient {
    Assignation assign(AssignParams params) throws IOException, WeedFSException;

    int write(WeedFSFile weedFSFile, Location location, File file) throws IOException, WeedFSException;

    int write(WeedFSFile file, Location location, byte[] dataToUpload, String fileName) throws IOException, WeedFSException;

    int write(WeedFSFile file, Location location, InputStream inputToUpload, String fileName) throws IOException, WeedFSException;

    void delete(WeedFSFile file, Location location) throws IOException, WeedFSException;

    List<Location> lookup(long volumeId) throws IOException, WeedFSException;

    InputStream read(WeedFSFile file, Location location) throws IOException, WeedFSException;

    MasterStatus getMasterStatus() throws IOException;

    VolumeStatus getVolumeStatus(Location location) throws IOException;
}
