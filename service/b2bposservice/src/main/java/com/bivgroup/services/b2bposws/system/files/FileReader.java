package com.bivgroup.services.b2bposws.system.files;

import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.seaweedfs.client.WeedFSFile;
import ru.diasoft.services.config.Config;

import java.io.*;
import java.net.URL;
import java.util.List;

public interface FileReader extends SeaweedsGetters {

    // копия из B2BPrintCustomFacade
    default String getUserUploadFilePath() {
        Config config = Config.getConfig(CONFIG_SERVICE_NAME);
        String result = config.getParam("userFilePath", "");
        if ((result == null) || (result.isEmpty())) {
            result = System.getProperty("user.home") + File.separator + "Diasoft" + File.separator + "USERUPLOAD";
        }
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    /** Чтение файла из распределенной файловой системы или из каталога на диске */
    default InputStream tryReadFileFromSeaweedOrDirectory(String fileName) throws Exception {
        InputStream inputStream = tryReadFileFromSeaweedOrDirectory(fileName, null);
        return inputStream;
    }

    /** Чтение файла из распределенной файловой системы или из каталога на диске */
    default InputStream tryReadFileFromSeaweedOrDirectory(String fileName, String filePath) throws Exception {
        InputStream inputStream;
        boolean isUseSeaweedFS = isUseSeaweedFS();
        if (isUseSeaweedFS) {
            // todo: проверить после подключения распределенной ФС
            String seaweedFSUrl = getSeaweedFSUrl();
            URL masterURL = new URL(seaweedFSUrl);
            WeedFSFile file = new WeedFSFile(fileName);
            WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
            long volumeId = file.getVolumeId();
            List<Location> locations = client.lookup(volumeId);
            if (locations.size() == 0) {
                System.out.println("file not found");
                throw new FileNotFoundException(String.format("File '%s' not found in seaweed file system!", fileName));
            } else {
                inputStream = client.read(file, locations.get(0));
            }
        } else {
            String fullPath = ((filePath == null) || (filePath.isEmpty())) ? getUserUploadFilePath() : filePath;
            String fullFileName = fullPath + File.separator + fileName;
            File file = new File(fullFileName);
            inputStream = new FileInputStream(file);
        }
        return inputStream;
    }

}
