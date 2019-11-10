package com.bivgroup.sessionutils;

import java.util.Map;

public class BroadcastTarget {
    private String ip;
    private String port;

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public BroadcastTarget(Map<String, Object> server) {
        Map<String, Object> serverParams = (Map<String, Object>) server.get("Service");
        this.ip = serverParams.get("Address").toString();
        this.port = serverParams.get("Port").toString();
    }

}