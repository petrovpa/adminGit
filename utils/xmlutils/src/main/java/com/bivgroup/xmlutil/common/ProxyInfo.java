package com.bivgroup.xmlutil.common;

public class ProxyInfo {
    private final String proxyUrl;
    private final Integer port;
    private final String login;
    private final String password;
    private final String domain;

    public ProxyInfo(String proxyUrl, Integer port, String login, String password, String domain) {
        this.proxyUrl = proxyUrl;
        this.port = port;
        this.login = login;
        this.password = password;
        this.domain = domain;
    }

    public String getProxyUrl() {
        return this.proxyUrl;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDomain() {
        return this.domain;
    }
}
