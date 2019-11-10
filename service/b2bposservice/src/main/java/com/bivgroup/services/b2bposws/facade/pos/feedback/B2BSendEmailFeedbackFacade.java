package com.bivgroup.services.b2bposws.facade.pos.feedback;

import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.seaweedfs.client.WeedFSFile;
import com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController;
import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController.B2B_USERACCOUNTID_PARAMNAME;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BSendEmailFeedback")
public class B2BSendEmailFeedbackFacade extends B2BDeclarationBaseFacade {
    private static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS;
    private static final String TEMPLATE_MESSAGE_PATH = "personalAreaFeedbackMessage";
    private static final String ADMIN_EMAIL;
    private static final String PROJECT_PARAM_NAME = "project";
    private static final String SUBJECT_PARAM_NAME = "TITLE";
    private static final String MESSAGE_PARAM_NAME = "NOTE";
    private static final String MESSAGE_ID_PARAM_NAME = "messageId";
    private static final Long EMAIL_TYPE_ID = 1006L;
    public static final String SUPPORT_EMAIL_ADDRESS_PARAMNAME = "SBERINSSUPPORTEMAIL";
    public static final String B2B_LK_CROSSSESSION_PARAMNAME = "b2bsessionId";

    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    protected static final String B2BPOSWS = Constants.B2BPOSWS;

    private final Logger logger = Logger.getLogger(this.getClass());

    static {
        Config config = Config.getConfig(B2BPOSWS);
        ADMIN_EMAIL = config.getParam("adminMail", "onshchukina@sberinsur.ru");
    }

