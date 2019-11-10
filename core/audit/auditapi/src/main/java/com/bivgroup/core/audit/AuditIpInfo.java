package com.bivgroup.core.audit;

/**
 * Класс для хранения информации об IP адресах клиентов, которые
 * соверашют запросы которые требуется зааудитить
 */
public class AuditIpInfo {
    /**
     * IP адрес внешний
     */
    private String externalIpAddress;
    /**
     * IP адрес внутренний
     */
    private String innerIpAddress;

    /**
     * Цепочка используемых IP адресов
     */
    private String ipChainAddresses;

    public AuditIpInfo() {
    }

    public AuditIpInfo(String ipAddress, String realIpAddress, String chainIpAddresses) {
        this.externalIpAddress = ipAddress;
        this.innerIpAddress = realIpAddress;
        this.ipChainAddresses = chainIpAddresses;
    }

    public String getExternalIpAddress() {
        return externalIpAddress;
    }

    public AuditIpInfo setExternalIpAddress(String externalIpAddress) {
        this.externalIpAddress = externalIpAddress;
        return this;
    }

    public String getInnerIpAddress() {
        return innerIpAddress;
    }

    public AuditIpInfo setInnerIpAddress(String innerIpAddress) {
        this.innerIpAddress = innerIpAddress;
        return this;
    }

    public String getIpChainAddresses() {
        return ipChainAddresses;
    }

    public AuditIpInfo setIpChainAddresses(String ipChainAddresses) {
        this.ipChainAddresses = ipChainAddresses;
        return this;
    }
}
