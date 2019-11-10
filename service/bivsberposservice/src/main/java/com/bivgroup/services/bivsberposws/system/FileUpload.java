/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import javax.activation.FileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author kkulkov
 */
public class FileUpload extends HttpServlet {

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String documentNameCrypt;
        documentNameCrypt = req.getParameter("fn");
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        String documentNameStr = scu.decryptURL(documentNameCrypt);
        String[] docArr = documentNameStr.split("@");
        String documentName = docArr[0];
        String userDocumentName = docArr[1];
//        String userDocumentName = req.getParameter("ufn");
        if (userDocumentName == null) {
            userDocumentName = "Полис подписанный.pdf";
        }
        if ((documentName != null) && (!documentName.isEmpty())) {
            String uploadPath = Config.getConfig().getParam("uploadPath", "");
            File f = new File(uploadPath + documentName);
            if (f.getCanonicalPath().startsWith(uploadPath)) {
                InputStream document = new FileInputStream(f);

                ServletContext context;
                context = getServletContext();

                // gets MIME type of the file
                String mimeType = context.getMimeType(uploadPath + documentName);
                if (mimeType == null) {
                    // set to binary type if MIME mapping not found
                    mimeType = "application/octet-stream";
                }
                System.out.println("MIME type: " + mimeType);
                userDocumentName = URLEncoder.encode(userDocumentName, "UTF-8");

                String userAgent = req.getHeader("user-agent");
                if (userAgent.indexOf("Firefox") > -1) {
                    resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + userDocumentName.replace("+", "%20"));
                } else {
                    resp.setHeader("Content-Disposition", String.format("attachement; filename=\"%s\"", userDocumentName.replace("+", "%20")));
                }
                //resp.setHeader("Content-Transfer-Encoding", "binary");
                resp.setContentType(mimeType + "; charset=UTF-8");
                resp.setContentLength((int) f.length());
                resp.setCharacterEncoding("UTF-8");
                try {

                    ServletOutputStream stream = resp.getOutputStream();
                    int read;
                    final byte[] bytes = new byte[1024];
                    while ((read = document.read(bytes)) != -1) {
                        stream.write(bytes, 0, read);
                    }
                    resp.flushBuffer();
                } finally {
                    document.close();
                }
            }
        }
    }
}
