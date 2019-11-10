package com.bivgroup.services.b2bposws.system.readers.xlsx;

import com.bivgroup.services.b2bposws.system.readers.common.ReaderColumn;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

// todo: перенести в отдельный проект и т.п.
public class ExcelFileTablePlainReader implements Iterator<Map<String, Object>> {

    /** Количество строк листа, которые анализируемых при поиске позицицй заголовков */
    // todo: при необходимости добавить поддержку в виде входного параметра или т.п.
    public static final int DEFAULT_TITLE_SEARCH_MAX_ROWS_COUNT = 50;

    /** Количество строк, отделяющих данные от нижней строки "шапки" (0 - сразу под "шапкой", 1 - через одну строку от "шапки" и т.д.) */
    // todo: при необходимости добавить поддержку в виде входного параметра или т.п.
    public static final int DEFAULT_DATA_SHIFT_ROWS_COUNT = 0;
    public static final String ROW_FILE_INDEX_KEYNAME = "rowFileIndex";
    public static final String ROW_DATA_INDEX_KEYNAME = "rowDataIndex";

    private Logger logger;

    private String pathname;
    private File file;
    private InputStream inputStream;
    private Workbook workbook;

    private HashMap<String, ExcelFileTablePlainReaderColumn> columnsMap;
    private HashSet<ExcelFileTablePlainReaderColumn> columnsSet;
    private Sheet selectedSheet;
    private boolean hasNext = true;
    /** номер текущей строки */
    private Integer rowIndex;
    /** запись, сформировання по данным из текущей строки */
    private HashMap<String, Object> record;
    /** номер строки с которой начинаются данные (номер первой строки с данными) */
    private int dataRowIndex;

    private static final HashMap<Class, CellType> cellTypeByClass;

    static {
        cellTypeByClass = new HashMap<>();
        cellTypeByClass.put(String.class, STRING);
        cellTypeByClass.put(Long.class, NUMERIC);
        cellTypeByClass.put(Date.class, NUMERIC);
        cellTypeByClass.put(Double.class, NUMERIC);
    }

    private long approximateTotalRowsCount = 0;

    public ExcelFileTablePlainReader(Logger logger, String pathname, ReaderColumn... readerColumns) throws ExcelFileTablePlainReaderException {
        logger.debug("ExcelFileTablePlainReader constructor start...");
        this.logger = logger;
        this.pathname = pathname;
        initFilesStreamsAndEtc();
        initWorkbook();
        initColumns(readerColumns);
        analyze();
        logger.debug("ExcelFileTablePlainReader constructor finished.");
    }

    public ExcelFileTablePlainReader(Logger logger, InputStream inputStream, ReaderColumn... readerColumns) throws ExcelFileTablePlainReaderException {
        logger.debug("ExcelFileTablePlainReader constructor start...");
        this.logger = logger;
        this.pathname = null;
        this.file = null;
        this.inputStream = inputStream;
        initWorkbook();
        initColumns(readerColumns);
        analyze();
        logger.debug("ExcelFileTablePlainReader constructor finished.");
    }

