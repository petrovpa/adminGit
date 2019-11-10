package ru.diasoft.services.bivsberlossws;

import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.seaweedfs.client.WeedFSFile;
import com.bivgroup.sessionutils.SessionController;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.services.inscore.system.external.impl.ExternalServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ru.diasoft.services.bivsberlossws.B2BFIleSessionController.*;
import static ru.diasoft.services.utils.Constants.SERVICE_NAME;

/**
 * @author rivanov
 */
public class B2BZipFileUploadServlet extends HttpServlet {

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private Logger logger = Logger.getLogger(this.getClass());

    ExternalService es = new ExternalServiceImpl();
    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    //private static final String SERVICE_NAME = Constants.B2BPOSWS;
    final private byte[] Salt = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    final private String divider = "__div__";
    private Long sessionTimeOut = 300L;

    public static final String FS_HARDDRIVE = "fsharddrive";
    public static final String FS_EXTERNAL = "fsexternal";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");

        return login;
    }

    private String getUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    /**
     * Добавить файл в архив
     *
     * @param document         - добавляемый файл
     * @param userDocumentName - имя добавляемого файла
     * @param zipOutputStream  - поток zip-archive
     * @param currentIndexFile - порядковый номер файла (для исключения перезаписи файлов с одним именем)
     * @throws IOException
     */
    private void addDocToArch(InputStream document, String userDocumentName, ZipOutputStream zipOutputStream, int currentIndexFile) throws IOException {
        int fileSize = 0;
        try {
            // создаем элемент архива
            userDocumentName = userDocumentName.replace("/", "_");
            userDocumentName = userDocumentName.replace("\\", "_");
            ZipEntry e = new ZipEntry(String.valueOf(currentIndexFile + 1) + ") " + userDocumentName);
            zipOutputStream.putNextEntry(e);
            // выливаем файл в архив
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = document.read(bytes)) != -1) {
                fileSize += read;
                zipOutputStream.write(bytes, 0, read);
            }
            // указываем конец файла
            e.setSize(fileSize);
            e.setTime(System.currentTimeMillis());
            zipOutputStream.closeEntry();
            document.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

