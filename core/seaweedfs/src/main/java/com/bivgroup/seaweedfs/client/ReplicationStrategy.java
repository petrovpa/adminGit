package com.bivgroup.seaweedfs.client;

public enum ReplicationStrategy {
    None("000"),
    OnceOnSameRack("001"),
    OnceOnDifferentRack("010"),
    OnceOnDifferentDC("100"),
    TwiceOnDifferentDC("200"),
    OnceOnDifferentRackAndOnceOnDifferentDC("110"),
    TwiceOnRack("002");

    private ReplicationStrategy(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    final String parameterValue;

    public static ReplicationStrategy fromParameterValue(String parameterValue) {
        // yes this is ugly
        switch (parameterValue) {
            case "000":
                return None;
            case "001":
                return OnceOnSameRack;
            case "010":
                return OnceOnDifferentRack;
            case "100":
                return OnceOnDifferentDC;
            case "200":
                return TwiceOnDifferentDC;
            case "110":
                return OnceOnDifferentRackAndOnceOnDifferentDC;
            case "002":
                return TwiceOnRack;
            default:
                throw new IllegalArgumentException("Unknown Replication Strategy: " + parameterValue);
        }
    }

}
