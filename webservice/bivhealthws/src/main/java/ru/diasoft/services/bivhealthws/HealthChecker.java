/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.diasoft.services.bivhealthws;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.external.ExternalService;

/**
 *
 * @author averichevsm
 */
public class HealthChecker extends HttpServlet {

    private static final String SERVICE_NAME = "bivhealthws";
    private Logger logger = Logger.getLogger(HealthChecker.class);

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return "";
        } else {
            return bean.toString();
        }
    }

    protected String getLogin() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("DEFAULTLOGIN", "os1");
        return login;
    }

    protected String getPassword() {
        String password;
        Config config = Config.getConfig(SERVICE_NAME);
        password = config.getParam("DEFAULTPASSWORD", "356a192b7913b04c54574d18c28d46e6395428ab");
        return password; //To change body of generated methods, choose Tools | Templates.
    }

    protected String getWarList() {
        String WarList;
        Config config = Config.getConfig(SERVICE_NAME);
        WarList = config.getParam("WARCHECKLIST", "");
        return WarList; //To change body of generated methods, choose Tools | Templates.
    }

    private Map<String, Object> callExternalService(String moduleName, String methodName, Map<String, Object> params, String login, String password) {
        if (moduleName != null && methodName != null) {
            logger.debug(String.format("Begin callExternalService [%s:%s] on behalf of [%s]", moduleName, methodName, login));
        } else {
            logger.debug("Begin callExternalService ");
        }
        Map<String, Object> result;
        ExternalService ex = ExternalService.createInstance();
        try {
            result = ex.callExternalService(moduleName, methodName, params, login, password);
        } catch (Exception ex1) {
            result = null;
            if (params != null) {
                logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with params [%s]", moduleName, methodName, login, params.toString()), ex1);
            } else {
                logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with null params", moduleName, methodName, login), ex1);
            }
        }
        logger.debug("End callExternalService ");
        return result;
    }

    public static final String[] checkingWSList = {
        "b2bposws",
        "bivsberposws",
        "paws"
    };

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "value");
        String login = getLogin();
        String password = getPassword();
        Boolean result = true;
        String body = "";
        String warListStr = getWarList();
        if (!warListStr.isEmpty()) {
            String[] warList = warListStr.split(",");

            for (String wsName : warList) {
                logger.debug("check " + wsName);
                Map<String, Object> res = callExternalService(wsName, "healthCheck", params, login, password);
                body = body + "check " + wsName + " - ";
                if (res == null || res.get("Status") == null || "Error".equalsIgnoreCase(res.get("Status").toString())) {
                    result = false;
                    break;
                }
                body = body + "done; ";
            }
        }
        if (result) {
            response.setStatus(200);
        } else {
            body = body + "fail; ";
            response.setStatus(500);
        }

        response.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HealthChecker" + request.getContextPath() + "</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + body + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }*/
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "ws health checker";
    }// </editor-fold>

}