    private void initFilesStreamsAndEtc() throws ExcelFileTablePlainReaderException {
        logger.debug("initFilesStreamsAndEtc start...");
        // инициализация файла
        file = new File(pathname);
        // инициализация потока
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            logger.error("initFilesStreamsAndEtc FileNotFoundException:", ex);
            throw new ExcelFileTablePlainReaderException(ex);
        }
        logger.debug("initFilesStreamsAndEtc finished.");
    }

    private void initWorkbook() throws ExcelFileTablePlainReaderException {
        logger.debug("initWorkbook start...");
        // инициализация книги
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException ex) {
            logger.error("initFilesStreamsAndEtc IOException:", ex);
            throw new ExcelFileTablePlainReaderException(ex);
        } catch (InvalidFormatException ex) {
            logger.error("initFilesStreamsAndEtc InvalidFormatException:", ex);
            throw new ExcelFileTablePlainReaderException(ex);
        }
        logger.debug("initWorkbook finished.");
    }

    private void initColumns(ReaderColumn[] readerColumns) throws ExcelFileTablePlainReaderException {
        logger.debug("initColumns start...");
        columnsSet = new HashSet<>();
        columnsMap = new HashMap<>();
        for (ReaderColumn readerColumn : readerColumns) {
            Class cellClass = readerColumn.getCellClass();
            CellType cellType = cellTypeByClass.get(cellClass);
            if (cellType == null) {
                throw new ExcelFileTablePlainReaderException(String.format(
                        "initColumns failed, unsupported cell class - '%s'!", cellClass.getCanonicalName()
                ));
            } else {
                ExcelFileTablePlainReaderColumn excelReaderColumn = new ExcelFileTablePlainReaderColumn(readerColumn, cellType);
                columnsSet.add(excelReaderColumn);
                columnsMap.put(excelReaderColumn.getTitle(), excelReaderColumn);
            }
        }
        logger.debug("initColumns finished.");
    }

    /** поиск нужного листа и определение позиций заголовков */
    private void analyze() throws ExcelFileTablePlainReaderException {
        logger.debug("analyze start...");
        // количество листов
        int numberOfSheets = workbook.getNumberOfSheets();
        int s = 0;
        selectedSheet = null;
        rowIndex = null;
        // поиск по всем листам
        while ((s < numberOfSheets) && (selectedSheet == null)) {
            logger.debug("s = " + s);
            Sheet sheet = workbook.getSheetAt(s);
            int maxRows = sheet.getPhysicalNumberOfRows();
            if (maxRows > DEFAULT_TITLE_SEARCH_MAX_ROWS_COUNT) {
                maxRows = DEFAULT_TITLE_SEARCH_MAX_ROWS_COUNT;
            }
            int r = 0;
            // поиск по первым строкам, но не дальше первых 100 строк
            int foundColumns = 0;
            while ((r < maxRows) && (rowIndex == null)) {
                logger.debug("r = " + r);
                Row row = sheet.getRow(r);
                if (row == null) {
                    logger.debug("row is null, skipping row...");
                } else {
                    // в некоторых случаях row может быть null (возможно если изначально имевшиеся в файле строки были удалены или т.п.)
                    int physicalNumberOfCells = (row != null) ? row.getPhysicalNumberOfCells() : 0;
                    int c = 0;
                    int cellCount = 0;
                    // поиск по всем заполненным колонкам
                    while ((cellCount < physicalNumberOfCells) && (rowIndex == null)) {
                        logger.debug("c = " + c);
                        Cell cell = row.getCell(c);
                        if (cell == null) {
                            logger.debug("cell is null, skipping cell...");
                        } else {
                            logger.debug("cell.getCellTypeEnum() = " + cell.getCellTypeEnum());
                            CellType cellType = cell.getCellTypeEnum();
                            if (STRING.equals(cellType)) {
                                String value = cell.getStringCellValue();
                                logger.debug("value = " + value);
                                ExcelFileTablePlainReaderColumn excelReaderColumn = columnsMap.get(value);
                                if (excelReaderColumn != null) {
                                    // найдена колонка, соответствующая описанию
                                    logger.debug("excelReaderColumn = " + excelReaderColumn);
                                    excelReaderColumn.setPosition(c);
                                    foundColumns++;
                                    if (foundColumns == columnsSet.size()) {
                                        // все искомые колонки найдены
                                        selectedSheet = sheet;
                                        dataRowIndex = r + DEFAULT_DATA_SHIFT_ROWS_COUNT + 1;
                                        rowIndex = dataRowIndex;
                                    }
                                }
                            }
                            cellCount++;
                        }
                        c++;
                    }
                }
                r++;
            }
            s++;
        }
        if ((selectedSheet == null) || (rowIndex == null)) {
            throw new ExcelFileTablePlainReaderException("analyze failed!");
        } else {
            approximateTotalRowsCount = selectedSheet.getPhysicalNumberOfRows();
        }
        logger.debug("analyze finished.");
    }

    public Map<String, Object> readRow() {
        if (selectedSheet == null) {
            record = null;
            hasNext = false;
        } else if (record != null) {
            return record;
        } else {
            // long startMs = System.currentTimeMillis();
            record = new HashMap<String, Object>();
            Row row = selectedSheet.getRow(rowIndex);
            if (row == null) {
                logger.debug(String.format(
                        "Row (with index %d) is null - will be ignored.", rowIndex
                ));
            } else {
                for (ExcelFileTablePlainReaderColumn column : columnsSet) {
                    Object value = null;
                    Integer position = column.getPosition();
                    Cell cell = row.getCell(position);
                    if (cell == null) {
                        logger.debug(String.format(
                                "Cell (with index %d in row with index %d) is null - will be ignored.",
                                position, rowIndex
                        ));
                    } else {
                        CellType cellType = cell.getCellTypeEnum();
                        CellType expectedCellType = column.getCellType();
                        // todo: использовать expectedCellType
                        Class cellClass = column.getCellClass();
                        if (STRING.equals(cellType)) {
                            String valueStr = cell.getStringCellValue();
                            if (Date.class.equals(cellClass)) {
                                // в ячейке вместо ожидаемой даты указана строка (как правило, '?')
                                // такие значения будут игнорироваться
                                // todo: исправить, когда будет получен ответ от аналитика по обработке знаков вопроса
                                logger.error(String.format(
                                        "Cell (with index %d in row with index %d) contains string ('%s') instead of date - this value will be ignored.",
                                        position, rowIndex, valueStr
                                ));
                            } else if (Long.class.equals(cellClass)) {
                                // в ячейке вместо ожидаемой даты указана строка (как правило, '?')
                                // такие значения будут игнорироваться, если не получится преобразовать их в число
                                // todo: исправить, когда будет получен ответ от аналитика по обработке знаков вопроса
                                try {
                                    value = Long.parseLong(valueStr);
                                } catch (NumberFormatException ex) {
                                    logger.error(String.format(
                                            "Cell (with index %d in row with index %d) contains unparsable string ('%s') instead of number - this value will be ignored.",
                                            position, rowIndex, valueStr
                                    ));
                                }
                            } else {
                                value = valueStr;
                            }
                        } else if (NUMERIC.equals(cellType)) {
                            if (Date.class.equals(cellClass)) {
                                value = cell.getDateCellValue();
                            } else if (Double.class.equals(cellClass)) {
                                value = cell.getNumericCellValue();
                            } else {
                                // getNumericCellValue всегда возвращает double
                                // для всех случаев, кроме тех, когда требуется собственно дробное значение дробную часть следует отбрасывать
                                value = (long) cell.getNumericCellValue();
                            }
                        }
                    }
                    if (value != null) {
                        record.put(column.getKeyName(), value);
                    }
                }
            }
            if (record.isEmpty()) {
                hasNext = false;
            } else {
                record.put(ROW_FILE_INDEX_KEYNAME, rowIndex);
                record.put(ROW_DATA_INDEX_KEYNAME, rowIndex - dataRowIndex);
            }
            // logger.error(String.format("[NOT ERROR] ExcelFileTablePlainReader.readRow takes %d ms.", System.currentTimeMillis() - startMs));
        }
        return record;
    }

    @Override
    public boolean hasNext() {
        readRow();
        // hasNext = (record != null) && (!record.isEmpty());
        return hasNext;
    }

    @Override
    public Map<String, Object> next() {
        Map<String, Object> record;
        if (hasNext) {
            record = readRow();
            this.record = null;
            rowIndex++;
        } else {
            record = null;
        }
        return record;
    }

    public long getApproximateTotalRowsCount() {
        return approximateTotalRowsCount;
    }

    public int getDataRowIndex() {
        return dataRowIndex;
    }
}
