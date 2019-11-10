package com.bivgroup.seaweedfs.client.net;

import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSFile;

public class AssignResult extends Result {
    public int count;
    public String fid;
    public String publicUrl;
    public String url;

    public Location getLocation() {
        Location ret = new Location();
        ret.publicUrl = publicUrl;
        ret.url = url;
        return ret;
    }

    public WeedFSFile getWeedFSFile() {
        return new WeedFSFile(fid);
    }

    public int getCount() {
        return count;
    }

}
