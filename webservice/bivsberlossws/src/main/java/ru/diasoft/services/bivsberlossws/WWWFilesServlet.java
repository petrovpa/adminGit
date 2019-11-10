package ru.diasoft.services.bivsberlossws;

import com.bivgroup.rest.api.system.crypto.JsonMapCrypter;
import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.seaweedfs.client.WeedFSFile;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.services.inscore.system.external.impl.ExternalServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.bivgroup.rest.api.system.ParamConstants.*;
import static ru.diasoft.services.utils.Constants.SERVICE_NAME;

public class WWWFilesServlet extends HttpServlet {

    ExternalService es = new ExternalServiceImpl();

    public static final String ERROR_HEADER_PARAM = "Error";

    private Long sessionTimeOut = 300L;

    public static final String FS_HARDDRIVE = "fsharddrive";
    public static final String FS_EXTERNAL = "fsexternal";

    private Logger logger = Logger.getLogger(this.getClass());
    private String logName;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    protected static Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected static Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    // аналог getLongParam, но с протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName) {
        Long paramValue = getLongParam(map, keyName);
        if (logger.isDebugEnabled()) {
            logger.debug(keyName + " = " + paramValue);
        }
        return paramValue;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String docInfoJsonStrEncrypted = request.getParameter("fn");
        if ((docInfoJsonStrEncrypted == null) || (docInfoJsonStrEncrypted.isEmpty())) {
            addErrorHeaderAndSendError(
                    response, HttpServletResponse.SC_BAD_REQUEST,
                    "File identifier not found in request parameters!",
                    "Не указан идентификатор файла!"
            );
            return;
        }

        JsonMapCrypter jsonMapCrypter = new JsonMapCrypter();
        Map<String, Object> docInfoMap = null;
        try {
            docInfoMap = jsonMapCrypter.decrypt(docInfoJsonStrEncrypted);
        } catch (Exception ex) {
            logger.error("Decrypting document info json caused exception!", ex);
        }
        if ((docInfoMap == null) || (docInfoMap.isEmpty())) {
            addErrorHeaderAndSendErrorAboutWrongFileId(response);
            return;
        } else {
            // для протоколирования
            docInfoMap.put("$request", request);
        }

        Long linkParamVersion;
        Long linkType;
        Long linkCreateMs;
        String fsType;
        String documentName;
        String userDocumentName;
        Long prodRepId = null;
        Long binFileId = null;
        try {
            linkParamVersion = getLongParamLogged(docInfoMap, FILE_LINK_VERSION_PARAMNAME);
            linkType = getLongParamLogged(docInfoMap, FILE_LINK_TYPE_PARAMNAME);
            linkCreateMs = getLongParamLogged(docInfoMap, FILE_LINK_CREATE_TIME_MS_PARAMNAME);
            fsType = (String) docInfoMap.get(FILE_LINK_FILE_SYSTEM_TYPE_PARAMNAME);
            documentName = (String) docInfoMap.get(FILE_LINK_FILE_PATH_PARAMNAME);
            userDocumentName = (String) docInfoMap.getOrDefault(FILE_LINK_USER_DOC_NAME_PARAMNAME, "Документ");
            binFileId = getLongParamLogged(docInfoMap, FILE_LINK_DATABASE_ID_PARAMNAME);
            if (linkParamVersion >= 3) {
                prodRepId = getLongParamLogged(docInfoMap, FILE_LINK_REPORT_DATABASE_ID_PARAMNAME);
            }
        } catch (Exception ex) {
            logger.error("Getting param's values from decrypted document info json caused exception! Details: " + docInfoMap, ex);
            addErrorHeaderAndSendErrorAboutWrongFileId(response);
            return;
        }

