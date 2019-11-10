package com.bivgroup.seaweedfs.client;

public class AssignParams {

    final ReplicationStrategy replicationStrategy;

    final int versionCount;

    final String collection;

    public static final AssignParams DEFAULT = new AssignParams();

    public AssignParams() {
        this(null, 1, null);
    }

    public AssignParams(int versionCount) {
        this(null, versionCount, null);
    }

    public AssignParams(ReplicationStrategy replicationStrategy) {
        this(null, 1, replicationStrategy);
    }

    public AssignParams(String collection, ReplicationStrategy replicationStrategy) {
        this(collection, 1, replicationStrategy);
    }

    public AssignParams(String collection, int versionCount, ReplicationStrategy replicationStrategy) {
        this.collection = collection;
        this.versionCount = versionCount;
        this.replicationStrategy = replicationStrategy;
    }

}