//    /**
//     * проверка валидности сессии
//     *
//     * @param scu
//     * @param req
//     * @param resp
//     * @return
//     */
//    private boolean isValidSession(StringCryptUtils scu, HttpServletRequest req, HttpServletResponse resp) {
//        Boolean isValidSession = true;
//
//        String sessionIdCoded = req.getParameter("sid");
//        if ((sessionIdCoded == null) || (sessionIdCoded.isEmpty())) {
//            sessionIdCoded = req.getParameter("pasid");
//        }
//
//        String sessionId = null;
//        if (!sessionIdCoded.isEmpty()) {
//            try {
//                sessionId = scu.decryptURL(sessionIdCoded);
//                //sessionId = SCU.decrypt(sessionIdCoded);
//            } catch (Exception e) {
//                resp.addHeader("Error", "Неверный ИД сессии");
//                isValidSession = false;
//            }
//            if (isValidSession) {
//                String[] s = sessionId.split(divider);
//                String login = s[0];
//                String password = s[1];
//                Long timeInMillis = Long.valueOf(s[2]);
//                Long timeOut = this.sessionTimeOut; // 10 минут таймаут
//                GregorianCalendar gcSessionValid = new GregorianCalendar();
//                gcSessionValid.setTimeInMillis(timeInMillis);
//                gcSessionValid.add(Calendar.MINUTE, timeOut.intValue());
//                GregorianCalendar gcNowDate = new GregorianCalendar();
//                gcNowDate.setTime(new Date());
//                if (gcSessionValid.getTimeInMillis() < gcNowDate.getTimeInMillis()) {
//                    // new Exception("Время сессии истекло");.
//                    resp.addHeader("Error", "Время сессии истекло");
//                    isValidSession = false;
//                }
//            }
//        }
//        return isValidSession;
//    }

    protected String getFilePath(String documentName, String fsType, HttpServletRequest req) {
        String fullPath = null;

        if (!(getUseSeaweedFS().equalsIgnoreCase("TRUE") && (fsType.equalsIgnoreCase(FS_EXTERNAL)))) {
            String fp = req.getParameter("fp");
            if ((fp != null) && (fp.equalsIgnoreCase("1"))) {
                fullPath = documentName;
            } else {
                String uploadPath = Config.getConfig().getParam("uploadPath", "");
                fullPath = uploadPath + documentName;
            }
        }
        return fullPath;
    }

    /**
     * создаем поток чтения файла, который будет добавлен в архив
     *
     * @param documentName     - имя документа на диске
     * @param userDocumentName - имя документа данное пользователем
     * @param fsType           - тип
     * @param fullPath         - полный путь, если есть
     * @return
     * @throws Exception
     */
    protected InputStream getDocument(String documentName, String userDocumentName, String fsType, String fullPath) throws Exception {
        InputStream document = null;
        // если файл находится удаленно, то скачиваем его в темпфайл
        // который в последствии будет добавлен в архив
        if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (fsType.equalsIgnoreCase(FS_EXTERNAL))) {
            String masterUrlString = getSeaweedFSUrl();
            URL masterURL = new URL(masterUrlString);
            WeedFSFile file = new WeedFSFile(documentName);
            WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
            List<Location> locations = client.lookup(file.getVolumeId());
            if (locations.size() == 0) {
                logger.error("file not found");
            } else {
                String uploadPath = getUploadFilePath();
                String tempFileName = uploadPath + UUID.randomUUID() + "_" + userDocumentName;
                document = client.read(file, locations.get(0));
                BufferedOutputStream bufferedOutput = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(tempFileName);
                    bufferedOutput = new BufferedOutputStream(fileOutputStream);
                    int read;
                    final byte[] bytes = new byte[1024];
                    while ((read = document.read(bytes)) != -1) {
                        bufferedOutput.write(bytes, 0, read);
                    }
                } finally {
                    document.close();
                    if (bufferedOutput != null) {
                        bufferedOutput.flush();
                        bufferedOutput.close();
                    }
                }
                File tmpFile = new File(tempFileName);
                if (tmpFile.exists() && (tmpFile.getCanonicalPath().startsWith(uploadPath) || (tmpFile.getCanonicalPath().startsWith("/data/diasoft/")))) {
                    document = new FileInputStream(tmpFile);
                }
            }
        } else {
            // если файл расположен локально, то возращаем путь к нему.
            File f = new File(fullPath);
            if (f.exists() && f.getCanonicalPath().startsWith(getUploadFilePath())) {
                document = new FileInputStream(f);
            } else {
                logger.error("file " + fullPath + " not found");
            }
        }
        return document;
    }

    /**
     * подготовка заголовка ответа
     *
     * @param req
     * @param resp
     * @throws UnsupportedEncodingException
     */
    protected void prepareHeader(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        //возращать будем zip архив
        String mimeType = "application/zip";
        //имя архива
        String userDocumentName = "doc.zip";

        userDocumentName = URLEncoder.encode(userDocumentName, "UTF-8");

        String userAgent = req.getHeader("user-agent");
        String contType = "attachment";

        if (userAgent.indexOf("Firefox") > -1) {
            resp.setHeader("Content-Disposition", contType + "; filename*=UTF-8''" + userDocumentName.replace("+", "%20"));
        } else {
            resp.setHeader("Content-Disposition", String.format("%s; filename=\"%s\"", contType, userDocumentName.replace("+", "%20")));
        }
        resp.setContentType(mimeType + "; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionController b2bController = new B2BSessionController(this.sessionTimeOut);
        String sessionId = req.getParameter("sid");
        sessionId = URLEncoder.encode(sessionId, "UTF-8");
        Map<String, Object> sessionParams = b2bController.checkSession(sessionId);
        if (!B2BSessionController.sessionWithError(sessionParams)) {
            // вытаскиваем лист зашифрованных файлов
            String[] documentNameCryptList;
            documentNameCryptList = req.getParameterValues("fn");
            // подгатавливаем заголовок ответа
            prepareHeader(req, resp);
            // поток для чтения файлов
            InputStream document = null;
            // поток отдачи архива
            ZipOutputStream outZipStream = null;
            SessionController b2bFileController = new B2BFIleSessionController(this.sessionTimeOut);
            try {
                outZipStream = new ZipOutputStream(resp.getOutputStream());
                int currentIndexFile = 0;
                for (String documentNameCrypt : documentNameCryptList) {
                    // разбираем зашифрованную строку, с информацией о файле
                    Map<String, Object> fileParams = b2bFileController.checkSession(URLEncoder.encode(documentNameCrypt, "UTF-8"));
                    // параметры не могут быть пустыми, если сессия сгенерена контроллером
                    String fsType = (String) fileParams.get(FS_TYPE_PARAMNAME);
                    String documentName = (String) fileParams.get(SOME_ID_PARAMNAME);
                    String userDocumentName = (String) fileParams.get(USER_DOCNAME_PARAMNAME);
                    // находим полный путь до файла, если он расположен локально
                    String fullPath = getFilePath(documentName, fsType, req);
                    // создаем поток до файла
                    document = getDocument(documentName, userDocumentName, fsType, fullPath);
                    if (document != null) {
                        // добавляем файл в архив
                        addDocToArch(document, userDocumentName, outZipStream, currentIndexFile);
                        currentIndexFile++;
                    }

                }
            } catch (Exception e) {
                logger.error(e);
            } finally {
                if (outZipStream != null) {
                    outZipStream.finish();
                    outZipStream.close();
                }
                if (document != null) {
                    document.close();
                }
            }
        }
    }
}