        if (FILE_LINK_TYPE_HOURS_48.equals(linkType)) {
            logger.debug("Link type - 24 hours.");
            GregorianCalendar nowDateGC = new GregorianCalendar();
            GregorianCalendar linkOutdatedDateGC = new GregorianCalendar();
            linkOutdatedDateGC.setTimeInMillis(linkCreateMs);
            // todo: отдельная мапа со сроками действия в зависимости от типа ссылки (или м.б. справочник, в БД и пр.)
            linkOutdatedDateGC.add(Calendar.HOUR, 48);
            if (nowDateGC.after(linkOutdatedDateGC)) {
                addErrorHeaderAndSendError(
                        response, HttpServletResponse.SC_FORBIDDEN,
                        "File link is outdated!",
                        "Истек срок действия ссылки!"
                );
                return;
            }
        } else if (FILE_LINK_TYPE_PERMANENT.equals(linkType)) {
            logger.debug("Link type - permanent.");
        } else {
            logger.error(String.format(
                    "Unsupported file link type value (%s) was found in decrypted document info! Details: %s",
                    linkType, docInfoMap
            ));
            addErrorHeaderAndSendErrorAboutWrongFileId(response);
            return;
        }
        String userAgent = request.getHeader("user-agent");
        // todo: добавить поддержку полных путей (когда/если потребуется)
        // String fp = request.getParameter("fp");
        String fp = null;
        if ((linkParamVersion >= 3) && (binFileId == null) && (prodRepId != null)) {
            // статический документ (памятка, правила или т.п.) - получение по полному пути
            fp = "1";
        }

