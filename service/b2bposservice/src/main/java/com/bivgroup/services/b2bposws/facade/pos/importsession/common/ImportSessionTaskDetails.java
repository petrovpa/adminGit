package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ImportSessionTaskDetailsStage.reportTime;

public class ImportSessionTaskDetails {

    private static final long LOGGING_INTERVAL_MS_DEFAULT = 60000L;

    private long lastLoggingMs = 0;
    // todo: Установка извне (например, по значению из конфига через передачу в конструктор или т.п.)
    private long loggingIntervalMs = LOGGING_INTERVAL_MS_DEFAULT;

    private Date startDate;
    private Date finishDate;
    private Long durationMs;

    public ImportSessionTaskDetailsStage reading = new ImportSessionTaskDetailsStage(2, "Чтение из файла");
    public ImportSessionTaskDetailsStage blocking = new ImportSessionTaskDetailsStage(3, "Блокировка");
    public ImportSessionTaskDetailsStage commit = new ImportSessionTaskDetailsStage(4, "Коммит");

    private Long recordsProcessed;
    private Long recordsTotal;

    public ImportSessionTaskDetails() {
        clear();
    }

    public void clear() {
        markStart();
        finishDate = null;
        durationMs = null;
        commit.clear();
        reading.clear();
        blocking.clear();
        recordsProcessed = 0L;
        recordsTotal = null;
        lastLoggingMs = 0;
    }

    public void markStart() {
        startDate = new Date();
    }

    public void markFinish() {
        if (finishDate == null) {
            finishDate = new Date();
            durationMs = finishDate.getTime() - startDate.getTime();
            reading.markFinish();
            blocking.markFinish();
            commit.markFinish();
        }
    }

    public void incRecordsProcessed() {
        this.recordsProcessed = this.recordsProcessed + 1;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startDate", startDate);
        map.put("finishDate", finishDate);
        map.put("recordsProcessed", recordsProcessed);
        map.put("recordsTotal", recordsTotal);
        map.put("duration", durationMs);
        if (startDate != null) {
            long operationTime = ((finishDate == null) ? System.currentTimeMillis() : finishDate.getTime()) - startDate.getTime();
            String durationStr = reportTime(operationTime, recordsProcessed, "Импорт из файла", "обработано");
            map.put("durationStr", durationStr);
        }
        map.put(reading.getNum() + " - reading", reading.toMap());
        map.put(blocking.getNum() + " - blocking", blocking.toMap());
        map.put(commit.getNum() + " - commit", commit.toMap());
        return map;
    }

    // getters and setters - (re)generable

    public Date getStartDate() {
        return startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public Long getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(Long recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public Map<String, Object> checkForLogging(Logger logger) {
        Map<String, Object> result = null;
        long nowMs = System.currentTimeMillis();
        boolean isLoggingRequired = ((nowMs - lastLoggingMs) > loggingIntervalMs);
        if (isLoggingRequired) {
            result = this.toMap();
            logger.error(String.format("[NOT ERROR] [TASKDETAILS] Task details: %s", result));
            lastLoggingMs = nowMs;
        }
        return result;
    }

}
