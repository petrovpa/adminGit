package com.bivgroup.services.b2bposws.system.files;

import com.bivgroup.seaweedfs.client.*;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public interface FileWriter extends SeaweedsGetters {

    // копия из B2BPrintCustomFacade
    default String getUploadFolder() {
        return Config.getConfig("reportws").getParam("reportOutput", System.getProperty("user.home"));
    }

    // копия из B2BPrintCustomFacade
    default String getReportFullPath(String reportName, String format) {
        String path = getUploadFolder();
        File reportFile = new File(path + File.separator + reportName + format);
        if (!reportFile.exists()) {
            reportFile = new File(path + File.separator + reportName + ".odt");
            if (!reportFile.exists()) {
                reportFile = new File(path + File.separator + reportName + ".ods");
                if (!reportFile.exists()) {
                    reportFile = null;
                }
            }
        }
        String fullPath = "";
        try {
            if (reportFile.getCanonicalPath().startsWith(path)) {
                if (reportFile != null) {
                    fullPath = reportFile.getAbsolutePath();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullPath;
    }

    /** Сохранение уже существующего файла в SeaweedFS (если требуется); возврат подробной информации о файле (всегда) */
    default Map<String, Object> trySaveReportToSeaweeds(String reportName, String format) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String reportFullPath = getReportFullPath(reportName, ".pdf");
        File file = new File(reportFullPath);
        if (!file.exists()) {
            throw new Exception("Source report file '" + reportFullPath + "' not found!");
        }
        String fileName = reportName + format;
        long size = 0;
        if (isUseSeaweedFS()) {
            String masterUrlString = getSeaweedFSUrl();
            URL masterURL = new URL(masterUrlString);
            WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
            AssignParams assignParams = new AssignParams("b2breport", ReplicationStrategy.TwiceOnRack);
            Assignation a = client.assign(assignParams);
            FileInputStream fileInputStream = new FileInputStream(file);
            size = client.write(a.weedFSFile, a.location, fileInputStream, fileName);
            if (size == 0) {
                throw new Exception("Unable to write file to SeaweedFS!");
            } else {
                result.put("FSID", a.weedFSFile.fid);
                result.put("FILEPATH", a.weedFSFile.fid);
            }
        } else {
            size = file.length();
            result.put("FILEPATH", fileName);
        }
        result.put("FILENAME", fileName);
        result.put("FILESIZE", size);
        return result;
    }

}
