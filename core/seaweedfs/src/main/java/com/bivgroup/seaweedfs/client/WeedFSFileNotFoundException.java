package com.bivgroup.seaweedfs.client;

public class WeedFSFileNotFoundException extends WeedFSException {

    private static final long serialVersionUID = 1L;

    public WeedFSFileNotFoundException(WeedFSFile file, Location location) {
        super(file.fid + " not found on " + location.publicUrl);
    }

}
