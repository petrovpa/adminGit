/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.diasoft.services.bivsberlossws;

import com.bivgroup.seaweedfs.client.*;
import com.bivgroup.sessionutils.SessionController;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
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

import static ru.diasoft.services.bivsberlossws.B2BFIleSessionController.*;
import static ru.diasoft.services.bivsberlossws.B2BSessionController.B2B_USERLOGIN_PARAMNAME;
import static ru.diasoft.services.utils.Constants.SERVICE_NAME;

/**
 * @author kkulkov
 */
public class B2BFileUpLoadServlet extends HttpServlet {

    ExternalService es = new ExternalServiceImpl();
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String sessionId = req.getParameter("sid");
        sessionId = URLEncoder.encode(sessionId, "UTF-8");
        SessionController controller = new B2BSessionController(this.sessionTimeOut);
        if ((sessionId == null) || (sessionId.isEmpty())) {
            sessionId = req.getParameter("pasid");
            URLEncoder.encode(sessionId, "UTF-8");
        }
        // тут только проверка на таймаут, если расшифровалось вообще
        Map<String, Object> sessionParams;
        if (!sessionId.isEmpty()) {
            sessionParams = controller.checkSession(sessionId);
            if (B2BFIleSessionController.sessionWithError(sessionParams)) {
                response.addHeader(ERROR, (String) sessionParams.get(ERROR));
                return;
            }
        }

        String documentNameCrypt;
        documentNameCrypt = req.getParameter("fn");
        documentNameCrypt = URLEncoder.encode(documentNameCrypt,"UTF-8");
        SessionController FScontroller = new B2BFIleSessionController(this.sessionTimeOut);
        Map<String, Object> fileSessionParams = FScontroller.checkSession(documentNameCrypt);
        if (B2BFIleSessionController.sessionWithError(fileSessionParams)) {
            response.addHeader(ERROR, (String) fileSessionParams.get(ERROR));
            return;
        }
        String fsType = (String) fileSessionParams.get(FS_TYPE_PARAMNAME);
        String documentName = (String) fileSessionParams.get(SOME_ID_PARAMNAME);
        String userDocumentName = (String) fileSessionParams.getOrDefault(USER_DOCNAME_PARAMNAME, "Документ");

