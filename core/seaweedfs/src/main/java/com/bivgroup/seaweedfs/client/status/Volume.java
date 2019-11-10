package com.bivgroup.seaweedfs.client.status;

import com.bivgroup.seaweedfs.client.ReplicationStrategy;

public class Volume {
    public int Id;
    public long Size;
    public String RepType;
    public String Collection;
    public String Version;
    public long FileCount;
    public long DeleteCount;
    public long DeletedByteCount;
    public boolean ReadOnly;

    public ReplicationStrategy getReplicationStrategy() {
        return ReplicationStrategy.fromParameterValue(RepType);
    }
}
