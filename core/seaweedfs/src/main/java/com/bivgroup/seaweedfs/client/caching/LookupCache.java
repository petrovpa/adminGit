package com.bivgroup.seaweedfs.client.caching;

import java.util.List;

import com.bivgroup.seaweedfs.client.Location;

public interface LookupCache {

    List<Location> lookup(long volumeId);

    void invalidate(long volumeId);

    void invalidate();

    void setLocation(long volumeId, List<Location> locations);
}
