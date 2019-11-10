package com.bivgroup.seaweedfs.client;

public class WeedFSFile {

    public final String fid;

    public int version = 0;

    public WeedFSFile(String fid) {
        this.fid = fid;
    }

    public long getVolumeId() {
        int pos = fid.indexOf(',');
        if (pos == -1) {
            throw new IllegalArgumentException("Cannot parse fid: " + fid);
        }
        try {
            return Long.parseLong(fid.substring(0, pos));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Cannot parse fid: " + fid, nfe);
        }
    }

    @Override
    public String toString() {
        return "WeedFSFile [fid=" + fid + ", version=" + version + "]";
    }

}