        // фрагмент кода отсюда и ниже скопирован в WWWFilesServlet.getFile
        // todo: вынести в общий метод (если/когда потребуются общие изменения в функционале)
        // todo: оставить разделение реализаций (если/когда потребуются различные функционалы по внешним и внутренним файлам)
        if ((documentName != null) && (!documentName.isEmpty())) {
            InputStream document = null;
            try {
                String fullPath;
                long fileLength = 0;
                if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (fsType.equalsIgnoreCase(FS_EXTERNAL))) {
                    String masterUrlString = getSeaweedFSUrl();
                    URL masterURL = new URL(masterUrlString);
                    WeedFSFile file = new WeedFSFile(documentName);
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    List<Location> locations = client.lookup(file.getVolumeId());
                    if (locations.size() == 0) {
                        System.out.println("file not found");
                        return;
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
                    } finally {
                        document.close();
                        if (bufferedOutput != null) {
                            bufferedOutput.flush();
                            bufferedOutput.close();
                        }
                        bufferedOutput.close();
                        fileOutputStream.close();

                    }
                    File tmpFile = new File(tempFileName);
                    if (tmpFile.getCanonicalPath().startsWith(uploadPath)) {
                        if (tmpFile.exists()) {
                            document = new FileInputStream(tmpFile);
                        }
                    }
                } else {
                    File f;
                    String fp = req.getParameter("fp");
                    String uploadPath = Config.getConfig().getParam("uploadPath", "");
                    if ((fp != null) && (fp.equalsIgnoreCase("1"))) {
                        fullPath = documentName;
                        f = new File(fullPath);
                    } else {
                        fullPath = uploadPath + documentName;
                        f = new File(fullPath);
                    }
                    if (f.exists() && (f.getCanonicalPath().startsWith(uploadPath) || f.getCanonicalPath().startsWith("/data/diasoft/"))) {
                        document = new FileInputStream(f);
                    }
                }
                ServletContext context;
                context = getServletContext();

                // gets MIME type of the file
                String mimeType = context.getMimeType(fullPath);
                if (mimeType == null) {
                    // set to binary type if MIME mapping not found
                    mimeType = "application/octet-stream";
                }
                System.out.println("MIME type: " + mimeType);
                userDocumentName = URLEncoder.encode(userDocumentName, "UTF-8");

                String userAgent = req.getHeader("user-agent");
                String contType = "attachment";
                if (mimeType.equalsIgnoreCase("application/pdf")) {
                    contType = "inline";
                }
                if (userAgent.indexOf("Firefox") > -1) {
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
                } finally {
                    document.close();
                }
                response.setContentLength((int) fileLength);
            } catch (Exception ex) {
                org.apache.log4j.Logger.getLogger(this.getClass()).debug("B2BFileUpLoadServlet#doGet exception: " + ex);
            } finally {
                document.close();
            }
        }
        // фрагмент кода отсюда и выше скопирован в WWWFilesServlet.getFile
        // todo: вынести в общий метод (если/когда потребуются общие изменения в функционале)
        // todo: оставить разделение реализаций (если/когда потребуются различные функционалы по внешним и внутренним файлам)
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sessionId = request.getParameter("sid");
        if (!sessionId.isEmpty()) {
            return;
        }
        SessionController controller = new B2BSessionController(this.sessionTimeOut);
        sessionId = URLEncoder.encode(sessionId, "UTF-8");
        Map<String, Object> sessionparams = controller.checkSession(sessionId);
        if (B2BSessionController.sessionWithError(sessionparams)) {
            response.addHeader(ERROR, (String) sessionparams.get(ERROR));
            return;
        }
        String login = (String) sessionparams.get(B2B_USERLOGIN_PARAMNAME);
        String password = (String) sessionparams.get(B2B_USERLOGIN_PARAMNAME);
        String path = Config.getConfig().getParam("uploadPath", WsConstants.DEFAULT_REPORT_PATH);
        try {
            List<Object> files = new ArrayList();
            Map<String, Object> params = new HashMap();
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(new File("/dir/for/temporary/files"));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(10240);
            params.put("PATH", path);
            params.put(WsConstants.LOGIN, login);
            params.put(WsConstants.PASSWORD, password);
            List items = upload.parseRequest(request);
            for (Iterator i = items.iterator(); i.hasNext(); ) {
                FileItem item = (FileItem) i.next();
                //Проверяем, является ли параметр обычным полем из HTML-формы,
                //если да, то помещаем в Map пару name=value...
                if (item.isFormField()) {
                    params.put(item.getFieldName(), item.getString());
                } else {
                    if (item.getSize() <= 0) {
                        continue;
                    }
                    files.add(item);
                }
            }
            //Сохраняем файл на сервере
            save(files, params);
            response.setContentType("text/html; charset=windows-1251");
            final PrintWriter writer = response.getWriter();
            writer.println("Файл успешно загружен<br>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }


    private void save(List files, Map<String, Object> params) throws IOException, Exception {
        try {
            for (Iterator i = files.iterator(); i.hasNext(); ) {
                FileItem item = (FileItem) i.next();

                String filePath = null;
                long fileSize = 0;
                if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                    String masterUrlString = getSeaweedFSUrl();
                    URL masterURL = new URL(masterUrlString);
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    Assignation a = client.assign(new AssignParams("b2battach", ReplicationStrategy.TwiceOnRack));
                    int writtenSize = client.write(a.weedFSFile, a.location, item.get(), item.getName());
                    if (item.getSize() != writtenSize) {
                        throw new Exception("Unable to write file to SeaweedFS");
                    }
                    filePath = a.weedFSFile.fid;
                    fileSize = writtenSize;
                } else {
                    //Файл, в который нужно произвести запись 
                    String diskFileName = UUID.randomUUID().toString();
                    filePath = params.get("PATH").toString() + File.separator + diskFileName;

                    final File file = new File(filePath);
                    if (file.getCanonicalPath().startsWith(params.get("PATH").toString())) {
                        FileOutputStream fos = new FileOutputStream(file);
                        try {
                            fos.write(item.get());
                        } finally {
                            fos.close();
                        }
                        fileSize = file.getTotalSpace();
                    }
                }
                Map<String, Object> qParams = new HashMap<String, Object>();
                qParams.put("OBJID", params.get("OBJID"));
                qParams.put("FILENAME", item.getName());
                qParams.put("FILEPATH", filePath);
                qParams.put("FILESIZE", fileSize);
                qParams.put("FILETYPEID", params.get("FILETYPEID"));
                if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                    qParams.put("FSID", filePath);
                }
                qParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
                es.callExternalService("b2bposws", "dsB2BContractDocument_BinaryFile_createBinaryFileInfo", qParams, params.get(WsConstants.LOGIN).toString(), params.get(WsConstants.PASSWORD).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
