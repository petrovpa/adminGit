package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImportSessionTaskDetailsStage {

    private int num;
    private String name;

    private Date startDate;
    private Date finishDate;
    private Long durationMs;

    private Long itemsProcessed;
    private Long itemsTotal;

    public ImportSessionTaskDetailsStage(int num, String name) {
        this.num = num;
        this.name = name;
        clear();
    }

    static public String reportTime(long operationTime, long rowsCount, String stageName, String operationName) {
        String result;
        double operationTimeInSeconds = ((double) operationTime) / 1000.0;
        if (operationTimeInSeconds > 0) {
            double operationTimeInMinutes = operationTimeInSeconds / 60.0;
            String operationTimeInMinutesStr;
            if (operationTimeInMinutes < 0.1) {
                operationTimeInMinutesStr = "";
            } else {
                operationTimeInMinutesStr = String.format(" (%.1f минут)", operationTimeInMinutes);
            }
            if (rowsCount > 0) {
                double recordsPerSecond = ((double) rowsCount) / operationTimeInSeconds;
                result = String.format("%s: %d записей %s примерно за %.3f секунд%s со средней скоростью %.5f записей в секунду.", stageName, rowsCount, operationName, operationTimeInSeconds, operationTimeInMinutesStr, recordsPerSecond);
            } else if (rowsCount == -1) {
                // -1: условное обозначение для полного объема данных (если запрос не возвращает оличество обработанных записей)
                result = String.format("%s: %s примерно за %.3f секунд%s.", stageName, operationName, operationTimeInSeconds, operationTimeInMinutesStr);
            } else {
                result = String.format("%s: %d записей %s примерно за %.3f секунд%s.", stageName, rowsCount, operationName, operationTimeInSeconds, operationTimeInMinutesStr);
            }
        } else {
            result = String.format("%s: %d записей %s почти мгновенно.", stageName, rowsCount, operationName);
        }
        return result;
    }

    public void clear() {
        startDate = null;
        finishDate = null;
        durationMs = null;
        itemsProcessed = 0L;
        itemsTotal = 0L;
    }

    public void markStart() {
        startDate = new Date();
    }

    public void markFinish() {
        if (finishDate == null) {
            // если еще не сделано отметки об окончании
            finishDate = new Date();
            if (startDate == null) {
                // если не сделано отметки о начале
                startDate = finishDate;
            }
            durationMs = finishDate.getTime() - startDate.getTime();
        }
    }

    public void markAllItemsProcessed() {
        itemsProcessed = itemsTotal;
    }

    public void incItemsProcessed() {
        itemsProcessed = (itemsProcessed == null ? 0 : itemsProcessed) + 1;
    }

    public void incItemsTotal() {
        itemsTotal = (itemsTotal == null ? 0 : itemsTotal) + 1;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("num", num);
        map.put("name", name);
        map.put("startDate", startDate);
        map.put("finishDate", finishDate);
        map.put("itemsProcessed", itemsProcessed);
        map.put("itemsTotal", itemsTotal);
        map.put("duration", durationMs);
        if (startDate != null) {
            boolean isInProgress = (finishDate == null);
            long operationTime = (isInProgress ? System.currentTimeMillis() : finishDate.getTime()) - startDate.getTime();
            String operationName = isInProgress ? "обработано" : "полностью обработано";
            String durationStr = reportTime(operationTime, itemsProcessed, name, operationName);
            map.put("durationStr", durationStr);
        }
        return map;
    }

    //

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public void setItemsTotal(Long itemsTotal) {
        this.itemsTotal = itemsTotal;
    }

    public Long getItemsTotal() {
        return itemsTotal;
    }

    public void setItemsProcessed(Long itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }

}