    @WsMethod(requiredParams = {CLIENT_PROFILE_ID_PARAMNAME, MESSAGE_ID_PARAM_NAME})
    public Map<String, Object> dsB2BSendFeedbackOnEmail(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Long clientProfileId = getLongParam(params, CLIENT_PROFILE_ID_PARAMNAME);

        String error = "";
        Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
        Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");
        if (client == null) {
            error = "Не удалось получить данные профиля клиента, сохраняющего заявление на изменение условий страхования!";
        }

        List<Map<String, Object>> contacts = new ArrayList<>();
        if (error.isEmpty()) {
            Map<String, Object> applicant = makeContragentFromClient(client);
            contacts = getOrCreateListParam(applicant, "contacts");
            contacts = contacts.stream().filter(new Predicate<Map<String, Object>>() {
                @Override
                public boolean test(Map<String, Object> stringObjectMap) {
                    return EMAIL_TYPE_ID.equals(getLongParam(stringObjectMap, "typeId"));
                }
            }).collect(Collectors.toList());
            if (contacts.isEmpty()) {
                error = "У пользователя отсутствует хотя бы один персональный адрес электронной почты";
            }
        }

        String messageTemplatePath = "";
        if (error.isEmpty()) {
            messageTemplatePath = getCoreSettingBySysName(TEMPLATE_MESSAGE_PATH, login, password);
            if (messageTemplatePath == null || messageTemplatePath.isEmpty()) {
                error = "Шаблон сообщения отсутствует";
            }
        }

        Map<String, Object> sendParams = new HashMap<>();
        List<Map<String, Object>> messages = new ArrayList<>();
        if (error.isEmpty()) {
            String email = getStringParam(contacts.get(0), "value");
            String toDayDate = df.format(new Date());
            // Согласно задаче 14929 тема сообщения должна содержать не текущую дату, а дату рождения клиента, т.е.:
            // "Ф.И.О. дата_рождения e-mail"
            Date dateOfBirth = getDateParam(client.get("dateOfBirth"));
            String dateOfBirthString = "";
            if (dateOfBirth != null){
                dateOfBirthString = df.format(dateOfBirth);
            }
            String surname = getStringParam(client, "surname");
            String name = getStringParam(client, "name");
            String patronymic = getStringParam(client, "patronymic");
            String fullName = String.format("%s %s %s", surname, name, patronymic);

            String smtpSubject = String.format("%s %s %s", fullName, dateOfBirthString, email);

            sendParams.put("SMTPSubject", smtpSubject);
            Map<String, Object> messageQury = new HashMap<>();
            messageQury.put("ID", getLongParam(params, MESSAGE_ID_PARAM_NAME));
            messageQury.put("CLIENTPROFILEID", clientProfileId);
            messages = this.callServiceAndGetListFromResultMapLogged(THIS_SERVICE_NAME,
                    "dsMessageBrowseListByParamEx", messageQury, login, password);
            if (messages == null || messages.isEmpty()) {
                error = "Не найдено сообщение с указаным идентификатором для данного пользователя";
            }
        }

        Map<String, Object> sendRes = new HashMap<>();
        if (error.isEmpty()) {
            Map<String, Object> message = messages.get(0);
            String subjectText = getStringParam(message, SUBJECT_PARAM_NAME);
            String emailText = getStringParam(message, MESSAGE_PARAM_NAME);
            sendParams.put("SMTPMESSAGE", "Тема: " + subjectText + "\n" + "Сообщение: " + emailText);
            sendParams.put("SMTPReceipt", ADMIN_EMAIL);
            Map<String, Object> attachListQueryParams = new HashMap<>();
            attachListQueryParams.put("ID", message.get("ID"));
            List<Map<String, Object>> attachList = this.callServiceAndGetListFromResultMap(THIS_SERVICE_NAME, "dsMessageDocumentBrowseListByParam",
                    attachListQueryParams, login, password);

            Map<String, Object> convertedAttachMap = new HashMap<>();
            error = handlingAttachments(convertedAttachMap, attachList);

            if (error.isEmpty()) {
                sendParams.put("ATTACHMENTMAP", convertedAttachMap);
                String htmlTest = getHtmlText(messageTemplatePath, subjectText, emailText, login, password);
                sendParams.put("HTMLTEXT", htmlTest);
            }

            try {
                boolean isError = false;
                saveEmailInFile(sendParams, login, password);
                for (int i = 0; i < 5; i++) {
                    if (sendRes == null || (sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    } else {
                        break;
                    }
                }

                if (sendRes == null || (sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    isError = true;
                    error = "Не удалось отправить сообщение";
                    logger.debug("mailSendFail");
                }

                if (!isError) {
                    logger.debug("mailSendSuccess");
                }

            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
                saveEmailInFile(sendParams, login, password);
            }
        }

        if (!error.isEmpty()) {
            if (sendRes == null) {
                sendRes = new HashMap<>();
            }
            sendRes.put(ERROR, error);
        }
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, sendRes);
        return result;
    }

    private String getHtmlText(String messageTemplatePath, String subject, String message, String login, String password) throws Exception {
        String project = getProjectName(login, password);
        String metadataUrl = getMetadataURL(login, password, project);

        String result = "";
        String fullPath = metadataUrl + messageTemplatePath;
        File input = new File(fullPath);
        if (input.exists() && input.getCanonicalPath().startsWith(metadataUrl)) {
            //BufferedReader reader = new BufferedReader(new FileReader(fullPath));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fullPath), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String resString = sb.toString();
            resString = resString.replaceAll("\\Q!" + "SUBJECT" + "!\\E", subject);
            resString = resString.replaceAll("\\Q!" + "MESSAGE" + "!\\E", message);
            result = resString;
        }
        return result;
    }

    private String getMetadataURL(String login, String password, String project) throws Exception {
        Map args = new HashMap();
        args.put("fileType", "REPORTS");
        args.put(PROJECT_PARAM_NAME, project);
        String adminwsURL = Config.getConfig().getParam("adminws", "http://localhost:8080/adminws/adminws");
        String COREWSURL = Config.getConfig().getParam("corews", "http://localhost:8080/corews/corews");
        try {
            XMLUtil xmlutil = new XMLUtil(login, password);
            args.put("PROJECTSYSNAME", project);
            Map resultMap = xmlutil.doURL(adminwsURL, "admProjectBySysname", xmlutil.createXML(args), null);

            List result = (List) resultMap.get("Result");
            if (logger.isDebugEnabled()) {
                logger.debug("projectBySysname result = " + result);
            }
            if ((result != null) && (result.size() == 1)
                    && (((Map) result.get(0)).containsKey("METADATAURL")) && ((((Map) result.get(0)).get("METADATAURL") instanceof String))
                    && (!((Map) result.get(0)).get("METADATAURL").toString().equals(""))) {
                return ((Map) result.get(0)).get("METADATAURL").toString();
            }

            resultMap = xmlutil.doURL(COREWSURL, "getSystemMetadataURL", xmlutil.createXML(args), null);
            if (logger.isDebugEnabled()) {
                logger.debug("getMetadataURL result = " + resultMap);
            }
            if (resultMap.containsKey("MetadataURL")) {
                return resultMap.get("MetadataURL").toString();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
        return null;
    }

    private String handlingAttachments(Map<String, Object> convertedAttachMap, List<Map<String, Object>> attachList) throws IOException {
        if (!attachList.isEmpty()) {
            String fsType;
            if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                fsType = Constants.FS_EXTERNAL;
            } else {
                fsType = Constants.FS_HARDDRIVE;
            }
            String uploadPath = getUploadFilePath();
            String masterUrlString = getSeaweedFSUrl();
            for (Map<String, Object> entry : attachList) {
                String fileName = getStringParam(entry, "FILENAME");
                String filePath = getStringParam(entry, "FILEPATH");
                if (fsType.equalsIgnoreCase(Constants.FS_EXTERNAL)) {
                    String tempFileName = uploadPath + UUID.randomUUID() + "_" + fileName;

                    // сохраняем файл из внешней системы в темповый на диск
                    URL masterURL = new URL(masterUrlString);
                    WeedFSFile file = new WeedFSFile(filePath);
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    List<Location> locations = client.lookup(file.getVolumeId());
                    if (locations.size() == 0) {
                        System.out.println("file not found");
                    } else {
                        InputStream fsFile = client.read(file, locations.get(0));
                        BufferedOutputStream bufferedOutput = null;
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(tempFileName);
                            bufferedOutput = new BufferedOutputStream(fileOutputStream);
                            int read;
                            final byte[] bytes = new byte[1024];
                            while ((read = fsFile.read(bytes)) != -1) {
                                bufferedOutput.write(bytes, 0, read);
                            }
                        } finally {
                            fsFile.close();
                            if (bufferedOutput != null) {
                                bufferedOutput.flush();
                                bufferedOutput.close();
                            }
                        }
                        File tmpFile = new File(tempFileName);
                        if (tmpFile.exists() && tmpFile.getCanonicalPath().startsWith(uploadPath)) {
                            convertedAttachMap.put(new String(fileName.getBytes("utf-8"), "utf-8"), tempFileName);
                        }
                    }
                } else {
                    convertedAttachMap.put(new String(fileName.getBytes("utf-8"), "utf-8"), uploadPath + filePath);
                }
            }
        }
        return "";
    }

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    @WsMethod()
    public Map<String,Object> dsB2BsendEmail(Map<String,Object> params){
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String subject = getStringParam(params, "subject");
        String text =  getStringParam(params, "text");
        String email = getStringParam(params, "email");
        String error = "Не верно заполнены поля вызова сервиса";
        String callError = "Ошибка при отправке сообщения";

        if (stringParamsAreNotNull(text, email, subject)) {
            Map<String, Object> callParams = new HashMap<>();
            callParams.put("SMTPMESSAGE", text);
            callParams.put("SMTPSubject", subject);
            callParams.put("SMTPReceipt", email);
            try {
                Map<String,Object> result = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", callParams, login, password);
                return result;
            } catch (Exception ex) {
                logger.error(callError + email + ":" + ex.toString());
                return B2BResult.error(callError + email);
            }
        } else {
            logger.error(error);
            return B2BResult.error(error);
        }
    }

    @WsMethod()
    public Map<String, Object> dsB2BsendEmailToSupport(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String subject = getStringParam(params, "subject");
        String text = getStringParam(params, "text");

        /*
        * Если сервер в дебаге, то почту берем из текстового конфига
        * иначе берем из БД, но если ее нет, то берем из конфига
        * */
        String emailFromDB = "";
        if (!isDebug) {
            emailFromDB = getCoreSettingBySysName(SUPPORT_EMAIL_ADDRESS_PARAMNAME, login, password);
        }

        if (emailFromDB.isEmpty()) {
            Config config = Config.getConfig(Constants.B2BPOSWS);
            emailFromDB = config.getParam(SUPPORT_EMAIL_ADDRESS_PARAMNAME, "");
        }

        if (emailFromDB.isEmpty()) {
            //ошибку ни кто не обрабатывает
            return B2BResult.error();
        }
        Map<String, Object> callParams = copyNodes(params, LOGIN + "," + PASSWORD);
        callParams.put("text", text);
        callParams.put("subject", subject);
        callParams.put("email", emailFromDB);
        return this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BsendEmail", callParams, login, password);
    }

    @WsMethod(requiredParams = {MESSAGE_ID_PARAM_NAME})
    public Map<String, Object> dsB2BSendFeedbackOnEmailKMSB(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Long userAccountId = getLongParam(params, B2B_USERACCOUNTID_PARAMNAME);
        Map<String, Object> userparams = new HashMap<>();
        //если запрос пришел из b2blk, то там будет передана исходная сессия Б2Б, пользователь на самом деле в ней
        //а все потому что вызываем из ПА, а там все сложнее, чем кажется
        String b2bSessionId = (String) params.get(B2B_LK_CROSSSESSION_PARAMNAME);
        if (b2bSessionId != null && !b2bSessionId.isEmpty()){
            Config config = Config.getConfig(B2BPOSWS);
            Long timeOut = 10L;
            if (config != null) {
                timeOut = Long.parseLong(config.getParam("maxSessionSize", "10"));
            }
            Map<String,Object> crossSessionParams = new B2BPosServiceSessionController(timeOut).checkSession(b2bSessionId);
            login = (String) crossSessionParams.get(B2BPosServiceSessionController.B2B_USERLOGIN_PARAMNAME);
            password = (String) crossSessionParams.get(B2BPosServiceSessionController.B2B_USERPASSWORD_PARAMNAME);
            userAccountId = Long.parseLong(crossSessionParams.get(B2B_USERACCOUNTID_PARAMNAME).toString());
        }

        //если у юзера роли как в фильтре RoleSupportFilter и это юзер перенаправлен из ПА2 по своей Б2Б-сессии, то почту отправлять не надо.
        if (userAccountId != null && b2bSessionId != null) {
            Map<String, Object> userRoleParams = new HashMap<>();
            userRoleParams.put("USERACCOUNTID", userAccountId);
            Map<String, Object> roleListRes = this.callService(B2BPOSWS, "dsAdminGetUserRoleList", userRoleParams, login, password);
            List<Map<String, Object>> roleList = (List<Map<String, Object>>) roleListRes.get(RESULT);
            if (roleList != null && roleList.size() > 0) {
                Long supportRoles = roleList.stream().filter(role -> new RoleSupportFilter().test(role)).count();
                //если найдены роли КМСБ
                if (supportRoles > 0L){
                    return B2BResult.ok();
                }
            }
        }

        String fullName = "";
        String error = "";
        String fromEmail = "";
        Long messageId = getLongParam(params, MESSAGE_ID_PARAM_NAME);
        Map<String, Object> messageEn = dctFindById(MESSAGE_ENTITY_NAME, messageId);
        String chatSubject = (String) getParamByRoute(messageEn, "chatId_EN.title", "");
        String messageText = (String) messageEn.get("note");
        userparams.put("username", login);
        userparams.put("passwordSha", password);
        Map<String, Object> userAccounts = this.selectQuery("dsCheckLogin", null, userparams);
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) userAccounts.get(RESULT);
        if (accounts != null && accounts.size() == 1) {
            Map<String, Object> account = accounts.get(0);
            fullName = account.getOrDefault("LASTNAME", "") + " ";
            fullName += account.getOrDefault("FIRSTNAME", "") + " ";
            fullName += account.getOrDefault("MIDDLENAME", "");
            fullName = fullName.replaceAll(" +", " "); // убиваем множественные пробелы
            fromEmail = (String) account.getOrDefault("EMAIL", "");
            if (fromEmail == null || fromEmail.isEmpty()) {
                error = "Не удалось найти адрес почтового ящика.";
            }
        } else {
            error = "Не удалось найти аккаунт для " + login;
            logger.error(error);
            return B2BResult.error(error);
        }

        String messageTemplatePath = "";
        if (error.isEmpty()) {
            messageTemplatePath = getCoreSettingBySysName(TEMPLATE_MESSAGE_PATH, login, password);
            if (messageTemplatePath == null || messageTemplatePath.isEmpty()) {
                error = "Шаблон сообщения отсутствует";
            }
        }

        String supportMail = "";
        if (!isDebug) {
            supportMail = getCoreSettingBySysName(SUPPORT_EMAIL_ADDRESS_PARAMNAME, login, password);
        }

        if (supportMail.isEmpty()) {
            Config config = Config.getConfig(Constants.B2BPOSWS);
            supportMail = config.getParam(SUPPORT_EMAIL_ADDRESS_PARAMNAME, "");
            if (supportMail.isEmpty()) {
                error = "Не удалось найти почту службы технической поддержки";
            }
        }

        Map<String, Object> sendParams = new HashMap<>(); //chatTopicTypeId
        if (error.isEmpty()) {
            String nowDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
            String smtpMessage = "Тема: " + chatSubject + "." + "\n" + "Сообщение: " + messageText + ".";
            smtpMessage = smtpMessage.replaceAll("\\.+", "\\."); // мало ли точки замножились.
            String chatType = (String) getParamByRoute(messageEn, "chatId_EN.chatTopicTypeId_EN.name", "");
            Object contractIdObj = getParamByRoute(messageEn, "chatId_EN.someParams сделай нормально тут", "");
            Long contractId = -1L;
            if (!contractIdObj.toString().isEmpty()){
                contractId = Long.parseLong(contractIdObj.toString());
            }
            String smtpSubject = "КМСБ1 " + fullName;
            smtpSubject += chatType.isEmpty() ? "" : ", " + chatType;
            smtpSubject += contractId.equals(-1L) ? "" : ", контракт " + contractId.toString();
            sendParams.put("SMTPSubject", smtpSubject);
            sendParams.put("SMTPReceipt", supportMail);
            sendParams.put("SMTPMESSAGE", smtpMessage);
            Map<String, Object> attachListQueryParams = new HashMap<>();
            attachListQueryParams.put("ID", messageId);
            List<Map<String, Object>> attachList = this.callServiceAndGetListFromResultMap(THIS_SERVICE_NAME, "dsMessageDocumentBrowseListByParam",
                    attachListQueryParams, login, password);
            Map<String, Object> convertedAttachMap = new HashMap<>();
            error = handlingAttachments(convertedAttachMap, attachList);
            if (error.isEmpty()) {
                sendParams.put("ATTACHMENTMAP", convertedAttachMap);
                String htmlTest = getHtmlText(messageTemplatePath, chatSubject, messageText, login, password);
                sendParams.put("HTMLTEXT", htmlTest);
            }
        }

        Map<String, Object> sendRes = new HashMap<>();
        if (error.isEmpty()) {
            try {
                boolean isError = false;
                saveEmailInFile(sendParams, login, password);
                for (int i = 0; i < 5; i++) {
                    if (sendRes == null || (sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    } else {
                        break;
                    }
                }

                if (sendRes == null || (sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    isError = true;
                    error = "Не удалось отправить сообщение";
                    logger.debug("mailSendFail");
                }

                if (!isError) {
                    logger.debug("mailSendSuccess");
                }

            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
                saveEmailInFile(sendParams, login, password);
            }
        }

        if (!error.isEmpty()) {
            if (sendRes == null) {
                sendRes = new HashMap<>();
            }
            sendRes.put(ERROR, error);
        }
        return B2BResult.ok(sendRes);
    }

    public static class RoleSupportFilter implements Predicate{
        @Override
        public boolean test(Object o) {
            Map<String, Object> role = (Map<String, Object>) o;
            String sysname = (String) role.get("ROLESYSNAME");
            return (sysname != null && (sysname.equalsIgnoreCase("TPKMSB") || sysname.equalsIgnoreCase("TPLK")));
        }
    }

}
