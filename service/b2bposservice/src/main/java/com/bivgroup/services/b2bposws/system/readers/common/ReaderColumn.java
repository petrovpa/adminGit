package com.bivgroup.services.b2bposws.system.readers.common;

public class ReaderColumn {

    private String title;
    private String keyName;
    private Integer position;
    private Class cellClass;

    public ReaderColumn(String title, String keyName, Class cellClass) {
        this.title = title;
        this.keyName = keyName;
        this.cellClass = cellClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Class getCellClass() {
        return cellClass;
    }

    public void setCellClass(Class cellClass) {
        this.cellClass = cellClass;
    }
}
