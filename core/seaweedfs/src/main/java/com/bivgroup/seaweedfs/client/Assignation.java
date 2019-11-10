package com.bivgroup.seaweedfs.client;

import com.bivgroup.seaweedfs.client.net.AssignResult;

public class Assignation {

    public WeedFSFile weedFSFile;

    public Location location;

    int versionCount;

    public Assignation(AssignResult result) {
        this.weedFSFile = result.getWeedFSFile();
        this.location = result.getLocation();
        this.versionCount = result.getCount();
    }

    public Assignation() {
    }

    @Override
    public String toString() {
        return "AssignedWeedFSFile [weedFSFile=" + weedFSFile + ", location=" + location + ", versionCount=" + versionCount + "]";
    }

}