        // получение файла (содержимое метода аналогично используемому коду в B2BFileUpLoadServlet.doGet)
        getFile(response, userAgent, fsType, documentName, userDocumentName, fp);

    }

    private void addErrorHeaderAndSendError(HttpServletResponse response, int errorResponse, String error, String errorRu) {
        response.addHeader(ERROR_HEADER_PARAM, error);
        try {
            response.sendError(errorResponse, error);
            /*
            byte[] ascii = error.getBytes(StandardCharsets.US_ASCII);
            String asciiString = Arrays.toString(ascii);
            response.sendError(errorResponse, asciiString);
            */
        } catch (IOException ex) {
            logName = this.getClass().getSimpleName();
            logger.error(String.format(
                    "%s#addErrorHeaderAndSendError exception on response.sendError: %s",
                    logName, ex.getLocalizedMessage()
            ), ex);
        }
    }

    /** Получение файла (фрагмент аналогичен используемому коду в B2BFileUpLoadServlet.doGet) */
    private void getFile(HttpServletResponse response, String userAgent, String fsType, String documentName, String userDocumentName, String fp) throws IOException {
        // фрагмент кода отсюда и ниже скопирован из B2BFileUpLoadServlet.doGet
        // todo: вынести в общий метод (если/когда потребуются общие изменения в функционале)
        // todo: оставить разделение реализаций (если/когда потребуются различные функционалы по внешним и внутренним файлам)
        if ((documentName == null) || (documentName.isEmpty())) {
            return;
        }
        InputStream document = null;
        try {
            String fullPath;
            long fileLength = 0;
            if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (FS_EXTERNAL.equalsIgnoreCase(fsType))) {
                String masterUrlString = getSeaweedFSUrl();
                URL masterURL = new URL(masterUrlString);
                WeedFSFile file = new WeedFSFile(documentName);
                WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                List<Location> locations = client.lookup(file.getVolumeId());
                if (locations.size() == 0) {
                    // System.out.println("file not found");
                    // return;
                    throw new FileNotFoundException("Seaweeds file not found!");
                }
                String uploadPath = getUploadFilePath();
                String tempFileName = uploadPath + UUID.randomUUID() + "_" + userDocumentName;
                fullPath = tempFileName;
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
                } catch (Exception ex) {
                    logger.error("File read error!", ex);
                    throw ex;
                } finally {
                    document.close();
                    if (bufferedOutput != null) {
                        bufferedOutput.flush();
                        bufferedOutput.close();
                    }
                    if (bufferedOutput != null) {
                        bufferedOutput.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
                File tmpFile = new File(tempFileName);
                if (tmpFile.getCanonicalPath().startsWith(uploadPath)) {
                    if (tmpFile.exists()) {
                        document = new FileInputStream(tmpFile);
                    }
                }
            } else {
                File f;
                // String fp = req.getParameter("fp");
                String uploadPath = Config.getConfig().getParam("uploadPath", "");
                if (!uploadPath.endsWith("/") && !uploadPath.endsWith("\\")) {
                    uploadPath = uploadPath + File.separator;
                }
                if ((fp != null) && (fp.equalsIgnoreCase("1"))) {
                    fullPath = documentName;
                    f = new File(fullPath);
                } else {
                    fullPath = uploadPath + documentName;
                    f = new File(fullPath);
                }
                if (f.exists() && (f.getCanonicalPath().startsWith(uploadPath) || f.getCanonicalPath().startsWith("/data/diasoft/"))) {
                    document = new FileInputStream(f);
                } else {
                    throw new FileNotFoundException(String.format("File '%s' not found!", f.getCanonicalPath()));
                }
            }
            //
            ServletContext context = getServletContext();
            // gets MIME type of the file
            String mimeType = context.getMimeType(fullPath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }
            // System.out.println("MIME type: " + mimeType);
            logger.debug("MIME type: " + mimeType);
            userDocumentName = URLEncoder.encode(userDocumentName, "UTF-8");

            // String userAgent = request.getHeader("user-agent");
            String contType = "attachment";
            if (mimeType.equalsIgnoreCase("application/pdf")) {
                contType = "inline";
            }
            if (userAgent.contains("Firefox")) {
                response.setHeader("Content-Disposition", contType + "; filename*=UTF-8''" + userDocumentName.replace("+", "%20"));
                //resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + userDocumentName.replace("+", "%20"));
            } else {
                response.setHeader("Content-Disposition", String.format("%s; filename=\"%s\"", contType, userDocumentName.replace("+", "%20")));
                //resp.setHeader("Content-Disposition", String.format("attachement; filename=\"%s\"", userDocumentName.replace("+", "%20")));
            }
            //resp.setHeader("Content-Transfer-Encoding", "binary");
            response.setContentType(mimeType + "; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try {
                ServletOutputStream stream = response.getOutputStream();
                int read;
                final byte[] bytes = new byte[1024];
                while ((read = document.read(bytes)) != -1) {
                    fileLength += read;
                    stream.write(bytes, 0, read);
                }
                response.flushBuffer();
            } catch (Exception ex) {
                logger.error("Output stream write error!", ex);
                throw ex;
            } finally {
                if (document != null) {
                    document.close();
                }
            }
            response.setContentLength((int) fileLength);
        } catch (FileNotFoundException ex) {
            logger.error(this.getClass().getSimpleName() + "#doGet file not found exception: " + ex.getLocalizedMessage(), ex);
            addErrorHeaderAndSendError(
                    response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Requested file not found!",
                    "Не найден запрашиваемый файл!"
            );
        } catch (Exception ex) {
            logger.error(this.getClass().getSimpleName() + "#doGet exception: " + ex.getLocalizedMessage(), ex);
            addErrorHeaderAndSendError(
                    response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Request processing error!",
                    "Ошибка при обработке запроса!"
            );
        } finally {
            if (document != null) {
                document.close();
            }
        }
        // фрагмент кода отсюда и выше скопирован из B2BFileUpLoadServlet.doGet
        // todo: вынести в общий метод (если/когда потребуются общие изменения в функционале)
        // todo: оставить разделение реализаций (если/когда потребуются различные функционалы по внешним и внутренним файлам)
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // throw new ServletException(new UnsupportedOperationException());
        addErrorHeaderAndSendError(
                response, HttpServletResponse.SC_NOT_IMPLEMENTED,
                "POST not supported",
                "POST-запросы не поддерживаются!"
        );
    }

    private void addErrorHeaderAndSendErrorAboutWrongFileId(HttpServletResponse response) {
        addErrorHeaderAndSendError(
                response, HttpServletResponse.SC_BAD_REQUEST,
                "Wrong file identifier!",
                "Неверный идентификатор файла!"
        );
    }

}
