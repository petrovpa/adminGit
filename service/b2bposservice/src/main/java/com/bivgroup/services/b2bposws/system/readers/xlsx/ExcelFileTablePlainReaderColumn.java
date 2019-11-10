package com.bivgroup.services.b2bposws.system.readers.xlsx;

import com.bivgroup.services.b2bposws.system.readers.common.ReaderColumn;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelFileTablePlainReaderColumn {

    private ReaderColumn readerColumn;
    private CellType cellType;

    public ExcelFileTablePlainReaderColumn(ReaderColumn readerColumn) {
        this.readerColumn = readerColumn;
    }

    public ExcelFileTablePlainReaderColumn(ReaderColumn readerColumn, CellType cellType) {
        this.readerColumn = readerColumn;
        this.cellType = cellType;
    }

    public String getTitle() {
        return readerColumn.getTitle();
    }

    public void setTitle(String title) {
        readerColumn.setTitle(title);
    }

    public String getKeyName() {
        return readerColumn.getKeyName();
    }

    public void setKeyName(String keyName) {
        readerColumn.setKeyName(keyName);
    }

    public Integer getPosition() {
        return readerColumn.getPosition();
    }

    public void setPosition(Integer position) {
        readerColumn.setPosition(position);
    }

    public Class getCellClass() {
        return readerColumn.getCellClass();
    }

    public void setCellClass(Class cellClass) {
        readerColumn.setCellClass(cellClass);
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

}
