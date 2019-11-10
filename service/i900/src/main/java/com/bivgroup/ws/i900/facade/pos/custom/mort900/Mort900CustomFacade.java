package com.bivgroup.ws.i900.facade.pos.custom.mort900;

import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.seaweedfs.client.WeedFSFile;
import com.bivgroup.ws.i900.InvParserImpl;
import com.bivgroup.ws.i900.Mort900Exception;
import com.bivgroup.ws.i900.Mort900Parser;
import com.bivgroup.ws.i900.facade.Mort900BaseFacade;
import com.bivgroup.ws.i900.system.CommonPaymentPurposeProcessor;
import com.bivgroup.ws.i900.system.Constants;
import com.bivgroup.ws.i900.system.DatesParser;
import org.apache.cayenne.access.Transaction;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author averichevsm
 */
@BOName("Mort900Custom")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class Mort900CustomFacade extends Mort900BaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    public static final String SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из b2bposservice на сервере подписи
    private static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    // Имя сервиса для вызова методов из paservice
    private static final String PAWS_SERVICE_NAME = Constants.PAWS;

    // Поставлен в очередь загрузки
    private static final String B2B_BANKSTATE_INLOADQUEUE = "B2B_BANKSTATE_INLOADQUEUE";
    // Поставлен в очередь обработки
    private static final String B2B_BANKSTATE_INPROCESSQUEUE = "B2B_BANKSTATE_INPROCESSQUEUE";
    // Ошибка
    private static final String B2B_BANKSTATE_ERROR = "B2B_BANKSTATE_ERROR";
    // Новый
    private static final String B2B_BANKSTATE_NEW = "B2B_BANKSTATE_NEW";
    // Обработан
    private static final String B2B_BANKSTATE_PROCESSED = "B2B_BANKSTATE_PROCESSED";

    // Новый
    private static final String B2B_BANKCASHFLOW_NEW = "B2B_BANKCASHFLOW_NEW";
    // Поставлен в очередь
    private static final String B2B_BANKCASHFLOW_INQUEUE = "B2B_BANKCASHFLOW_INQUEUE";
    // Ошибка
    private static final String B2B_BANKCASHFLOW_ERROR = "B2B_BANKCASHFLOW_ERROR";
    // Нераспознан
    private static final String B2B_BANKCASHFLOW_NOTEMPLATE = "B2B_BANKCASHFLOW_NOTEMPLATE";
    // Обработан
    private static final String B2B_BANKCASHFLOW_PROCESSED = "B2B_BANKCASHFLOW_PROCESSED";
    // Исключен
    private static final String B2B_BANKCASHFLOW_EXCLUDE = "B2B_BANKCASHFLOW_EXCLUDE";

    // список статусов объектов, исключаемых из проверки дубликатов для объектов учета «Движение денежных средств по расчетному счету»
    private static final String BANKCASHFLOW_DUPLICATES_EXCLUDED_SYSNAMELIST = "'" + B2B_BANKCASHFLOW_NEW + "', '" + B2B_BANKCASHFLOW_EXCLUDE + "'";

    // список статусов объектов учета «Движение денежных средств по расчетному счету», используемых при проверке перед переводом объектов учета «Банковская выписка» в статус «Обработан»
    private static final String BANKCASHFLOW_FINALISATION_INCLUDED_SYSNAMELIST = "'" + B2B_BANKCASHFLOW_NEW + "', '" + B2B_BANKCASHFLOW_INQUEUE + "'";

    // список статусов объекта учета «Контракт» для последовательного перевода из статуса «Черновик» в статус «Подписан»
    private static final String[] CONTRACT_STATE_TRANSITION_PATH = {"B2B_CONTRACT_PREPRINTING", "B2B_CONTRACT_SG"};

    // шаблон текста СМС, отправляемого при автоматическом прикреплении создаваемых договоров в ЛК 
    // todo: действительный текст СМС (в ФТ от 16.02.2016 указан как "Текст SMS"; устно от 24.02.2016 - "Пока использовать в качестве SMS ссылку на ЛК: https://online.sberbankins.ru/lk/index.html")
    // todo: возможно, чтение шаблона из БД/конфига?
    private static final String CONTRACT_ATTACHMENT_SMS_TEMPLATE = "https://online.sberbankins.ru/lk/index.html";

    private static volatile int bankStatementsProcessingThreadCount = 0;
    private static volatile int cashFlowsPreparingThreadCount = 0;
    private static volatile int cashFlowsProcessingThreadCount = 0;
    private static volatile int bankStatementsFinalizeThreadCount = 0;

    // константы системных имен продуктов - копия из ProductContractCustomFacade
    private static final String SYSNAME_MORTGAGE900 = "001"; // Пролонгация ипотеки через SMS 900
    private static final String SYSNAME_HOUSE900 = "B2B_HIB_900"; // Защита дома 900
    private static final String SYSNAME_CARD900 = "B2B_CIB_900"; // Защита карты 900
    private static final String SYSNAME_MORTGAGETM = "B2B_MORTGAGE_TELEMARKETING"; // Пролонгация ипотеки через ТМ
    private static final String SYSNAME_HIBTM = "B2B_HIB_TM"; // Мобильная защита дома
    private static final String SYSNAME_CIBTM = "B2B_CIB_TM"; // Мобильная защита карты

    private static DatesParser datesParser;

    // флаг подробного протоколирования операций с датами
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    private final int MAX_BATCH_SIZE = 500;
    private static final Long B2B_BANKCASHFLOW_NEW_ID = 4014L;
    private static final Long B2B_BANKCASHFLOW_INQUEUE_ID = 4015L;
    private static final Long B2B_BANKCASHFLOW_PROCESSED_ID = 4017L;
    private static final Long B2B_BANKCASHFLOW_ERROR_ID = 4016L;
    private static final Long B2B_BANKCASHFLOW_TYPEID = 2250L;
    private static final String B2B_BANKCASHFLOW_TYPENAME = "B2B_BANKCASHFLOW";

    private static final Long MAXPROCESSRECORD = 3000L;

    public Mort900CustomFacade() {
        super();
        init();
    }

    private void init() {
        datesParser = new DatesParser();
        // протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
    }

    private String getUserFilePath() {
        String result = Config.getConfig("webclient").getParam("userFilePath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    private Map<String, Object> selectAndSetProgram(Map<String, Object> contract, Map<String, Object> product, String login, String password) {
        String inputProdProgSysName = getStringParamLogged(contract, "PRODPROGSYSNAME");
        Map<String, Object> selectedProdProg = getProdProgBySysName(product, inputProdProgSysName);
        if (selectedProdProg != null) {
            Long prodProgID = getLongParam(selectedProdProg, "PRODPROGID");
            setOverridedParam(contract, "PRODPROGID", prodProgID, logger.isDebugEnabled());
            contract.put("PRODPROG", selectedProdProg);
        }
        return selectedProdProg;
    }

    private Map<String, Object> getProdProgBySysName(Map<String, Object> product, String inputProdProgSysName) {
        Map<String, Object> selectedProdProg = null;
        if (!inputProdProgSysName.isEmpty()) {
            Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
            if (prodVer != null) {
                List<Map<String, Object>> prodProgList = (List<Map<String, Object>>) prodVer.get("PRODPROGS");
                if (prodProgList != null) {
                    for (Map<String, Object> prodProg : prodProgList) {
                        String prodProgSysName = getStringParamLogged(prodProg, "SYSNAME");
                        if (inputProdProgSysName.equalsIgnoreCase(prodProgSysName)) {
                            selectedProdProg = prodProg;
                            break;
                        }
                    }
                }
            }
        }
        return selectedProdProg;
    }

    private void calcInsAmValueByProgram(Map<String, Object> contract, Map<String, Object> prodProg, String login, String password) {
        logger.debug("Try to calculate insurance sum by coefficient from selected program...");
        String prodProgSysName = getStringParamLogged(prodProg, "SYSNAME");
        if (!prodProgSysName.isEmpty()) {
            String prodProgSysNamePrefix = "INSAMVALUE_DIV_";
            if (prodProgSysName.startsWith(prodProgSysNamePrefix)) {
                // согласно сиснейму программы выбрана программа, указывающая на метод расчета страховой суммы
                // в этом случае NAME и/или PROGCODE (и т.д.) будет содержать коэффициент для вычисления
                Double k = null;
                k = tryToGetDoubleFromStrField(prodProg, "NAME", "PROGCODE", "EXTERNALID");
                if (k == null) {
                    // не удалось получить коэффициент из поля PROGCODE программы - следует использовать константы
                    if ("INSAMVALUE_DIV_000250".equalsIgnoreCase(prodProgSysName)) {
                        k = 0.00250D;
                    } else if ("INSAMVALUE_DIV_000225".equalsIgnoreCase(prodProgSysName)) {
                        k = 0.00225D;
                    }
                    // todo: дополнить список, если будет введены программы без коэффициента в PROGCODE
                }
                Double premValue = getDoubleParam(contract, "PREMVALUE");
                Double insAmValue = roundSum(premValue / k);
                setOverridedParam(contract, "INSAMVALUE", insAmValue, logger.isDebugEnabled());
            }
        }
        logger.debug("Calculating insurance sum by coefficient from selected program finished.");
    }

    private Double tryToGetDoubleFromStrField(Map<String, Object> prodProg, String... kFieldNames) {
        Double k = null;
        for (String kFieldName : kFieldNames) {
            String rawValue = getStringParam(prodProg, kFieldName);
            String cleanValue = rawValue.replace("%", "").replace(",", ".");
            try {
                k = getDoubleParam(cleanValue);
            } catch (Exception ex) {
                logger.error(String.format("Unparsable (to double) value (%s) from selected program field with name '%s' caused exception: ", rawValue, kFieldName), ex);
            }
            if (k != null) {
                if (rawValue.contains("%")) {
                    // исходное поле содержало символ процента - значит значение было в процентах и для получения корректного значения коэффициента требуется привести проценты к долям
                    k = k / 100;
                }
                logger.debug(String.format("Value (%s) from selected program field with name '%s' parsed to double = %.5f.", rawValue, kFieldName, k));
                break;
            }
        }
        return k;
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

    @WsMethod(requiredParams = {"CASHFLOWLIST", "BANKSTATEMENTID"})
    public Map<String, Object> dsB2BMort900CashFlowMassCreate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("CASHFLOWLIST");
        List<Map<String, Object>> createdCashFlows = new ArrayList<Map<String, Object>>();
        Long bankStatementID = getLongParam(params.get("BANKSTATEMENTID"));
        Long templateId = getLongParam(params.get("BANKSTATETEMPLATEID"));

        /*        for (Map<String, Object> cashFlow : cashFlows) {
            Map<String, Object> createdCashFlow = createCashFlow(cashFlow, bankStatementID, templateId, login, password);
            Long bankCashFlowID = getLongParam(createdCashFlow.get("BANKCASHFLOWID"));
            if (bankCashFlowID == null) {
                throw new Mort900Exception(
                        "Неустановленная ошибка при создании записей о движении денежных средств",
                        "Bank cash flow record creating unknown error"
                );
            } else {
                logger.debug("Bank cash flow record created with ID (BANKCASHFLOWID) = " + bankCashFlowID);

                // todo: создание детализации назначения платежа для конкретного движения денежных средств
                String bankCashFlowPurpose = getStringParam(createdCashFlow.get("PURPOSE"));
                createBankPurposeDetails(bankCashFlowID, bankCashFlowPurpose, login, password);

                createdCashFlows.add(createdCashFlow);
            }
        }
         */
        long counter = 0;
        List<Map<String, Object>> batchCashFlows = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> cashFlow : cashFlows) {
            batchCashFlows.add(cashFlow);
            counter++;

            if (counter >= MAX_BATCH_SIZE) {
                Map<String, Object> cashFlowParams = new HashMap<String, Object>();
                cashFlowParams.put("ReturnAsHashMap", true);
                cashFlowParams.put("CASHFLOWLIST", batchCashFlows);
                cashFlowParams.put("BANKSTATEMENTID", bankStatementID);
                cashFlowParams.put("BANKSTATETEMPLATEID", templateId);
                Map<String, Object> cashFlowResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMort900CashFlowBatchCreate", cashFlowParams, login, password);
                if ((cashFlowResult != null) && (cashFlowResult.get("CASHFLOWLIST") != null)) {
                    List<Map<String, Object>> resultCashFlows = (List<Map<String, Object>>) cashFlowResult.get("CASHFLOWLIST");
                    createdCashFlows.addAll(resultCashFlows);
                }
                batchCashFlows.clear();
                counter = 0;
            }
        }
        // last items
        if (!batchCashFlows.isEmpty()) {
            Map<String, Object> cashFlowParams = new HashMap<String, Object>();
            cashFlowParams.put("ReturnAsHashMap", true);
            cashFlowParams.put("CASHFLOWLIST", batchCashFlows);
            cashFlowParams.put("BANKSTATEMENTID", bankStatementID);
            cashFlowParams.put("BANKSTATETEMPLATEID", templateId);
            Map<String, Object> cashFlowResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMort900CashFlowBatchCreate", cashFlowParams, login, password);
            if ((cashFlowResult != null) && (cashFlowResult.get("CASHFLOWLIST") != null)) {
                List<Map<String, Object>> resultCashFlows = (List<Map<String, Object>>) cashFlowResult.get("CASHFLOWLIST");
                createdCashFlows.addAll(resultCashFlows);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CASHFLOWLIST", createdCashFlows);
        return result;

    }

    private String getFullFilePathFromFileName(String fileNameOrPath) {
        String userUploadFilePath = getUserUploadFilePath();
        logger.debug("User's uploaded files path = " + userUploadFilePath);
        logger.debug("Full path or filename of bank statement file (FILEPATH) from DB = " + fileNameOrPath);
        int fileNameIndexA = fileNameOrPath.lastIndexOf("\\");
        int fileNameIndexB = fileNameOrPath.lastIndexOf("/");
        int fileNameIndex = Math.max(fileNameIndexA, fileNameIndexB);
        String fileNameOnly = fileNameOrPath.substring(fileNameIndex + 1);
        logger.debug("Filename only of bank statement file from DB = " + fileNameOnly);
        String fullFileNameWithPath = userUploadFilePath + fileNameOnly;
        logger.debug("Full path including filename of bank statement file = " + fullFileNameWithPath);
        return fullFileNameWithPath;
    }

    @WsMethod(requiredParams = {"BANKSTATEMENTID", "STATESYSNAME", "FILEPATH"})
    public Map<String, Object> dsB2BMort900BankStatementFileReadContentToMap(Map<String, Object> params) throws Exception {

        logger.debug("Start reading bank statement file content...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> parseResult = null;
        try {
            parseResult = doBankStatementFileReadContentToMap(params);
        } catch (Mort900Exception ex) {
            processBankStatementException(params, ex, login, password);
            parseResult = params;
        }

        logger.debug("Reading bank statement file content finished.");

        return parseResult;

    }

    private Map<String, Object> doBankStatementFileReadContentToMap(Map<String, Object> params) throws Mort900Exception {
        String fileNameOrPath = getStringParam(params.get("FILEPATH"));
        String fullFileNameWithPath = getFullFilePathFromFileName(fileNameOrPath);
        InputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(fullFileNameWithPath);
        } catch (FileNotFoundException ex) {
            throw new Mort900Exception(
                    "Файл банковской выписки не найден: " + ex.getLocalizedMessage(),
                    "Bank statement file not found: " + ex.getMessage(),
                    ex
            );
        }
//        Mort900Parser mort900Parser = new Mort900ParserImpl();
        Mort900Parser mort900Parser = new InvParserImpl();
        Map<String, Object> parseResult = mort900Parser.parse(fileInputStream);
        logger.debug("parseResult = " + parseResult);
        return parseResult;
    }

    private Map<String, Object> doBankStatementsProcessSingleRecord(Map<String, Object> bankStatement, String login, String password) throws Exception {

        //logger.debug("Start bank statement single record processing...");
        Map<String, Object> result = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> allCreatedCashFlows = new ArrayList<Map<String, Object>>();

        Long bankStatementID = getLongParam(bankStatement.get("BANKSTATEMENTID"));
        logger.debug("BANKSTATEMENTID = " + bankStatementID);
        Long bankStatementTemplateId = getLongParam(bankStatement.get("BANKSTATETEMPLATEID"));
        logger.debug("BANKSTATETEMPLATEID = " + bankStatementTemplateId);

        Map<String, Object> bankStatementDocumentsParams = new HashMap<String, Object>();
        bankStatementDocumentsParams.put("BANKSTATEMENTID", bankStatementID);

        List<Map<String, Object>> bankStatementDocuments;
        try {
            Map<String, Object> bankStatementDocsCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateDocumentBrowseListByParam", bankStatementDocumentsParams, login, password);
            bankStatementDocuments = WsUtils.getListFromResultMap(bankStatementDocsCallResult);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при получении списка файлов: " + ex.getLocalizedMessage(),
                    "Documents browse error: " + ex.getMessage(),
                    ex
            );
        }

        if (bankStatementDocuments.isEmpty()) {
            throw new Mort900Exception(
                    "Не приложено ни одного документа",
                    "No documents attached"
            );
        }

        for (Map<String, Object> bankStatementDocument : bankStatementDocuments) {

            Long bankStatementDocumentID = getLongParam(bankStatementDocument.get("BANKSTATEDOCID"));
            logger.debug("BANKSTATEDOCID = " + bankStatementDocumentID);

            Map<String, Object> binDocsParams = new HashMap<String, Object>();
            binDocsParams.put("OBJTABLENAME", "B2B_BANKSTATEDOC"); // параметр временно требуется из-за ошибки в аспекте
            binDocsParams.put("OBJID", bankStatementDocumentID);
            List<Map<String, Object>> binDocs;

            try {
                //binDocs = this.callServiceAndGetListFromResultMap(B2BPOSWS, "dsB2BBankStateDocument_BinaryFile_BinaryFileBrowseListByParam", binDocsParams, login, password);
                Map<String, Object> binDocsCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateDocument_BinaryFile_BinaryFileBrowseListByParam", binDocsParams, login, password);
                binDocs = WsUtils.getListFromResultMap(binDocsCallResult);
            } catch (Exception ex) {
                throw new Mort900Exception(
                        "Ошибка при получении списка файлов, приложенных к банковской выпискe: " + ex.getLocalizedMessage(),
                        "Bank statement files browse error: " + ex.getMessage(),
                        ex
                );
            }

            for (Map<String, Object> binDoc : binDocs) {
                String fileNameOrPath = "";
                if (binDoc.get("FSID") != null) {
                    String uploadPath = getUserFilePath();
                    String tempFileName = UUID.randomUUID() + "_" + binDoc.get("FILENAME").toString();
                    String tempFileFullName = uploadPath + tempFileName;
                    // сохраняем файл из внешней системы в темповый на диск
                    String masterUrlString = getSeaweedFSUrl();
                    URL masterURL = new URL(masterUrlString);
                    WeedFSFile file = new WeedFSFile(binDoc.get("FSID").toString());
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    List<Location> locations = client.lookup(file.getVolumeId());
                    if (locations.size() == 0) {
                        System.out.println("file not found");
                    } else {
                        InputStream fsFile = client.read(file, locations.get(0));
                        BufferedOutputStream bufferedOutput = null;
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(tempFileFullName);
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
                        File tmpFile = new File(tempFileFullName);
                        if (tmpFile.exists() && tmpFile.getCanonicalPath().startsWith(uploadPath)) {
                            fileNameOrPath = tempFileName;
                        }
                    }
                } else {
                    fileNameOrPath = getStringParam(binDoc.get("FILEPATH"));
                }
                if ((fileNameOrPath != null) && (!fileNameOrPath.isEmpty())) {
                    Map<String, Object> fileParams = new HashMap<String, Object>();
                    fileParams.put("BANKSTATEMENTID", bankStatementID);
                    fileParams.put("STATESYSNAME", getStringParam(bankStatement.get("STATESYSNAME")));
                    fileParams.put("FILEPATH", fileNameOrPath);
                    fileParams.put(RETURN_AS_HASH_MAP, true);

                    Map<String, Object> parseResult;
                    try {
                        parseResult = this.callService(THIS_FILE_SERVICE_NAME, "dsB2BMort900BankStatementFileReadContentToMap", fileParams, login, password);
                    } catch (Exception ex) {
                        Exception cause = (Exception) ex.getCause();
                        if (cause != null) {
                            throw cause;
                        } else {
                            throw ex;
                        }
                    }

                    // todo: обработать также "Error", поскольку если THIS_FILE_SERVICE_NAME не localhost, то исключения не будет
                    String errorStr = getStringParam(parseResult, "Error");
                    if (!errorStr.isEmpty()) {
                        throw new Mort900Exception(
                                "Ошибка при чтении или анализе содержимого файла, приложенного к банковской выпискe: " + errorStr,
                                "Bank statement file reading or parsing error: " + errorStr
                        );
                    }

                    String errorMsg = getStringParam(parseResult.get("ErrorMsg"));
                    if (!errorMsg.isEmpty()) {
                        return parseResult;
                    }

                    List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) ((Map<String, Object>) parseResult.get("documents")).get("paymentOrder");
                    Map<String, Object> createCashFlowsParams = new HashMap<String, Object>();
                    createCashFlowsParams.put("CASHFLOWLIST", cashFlows);
                    createCashFlowsParams.put("BANKSTATEMENTID", bankStatementID);
                    createCashFlowsParams.put("BANKSTATETEMPLATEID", bankStatementTemplateId);

                    ArrayList<Map<String, Object>> createdCashFlows = null;
                    try {
                        createdCashFlows = (ArrayList<Map<String, Object>>) this.callServiceAndGetOneValue(THIS_SERVICE_NAME, "dsB2BMort900CashFlowMassCreate", createCashFlowsParams, login, password, "CASHFLOWLIST");
                    } catch (Exception ex) {
                        throw new Mort900Exception(
                                "Ошибка при создании записей: " + ex.getLocalizedMessage(),
                                "Record creation error: " + ex.getMessage(),
                                ex
                        );
                    }

                    if ((createdCashFlows != null) && (!createdCashFlows.isEmpty())) {
                        allCreatedCashFlows.addAll(createdCashFlows);
                    } else {
                        throw new Mort900Exception(
                                "Неустановленная ошибка при создании записей о движении денежных средств - ни одной записи не создано",
                                "Bank cash flow record creation unknown error - no records created"
                        );
                    }

                    Map<String, Object> header = (Map<String, Object>) parseResult.get("header");
                    Map<String, Object> headerParams = new HashMap<String, Object>();
                    if (header != null) {
                        Date createTime = getDateParam(header.get("createTime"));
                        Date createDate = getDateParam(header.get("createDate"));
                        Date startDate = getDateParam(header.get("startDate"));
                        Date endDate = getDateParam(header.get("endDate"));
                        GregorianCalendar gc = new GregorianCalendar();
                        GregorianCalendar gcTime = new GregorianCalendar();
                        gc.setTime(createDate);
                        gcTime.setTime(createTime);

                        gc.set(Calendar.HOUR_OF_DAY, gcTime.get(Calendar.HOUR_OF_DAY));
                        gc.set(Calendar.MINUTE, gcTime.get(Calendar.MINUTE));
                        gc.set(Calendar.SECOND, gcTime.get(Calendar.SECOND));
                        gc.set(Calendar.MILLISECOND, gcTime.get(Calendar.MILLISECOND));

                        headerParams.put("BANKSTATEMENTID", bankStatementID);
                        headerParams.put("INPUTDATE", gc.getTime());
                        headerParams.put("INPUTBEGINDATE", startDate);
                        headerParams.put("INPUTFINISHDATE", endDate);
                        headerParams.put("DOCDATE", new Date());
                    } else {
                        headerParams.put("BANKSTATEMENTID", bankStatementID);
                        headerParams.put("INPUTDATE", new Date());
                        headerParams.put("INPUTBEGINDATE", new Date());
                        headerParams.put("INPUTFINISHDATE", new Date());
                        headerParams.put("DOCDATE", new Date());
                    }
                    try {
                        XMLUtil.convertDateToFloat(headerParams);
                        // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
                        this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BBankStateUpdate", headerParams, login, password);
                    } catch (Exception ex) {
                        throw new Mort900Exception(
                                "Ошибка при обновлении сведений банковской выписки: " + ex.getLocalizedMessage(),
                                "Bank statement updating error: " + ex.getMessage(),
                                ex
                        );
                    }
                }

            }

        }

        result.put("CASHFLOWLIST", allCreatedCashFlows);

        // todo: перевод статуса в успешно обработанный
        Map<String, Object> transResult;
        if (bankStatementTemplateId != null) {
            transResult = bankStatementMakeTrans(bankStatement, B2B_BANKSTATE_INPROCESSQUEUE, " ", login, password);
        } else {
            transResult = bankStatementMakeTrans(bankStatement, B2B_BANKSTATE_NEW, " ", login, password);
        }
        result.putAll(transResult);

        //logger.debug("Bank statement single record processing finished.");
        return result;
    }

    @WsMethod(requiredParams = {"BANKSTATEMENTID", "STATESYSNAME"})
    public Map<String, Object> dsB2BMort900BankStatementsProcessSingleRecord(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        logger.debug("Start bank statement single record processing...");

        try {
            result = doBankStatementsProcessSingleRecord(params, login, password);
        } catch (Exception ex) {
            result = processBankStatementException(params, ex, login, password);
        }

        logger.debug("Bank statement single record processing finished.");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900BankStatementsProcess(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        if (bankStatementsProcessingThreadCount == 0) {
            bankStatementsProcessingThreadCount = 1;
            try {
                logger.debug("");
                logger.debug("doBankStatementsProcess start");
                result = doBankStatementsProcess(params);
            } finally {
                bankStatementsProcessingThreadCount = 0;
                logger.debug("doBankStatementsProcess finish\n");
            }
        } else {
            logger.debug("doBankStatementsProcess already running");
        }
        return result;

    }

    @WsMethod(requiredParams = {"BANKSTATEMENTID", "STATESYSNAME", "TOSTATESYSNAME"})
    public Map<String, Object> dsB2BBankStatementMakeTrans(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String toStateSysName = getStringParam(params.get("TOSTATESYSNAME"));
        return bankStatementMakeTrans(params, toStateSysName, login, password);
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "STATESYSNAME", "ERRORTEXT"})
    public Map<String, Object> dsB2BBankCashFlowMakeTransToError(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String toStateSysName = B2B_BANKCASHFLOW_ERROR;
        String errorText = getStringParam(params.get("ERRORTEXT"));
        Map<String, Object> result = bankCashFlowMakeTrans(params, toStateSysName, errorText, login, password);
        return result;
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "STATESYSNAME"})
    public Map<String, Object> dsB2BBankCashFlowMakeTransToProcessed(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String toStateSysName = B2B_BANKCASHFLOW_PROCESSED;
        String errorText = " ";
        Map<String, Object> result = bankCashFlowMakeTrans(params, toStateSysName, errorText, login, password);
        return result;
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "STATESYSNAME", "TOSTATESYSNAME"})
    public Map<String, Object> dsB2BBankCashFlowMakeTrans(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String toStateSysName = getStringParam(params.get("TOSTATESYSNAME"));
        String errorText = getStringParam(params.get("ERRORTEXT"));
        Map<String, Object> result;
        if (errorText.isEmpty()) {
            result = bankCashFlowMakeTrans(params, toStateSysName, login, password);
        } else {
            result = bankCashFlowMakeTrans(params, toStateSysName, errorText, login, password);
        }
        return result;
    }

    private Map<String, Object> bankStatementUpdateErrorText(Map<String, Object> bankStatement, String errorText, String login, String password) throws Exception {
        String updateMethodName = "dsB2BBankStateUpdate";
        String idFieldName = "BANKSTATEMENTID";
        return updateRecordErrorText(bankStatement, updateMethodName, idFieldName, errorText, login, password);
    }

    // сохранение текста ошибки в запись БД
    private Map<String, Object> updateRecordErrorText(Map<String, Object> record, String updateMethodName, String idFieldName, String errorText, String login, String password) throws Exception {

        Long recordID = getLongParam(record, idFieldName);
        errorText = errorText.replace("При выполнении метода произошла ошибка: ", "");
        if (errorText.length() > 255) {
            errorText = errorText.substring(0, 250) + "...";
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Changing error text by using method '%s' for record with id (%s) = %d to '%s'...", updateMethodName, idFieldName, recordID, errorText));
        }

        Map<String, Object> updateParams = new HashMap<String, Object>();
        updateParams.put(idFieldName, recordID);
        updateParams.put("ERRORTEXT", errorText);
        updateParams.put(RETURN_AS_HASH_MAP, true);
        // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
        Map<String, Object> result = this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, updateMethodName, updateParams, login, password);

        logger.debug("Changing error text finished with result: " + result);

        return result;
    }

    private Map<String, Object> bankCashFlowUpdateContarctID(Map<String, Object> bankCashFlow, Map<String, Object> contract, String login, String password) throws Exception {
        Long bankCashFlowID = getLongParam(bankCashFlow, "BANKCASHFLOWID");
        Long contractID = getLongParam(contract, "CONTRID");
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Updating bank cash flow record with id (BANKCASHFLOWID) = '%d' with contract id (CONTRID) = '%d'...", bankCashFlowID, contractID));
        }
        Map<String, Object> updateParams = new HashMap<String, Object>();
        updateParams.put("BANKCASHFLOWID", bankCashFlowID);
        updateParams.put("CONTRID", contractID);
        updateParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = bankCashFlowUpdate(updateParams, login, password);
        logger.debug("Updating bank cash flow record finished with result: " + result);
        return result;
    }

    private Map<String, Object> bankCashFlowUpdate(Map<String, Object> updateParams, String login, String password) throws Exception {
        return this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowUpdate", updateParams, login, password);
    }

    private Map<String, Object> bankCashFlowMakeTrans(Map<String, Object> bankCashFlow, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = "BANKCASHFLOWID";
        String methodNamePrefix = "dsB2BBankCashFlow";
        String typeSysName = "B2B_BANKCASHFLOW";
        return recordMakeTrans(bankCashFlow, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
    }

    private Map<String, Object> bankCashFlowMakeTrans(Map<String, Object> cashFlow, String toStateSysName, String errorText, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> transResult = bankCashFlowMakeTrans(cashFlow, toStateSysName, login, password);
        result.putAll(transResult);
        if (errorText != null) {
            Map<String, Object> updateResult = cashFlowUpdateErrorText(cashFlow, errorText, login, password);
            result.putAll(updateResult);
        }
        return result;
    }

    private Map<String, Object> contractMakeTrans(Map<String, Object> contract, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = "CONTRID";
        String methodNamePrefix = "dsB2BContract";
        String typeSysName = "B2B_CONTRACT";
        return recordMakeTrans(contract, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
    }

    private Map<String, Object> cashFlowUpdateErrorText(Map<String, Object> bankStatement, String errorText, String login, String password) throws Exception {
        return updateRecordErrorText(bankStatement, "dsB2BBankCashFlowUpdate", "BANKCASHFLOWID", errorText, login, password);
    }

    private Map<String, Object> bankStatementMakeTrans(Map<String, Object> bankStatement, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = "BANKSTATEMENTID";
        String methodNamePrefix = "dsB2BBankState";
        String typeSysName = "B2B_BANKSTATE";
        return recordMakeTrans(bankStatement, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
    }

    private Map<String, Object> bankStatementMakeTrans(Map<String, Object> bankStatement, String toStateSysName, String errorText, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> transResult = bankStatementMakeTrans(bankStatement, toStateSysName, login, password);
        result.putAll(transResult);
        if (errorText != null) {
            Map<String, Object> updateResult = bankStatementUpdateErrorText(bankStatement, errorText, login, password);
            result.putAll(updateResult);
        }
        return result;
    }

    private Map<String, Object> doBankStatementsProcess(Map<String, Object> params) throws Exception {

        logger.debug("Bank statements browse...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> bankStatementsParams = new HashMap<String, Object>();
        bankStatementsParams.putAll(params);
        bankStatementsParams.put("STATESYSNAME", B2B_BANKSTATE_INLOADQUEUE);

        Map<String, Object> bankStatementsCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateBrowseListByParam", bankStatementsParams, login, password);
        List<Map<String, Object>> bankStatements = WsUtils.getListFromResultMap(bankStatementsCallResult);

        int totalCount = bankStatements.size();
        int current = 1;
        logger.debug(String.format("Found %d bank statements for processing.", totalCount));

        for (Map<String, Object> bankStatement : bankStatements) {

            logger.debug("");
            logger.debug(String.format("Preparing for processing %d bank statement (from total of %d found bank statements)...", current, totalCount));

            // todo: перевод статуса в обрабатываемый
            // текущей версией ФТ данный статус не предусматриватея 
            //try {
            //    bankStatementMakeTrans(bankStatement, ?, login, password);
            //} catch (Exception ex) {
            //    
            //}
            //Long bankStatementID = getLongParam(bankStatement.get("BANKSTATEMENTID"));
            //Long bankStatementID = getLongParam(bankStatement.get("BANKSTATEMENTID"));
            Map<String, Object> bankStatementParams = new HashMap<String, Object>();
            bankStatementParams.putAll(bankStatement);
            bankStatementParams.put(RETURN_AS_HASH_MAP, true);
            //bankStatementParams.put("BANKSTATEMENTID", bankStatementID);
            //bankStatementParams.put("STATESYSNAME", bankStatementID);

            Map<String, Object> bankStatementProcessResult;
            try {
                bankStatementProcessResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMort900BankStatementsProcessSingleRecord", bankStatementParams, login, password);
                bankStatement.putAll(bankStatementProcessResult);
            } catch (Exception ex) {
                processBankStatementException(bankStatement, ex, login, password);
            }

            current += 1;

        }

        result.put("BANKSTATEMENTLIST", bankStatements);
        logger.debug("Bank statements processing finished...");
        return result;
    }

    private Map<String, Object> processBankStatementException(Map<String, Object> bankStatement, Exception ex, String login, String password) throws Exception {
        bankStatement.put("ErrorMsg", ex);
        logger.error(ex);
        // todo: перевод статуса в обработанный с ошибкой
        String errorText;
        if (ex instanceof Mort900Exception) {
            errorText = ((Mort900Exception) ex).getRussianMessage();
        } else {
            Throwable cause = ex.getCause();
            if (cause instanceof Mort900Exception) {
                errorText = ((Mort900Exception) cause).getRussianMessage();
            } else {
                errorText = cause.getMessage();
            }
        }
        Map<String, Object> transResult = bankStatementMakeTrans(bankStatement, B2B_BANKSTATE_ERROR, errorText, login, password);
        bankStatement.putAll(transResult);
        return bankStatement;
    }

    private Map<String, Object> createCashFlow(Map<String, Object> cashFlow, Long bankStatementDocumentID, Long bankStatementTemplateId, String login, String password) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("BANKCASHFLOWFILEMAP", cashFlow);

        Map<String, Object> cashFlowParams = new HashMap<String, Object>();

        cashFlowParams.put("BANKSTATEMENTID", bankStatementDocumentID); //ИД банковской выписки
        if (bankStatementTemplateId != null) {
            cashFlowParams.put("BANKSTATETEMPLATEID", bankStatementTemplateId);
        }
        cashFlowParams.put("AMVALUE", cashFlow.get("sum")); //Сумма движения
        cashFlowParams.put("BFCVALUE", cashFlow.get("KBK")); //Показатель КБК
        cashFlowParams.put("CODE", cashFlow.get("code")); //Код
        cashFlowParams.put("INDEXBASE", cashFlow.get("purposeMark")); //Показатель основания
        cashFlowParams.put("INDEXDATE", cashFlow.get("dateMark")); //Показатель даты
        cashFlowParams.put("INDEXNUMBER", cashFlow.get("numberMark")); //Показатель номера
        cashFlowParams.put("INDEXPERIOD", cashFlow.get("periodMark")); //Показатель периода
        cashFlowParams.put("INDEXTYPE", cashFlow.get("typeMark")); //Показатель типа
        cashFlowParams.put("INPUTDATE", cashFlow.get("documentDate")); //Входящая дата
        cashFlowParams.put("INPUTNUMBER", cashFlow.get("number")); //Входящий номер
        cashFlowParams.put("OKATO", cashFlow.get("OKATO")); //ОКАТО
        cashFlowParams.put("ORIGINATORSTATE", cashFlow.get("creatorStatus")); //Статус составителя

        cashFlowParams.put("PAYMENTMETHOD", cashFlow.get("paymentKind")); //Вид оплаты
        cashFlowParams.put("PAYMENTTYPE", cashFlow.get("paymentType")); //Вид платежа
        cashFlowParams.put("PURPOSE", cashFlow.get("description")); //Назначение платежа

        //cashFlowParams.put("PRIORITY", cashFlow.get("priority")); //Очередность
        // 'Очередность' в БД на данный момент - строка
        cashFlowParams.put("PRIORITY", getStringParam(cashFlow.get("priority"))); //Очередность

        Map<String, Object> payer = (Map<String, Object>) cashFlow.get("payer");
        if (payer != null) {
            Map<String, Object> payerInfo = (Map<String, Object>) payer.get("contragentInfo");
            cashFlowParams.put("PAYDATE", payer.get("withdrawDate")); //Дата списания со счета плательщика
            cashFlowParams.put("PAYERACCOUNT", payer.get("bankAccount")); //Счет плательщика
            cashFlowParams.put("PAYER", payerInfo.get("name")); //Наименование плательщика
            cashFlowParams.put("PAYERBANK", payerInfo.get("bankName")); //Наименование банка плательщика
            cashFlowParams.put("PAYERBIK", payerInfo.get("bankBIK")); //БИК банка плательщика
            cashFlowParams.put("PAYERCORACCOUNT", payerInfo.get("bankCorrespondAccount")); //Корр счет банка плательщика
            cashFlowParams.put("PAYERINN", payerInfo.get("INN")); //ИНН плательщика
            cashFlowParams.put("PAYERKPP", payerInfo.get("KPP")); //КПП плательщика
            cashFlowParams.put("PAYERRSACCOUNT", payerInfo.get("account")); //Расчетный счет плательщика
        }
        Map<String, Object> receiver = (Map<String, Object>) cashFlow.get("receiver");
        if (receiver != null) {
            Map<String, Object> receiverInfo = (Map<String, Object>) receiver.get("contragentInfo");
            cashFlowParams.put("RECEIPTDATE", receiver.get("withdrawDate")); //Дата поступления на счет получателя
            cashFlowParams.put("RECIPIENTACCOUNT", receiver.get("bankAccount")); //Счет получателя
            cashFlowParams.put("RECIPIENT", receiverInfo.get("name")); //Наименование получателя
            cashFlowParams.put("RECIPIENTBANK", receiverInfo.get("bankName")); //Наименование банка получателя
            cashFlowParams.put("RECIPIENTBIK", receiverInfo.get("bankBIK")); //БИК банка получателя
            cashFlowParams.put("RECIPIENTCORACCOUNT", receiverInfo.get("bankCorrespondAccount")); //Корр счет банка получателя
            cashFlowParams.put("RECIPIENTINN", receiverInfo.get("INN")); //ИНН получателя
            cashFlowParams.put("RECIPIENTKPP", receiverInfo.get("KPP")); //КПП получателя
            cashFlowParams.put("RECIPIENTRSACCOUNT", receiverInfo.get("account")); //Расчетный счет получателя
        }

        //cashFlowParams.put("BANKCASHFLOWID", cashFlow.get("")); //ИД движения средств по расчетному счету
        //cashFlowParams.put("BANKSTATETEMPLATEID", cashFlow.get("")); //ИД шаблона обработки банковской выписки
        //cashFlowParams.put("CREATEDATE", cashFlow.get("")); //Дата создания потока
        //cashFlowParams.put("CREATEUSERID", cashFlow.get("")); //ИД создавшего пользователя
        //cashFlowParams.put("ERRORTEXT", cashFlow.get("")); //Текст ошибки
        //cashFlowParams.put("STATEID", cashFlow.get("")); //Статус
        //cashFlowParams.put("TEMPLATE", cashFlow.get("")); //Шаблон
        //cashFlowParams.put("TYPE", cashFlow.get("")); //Тип движения поступление или списание
        //cashFlowParams.put("UPDATEDATE", cashFlow.get("")); //Дата изменения
        //cashFlowParams.put("UPDATEUSERID", cashFlow.get("")); //ИД изменившего пользователя
        result.putAll(cashFlowParams);

        Long bankCashFlowID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BBankCashFlowCreate", cashFlowParams, login, password, "BANKCASHFLOWID"));

        result.put("BANKCASHFLOWID", bankCashFlowID);

        return result;
    }

    //private String getFullFilePathFromBinDocInfo(Map<String, Object> binDoc, String uploadDirPath) {
    //    String filePath = getStringParam(binDoc.get("FILEPATH"));
    //    logger.debug("Full path or filename of bank statement file (FILEPATH) from DB = " + filePath);
    //    int fileNameIndexA = filePath.lastIndexOf("\\");
    //    int fileNameIndexB = filePath.lastIndexOf("/");
    //    int fileNameIndex = Math.max(fileNameIndexA, fileNameIndexB);
    //    String fileName = filePath.substring(fileNameIndex + 1);
    //    logger.debug("Filename only of bank statement file from DB = " + fileName);
    //    filePath = uploadDirPath + fileName;
    //    logger.debug("Full path including filename of bank statement file = " + filePath);
    //    return filePath;
    //}
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900BrowseListByParamCustomeWhereEx(Map<String, Object> params) throws Exception {
        logger.debug("Start bank statement browse...");
//        String idFieldName = "BANKSTATEMENTID";
        Map<String, Object> result = this.selectQuery("dsB2BMort900BrowseListByParamCustomeWhereEx", params);

        logger.debug("Bank statement browse finish.\n");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankStateCreateEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(params, Double.class);

        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateCreate", params, login, password);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankStateDocumentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BBankStateDocumentBrowseListByParamEx", params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(result, String.class);

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankCashFlowBrowseListByParamEx(Map<String, Object> params) throws Exception {

        Map<String, Object> result = this.selectQuery("dsB2BBankCashFlowBrowseListByParamEx", params);
        // убрано потому как делается ограничение (UPPER(T.BANKSTATEMENTID) = UPPER('87012')) 
        // которое не попадает ни в один индекс
        /*
        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(params, Double.class);

        String idFieldName = "BANKCASHFLOWID";
        String customWhereQueryName = "dsB2BBankCashFlowBrowseListByParamEx";
        Map<String, Object> result = doCustomWhereQuery(customWhereQueryName, idFieldName, params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(result, String.class);
         */
        return result;
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "BANKSTATETEMPLATEID"})
    public Map<String, Object> dsB2BpcfPurposeDetailBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BpcfPurposeDetailBrowseListByParamEx", "dsB2BpcfPurposeDetailBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900BankCashFlowsPrepare(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        if (cashFlowsPreparingThreadCount == 0) {
            cashFlowsPreparingThreadCount = 1;
            try {
                logger.debug("doCashFlowsPrepare start");
                result = doCashFlowsPrepare(params);
            } finally {
                cashFlowsPreparingThreadCount = 0;
                logger.debug("doCashFlowsPrepare finish\n");
            }
        } else {
            logger.debug("doCashFlowsPrepare already running");
        }
        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankCashFlowBrowseListByParamExForProcessing(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsB2BBankCashFlowBrowseListByParamExForProcessing", params);
    }

    private Map<String, Object> doCashFlowsPrepare(Map<String, Object> params) throws Exception {

        logger.debug("");
        logger.debug("Start cash flows preparing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> cashFlowsParams = new HashMap<String, Object>();
        cashFlowsParams.putAll(params);
        cashFlowsParams.put("BANKSTATEMENTSTATESYSNAME", B2B_BANKSTATE_INPROCESSQUEUE);
        cashFlowsParams.put("STATESYSNAME", B2B_BANKCASHFLOW_NEW);

        //List<Map<String, Object>> cashFlows = WsUtils.getListFromResultMap(this.callService(B2BPOSWS, "dsB2BBankCashFlowBrowseListByParamExForProcessing", cashFlowsParams, login, password));
        Map<String, Object> cashFlowsCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowBrowseListByParamExForProcessing", cashFlowsParams, login, password);
        List<Map<String, Object>> cashFlows = WsUtils.getListFromResultMap(cashFlowsCallResult);

        int totalCount = cashFlows.size();
        int current = 1;
        logger.debug(String.format("Found %d cash flows for preparing.", totalCount));

        for (Map<String, Object> cashFlow : cashFlows) {

            logger.debug("");
            logger.debug(String.format("Preparing %d cash flow (from total of %d found cash flows)...", current, totalCount));

            Map<String, Object> prepareParams = new HashMap<String, Object>();
            prepareParams.putAll(cashFlow);
            prepareParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> prepareResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMort900BankCashFlowsPrepareSingleRecord", prepareParams, login, password);
            cashFlow.putAll(prepareResult);

            current += 1;

        }

        result.put("BANKCASHFLOWLIST", cashFlows);
        logger.debug("Cash flows processing finished.");
        logger.debug("");
        return result;

    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "STATESYSNAME"})
    public Map<String, Object> dsB2BMort900BankCashFlowsPrepareSingleRecord(Map<String, Object> params) throws Exception {

        logger.debug("Start bank cash flow single record preparing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result;
        /*
        // Выполняется проверка уникальности (дублей) объектов согласно параметрам условия:
        Map<String, Object> duplicatesParams = new HashMap<String, Object>();
        // * р/с получателя
        duplicatesParams.put("RECIPIENTRSACCOUNT", params.get("RECIPIENTRSACCOUNT"));
        // Статус отличен от «Новый» или «Исключен»
        duplicatesParams.put("EXCLUDEDSYSNAMELIST", BANKCASHFLOW_DUPLICATES_EXCLUDED_SYSNAMELIST);
        // * Входящая дата
        duplicatesParams.put("INPUTDATE", params.get("INPUTDATE"));
        // * Входящий номер
        duplicatesParams.put("INPUTNUMBER", params.get("INPUTNUMBER"));
        // * р/с плательщика (Если «ИНН плательщика» или «Р/с плательщика» пустые – параметры исключаются из условия поиска)
        String payerRSAccount = getStringParam(params.get("PAYERRSACCOUNT"));
        if (!payerRSAccount.isEmpty()) {
            duplicatesParams.put("PAYERRSACCOUNT", payerRSAccount);
        }
        // * ИНН плательщика (Если «ИНН плательщика» или «Р/с плательщика» пустые – параметры исключаются из условия поиска)
        String payerINN = getStringParam(params.get("PAYERINN"));
        if (!payerINN.isEmpty()) {
            duplicatesParams.put("PAYERINN", payerINN);
        }

        duplicatesParams.put("EXCLUDEDBANKCASHFLOWID", getLongParam(params.get("BANKCASHFLOWID")));
        Map<String, Object> duplicates = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowBrowseListByParamExForProcessing", duplicatesParams, login, password);
        //logger.debug(duplicates);
        Long duplicatesCount = getLongParam(duplicates.get(TOTALCOUNT));
        logger.debug("Duplicates count for current bank cash flow record: " + duplicatesCount);
         */
        Long duplicatesCount = 0L;
        if (duplicatesCount == 0L) {
            // дубликатов нет - выбор шаблона и смена статуса на «Поставлен в очередь»

            // выбор шаблона
            Long chosenTemplateID = bankCashFlowTemplateSelect(params, login, password);

            if (chosenTemplateID == null) {
                // если шаблон не выбран - смена статуса на «Ошибка», заполнение описания ошибки
                // по требованию от 25,11,2015 добавлено состояние "Нераспознан" в который надо переводить при остутсвии шаблона
                result = bankCashFlowMakeTrans(params, B2B_BANKCASHFLOW_NOTEMPLATE, "Не удалось определить шаблон для текущей записи о движении денежных средств", login, password);
                //result = bankCashFlowMakeTrans(params, B2B_BANKCASHFLOW_ERROR, "Не удалось определить шаблон для текущей записи о движении денежных средств", login, password);
            } else {
                // смена статуса на «Поставлен в очередь»
                result = bankCashFlowMakeTrans(params, B2B_BANKCASHFLOW_INQUEUE, " ", login, password);
            }

        } else {
            // есть дубликаты - смена статуса на «Ошибка», заполнение описания ошибки
            result = bankCashFlowMakeTrans(params, B2B_BANKCASHFLOW_ERROR, "Проверка уникальности выявила наличие дубликатов для текущей записи о движении денежных средств", login, password);
        }

        logger.debug("Bank cash flow single record preparing finished.");

        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900BankCashFlowsProcess(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        if (cashFlowsProcessingThreadCount == 0) {
            cashFlowsProcessingThreadCount = 1;
            try {
                logger.debug("doCashFlowsProcess start");
                result = doCashFlowsProcess(params);
            } finally {
                cashFlowsProcessingThreadCount = 0;
                logger.debug("doCashFlowsProcess finish\n");
            }
        } else {
            logger.debug("doCashFlowsProcess already running");
        }
        return result;

    }

    private Map<String, Object> doCashFlowsProcess(Map<String, Object> params) throws Exception {
        logger.debug("");
        logger.debug("Start cash flows processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // Поиск bankstatementid для загрузки одного типа записей
        Map<String, Object> cashFlowsParams = new HashMap<String, Object>();
        cashFlowsParams.putAll(params);
        // основное условие выборки для обработки - запись о движении средств должна быть в статусе "Поставлен в очередь обработки"
        cashFlowsParams.put("STATESYSNAME", B2B_BANKCASHFLOW_INQUEUE);
        // дополнительное условие выборки для обработки - договор еще не должен быть создан
        // (записи о движении средств в статусе "Поставлен в очередь обработки" и с успешно созданным договором обрабатываются другим регламентным заданием - dsPAAttachableContractsProcess)
        cashFlowsParams.put("CONTRIDISNULL", true);
        // ограничение по количеству выбираемых записей
        cashFlowsParams.put("MAXPROCESSRECORD", 1L);
        cashFlowsParams.put(RETURN_AS_HASH_MAP, true);

        Map<String, Object> cashFlowsCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowBrowseListByParamExForProcessing", cashFlowsParams, login, password);
        if (cashFlowsCallResult.get("BANKSTATEMENTID") == null) {
            return result;
        }

        Long bsId = getLongParam(cashFlowsCallResult.get("BANKSTATEMENTID"));

        cashFlowsParams.clear();
        cashFlowsParams.putAll(params);
        // основное условие выборки для обработки - запись о движении средств должна быть в статусе "Поставлен в очередь обработки"
        cashFlowsParams.put("STATESYSNAME", B2B_BANKCASHFLOW_INQUEUE);
        // дополнительное условие выборки для обработки - договор еще не должен быть создан
        // (записи о движении средств в статусе "Поставлен в очередь обработки" и с успешно созданным договором обрабатываются другим регламентным заданием - dsPAAttachableContractsProcess)
        cashFlowsParams.put("CONTRIDISNULL", true);
        // ограничение по количеству выбираемых записей
        cashFlowsParams.put("MAXPROCESSRECORD", MAXPROCESSRECORD);
        cashFlowsParams.put("BANKSTATEMENTID", bsId);

        cashFlowsCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowBrowseListByParamExForProcessing", cashFlowsParams, login, password);
        List<Map<String, Object>> cashFlows = WsUtils.getListFromResultMap(cashFlowsCallResult);

        try {
            Map<String, Object> processParams = new HashMap<String, Object>();
            processParams.put("BANKCASHFLOWLIST", cashFlows);
            processParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> processResult = this.callService(THIS_SERVICE_NAME, "dsB2BMort900BankCashFlowsProcessSingleRecord3", processParams, login, password);
        } catch (Exception ex) {
            String exLocalizedMessage = ex.getLocalizedMessage();
            logger.warn("Catched exception: " + exLocalizedMessage);
            logger.error("Catched exception call stack: ", ex); // !только для отладки!
            logger.error("Catched exception cause call stack: ", ex.getCause()); // !только для отладки!
        }
        /*        
        int totalCount = cashFlows.size();
        int current = 1;
        logger.debug(String.format("Found %d cash flows for processing.", totalCount));

        for (Map<String, Object> cashFlow : cashFlows) {

            logger.debug("");
            logger.debug(String.format("Preparing for processing %d cash flow (from total of %d found cash flows)...", current, totalCount));

            Map<String, Object> processParams = new HashMap<String, Object>();
            processParams.putAll(cashFlow);
            processParams.put(RETURN_AS_HASH_MAP, true);
            try {
                Map<String, Object> processResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMort900BankCashFlowsProcessSingleRecord2", processParams, login, password);
                cashFlow.putAll(processResult);
            } catch (Exception ex) {
                String exLocalizedMessage = ex.getLocalizedMessage();
                logger.warn("Catched exception: " + exLocalizedMessage);
                logger.error("Catched exception call stack: ", ex); // !только для отладки!
                logger.error("Catched exception cause call stack: ", ex.getCause()); // !только для отладки!
                cashFlow.put("EXCEPTION", exLocalizedMessage);

                // получение текста ошибки для сохранения в БД
                String errorMessage = getRussianMessageFromException(ex);
                logger.debug("Catched exception info for saving in DB: " + errorMessage);
                cashFlow.put("EXCEPTIONTEXT", errorMessage);

                // перевод статуса в обработанный с ошибкой
                Map<String, Object> transResult = tryTransBankCashFlowToErrorByExternalCall(params, errorMessage, login, password);
                cashFlow.putAll(transResult);
            }

            current += 1;

        }
         */
        result.put("BANKCASHFLOWLIST", cashFlows);
        logger.debug("Cash flows processing finished.");
        logger.debug("");
        return result;
    }

    private Map<String, Object> tryTransBankCashFlowToErrorByExternalCall(Map<String, Object> params, String errorMessage, String login, String password) {
        logger.error("Trying to change bank cash flow record status and update record with error info...");
        Map<String, Object> transParams = new HashMap<String, Object>();
        transParams.putAll(params);
        transParams.put("ERRORTEXT", errorMessage);
        transParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> transResult;
        String transError;
        try {
            // используется callExternalService для выполнения обновления записи о движении денежных средств в отдельной транзакции
            transResult = this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsB2BBankCashFlowMakeTransToError", transParams, login, password);
            transError = getStringParam(transResult, "Error");
            if (!transError.isEmpty()) {
                logger.error("Error while saving processing error info to DB:\n" + transError);
            } else {
                logger.error("Changed bank cash flow record status and update record with error info successfully with result: " + transResult);
            }
        } catch (Exception transEx) {
            transError = "Unknown exception while saving processing error info to DB";
            logger.error(transError, transEx);
            transResult = new HashMap<String, Object>();
            transResult.put("Error", transError + ": " + transEx.getLocalizedMessage());
        }
        return transResult;
    }

    // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
    private void updateSessionParamsIfNullByCallingUserCreds(Map<String, Object> contract, String login, String password) throws Exception {
        if ((contract.get(Constants.SESSIONPARAM_USERACCOUNTID) == null) && (contract.get(Constants.SESSIONPARAM_DEPARTMENTID) == null)) {
            Map<String, Object> checkLoginParams = new HashMap<String, Object>();
            checkLoginParams.put("username", XMLUtil.getUserName(login));
            checkLoginParams.put("passwordSha", password);
            Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsB2BMort900CheckLogin", checkLoginParams));
            if (checkLoginResult != null) {
                contract.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
                contract.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
            }
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamExForTM(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamExForTM", params);
        return result;
    }

    // проверка на наличие существующего договора (если будет найден - вернет мапу этого договора полностью)
    // если договор не будет найден - платеж из выписки первый и нужно создать новый договор
    // если договор будет найден - платеж из выписки очередной и договор уже сущестует, но его требуется обновить
    private Map<String, Object> checkForExistedContract(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        logger.debug("Checking for exisiting contracts...");

        // получение из БД списка подходящих договоров и их проверка по условиям
        List<Map<String, Object>> existedContractsAfterChecksList = getExistedAndCheckedContractList(contract, login, password);

        // анализ полученного после выборки и отбраковки по всем условиям списка и выбор конкретного договора для последующего обновления
        // кроме того, дополнительно загружаются подробные сведения о выбранном договоре (чтобы можно было передать их в метод по универсальному сохранению договора)
        Map<String, Object> existedContractFullMap = choseAndLoadSingleExistedContract(existedContractsAfterChecksList, login, password);

        if (existedContractFullMap != null) {
            // «Сумма» из движения средств (она же сумма платежа, она жа сумма взноса при поэтапной оплате договора)
            existedContractFullMap.put("BANKCASHFLOWAMVALUE", contract.get("BANKCASHFLOWAMVALUE"));
            // Входящая дата движения средств
            existedContractFullMap.put("BANKCASHFLOWINPUTDATE", contract.get("BANKCASHFLOWINPUTDATE"));
        }

        return existedContractFullMap;
    }

    // проверка на наличие существующего договора (если будет найден - вернет мапу этого договора полностью)
    // при определении даты начала учитывает наденный договор до исключения полностью оплаченных (требуется согласно ФТ из задачи #4495)
    private Map<String, Object> checkForExistedContractWithoutPayedExcluding(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        logger.debug("Checking for exisiting contracts without excluding fully payed contracts...");

        // получение из БД списка подходящих договоров
        List<Map<String, Object>> existedContractsList = getExistedContractList(contract, login, password);

        // Входящая дата из движения средств
        Date bankCashFlowInputDate = getDateParamLogged(contract, "BANKCASHFLOWINPUTDATE");

        // системное имя продукта
        String productSysName = getStringParam(contract, "PRODSYSNAME");
        if (productSysName.isEmpty()) {
            throw new Mort900Exception(
                    "Не найдены сведения о продукте - нет возможности определить наличие существующих договоров",
                    "No product info was found - existed contracts can not be determined"
            );
        }

        if (productSysName.equals(SYSNAME_HIBTM)) {
            // #4495 - "Среди найденных договоров определяется последний по «Дата начала действия»"
            existedContractsList = getLastContractListByStartDateFromList(existedContractsList, login, password);
            // при определении даты начала требуется учитывать найденный договор до исключения полностью оплаченных (требуется согласно ФТ из задачи #4495)
            if ((existedContractsList != null) && (existedContractsList.size() == 1)) {
                logger.debug("Selecting start date for new contract by found old contract finish date...");
                // #4495 - "если договор был найден:"
                Map<String, Object> existedContract = existedContractsList.get(0);
                Object contractFinishDateObj = existedContract.get("FINISHDATE");
                if (contractFinishDateObj == null) {
                    throw new Mort900Exception(
                            "Не удалось определить дату окончания действия найденного договора",
                            "Unable to get finish date from found contract data"
                    );
                } else {
                    // "«Дата окончания действия» найденного договора"
                    Date contractFinishDate = (Date) datesParser.parseAnyDate(contractFinishDateObj, Date.class, "FINISHDATE", IS_VERBOSE_LOGGING);
                    // #4495 - "Дата начала договора определяется как ..."
                    GregorianCalendar contractStartDateGC = new GregorianCalendar();
                    if (contractFinishDate.after(bankCashFlowInputDate)) {
                        // #4495 - "... «Дата окончания действия» найденного договора + 1 день, если «Дата окончания действия» найденного договора больше «Входящая дата» из движения средств"
                        contractStartDateGC.setTime(contractFinishDate);
                        contractStartDateGC.add(Calendar.DAY_OF_YEAR, 1);
                    } else {
                        // #4495 - "...«Входящая дата» из движения средств + 1 день, если «Дата окончания действия» найденного договора меньше или равна «Входящая дата» из движения средств"
                        contractStartDateGC.setTime(bankCashFlowInputDate);
                        contractStartDateGC.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    Date contractStartDate = contractStartDateGC.getTime();
                    //contract.put("STARTDATE", contractStartDate);
                    setOverridedParam(contract, "STARTDATE", contractStartDate, IS_VERBOSE_LOGGING);
                    logger.debug("Selected start date for new contract by found old contract finish date: " + contractStartDate);
                }
            }

        }

        // получение из списка подходящих договоров второго списка - с учетом проверки по условиям
        List<Map<String, Object>> existedContractsAfterChecksList = getCheckedContractList(existedContractsList, bankCashFlowInputDate, login, password);

        // анализ полученного после выборки и отбраковки по всем условиям списка и выбор конкретного договора для последующего обновления
        // кроме того, дополнительно загружаются подробные сведения о выбранном договоре (чтобы можно было передать их в метод по универсальному сохранению договора)
        Map<String, Object> existedContractFullMap = choseAndLoadSingleExistedContract(existedContractsAfterChecksList, login, password);

        if (existedContractFullMap != null) {
            // «Сумма» из движения средств (она же сумма платежа, она жа сумма взноса при поэтапной оплате договора)
            existedContractFullMap.put("BANKCASHFLOWAMVALUE", contract.get("BANKCASHFLOWAMVALUE"));
            // Входящая дата движения средств
            existedContractFullMap.put("BANKCASHFLOWINPUTDATE", contract.get("BANKCASHFLOWINPUTDATE"));
        }

        return existedContractFullMap;
    }

    // анализ полученного после выборки и отбраковки по всем условиям списка и выбор конкретного договора для последующего обновления
    // кроме того, дополнительно загружаются подробные сведения о выбранном договоре (чтобы можно было передать их в метод по универсальному сохранению договора)
    private Map<String, Object> choseAndLoadSingleExistedContract(List<Map<String, Object>> existedContractsAfterChecksList, String login, String password) throws Mort900Exception {
        Map<String, Object> existedContractFullMap = null;
        // в текущей версии ФТ не описана ситуация, когда по всем условиям поиска в итоге найдено более одного договора - поэтому пока что договор выбирается только если найден единственный, иначе - ошибка
        // todo: поменять, если от клиента поступять уточнения
        Long existedContractID = null;
        Map<String, Object> existedContractBriefInfo = null;
        if (!existedContractsAfterChecksList.isEmpty()) {
            if (existedContractsAfterChecksList.size() == 1) {
                logger.debug("Exisiting contract was found - will be updated accordingly.");
                existedContractBriefInfo = existedContractsAfterChecksList.get(0);
                existedContractID = getLongParamLogged(existedContractBriefInfo, "CONTRID");
                if (existedContractID == null) {
                    throw new Mort900Exception(
                            "В ходе определения очередности текущего платежа у найденного договора, подходящего по условиям, не удалось определить идентификатор",
                            "During order of current payment determination was found one existed contract, but with no id"
                    );
                }
            } else {
                //logger.error("Found multiple exisiting contracts: ");
                for (Map<String, Object> existedContractRepeated : existedContractsAfterChecksList) {
                    logger.error("Found multiple exisiting contracts, including this one: " + existedContractRepeated);
                }
                // todo: возможно, дополнительно указать номера найденных договоров?
                throw new Mort900Exception(
                        "В ходе определения очередности текущего платежа найдено более одного договора, подходящего по условиям",
                        "During order of current payment determination more then one contracts was found"
                );
            }
        } else {
            logger.debug("No exisiting contracts was found - new one will be created.");
        }
        if (existedContractID != null) {

            // получение полных сведений найденного договора
            Map<String, Object> existedContractParams = new HashMap<String, Object>();
            existedContractParams.put("CONTRID", existedContractID);
            existedContractParams.put("LOADCONTRSECTION", 1L);
            existedContractParams.put(RETURN_AS_HASH_MAP, true);

            try {
                existedContractFullMap = this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", existedContractParams, login, password);
            } catch (Exception ex) {
                throw new Mort900Exception(
                        "Ошибка при получении полных данных существующего договора - " + ex.getLocalizedMessage(),
                        "Error during getting full data of existed contract - " + ex.getMessage(),
                        ex
                );
            }

            if ((existedContractFullMap == null) || (!isCallResultOK(existedContractFullMap))) {
                // todo: возможно, включать в исключение сведения об ошибке, если они доступны в existedContractFullMap.Error
                throw new Mort900Exception(
                        "Ошибка при получении полных данных существующего договора",
                        "Error during getting full data of existed contract"
                );
            }

            // получение плана платежей для полной мапы договора
            ArrayList<Map<String, Object>> existedContractPayPlanList = (ArrayList<Map<String, Object>>) existedContractBriefInfo.get(PAYMENTPLAN_KEYNAME);
            if (existedContractPayPlanList == null) {
                existedContractPayPlanList = getContractPaymentsPlanSortedList(existedContractFullMap, login, password);
            }
            logger.debug(PAYMENTPLAN_KEYNAME + ": " + existedContractPayPlanList);
            existedContractFullMap.put(PAYMENTPLAN_KEYNAME, existedContractPayPlanList);
            // получение факта платежей для полной мапы договора
            ArrayList<Map<String, Object>> existedContractPayFactList = (ArrayList<Map<String, Object>>) existedContractBriefInfo.get(PAYMENTFACT_KEYNAME);
            if (existedContractPayFactList == null) {
                existedContractPayFactList = getContractPaymentsFactSortedList(existedContractFullMap, login, password);
            }
            logger.debug(PAYMENTFACT_KEYNAME + ": " + existedContractPayFactList);
            existedContractFullMap.put(PAYMENTFACT_KEYNAME, existedContractPayFactList);

        }
        logger.debug("Checking for exisiting contracts finished.");
        return existedContractFullMap;
    }

    // получение из БД списка подходящих договоров
    private List<Map<String, Object>> getExistedContractList(Map<String, Object> contract, String login, String password) throws Mort900Exception {
        // Входящая дата из движения средств - Double, для query в getExistedContractsByParamsForTM
        Double bankCashFlowInputDateDouble = (Double) datesParser.parseAnyDate(contract.get("BANKCASHFLOWINPUTDATE"), Double.class, "BANKCASHFLOWINPUTDATE", true);
        // системное имя продукта
        String productSysName = getStringParam(contract, "PRODSYSNAME");
        // номер телефона страхователя
        String insurerPhone = "";
        Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
        if (insurer != null) {
            List<Map<String, Object>> contactsList = (List<Map<String, Object>>) insurer.get("contactList");
            if (contactsList != null) {
                for (Map<String, Object> contact : contactsList) {
                    String contactSysName = getStringParam(contact, "CONTACTTYPESYSNAME");
                    if ("MobilePhone".equals(contactSysName)) {
                        insurerPhone = getStringParam(contact, "VALUE");
                        break;
                    }
                }
            }
        }
        if (productSysName.isEmpty() || insurerPhone.isEmpty()) {
            throw new Mort900Exception(
                    "Не найдены сведения о продукте и/или номере телефона страхователя - нет возможности определить очередность текущего платежа",
                    "No product info and/or insurer phone number was found - order of current payment can not be determined"
            );
        }
        Map<String, Object> existedContractsParams = new HashMap<String, Object>();
        // системное имя продукта и номер телефона страхователя
        existedContractsParams.put("PRODSYSNAME", productSysName);
        existedContractsParams.put("INSURERPHONE", insurerPhone);
        // для проверки "«Входящая дата» из движения средств предшествует одному из следующих неоплаченных периодов по договору"
        existedContractsParams.put("BANKCASHFLOWINPUTDATE", bankCashFlowInputDateDouble);
        // флаг исключения из результатов запроса договоров у которых количество плановых платежей равно количеству фактических
        existedContractsParams.put("EXCLUDEEQUALPAYCOUNTS", true);
        List<Map<String, Object>> existedContractsList = getExistedContractsByParamsForTM(existedContractsParams, login, password);
        int existedContractsCount = existedContractsList.size();
        logger.debug("Found exisiting contracts: " + existedContractsCount);

        // только для продукта "Защита дома ТМ":
        // "Если найдено несколько договоров, добавляется дополнительное условие: «Адрес застрахованного имущества» (Назначение платежа 8)"
        // #4495 - поиск для hibtm всегда с учетом адреса.
        /*
        if ((existedContractsCount > 1) && (productSysName.equals(SYSNAME_HIBTM))) {
            logger.debug("Found multiple contracts - for current product will be used additional restriction by insured property address.");
            String propertyAddressText = "";
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap != null) {
                propertyAddressText = getStringParamLogged(contrExtMap, "propertyAddress");
            }
            if (propertyAddressText.isEmpty()) {
                throw new Mort900Exception(
                        "Не удалось определеить адрес застрахованного имущества из результатов анализа назначения платежа - данный адрес обязателен для исключения договоров, если по предыдущим ограничениям найдено более одного",
                        "Unable to find insured property address in parsed payment purpose data - this address is requiried for excluding contracts from serch result if other restriction returned more than one contract"
                );
            }
            // адрес срахуемого имущества
            existedContractsParams.put("PROPERTYADDRESSTEXT", propertyAddressText);
            existedContractsList = getExistedContractsByParamsForTM(existedContractsParams, login, password);
            existedContractsCount = existedContractsList.size();
            logger.debug("Found exisiting contracts (excluding ones with differs insured property address): " + existedContractsCount);
        }
         */
        return existedContractsList;

    }

    // получение из списка подходящих договоров второго списка - с учетом проверки по условиям
    private List<Map<String, Object>> getCheckedContractList(List<Map<String, Object>> existedContractsList, Date bankCashFlowInputDate, String login, String password) throws Mort900Exception {
        List<Map<String, Object>> existedContractsAfterChecksList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> existedContract : existedContractsList) {
            logger.debug("Checking existing contract...");
            getLongParamLogged(existedContract, "CONTRID");
            getLongParamLogged(existedContract, "CONTRNODEID");
            // план и факт платежей для существующего договора
            logger.debug("Getting existing contract's payments plan and fact lists...");
            ArrayList<Map<String, Object>> existedContractPaymentsPlanList = getContractPaymentsPlanSortedList(existedContract, login, password);
            ArrayList<Map<String, Object>> existedContractPaymentsFactList = getContractPaymentsFactSortedList(existedContract, login, password);

            logger.debug("Checking if existing contract last period is payed...");
            // предпоследний плановый платеж и его дата
            Map<String, Object> previouslastPlanPayment = existedContractPaymentsPlanList.get(existedContractPaymentsPlanList.size() - 2);
            Date previouslastPlanPaymentDate = getDateParamLogged(previouslastPlanPayment, "PAYDATE");
            // последний плановый платеж и его дата
            Map<String, Object> lastPlanPayment = existedContractPaymentsPlanList.get(existedContractPaymentsPlanList.size() - 1);
            Date lastPlanPaymentDate = getDateParamLogged(lastPlanPayment, "PAYDATE");
            // последний фактический платеж и его дата
            Map<String, Object> lastFactPayment = existedContractPaymentsFactList.get(existedContractPaymentsFactList.size() - 1);
            Date lastFactPaymentDate = getDateParamLogged(lastFactPayment, "PAYFACTDATE");

            if ((lastFactPaymentDate.after(previouslastPlanPaymentDate)) && (lastFactPaymentDate.before(lastPlanPaymentDate))) {
                // последний фактический платеж попадает в период последней плановой оплаты - следовательно, последний период оплачен
                logger.debug("Excluding this existing contract from search results - this contrat's last period is payed.");
            } else {
                // последний фактический платеж не попадает в период последней плановой оплаты - следовательно, последний период не оплачен
                logger.debug("This existing contract last period is unpayed.");
                // проверка "«Входящая дата» из движения средств предшествует одному из следующих неоплаченных периодов по договору"
                logger.debug("Checking existing contract unpayed periods with bank cash flow input date...");
                boolean isBankCashFlowInputDateBeforeAnyUnpayedPeriod = false;
                int paymentsPlanCount = existedContractPaymentsPlanList.size(); // например, 8
                logger.debug("This existing contract unpayed periods: " + paymentsPlanCount);
                int paymentsFactCount = existedContractPaymentsFactList.size(); // например, 2
                logger.debug("This existing contract payed periods: " + paymentsFactCount);
                // например, 2 .. 7 - итого шесть неоплаченых плановых платежей требуется проверить                
                for (int p = paymentsFactCount; p < paymentsPlanCount; p++) {
                    logger.debug("Checking existing contract unpayed periods No " + (p + 1) + "..."); // нумерация периодов с 1 для протокола
                    Map<String, Object> unpayedPlanPayment = existedContractPaymentsPlanList.get(p); // нумерация периодов с 0 для списка
                    Date unpayedPlanPaymentDate = getDateParamLogged(unpayedPlanPayment, "PAYDATE");
                    if (bankCashFlowInputDate.before(unpayedPlanPaymentDate)) {
                        // выполняется условие "«Входящая дата» из движения средств предшествует одному из следующих неоплаченных периодов по договору"
                        logger.debug("Bank cash flow input date IS before this unpayed period - keeping this existing contract in search results.");
                        isBankCashFlowInputDateBeforeAnyUnpayedPeriod = true;
                        break;
                    } else {
                        logger.debug("Bank cash flow input date after this unpayed period.");
                    }
                }
                if (isBankCashFlowInputDateBeforeAnyUnpayedPeriod) {
                    // дополнение краткой информации по договору списками плановых и фактических платежей
                    existedContract.put(PAYMENTPLAN_KEYNAME, existedContractPaymentsPlanList);
                    existedContract.put(PAYMENTFACT_KEYNAME, existedContractPaymentsFactList);
                    // включение договора в итоговый список найденных
                    existedContractsAfterChecksList.add(existedContract);
                } else {
                    // не выполняется условие "«Входящая дата» из движения средств предшествует одному из следующих неоплаченных периодов по договору"
                    logger.debug("Bank cash flow input date after all unpayed periods - excluding this existing contract from search results.");
                }
            }
        }
        return existedContractsAfterChecksList;
    }

    // получение из БД списка подходящих договоров и их проверка по условиям
    private List<Map<String, Object>> getExistedAndCheckedContractList(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        // получение из БД списка подходящих договоров
        List<Map<String, Object>> existedContractsList = getExistedContractList(contract, login, password);

        // Входящая дата из движения средств
        Date bankCashFlowInputDate = getDateParamLogged(contract, "BANKCASHFLOWINPUTDATE");
        // получение из списка подходящих договоров второго списка - с учетом проверки по условиям
        List<Map<String, Object>> existedContractsAfterChecksList = getCheckedContractList(existedContractsList, bankCashFlowInputDate, login, password);

        return existedContractsAfterChecksList;
    }

    private List<Map<String, Object>> getExistedContractsByParamsForTM(Map<String, Object> existedContractsParams, String login, String password) throws Mort900Exception {
        logger.debug("Exisiting contract check parameters: " + existedContractsParams);
        List<Map<String, Object>> existedContractsList = null;
        try {
            //datesParser.parseDates(existedContractsParams, Double.class);
            Map<String, Object> existedContractsRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamExForTM", existedContractsParams, login, password);
            existedContractsList = WsUtils.getListFromResultMap(existedContractsRes);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при получении списка существующих договоров - " + ex.getLocalizedMessage(),
                    "Error during getting list of existed contracts - " + ex.getMessage(),
                    ex
            );
        }
        if (existedContractsList == null) {
            throw new Mort900Exception(
                    "Ошибка при получении списка существующих договоров",
                    "Error during getting list of existed contracts"
            );
        }
        return existedContractsList;
    }

    private List<Map<String, Object>> getLastContractListByStartDateFromList(List<Map<String, Object>> contractsList, String login, String password) throws Mort900Exception {
        logger.debug("Getting list with only last (by start date) contract from full contract list...");
        List<Map<String, Object>> lastContractList;
        if ((contractsList != null) && (contractsList.size() > 1)) {
            // #4495 - "Среди найденных договоров определяется последний по «Дата начала действия»"
            datesParser.parseDates(contractsList, Date.class);
            CopyUtils.sortByDateFieldName(contractsList, "STARTDATE");
            lastContractList = contractsList.subList(contractsList.size() - 1, contractsList.size());
        } else {
            lastContractList = contractsList;
        }
        logger.debug("List with only last (by start date) contract: " + lastContractList);
        return lastContractList;
    }

    private ArrayList<Map<String, Object>> getContractPaymentsFactSortedList(Map<String, Object> existedContract, String login, String password) throws Mort900Exception {
        Map<String, Object> paymentsFactParams = new HashMap<String, Object>();
        paymentsFactParams.put("CONTRNODEID", existedContract.get("CONTRNODEID"));
        ArrayList<Map<String, Object>> existedContractPaymentsFactList = null;
        try {
            Map<String, Object> existedContractPaymentsFactRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParam", paymentsFactParams, login, password);
            existedContractPaymentsFactList = new ArrayList<Map<String, Object>>(WsUtils.getListFromResultMap(existedContractPaymentsFactRes));
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при получении списка плановых платежей для одного из найденных существующих договоров - " + ex.getLocalizedMessage(),
                    "Error during getting list of plan payment for one of found existed contracts - " + ex.getMessage(),
                    ex
            );
        }
        if (existedContractPaymentsFactList == null) {
            throw new Mort900Exception(
                    "Не удалось получить список плановых платежей для одного из найденных существующих договоров",
                    "Unable to get list of plan payment for one of found existed contracts"
            );
        }
        CopyUtils.sortByDateFieldName(existedContractPaymentsFactList, "PAYFACTDATE");
        return existedContractPaymentsFactList;
    }

    private ArrayList<Map<String, Object>> getContractPaymentsPlanSortedList(Map<String, Object> existedContract, String login, String password) throws Mort900Exception {
        Map<String, Object> paymentsPlanParams = new HashMap<String, Object>();
        paymentsPlanParams.put("CONTRID", existedContract.get("CONTRID"));
        ArrayList<Map<String, Object>> existedContractPaymentsPlanList = null;
        try {
            Map<String, Object> existedContractPaymentsPlanRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentBrowseListByParam", paymentsPlanParams, login, password);
            existedContractPaymentsPlanList = new ArrayList<Map<String, Object>>(WsUtils.getListFromResultMap(existedContractPaymentsPlanRes));
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при получении списка плановых платежей для одного из найденных существующих договоров - " + ex.getLocalizedMessage(),
                    "Error during getting list of plan payment for one of found existed contracts - " + ex.getMessage(),
                    ex
            );
        }
        if (existedContractPaymentsPlanList == null) {
            throw new Mort900Exception(
                    "Не удалось получить список плановых платежей для одного из найденных существующих договоров",
                    "Unable to get list of plan payment for one of found existed contracts"
            );
        }
        CopyUtils.sortByDateFieldName(existedContractPaymentsPlanList, "PAYDATE");
        return existedContractPaymentsPlanList;
    }

    // подготовка (и включение в мапу договора) плана ежеквартальных платежей, без сохранения в БД (будет выполнено позднее, после сохранения договора)
    private ArrayList<Map<String, Object>> prepareQuarterlyPayPlan(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        logger.debug("Preparing quarterly pay plan records list...");

        // «Сумма» из движения средств (она же сумма платежа, она жа сумма взноса при поэтапной оплате договора)
        Double payFactSum = getDoubleParamLogged(contract, "BANKCASHFLOWAMVALUE");

        // получение мапы плановых платежей по данным из продукта, включенного в мапу договора (ключ - порядковый номер планового платежа (NUM), значение - сведения о плановом платеже)
        // если в мапе договора отсутствуют сведения о продукте - они будут загружены из БД
        Map<Long, Map<String, Object>> payPlanMap = null;
        try {
            payPlanMap = getPayPlanMapFromContract(contract, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при получении сведений о варианте оплаты из данных продукта - " + ex.getLocalizedMessage(),
                    "Error during getting payment variant info from product data - " + ex.getMessage(),
                    ex
            );
        }
        // проверка полученной мапы
        if ((payPlanMap == null) || (payPlanMap.size() < 1)) {
            throw new Mort900Exception(
                    "Не найдены сведения о варианте оплаты в данных по продукту - нет возможности сформировать график плановых платежей",
                    "No product info and/or insurer phone number was found - order of current payment can not be determined"
            );
        }

        // дата начала действия договора (она же дата начала периода для первого планового платежа)
        Date contractStartDate = (Date) contract.get("STARTDATE");
        GregorianCalendar payDateGC = new GregorianCalendar();
        payDateGC.setTime(contractStartDate);
        payDateGC.add(Calendar.DATE, -1);
        payDateGC.set(Calendar.HOUR_OF_DAY, 23);
        payDateGC.set(Calendar.MINUTE, 59);
        payDateGC.set(Calendar.SECOND, 59);
        payDateGC.set(Calendar.MILLISECOND, 0);

        // дата окончания действия договора (она же дата окончания периода для последнего планового платежа)
        //Date finishDate = (Date) contract.get("FINISHDATE");
        // заготовка списка плановых платежей
        ArrayList<Map<String, Object>> paymentPlanList = new ArrayList<Map<String, Object>>();
        for (long p = 1; p <= payPlanMap.size(); p++) { // порядковый номер платежа (NUM) начинаетcя с 1
            // инфо о плановом платеже из продукта по порядковому номеру платежа (NUM)
            Map<String, Object> plannedPaymentInfoFromPoduct = payPlanMap.get(p);
            // сдвиг в месяцах для текущего планового платежа относительно предыдущего
            Long plannedPaymentDateShiftMonths = getLongParam(plannedPaymentInfoFromPoduct, "DATESHIFT");
            payDateGC.add(Calendar.MONTH, plannedPaymentDateShiftMonths.intValue());
            // запись о плановом платеже - сумма и дата оплаты
            Map<String, Object> plannedPayment = new HashMap<String, Object>();
            plannedPayment.put("AMOUNT", payFactSum);
            plannedPayment.put("PAYDATE", payDateGC.getTime());
            paymentPlanList.add(plannedPayment);
            // протоколирование сформированной записи
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Planned payment No %d (%d months after previous):", p, plannedPaymentDateShiftMonths));
                for (Map.Entry<String, Object> paymentParam : plannedPayment.entrySet()) {
                    String key = paymentParam.getKey();
                    Object value = paymentParam.getValue();
                    logger.debug("   " + key + " = " + value);
                }
            }
        }

        contract.put(PAYMENTPLAN_KEYNAME, paymentPlanList);

        logger.debug("Preparing quarterly pay plan records list finished with result: " + paymentPlanList);

        return paymentPlanList;
    }

    // подготовка (и включение в мапу договора) дополнительной секции, без сохранения в БД (будет выполнено позднее, в ходе сохранения договора)
    private Map<String, Object> addNewContractSection(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        logger.debug("Adding new quarterly section to existed contract map...");

        ArrayList<Map<String, Object>> existedContractPayPlanList = (ArrayList<Map<String, Object>>) contract.get(PAYMENTPLAN_KEYNAME);
        ArrayList<Map<String, Object>> existedContractPayFactList = (ArrayList<Map<String, Object>>) contract.get(PAYMENTFACT_KEYNAME);

        // очередной неоплаченный период из плана платежей
        int lastNonPayedPlanPeriodIndex = existedContractPayFactList.size();
        Map<String, Object> lastNonPayedPlanPeriod = existedContractPayPlanList.get(lastNonPayedPlanPeriodIndex);

        // «Сумма» из движения средств (она же сумма платежа, она жа сумма взноса при поэтапной оплате договора)
        Double bankCashFlowSum = getDoubleParamLogged(contract, "BANKCASHFLOWAMVALUE");
        // Сумма из плана платежей
        Double payPlanSum = getDoubleParamLogged(lastNonPayedPlanPeriod, "AMOUNT");
        // проверка на соответствие сумм (из плана платежей и из фактического платежа)
        Double absDelta = Math.abs(payPlanSum - bankCashFlowSum);
        if (absDelta >= 0.01) {
            throw new Mort900Exception(
                    String.format("Сумма из записи о движении средств (%.2f) не соответсвует плановой сумме платежа (%.2f)", bankCashFlowSum, payPlanSum),
                    String.format("Sum from bank cash flow (%.2f) do not correspond to plan payment sum (%.2f)", bankCashFlowSum, payPlanSum)
            );
        }

        // дата начала действия новой секции - соответствует дате последнего неоплаченного периода (со сдвигом на одну минуту, поскольку в дате периода 23:59 последнего дня оплаты)
        Date newSectionStartDate = getDateParamLogged(lastNonPayedPlanPeriod, "PAYDATE");
        GregorianCalendar newSectionStartDateGC = new GregorianCalendar();
        newSectionStartDateGC.setTime(newSectionStartDate);
        newSectionStartDateGC.add(Calendar.MINUTE, 1);
        newSectionStartDate = newSectionStartDateGC.getTime();

        // дата начала действия новой секции - соответствует дате следующего неоплаченного периода или дате окончания договора
        Date newSectionFinishDate;
        int nextNonPayedPlanPeriodIndex = lastNonPayedPlanPeriodIndex + 1;
        if (nextNonPayedPlanPeriodIndex < existedContractPayPlanList.size()) {
            // ... соответствует дате следующего неоплаченного периода
            Map<String, Object> nextNonPayedPlanPeriod = existedContractPayPlanList.get(nextNonPayedPlanPeriodIndex);
            newSectionFinishDate = getDateParamLogged(nextNonPayedPlanPeriod, "PAYDATE");
        } else {
            // ... соответствует дате окончания договора (c учетом времени, поскольку 23:59 и тп)
            newSectionFinishDate = (Date) datesParser.parseAnyDate(contract.get("FINISHDATE"), contract.get("FINISHDATETIME"), Date.class, "FINISHDATE", IS_VERBOSE_LOGGING);// getDateParamLogged(contract, "FINISHDATE");
        }

        // шаблон для построения новой ветки дерева структуры страхового продукта договора
        // (генерация новой ветки под видом единственной, посколькоу все секции договора идентичны за исключением дат)
        Map<String, Object> сontractWithNewSectionOnly = new HashMap<String, Object>();
        сontractWithNewSectionOnly.putAll(contract);
        // исключение не требующихся для генерация новой ветки (секции) параметров из мапы договора (для снижения объема передаваемых данных)
        сontractWithNewSectionOnly.remove("INSURERMAP");
        сontractWithNewSectionOnly.remove("INSOBJGROUPLIST");
        сontractWithNewSectionOnly.remove(PAYMENTPLAN_KEYNAME);
        сontractWithNewSectionOnly.remove(PAYMENTFACT_KEYNAME);
        сontractWithNewSectionOnly.remove("PRODUCTMAP"); // текущая версия метода dsB2BUpdateContractInsuranceProductStructure не поддерживает мапу продукта, полученную новым универсальным методом
        // исключение сведений о существующих секциях (чтобы сработал флаг ISMISSINGSTRUCTSCREATED метода dsB2BUpdateContractInsuranceProductStructure)
        сontractWithNewSectionOnly.put("CONTRSECTIONLIST", new ArrayList<Map<String, Object>>());

        // получение сведений продукта старым методом (dsProductBrowseByParams) и размещение в мапе договора по ключу PRODCONF
        // эти данные требуются для корректной работы текущей версии метода dsB2BUpdateContractInsuranceProductStructure, однако они не возвращаются стандартным dsB2BContrLoad
        Map<String, Object> productOldStyleMap;
        try {
            productOldStyleMap = getOrLoadProduct(сontractWithNewSectionOnly, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при получении сведений о продукте для создания новой секции существующего договора - " + ex.getLocalizedMessage(),
                    "Error during getting product info for creation existed contract's new section - " + ex.getMessage(),
                    ex
            );
        }
        if (productOldStyleMap == null) {
            throw new Mort900Exception(
                    "Не удалось получить сведения о продукте для создания новой секции существующего договора",
                    "Unable to get product info for creation existed contract's new section"
            );
        }

        сontractWithNewSectionOnly.put("STARTDATE", newSectionStartDate);
        сontractWithNewSectionOnly.put("FINISHDATE", newSectionFinishDate);
        сontractWithNewSectionOnly.put("PREMVALUE", payPlanSum);

        // подготовка параметров для построения дерева структуры страхового продукта договора
        Map<String, Object> updateContrInsProdStructParams = new HashMap<String, Object>();
        updateContrInsProdStructParams.put("CONTRMAP", сontractWithNewSectionOnly);
        updateContrInsProdStructParams.put("ISMISSINGSTRUCTSCREATED", true);
        updateContrInsProdStructParams.put("ISCREATESECTIONS", true);
        updateContrInsProdStructParams.put(RETURN_AS_HASH_MAP, true);
        // вызов построения дерева структуры страхового продукта договора
        try {
            сontractWithNewSectionOnly = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", updateContrInsProdStructParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Ошибка при подготовке данных новой секции существующего договора - " + ex.getLocalizedMessage(),
                    "Error during preparing existed contract's new section data - " + ex.getMessage(),
                    ex
            );
        }
        if (сontractWithNewSectionOnly == null) {
            throw new Mort900Exception(
                    "Не удалось подготовить данные новой секции существующего договора",
                    "Unable to prepare existed contract's new section data"
            );
        }

        // получение новой секции из результатов вызова dsB2BUpdateContractInsuranceProductStructure
        Map<String, Object> newSection = null;
        ArrayList<Map<String, Object>> sectionList = (ArrayList<Map<String, Object>>) сontractWithNewSectionOnly.get("CONTRSECTIONLIST");
        if ((sectionList != null) && (sectionList.size() > 0)) {
            newSection = sectionList.get(0);
        }
        if (newSection == null) {
            throw new Mort900Exception(
                    "Не удалось подготовить данные новой секции существующего договора",
                    "Unable to prepare existed contract's new section data"
            );
        }

        // добавление новой секции в полную мапу договора (которая позднее будет передана в универсальное сохранение)
        sectionList = (ArrayList<Map<String, Object>>) contract.get("CONTRSECTIONLIST");
        if (sectionList == null) {
            // полная мапа договора может не содержать списка секций (например, если была загружена без флага поддержки секций или сведения о существующих секциях были исключены из мапы для снижения объема данных)
            // для универсального сохранения сведения о существующих секциях не требуются - существующие секции остануться без изменений, если не будут переданы в универсальное сохранение договора
            sectionList = new ArrayList<Map<String, Object>>();
            contract.put("CONTRSECTIONLIST", sectionList);
        }
        sectionList.add(newSection);

        logger.debug("Adding new quarterly section to existed contract map finihed.");

        return contract;
    }

    //@WsMethod(requiredParams = {"BANKCASHFLOWID", "BANKSTATETEMPLATEID", "STATESYSNAME", "PURPOSE", "AMVALUE", "INPUTDATE", "INPUTNUMBER"})
    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BMort900ProcessPaymentPurposeMortProlongBySMS900(Map<String, Object> params) throws Exception {

        //String login = params.get(WsConstants.LOGIN).toString();
        //String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contract = commonProcessPaymentPurpose(params);

        // Системное имя продукта - требуется при сохранении договора
        contract.put("PRODSYSNAME", SYSNAME_MORTGAGE900);

        // Наименование способа оплаты и примечание будут использованы при создании фактического платежа в ходе сохранения договора
        contract.put("PAYMENTNAME", "Оплата мобильный банк (SMS 900)"); // "В качестве способа оплаты указывается «Оплата мобильный банк (SMS 900)»"
        contract.put("PAYMENTNOTE", "Пролонгация ипотеки через SMS 900");

        return contract;
    }

    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BHib900ProcessPaymentPurposeProlongBySMS900(Map<String, Object> params) throws Exception {

        //String login = params.get(WsConstants.LOGIN).toString();
        //String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contract = commonProcessPaymentPurpose(params);

        // Системное имя продукта - требуется при сохранении договора
        contract.put("PRODSYSNAME", SYSNAME_HOUSE900);

        // Наименование способа оплаты и примечание будут использованы при создании фактического платежа в ходе сохранения договора
        contract.put("PAYMENTNAME", "Оплата мобильный банк (SMS 900)"); // "В качестве способа оплаты указывается «Оплата мобильный банк (SMS 900)»"
        contract.put("PAYMENTNOTE", "Зашита дома SMS 900");

        return contract;
    }

    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BCib900ProcessPaymentPurposeProlongBySMS900(Map<String, Object> params) throws Exception {

        //String login = params.get(WsConstants.LOGIN).toString();
        //String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contract = commonProcessPaymentPurpose(params);

        // Системное имя продукта - требуется при сохранении договора
        contract.put("PRODSYSNAME", SYSNAME_CARD900);

        // Наименование способа оплаты и примечание будут использованы при создании фактического платежа в ходе сохранения договора
        contract.put("PAYMENTNAME", "Оплата мобильный банк (SMS 900)"); // "В качестве способа оплаты указывается «Оплата мобильный банк (SMS 900)»"
        contract.put("PAYMENTNOTE", "Защита карты SMS 900");

        return contract;
    }

    //@WsMethod(requiredParams = {"BANKCASHFLOWID", "BANKSTATETEMPLATEID", "STATESYSNAME", "PURPOSE", "AMVALUE", "INPUTDATE", "INPUTNUMBER"})
    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BMort900ProcessPaymentPurposeMortProlongByTM(Map<String, Object> params) throws Exception {

        //String login = params.get(WsConstants.LOGIN).toString();
        //String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contract = commonProcessPaymentPurpose(params);

        // Системное имя продукта - требуется при сохранении договора
        contract.put("PRODSYSNAME", SYSNAME_MORTGAGETM);

        // Наименование способа оплаты и примечание будут использованы при создании фактического платежа в ходе сохранения договора
        contract.put("PAYMENTNAME", "Оплата мобильный банк"); // "В качестве способа оплаты указывается «Оплата мобильный банк»"
        contract.put("PAYMENTNOTE", "Пролонгация ипотеки через ТМ");

        return contract;
    }

    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BHibTMProcessPaymentPurposeProlongByTM(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> contract = commonProcessPaymentPurpose(params);

        // Системное имя продукта - требуется при сохранении договора
        contract.put("PRODSYSNAME", SYSNAME_HIBTM);

        // проверка на наличие существующего договора (если будет найден - вернет мапу этого договора полностью)
        // если договор не будет найден - платеж из выписки первый и нужно создать новый договор
        // если договор будет найден - платеж из выписки очередной и договор уже сущестует, но его требуется обновить
        Map<String, Object> existedContract = checkForExistedContract(contract, login, password);

        if (existedContract == null) {
            // подготовка (и включение в мапу договора) плана ежеквартальных платежей, без сохранения в БД (будет выполнено позднее, после сохранения договора)
            prepareQuarterlyPayPlan(contract, login, password);
        } else {
            // подготовка (и включение в мапу договора) дополнительной секции, без сохранения в БД (будет выполнено позднее, в ходе сохранения договора)
            contract = addNewContractSection(existedContract, login, password);
        }

        // Наименование способа оплаты и примечание будут использованы при создании фактического платежа в ходе сохранения договора
        contract.put("PAYMENTNAME", "Оплата мобильный банк"); // "В качестве способа оплаты указывается «Оплата мобильный банк»"
        contract.put("PAYMENTNOTE", "Защита дома ТМ");

        return contract;
    }

    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BCibTMProcessPaymentPurposeProlongByTM(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> contract = commonProcessPaymentPurpose(params);

        // Системное имя продукта - требуется при сохранении договора
        contract.put("PRODSYSNAME", SYSNAME_CIBTM);

        // проверка на наличие существующего договора (если будет найден - вернет мапу этого договора полностью)
        // если договор не будет найден - платеж из выписки первый и нужно создать новый договор
        // если договор будет найден - платеж из выписки очередной и договор уже сущестует, но его требуется обновить
        Map<String, Object> existedContract = checkForExistedContract(contract, login, password);

        if (existedContract == null) {
            // подготовка (и включение в мапу договора) плана ежеквартальных платежей, без сохранения в БД (будет выполнено позднее, после сохранения договора)
            prepareQuarterlyPayPlan(contract, login, password);
        } else {
            // подготовка (и включение в мапу договора) дополнительной секции, без сохранения в БД (будет выполнено позднее, в ходе сохранения договора)
            contract = addNewContractSection(existedContract, login, password);
        }

        // Наименование способа оплаты и примечание будут использованы при создании фактического платежа в ходе сохранения договора
        contract.put("PAYMENTNAME", "Оплата мобильный банк"); // "В качестве способа оплаты указывается «Оплата мобильный банк»"
        contract.put("PAYMENTNOTE", "Защита карты ТМ");

        return contract;
    }

    @WsMethod(requiredParams = {"TEMPLATEMETHODNAME", "BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BMort900CheckPaymentPurpose(Map<String, Object> params) throws Exception {

        logger.debug("Checking payment purpose by given details templates...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        String templateMethodName = getStringParam(params.get("TEMPLATEMETHODNAME"));
        logger.debug("Method name from template: " + templateMethodName);

        Map<String, Object> processParams = params;
        processParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract;
        try {
            contract = this.callService(THIS_SERVICE_NAME, templateMethodName, processParams, login, password);
            // если нет исключений - данные прошли проверку
            contract.put("ISPURPOSEDETAILSVALID", true);
        } catch (Exception ex) {

            contract = new HashMap<String, Object>();

            String exLocalizedMessage = ex.getLocalizedMessage();
            logger.warn("Catched exception: " + exLocalizedMessage);
            logger.error("Catched exception call stack: ", ex); // !только для отладки!
            logger.error("Catched exception cause call stack: ", ex.getCause()); // !только для отладки!            
            contract.put("EXCEPTION", exLocalizedMessage);

            // получение текста ошибки для отображения в интерфейсе
            String errorMessage = getRussianMessageFromException(ex);
            logger.debug("Catched exception description: " + errorMessage);
            contract.put("EXCEPTIONTEXT", errorMessage);

            // данные не прошли проверку
            contract.put("ISPURPOSEDETAILSVALID", false);

        }

        logger.debug("Checking payment purpose by given details templates finished.");

        return contract;
    }

    private Map<String, Object> commonProcessPaymentPurpose(Map<String, Object> processParams) throws Exception, Mort900Exception {

        // Назначение платежа
        List<Map<String, Object>> purposeDetails = (List<Map<String, Object>>) processParams.get("BANKPURPOSEDETAILLIST");
        // список деталей назначения платежа из шаблона обработки банковской выписки
        List<Map<String, Object>> purposeTemplateDetails = (List<Map<String, Object>>) processParams.get("BANKSTATETEMPLATEPURPOSEDETAILLIST");
        // основные данные банковской выписки
        Map<String, Object> bankCashFlow = (Map<String, Object>) processParams.get("BANKCASHFLOW");

        CommonPaymentPurposeProcessor paymentPurposeProcessor = new CommonPaymentPurposeProcessor();
        Map<String, Object> contract = paymentPurposeProcessor.universalProcessPaymentPurpose(purposeDetails, purposeTemplateDetails, bankCashFlow);

        return contract;

    }

    private List<Map<String, Object>> getPurposeDetailsByBankCashFlowID(Long bankCashFlowID, String login, String password) throws Exception {
        logger.debug("Browsing payment purpose details for bank cash flow record with id (BANKCASHFLOWID): " + bankCashFlowID);

        Map<String, Object> detailsParams = new HashMap<String, Object>();
        detailsParams.put("BANKCASHFLOWID", bankCashFlowID);

        Map<String, Object> bankPurposeDetailsCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankPurposeDetailBrowseListByParam", detailsParams, login, password);
        List<Map<String, Object>> bankPurposeDetails = WsUtils.getListFromResultMap(bankPurposeDetailsCallResult);

        logger.debug("Browsing payment purpose details finished.");

        return bankPurposeDetails;
    }

    private String[] getPurposeArrayFromPurposeDetails(List<Map<String, Object>> bankPurposeDetails) throws Exception {

        int maxNum = 0;
        for (Map<String, Object> bankPurposeDetail : bankPurposeDetails) {
            int num = getLongParam(bankPurposeDetail.get("NUM")).intValue();
            if (num > maxNum) {
                maxNum = num;
            }
        }

        String[] purpose = new String[maxNum];
        for (int i = 0; i < purpose.length; i++) {
            purpose[i] = "";
        }

        for (Map<String, Object> bankPurposeDetail : bankPurposeDetails) {
            int num = getLongParam(bankPurposeDetail.get("NUM")).intValue();
            String value = getStringParam(bankPurposeDetail.get("VALUE"));
            logger.debug(num + " = " + value);
            purpose[num - 1] = value;
        }

        return purpose;

    }

    private String[] getPurposeArrayByBankCashFlowID(Long bankCashFlowID, String login, String password) throws Exception {
        List<Map<String, Object>> bankPurposeDetails = getPurposeDetailsByBankCashFlowID(bankCashFlowID, login, password);
        String[] purpose = getPurposeArrayFromPurposeDetails(bankPurposeDetails);
        return purpose;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankStateTemplatePurposeDetailBrowseListByParamEx(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsB2BBankStateTemplatePurposeDetailBrowseListByParamEx", params);
    }

    public HashMap<Long, List<Map<String, Object>>> cachePurposeTemplate = new HashMap();

    // запрос из шаблона обработки банковской выписки списка деталей назначения платежа
    private List<Map<String, Object>> getPurposeTemplateDetailsByBankStatementTemplateID(Long bankStatementTemplateID, String login, String password) throws Exception {
        logger.debug("Browsing payment purpose details template for bank bank statement template with id (BANKSTATETEMPLATEID): " + bankStatementTemplateID);
        List<Map<String, Object>> purposeTemplateDetails = cachePurposeTemplate.get(bankStatementTemplateID);
        if (purposeTemplateDetails != null) {
            return purposeTemplateDetails;
        }

        Map<String, Object> detailsParams = new HashMap<String, Object>();
        detailsParams.put("BANKSTATETEMPLATEID", bankStatementTemplateID);
        Map<String, Object> purposeTemplateDetailsCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankStateTemplatePurposeDetailBrowseListByParamEx", detailsParams, login, password);
        purposeTemplateDetails = WsUtils.getListFromResultMap(purposeTemplateDetailsCallResult);
        cachePurposeTemplate.put(bankStatementTemplateID, purposeTemplateDetails);
        logger.debug("Browsing payment purpose details template finished.");

        return purposeTemplateDetails;

    }

    @WsMethod(requiredParams = {"PRODVERID", "EMAIL", "TEST"})
    public Map<String, Object> dsB2BReprintAndSendByAllContr(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        //1. выбрать все договоры по продверид
        Map<String, Object> contrParams = new HashMap<String, Object>();
        contrParams.put("PRODVERID", params.get("PRODVERID"));
        Map<String, Object> contrRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contrParams, login, password);
        if (contrRes != null) {
            if (contrRes.get(RESULT) != null) {
                List<Map<String, Object>> contrList = (List<Map<String, Object>>) contrRes.get(RESULT);
                if (!contrList.isEmpty()) {
                    boolean test = false;
                    if (params.get("TEST") != null) {
                        if ("TRUE".equalsIgnoreCase(params.get("TEST").toString())) {
                            test = true;
                        }
                    }
                    int i = 1;
                    int count = contrList.size();
                    logger.debug("reprintNresend: start process " + count + " contracts to email " + params.get("EMAIL").toString());
                    for (Map<String, Object> contrMap : contrList) {
                        logger.debug("reprintNresend: " + i + " of " + count + " with id " + contrMap.get("CONTRID").toString());
                        Map<String, Object> printParams = new HashMap<String, Object>();
                        printParams.put("CONTRID", contrMap.get("CONTRID"));
                        printParams.put("PRODCONFID", contrMap.get("PRODCONFID"));
                        printParams.put("CONTRNUMBER", contrMap.get("CONTRNUMBER"));
                        printParams.put("EMAILtoSendCopy", params.get("EMAIL"));
                        printParams.put("ONLYPRINT", "TRUE");
                        printParams.put("NEEDREPRINT", "TRUE");
                        printParams.put("SENDCOPY", "TRUE");
                        Map<String, Object> printRes = this.callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BPrintAndSendAllDocument", printParams, login, password);
                        i++;
                        //3. если флаг test = true то печатать и отправлять только 1 договор.
                        if (test) {
                            logger.debug("reprintNresend: in test. finish work");
                            break;
                        }
                    }
                    logger.debug("reprintNresend: Done");
                }
            }
        }

        //2. в цикле для каждого договора запустить репринт и отправку по почте.
        return null;
    }

    private Map<String, Object> doBankCashFlowsProcessSingleRecord(Map<String, Object> cashFlow, String login, String password) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        // Назначение платежа
        // чтение из поля "PURPOSE"
        //String purposeLine = getStringParam(params.get("PURPOSE"));
        //String[] purpose = purposeLine.split(";");
        // чтение из БД - правильнее, но более ресуроемкая операция
        Long bankCashFlowID = getLongParam(cashFlow.get("BANKCASHFLOWID"));
        List<Map<String, Object>> purposeDetails = getPurposeDetailsByBankCashFlowID(bankCashFlowID, login, password);

        // получение из БД списка объектов учета «Структура назначения платежа банковской выписки»
        Long bankStatementTemplateID = getLongParam(cashFlow.get("BANKSTATETEMPLATEID"));
        // запрос из шаблона обработки банковской выписки списка деталей назначения платежа
        List<Map<String, Object>> purposeTemplateDetails = getPurposeTemplateDetailsByBankStatementTemplateID(bankStatementTemplateID, login, password);

        String templateMethodName = getStringParam(cashFlow.get("TEMPLATEMETHODNAME"));
        logger.debug("Method name from template: " + templateMethodName);

        Map<String, Object> processParams = new HashMap<String, Object>();
        processParams.put("BANKPURPOSEDETAILLIST", purposeDetails);
        processParams.put("BANKSTATETEMPLATEPURPOSEDETAILLIST", purposeTemplateDetails);
        processParams.put("BANKCASHFLOW", cashFlow); // основные данные банковской выписки
        processParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = this.callService(THIS_SERVICE_NAME, templateMethodName, processParams, login, password);

        // todo: проверять - если возвращен существующий договор - пропускать создание, обновлять возвращенный (добавлять секцию)
        //Map<String, Object> contrParams = new HashMap<String, Object>();
        //contrParams.putAll(cashFlow);
        //contrParams.put(RETURN_AS_HASH_MAP, true);
        //String templateMethodName = getStringParam(cashFlow.get("TEMPLATEMETHODNAME"));
        //logger.debug("Method name from template: " + templateMethodName);
        //Map<String, Object> contract = this.callService(THIS_SERVICE_NAME, templateMethodName, contrParams, login, password);
        try {
            // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
            updateSessionParamsIfNullByCallingUserCreds(contract, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при определении данных пользователя для назначения прав на создаваемый договор: " + ex.getLocalizedMessage(),
                    "Contract rights user data getting error: " + ex.getMessage(),
                    ex
            );
        }

        Long existedContractID = getLongParam(contract, "CONTRID");

        Map<String, Object> savedContract = null;

        // номер платежного поручения
        String bankCashFlowNumber = getStringParamLogged(cashFlow, "INPUTNUMBER");
        // «Входящая дата» из движения средств
        Date bankCashFlowInputDate = (Date) datesParser.parseAnyDate(cashFlow.get("INPUTDATE"), Date.class, "INPUTDATE", true);
        // «Сумма» из движения банковских средств
        Double bankCashFlowSum = getDoubleParamLogged(cashFlow, "AMVALUE");

        if (existedContractID == null) {

            // создание договора - возвращает договор (если был успешно создан) или выбрасывает исключение (в случае ошибок в ходе создания)
            Map<String, Object> createdContract = tryCreateContract(contract, login, password);

            //<editor-fold defaultstate="collapsed" desc="прикрепление договора в ЛК (с автоматической регистрацией пользователя при необходимости) - перенесено в отдельное регламентное задание dsPAAttachableContractsProcess (paws)">
            // договор создан - прикрепление договора в ЛК (с автоматической регистрацией пользователя при необходимости) 
            //Map<String, Object> attachResult = attachContractInPA(savedContract, login, password);
            //result.put("PAATTACHRESULT", attachResult);           
            //</editor-fold>
            // договор создан - создание записи об оплате
            // "Дата оплаты соответствует «Входящая дата» из движения средств"
            Date payFactDate = bankCashFlowInputDate;
            // "Сумма оплаты соответствует «Сумма» из движения средств"
            // сумма оплаты (посклольку сам договор уже сохранен, то сумма уже проверена в ходе подготовки к сохранению договора и соответствует величине взноса)
            Double payFactSum = bankCashFlowSum;
            // создание записи об оплате
            ArrayList<Map<String, Object>> paymentFactList = tryCreatePayFact(createdContract, bankCashFlowNumber, payFactDate, payFactSum, login, password);

            // создание плана оплаты
            //ArrayList<Map<String, Object>> paymentPlanList = tryCreatePayPlan(savedContract, payFactDate, login, password); 
            tryCreatePayPlan(createdContract, payFactDate, login, password);

            // договор и платежи созданы - смена состояния договора и обновление записи о движении денежных средств идентификатром созданного договора
            // смена состояния договора (черновки - предварительная печать - подписан)
            try {
                for (String transitionStepStateSysName : CONTRACT_STATE_TRANSITION_PATH) {
                    Map<String, Object> transResult = contractMakeTrans(createdContract, transitionStepStateSysName, login, password);
                    createdContract.putAll(transResult);
                }
            } catch (Exception ex) {
                throw new Mort900Exception(
                        "Возникла ошибка при смене состояния договора: " + ex.getLocalizedMessage(),
                        "Contract state changing error: " + ex.getMessage(),
                        ex
                );
            }

            // формирование результата
            savedContract = createdContract;
            savedContract.put(PAYMENTFACT_KEYNAME, paymentFactList);

        } else {

            // обновление договора - возвращает договор (если был успешно обновлен) или выбрасывает исключение (в случае ошибок в ходе обновления)
            Map<String, Object> updatedContract = tryUpdateContract(contract, login, password);

            // !только для отладки - возврат без сохранения!
            //result.put("CONTRMAP", contract);
            //return result;
            //            
            // договор создан - создание записи об оплате
            // "Дата оплаты соответствует «Входящая дата» из движения средств"
            Date payFactDate = bankCashFlowInputDate;
            // "Сумма оплаты соответствует «Сумма» из движения средств"
            Double payFactSum = getDoubleParam(cashFlow, "AMVALUE");
            // создание записи об оплате
            ArrayList<Map<String, Object>> paymentFactList = tryCreatePayFact(updatedContract, bankCashFlowNumber, payFactDate, payFactSum, login, password);

            // формирование результата
            savedContract = updatedContract;
            savedContract.put(PAYMENTFACT_KEYNAME, paymentFactList);

        }

        //<editor-fold defaultstate="collapsed" desc="перенесено в отдельное регламентное задание dsPAAttachableContractsProcess (paws)">
        // смена статуса записи о движении денежных средств на «Обработан» - перенесено в отдельное регламентное задание
        //Map<String, Object> transResult = bankCashFlowMakeTrans(cashFlow, B2B_BANKCASHFLOW_PROCESSED, " ", login, password);
        //</editor-fold>
        // обновление записи о движении денежных средств идентификатром созданного договора
        Map<String, Object> updateResult = null;
        try {
            updateResult = bankCashFlowUpdateContarctID(cashFlow, savedContract, login, password);
            result.putAll(updateResult);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при обновлении записи о движении денежных средств: " + ex.getLocalizedMessage(),
                    "Bank cash flow record updating error: " + ex.getMessage(),
                    ex
            );
        }

        if ((updateResult == null) || (updateResult.get("BANKCASHFLOWID") == null)) {
            // обновление записи о движении денежных средств не удалось
            throw new Mort900Exception(
                    "Не удалось обновить запись о движении денежных средств",
                    "Bank cash flow record update failed"
            );
        }

        if (existedContractID != null) {
            // если обновлялся существующий договор договор, то он уже прикреплен в ЛК (или будет прикреплен при обработке той записи о движении средств, по которой был создан)
            // и для текущей записи о движении средств данный этап можно пропустить (переведя её сразу в статус "Обработан")
            Map<String, Object> transResult = bankCashFlowMakeTrans(cashFlow, B2B_BANKCASHFLOW_PROCESSED, " ", login, password);
        }

        //<editor-fold defaultstate="collapsed" desc="!только для отладки!">
        // !только для отладки!
        //logger.debug("savedContract.get(\"CONTRID\") = " + savedContract.get("CONTRID"));
        //logger.debug("attachResult.get(\"PAUSER\").get(\"USERID\") = " + ((Map<String, Object>) attachResult.get("PAUSER")).get("USERID"));
        //logger.debug("attachResult.get(\"PACONTRACT\").get(\"PAOBJECTID\") = " + ((Map<String, Object>) attachResult.get("PACONTRACT")).get("PAOBJECTID"));
        //logger.debug("savedPayFact.get(\"PAYFACTID\") = " + savedPayFact.get("PAYFACTID"));
        //if (bankCashFlowNumber.equals("666")) {
        //    throw new Mort900Exception("Тестовое исключение", "Test exception");
        //}
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="перенесено в отдельное регламентное задание dsPAAttachableContractsProcess (paws)">
        //договор переведен в статус "Оплачен", платеж создан, выполнено связывание с ЛК.
        //здесь необходимо сформировать печатный документ и отправить его в банк.
        //Map<String, Object> printParams = new HashMap<String, Object>();
        //printParams.put("CONTRID", savedContract.get("CONTRID"));
        //printParams.put("PRODCONFID", savedContract.get("PRODCONFID"));
        //printParams.put("CONTRNUMBER", savedContract.get("CONTRNUMBER"));
        //printParams.put("ONLYPRINT", "TRUE");
        //printParams.put("SENDCOPY", "TRUE");
        //Map<String, Object> printRes = this.callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BPrintAndSendAllDocument", printParams, login, password);
        //</editor-fold>
        // формирование результата
        //savedContract.put(PAYMENTPLAN_KEYNAME, paymentPlanList); // выполняется в tryCreatePayPlan
        //result.putAll(transResult);
        result.put("CONTRMAP", savedContract);
        //result.put("PRINTRES", printRes);

        return result;
    }

    private ArrayList<Map<String, Object>> tryCreatePayPlan(Map<String, Object> savedContract, Date payFactDate, String login, String password) throws Mort900Exception {
        logger.debug("Trying to creating necessary pay plan records...");

        // список плановых платежей может быть подготовлен и включен в мапу договора ранее (при разборе банковской выписки)
        ArrayList<Map<String, Object>> paymentPlanList = (ArrayList<Map<String, Object>>) savedContract.get(PAYMENTPLAN_KEYNAME);

        if (paymentPlanList == null) {
            // список плановых платежей не был подготовлен и включен в мапу договора ранее (при разборе банковской выписки) -
            // требуется создание простого плана единовременной оплаты по данным из выписки и созданного договора
            logger.debug("No pre-generated pay plan found - simple single pay plan will be created.");
            paymentPlanList = new ArrayList<Map<String, Object>>();
            Map<String, Object> savedPayPlan = createSinglePayPlanRecord(savedContract, payFactDate, login, password);
            paymentPlanList.add(savedPayPlan);
            savedContract.put(PAYMENTPLAN_KEYNAME, paymentPlanList);
        } else {
            // список плановых платежей был подготовлен и включен в мапу договора ранее (при разборе банковской выписки) - требуется сохранение этого плана
            logger.debug("Pre-generated pay plan found - plan payments records will be created according to it.");
            Long contractID = getLongParam(savedContract, "CONTRID");
            for (Map<String, Object> payment : paymentPlanList) {
                payment.put("CONTRID", contractID);
                Map<String, Object> savedPayPlan = createPayPlanRecord(payment, login, password);
                payment.putAll(savedPayPlan);
            }
        }

        logger.debug("Creating necessary pay plan records finished.");

        return paymentPlanList;
    }

    private ArrayList<Map<String, Object>> tryCreatePayFact(Map<String, Object> savedContract, String bankCashFlowNumber, Date payFactDate, Double payFactSum, String login, String password) throws Mort900Exception {
        logger.debug("Trying to creating necessary pay fact records...");
        // список фактов оплаты - получение из мапы договора
        ArrayList<Map<String, Object>> paymentFactList = (ArrayList<Map<String, Object>>) savedContract.get(PAYMENTFACT_KEYNAME);
        if (paymentFactList == null) {
            // список фактов оплаты - в мапе договоре отсутсвует, создание нового списка
            paymentFactList = new ArrayList<Map<String, Object>>();
        }
        Map<String, Object> savedPayFact = null;
        try {
            // создание записи о фактической оплате
            savedPayFact = createPayFact(savedContract, bankCashFlowNumber, payFactDate, payFactSum, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении фактического платежа: " + ex.getLocalizedMessage(),
                    "Pay fact creating error: " + ex.getMessage(),
                    ex
            );
        }
        if ((savedPayFact == null) || (savedPayFact.get("PAYFACTID") == null)) {
            // платеж не создан
            throw new Mort900Exception(
                    "Не удалось создать запись о фактическом платеже",
                    "No pay fact record created"
            );
        }
        // дополнение списка фактов оплаты созданным
        paymentFactList.add(savedPayFact);

        logger.debug("Creating necessary pay fact records finished.");
        return paymentFactList;
    }

    // создание договора - возвращает договор (если был успешно создан) или выбрасывает исключение (в случае ошибок в ходе создания)
    private Map<String, Object> tryCreateContract(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        logger.debug("Creating contract...");
        Map<String, Object> saveParams = new HashMap<String, Object>();
        saveParams.put("CONTRMAP", contract);
        saveParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> savedContract = null;
        try {
            // создание договора
            // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
            savedContract = this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BContrSave", saveParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении договора: " + ex.getLocalizedMessage(),
                    "Contract creating error: " + ex.getMessage(),
                    ex
            );
        }
        String savedContractErrorText = getStringParam(savedContract, "Error");
        if (!savedContractErrorText.isEmpty()) {
            // договор не создан по указанной в "Error" причине
            throw new Mort900Exception(
                    "Не удалось создать договор на основании сведений из записи о движении денежных средств - " + savedContractErrorText,
                    "No contract created by info from bank cash flow record - " + savedContractErrorText
            );
        } else if ((savedContract == null) || (savedContract.get("CONTRID") == null)) {
            // договор не создан по неустановленной причине
            throw new Mort900Exception(
                    "Не удалось создать договор на основании сведений из записи о движении денежных средств",
                    "No contract created by info from bank cash flow record"
            );
        }
        logger.debug("Creating contract finished.");
        return savedContract;

    }

    // обновление договора - возвращает договор (если был успешно обновлен) или выбрасывает исключение (в случае ошибок в ходе обновления)
    private Map<String, Object> tryUpdateContract(Map<String, Object> contract, String login, String password) throws Mort900Exception {

        logger.debug("Updating existing contract...");
        Map<String, Object> saveParams = new HashMap<String, Object>();
        //
        // для dsB2BContrSave
        //saveParams.put("CONTRMAP", contract);
        //
        // для dsB2BContractUniversalSave
        ArrayList<Map<String, Object>> contrList = new ArrayList<Map<String, Object>>();
        contrList.add(contract);
        saveParams.put("CONTRLIST", contrList);
        //
        // для dsB2BContrSave или dsB2BContractUniversalSave
        saveParams.put(RETURN_AS_HASH_MAP, true);

        Map<String, Object> updatedContract = null;
        try {
            // обновление договора
            // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
            //
            // обновление через dsB2BContrSave
            //updatedContract = this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BContrSave", saveParams, login, password);
            //
            // обновление через dsB2BContractUniversalSave
            datesParser.parseDates(saveParams, Double.class);
            // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
            Map<String, Object> updatedContractRes = this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BContractUniversalSave", saveParams, login, password);
            contrList = (ArrayList<Map<String, Object>>) updatedContractRes.get("CONTRLIST");
            updatedContract = contrList.get(0);

        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при обновлении существующего договора: " + ex.getLocalizedMessage(),
                    "Existed contract updating error: " + ex.getMessage(),
                    ex
            );
        }
        String savedContractErrorText = getStringParam(updatedContract, "Error");
        if (!savedContractErrorText.isEmpty()) {
            // договор не обновлен по указанной в "Error" причине
            throw new Mort900Exception(
                    "Не удалось обновить существующий договор на основании сведений из записи о движении денежных средств - " + savedContractErrorText,
                    "No existed contract updated by info from bank cash flow record - " + savedContractErrorText
            );
        } else if ((updatedContract == null) || (updatedContract.get("CONTRID") == null)) {
            // договор не обновлен по неустановленной причине
            throw new Mort900Exception(
                    "Не удалось обновить существующий договор на основании сведений из записи о движении денежных средств",
                    "No existed contract updated by info from bank cash flow record"
            );
        }
        logger.debug("Updating contract finished.");
        return updatedContract;

    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "TEMPLATEMETHODNAME", "STATESYSNAME", "PURPOSE", "AMVALUE", "INPUTDATE", "INPUTNUMBER"})
    public Map<String, Object> dsB2BMort900BankCashFlowsProcessSingleRecord(Map<String, Object> params) throws Exception {

        logger.debug("Start bank cash flow single record processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {

            result = doBankCashFlowsProcessSingleRecord2(params, login, password);

        } catch (Exception ex) {

            String exLocalizedMessage = ex.getLocalizedMessage();
            logger.warn("Catched exception: " + exLocalizedMessage);
            logger.error("Catched exception call stack: ", ex); // !только для отладки!
            logger.error("Catched exception cause call stack: ", ex.getCause()); // !только для отладки!
            result.put("EXCEPTION", exLocalizedMessage);

            // получение текста ошибки для сохранения в БД
            String errorMessage = getRussianMessageFromException(ex);
            logger.debug("Catched exception info for saving in DB: " + errorMessage);

            // откат текущей транзакции и создание взамен новой
            String transactionError = rollbackCurrentAndBeginNewTransaction();

            // если были проблемы при работе с транзакциями - дополнение описания ошибки для сохранения в БД упоминанием о транзакциях
            if (transactionError != null) {
                errorMessage = String.format("Ошибка при откате изменений: %s. Причина выполнения отката - %s.", transactionError, errorMessage);
                logger.debug("Catched exception info plus transaction error description for saving in DB: " + errorMessage);
            }
            result.put("EXCEPTIONTEXT", errorMessage);

            // перевод статуса в обработанный с ошибкой
            Map<String, Object> transResult = tryTransBankCashFlowToErrorByExternalCall(params, errorMessage, login, password);
            result.putAll(transResult);

        }

        logger.debug("Bank cash flow single record processing finished.");

        return result;

    }

    // откат текущей транзакции и создание взамен новой
    // (скопирован в ContractCustomFacade для paws)
    // todo: перенести в Base-фасад или тп
    private String rollbackCurrentAndBeginNewTransaction() {
        String transactionError = null;
        try {
            logger.debug("Getting current thread transaction...");
            Transaction threadTransaction = Transaction.getThreadTransaction();
            if (threadTransaction != null) {
                logger.debug("Rollback current transaction...");
                threadTransaction.rollback();
                Transaction.bindThreadTransaction(null);
                logger.debug("Rollback current transaction finished.");
                logger.debug("Starting new transaction (to replace rollbacked one)...");
                Transaction.bindThreadTransaction(Transaction.internalTransaction(null));
                threadTransaction = Transaction.getThreadTransaction();
                //threadTransaction = null; // !только для отладки!
                if (threadTransaction != null) {
                    threadTransaction.setStatus(Transaction.STATUS_NO_TRANSACTION);
                    threadTransaction.begin();
                    logger.debug("Starting new transaction (to replace rollbacked one) finished.");
                } else {
                    logger.error("Starting new transaction (to replace rollbacked one) failed!");
                    transactionError = "не удалось создать новую транзакцию";
                }
            } else {
                logger.error("Getting current transaction failed!");
                transactionError = "не удалось получить текущую транзакцию";
            }
        } catch (Exception rollbackEx) {
            transactionError = rollbackEx.getLocalizedMessage();
        }
        return transactionError;
    }

    // получение текста ошибки для сохранения в БД   
    private String getRussianMessageFromException(Exception ex) {
        String errorText;
        if (ex instanceof Mort900Exception) {
            errorText = ((Mort900Exception) ex).getRussianMessage();
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                if (cause instanceof Mort900Exception) {
                    errorText = ((Mort900Exception) cause).getRussianMessage();
                } else {
                    errorText = cause.getLocalizedMessage();
                }
            } else {
                errorText = ex.getLocalizedMessage();
            }
        }
        return errorText;
    }

    // создает плановый платеж по данным договора и записи о движении средств
    private Map<String, Object> createSinglePayPlanRecord(Map<String, Object> savedContract, Date payFactDate, String login, String password) throws Mort900Exception {

        logger.debug("Creating simple single pay plan...");

        Double premValue = getDoubleParam(savedContract, "PREMVALUE");
        Long contractID = getLongParam(savedContract, "CONTRID");
        Map<String, Object> payPlanRecord = createPayPlanRecord(contractID, premValue, payFactDate, login, password);

        logger.debug("Creating simple single pay plan finished.");

        return payPlanRecord;
    }

    private Map<String, Object> createPayPlanRecord(Long contractID, Double premValue, Date planPayDate, String login, String password) throws Mort900Exception {
        Date planPayStartDate = null; // Дата начала периода (для создания групп плановых платежей с периодичностью)
        return createPayPlanRecord(contractID, premValue, planPayDate, planPayStartDate, login, password);
    }

    private Map<String, Object> createPayPlanRecord(Long contractID, Double premValue, Date planPayDate, Date planPayStartDate, String login, String password) throws Mort900Exception {
        Map<String, Object> payPlanRecordParams = new HashMap<String, Object>();
        payPlanRecordParams.put("CONTRID", contractID);
        payPlanRecordParams.put("AMOUNT", premValue);
        // Дата начала периода (для создания групп плановых платежей с периодичностью)
        if (planPayStartDate != null) {
            payPlanRecordParams.put("STARTDATE", planPayStartDate);
        }
        // Плановая дата платежа
        payPlanRecordParams.put("PAYDATE", planPayDate);
        Map<String, Object> payPlanRecord = createPayPlanRecord(payPlanRecordParams, login, password);
        return payPlanRecord;
    }

    private Map<String, Object> createPayPlanRecord(Map<String, Object> payPlanRecordParams, String login, String password) throws Mort900Exception {
        logger.debug("Creating pay plan record...");
        logger.debug("Pay plan record params: " + payPlanRecordParams);
        payPlanRecordParams.put(RETURN_AS_HASH_MAP, true);
        // создание записи о плановой оплате
        Map<String, Object> payPlanRecord = null;
        try {
            datesParser.parseDates(payPlanRecordParams, Double.class);
            // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
            payPlanRecord = this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BPaymentCreate", payPlanRecordParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении планового платежа: " + ex.getLocalizedMessage(),
                    "Pay plan creating error: " + ex.getMessage(),
                    ex
            );
        }
        if ((payPlanRecord == null) || (payPlanRecord.get("PAYID") == null)) {
            // платеж не создан
            throw new Mort900Exception(
                    "Не удалось создать запись о плановом платеже",
                    "No pay plan record created"
            );
        }
        logger.debug("Creating pay plan record finished with result: " + payPlanRecord);
        return payPlanRecord;
    }

    // создает фактический платеж по данным договора и записи о движении средств
    private Map<String, Object> createPayFact(Map<String, Object> savedContract, String payFactNumber, Date payFactDate, Double payFactSum, String login, String password) throws NumberFormatException, Exception {

        logger.debug("Creating pay fact record...");

        Map<String, Object> payFactParams = new HashMap<String, Object>();
        payFactParams.put("CONTRNODEID", savedContract.get("CONTRNODEID"));
        //Object premValue = savedContract.get("PREMVALUE");
        // Сумма оплаты соответствует «Сумма» из движения средств
        Object premValue = payFactSum;
        payFactParams.put("AMVALUE", payFactSum);
        String premCurrencyID = getStringParam(savedContract.get("PREMCURRENCYID"));
        if (!premCurrencyID.isEmpty()) {
            payFactParams.put("AMCURRENCYID", premCurrencyID);
            Object currencyRate = savedContract.get("CURRENCYRATE");
            if ("3".equals(premCurrencyID) || "2".equals(premCurrencyID)) {
                if (currencyRate != null) {
                    BigDecimal curRate = BigDecimal.valueOf(Double.valueOf(currencyRate.toString()));
                    if (premValue != null) {
                        BigDecimal amvalue = BigDecimal.valueOf(Double.valueOf(premValue.toString()));
                        BigDecimal amValueRub = amvalue.multiply(curRate).setScale(2, RoundingMode.HALF_UP);
                        payFactParams.put("AMVALUERUB", amValueRub.doubleValue());
                    }
                }
                payFactParams.put("AMVALUE", premValue);
            } else {
                if (currencyRate != null) {
                    BigDecimal curRate = BigDecimal.valueOf(Double.valueOf(currencyRate.toString()));
                    if (premValue != null) {
                        BigDecimal amvalueRub = BigDecimal.valueOf(Double.valueOf(premValue.toString()));
                        BigDecimal amValue = amvalueRub.divide(curRate, 2, RoundingMode.HALF_UP);
                        payFactParams.put("AMVALUE", amValue.doubleValue());
                    }
                }
                payFactParams.put("AMVALUERUB", premValue);
            }
        }
        // генерим номер платежа по правилу ДатаНомерРазделительТелефон
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String phone = "";
        if (savedContract.get("INSURERMAP") != null) {
            Map<String, Object> insMap = (Map<String, Object>) savedContract.get("INSURERMAP");
            if (insMap.get("contactList") != null) {
                List<Map<String, Object>> contactList = (List<Map<String, Object>>) insMap.get("contactList");
                if (!contactList.isEmpty()) {
                    if (contactList.get(0).get("VALUE") != null) {
                        phone = contactList.get(0).get("VALUE").toString();
                    }
                }
            }
        }

        String payFactNum = sdf.format(payFactDate) + payFactNumber + "F" + phone;

        payFactParams.put("PAYFACTNUMBER", payFactNum);
        payFactParams.put("PAYFACTTYPE", 3);

        // Наименование способа оплаты может быть передано в явном виде
        String paymentName = getStringParamLogged(savedContract, "PAYMENTNAME");
        if (paymentName.isEmpty()) {
            // В качестве способа оплаты указывается «Оплата мобильный банк (SMS 900)».
            paymentName = "Оплата мобильный банк (SMS 900)";
        }
        // Примечание к оплате может быть передано в явном виде
        String paymentNote = getStringParamLogged(savedContract, "PAYMENTNOTE");
        if (paymentNote.isEmpty()) {
            paymentNote = "Пролонгация ипотеки через SMS 900";
        }
        payFactParams.put("NAME", paymentName); //setGeneratedParam(payFactParams, "NAME", paymentName, IS_VERBOSE_LOGGING); 
        payFactParams.put("NOTE", paymentNote); //setGeneratedParam(payFactParams, "NOTE", paymentNote, IS_VERBOSE_LOGGING); 

        // Дата оплаты соответствует «Входящая дата» из движения средств
        payFactParams.put("PAYFACTDATE", payFactDate);

        if (logger.isDebugEnabled()) {
            for (Map.Entry<String, Object> entrySet : payFactParams.entrySet()) {
                String key = entrySet.getKey();
                Object value = entrySet.getValue();
                logger.debug("    " + key + " = " + value);
            }
        }

        datesParser.parseDates(payFactParams, Double.class);

        payFactParams.put(RETURN_AS_HASH_MAP, true);
        // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
        Map<String, Object> payFactRes = this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BPaymentFactCreate", payFactParams, login, password);

        logger.debug("Creating pay fact record finished with result: " + payFactRes);

        return payFactRes;

    }

    private boolean validateSaveParams(Map<String, Object> contract) {

        boolean isDataInvalid = false;
        String errorText = "";

        // todo: если потребуется - проверка параметров перед созданием договора
        if (isDataInvalid) {
            errorText = errorText + "Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", errorText);
        }
        return !isDataInvalid;
    }

    private void genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {

        boolean isParamsChangingLogged = logger.isDebugEnabled();

        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));

        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            //contract.put("PRODCONFID", prodConfID);
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }

        // инициализация даты документа
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if (docDate == null) {
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            //contract.put("DOCUMENTDATE", documentDateGC.getTime());
            setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), isParamsChangingLogged);
        } else {
            documentDateGC.setTime((Date) datesParser.parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }

        // безусловное вычисление даты начала действия - "Дата начала договора определяется как «Дата оформления» + 1 день"
        // старт дата установлена по правилу в universalProcessPaymentPurpose/
        Date startDate = getDateParam(contract.get("STARTDATE"));
        GregorianCalendar startDateGC = new GregorianCalendar();
        startDateGC.setTime(documentDateGC.getTime());
        if (startDate == null) {
            startDateGC.add(Calendar.DATE, 1);
            startDate = startDateGC.getTime();
            setOverridedParam(contract, "STARTDATE", startDate, isParamsChangingLogged);
        }
        // расширенные атрибуты договора
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // безусловное вычисление даты окончания действия - "Дата окончания договора определяется как «Дата начала» + 1 год"
        GregorianCalendar finishDateGC = new GregorianCalendar();
        finishDateGC.setTime(startDate);
        finishDateGC.add(Calendar.YEAR, 1);
        finishDateGC.add(Calendar.DATE, -1);
        finishDateGC.set(Calendar.HOUR_OF_DAY, 23);
        finishDateGC.set(Calendar.MINUTE, 59);
        finishDateGC.set(Calendar.SECOND, 59);
        finishDateGC.set(Calendar.MILLISECOND, 0);
        setOverridedParam(contract, "FINISHDATE", finishDateGC.getTime(), isParamsChangingLogged);

        // безусловное вычисление срока действия договора в днях
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        // в сутках (24*60*60*1000) милисекунд
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
        //contract.put("DURATION", duration);
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        // список типов объектов - создание нового или выбор (если уже существует в договоре)
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        List<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract == null) {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        }
        /*else {
         insObjGroupList = (List<Map<String, Object>>) insObjGroupListFromContract;
         }*/

        // страхователь - создание нового или выбор (если уже существует в договоре)
        Object insurerFromContractObj = contract.get("INSURERMAP");
        Map<String, Object> insurer;
        if (insurerFromContractObj == null) {
            insurer = new HashMap<String, Object>();
            contract.put("INSURERMAP", insurer);
        } else {
            insurer = (Map<String, Object>) insurerFromContractObj;
        }

        // страхователь - значения по-умолчанию
        setGeneratedParamIfNull(insurer, "PARTICIPANTTYPE", "1", isParamsChangingLogged); // ФЛ
        setGeneratedParamIfNull(insurer, "ISBUSINESSMAN", "0", isParamsChangingLogged); // не ИП
        setGeneratedParamIfNull(insurer, "ISCLIENT", "1", isParamsChangingLogged); // клиент
        setGeneratedParamIfNull(insurer, "CITIZENSHIP", "0", isParamsChangingLogged); // РФ
        setGeneratedParamIfNull(insurer, "GENDER", "0", isParamsChangingLogged); // пол - мужской

        // валюта - значения по-умолчанию
        setGeneratedParamIfNull(contract, "PREMCURRENCYID", "1", isParamsChangingLogged); // рубли
        setGeneratedParamIfNull(contract, "INSAMCURRENCYID", "1", isParamsChangingLogged); // рубли
        setGeneratedParamIfNull(contract, "PAYCURRENCYID", "1", isParamsChangingLogged); // рубли
        setGeneratedParamIfNull(contract, "CURRENCYRATE", "1", isParamsChangingLogged); // рубли

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
        contract.put("PRODCONF", product);

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);
        logger.debug("CONTREXTMAP: " + contractExtValues);
        // Определение программы по страховой премии.
        String programCode = null;
        Map<String, Object> contractExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (null == contractExtMap) {
            contractExtMap = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtMap);
        }
        programCode = getStringParam(contractExtMap.get("insuranceProgram"));
        if ((null == programCode) || (null != programCode) && (programCode.isEmpty())) {
            if (null != contract.get("PREMVALUE")) {
                BigDecimal premValue = (BigDecimal.valueOf(Double.valueOf(contract.get("PREMVALUE").toString())));
                Long prodProgId = getLongParam(contract.get("PRODPROGID"));
                if (prodProgId != null) {
                    if (product.get("PRODVER") != null) {
                        Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
                        if (prodVerMap.get("PRODPROGS") != null) {
                            List<Map<String, Object>> prodProgList = (List<Map<String, Object>>) prodVerMap.get("PRODPROGS");
                            CopyUtils.sortByLongFieldName(prodProgList, "PRODPROGID");
                            if (!prodProgList.isEmpty()) {
                                for (Map<String, Object> prodProg : prodProgList) {
                                    if (null != prodProg) {
                                        BigDecimal prodProgPremValue = BigDecimal.valueOf(Double.valueOf(prodProg.get("PREMVALUE").toString()));
                                        if (Math.abs(premValue.doubleValue() - prodProgPremValue.doubleValue()) < 0.01) {
                                            prodProgId = Long.valueOf(prodProg.get("PRODPROGID").toString());
                                            contractExtMap.put("insuranceProgram", prodProg.get("PROGCODE"));
                                            if (prodProg.get("INSAMVALUE") != null) {
                                                contract.put("INSAMVALUE", prodProg.get("INSAMVALUE"));
                                            }
                                        }
                                    }
                                }
                            } else {
                                logger.debug("dsB2BMort900ContractPrepareToSave - Не удалось опрделить программу!");

                            }
                        }
                    }
                }
            }

        }
        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор (после регистрации продукта в БД)
        Map<String, Object> updateContrInsProdStructParams = new HashMap<String, Object>();
        updateContrInsProdStructParams.put("CONTRMAP", contract);
        updateContrInsProdStructParams.put("ISMISSINGSTRUCTSCREATED", true);
        contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", updateContrInsProdStructParams, login, password);
        //updateContractInsuranceProductStructure(contract, product, getStringParam(contractExtValues.get("insuranceProgram")), login, password);

    }

    private void genAdditionalSaveParamsFixContr(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));

        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            //contract.put("PRODCONFID", prodConfID);
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }

        // инициализация даты документа
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if (docDate == null) {
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            //contract.put("DOCUMENTDATE", documentDateGC.getTime());
            setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), isParamsChangingLogged);
        } else {
            documentDateGC.setTime((Date) datesParser.parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }

        // безусловное вычисление даты начала действия - "Дата начала договора определяется как «Дата оформления» + 1 день"
        // старт дата установлена по правилу в universalProcessPaymentPurpose/
        GregorianCalendar startDateGC = new GregorianCalendar();
        Date startDate = getDateParam(contract.get("STARTDATE"));
        if (startDate == null) {
            startDateGC.setTime(documentDateGC.getTime());
            startDateGC.add(Calendar.DATE, 1);
            setOverridedParam(contract, "STARTDATE", startDate, isParamsChangingLogged);
        } else {
            startDateGC.setTime((Date) datesParser.parseAnyDate(startDate, Date.class, "STARTDATE"));
        }
        startDate = startDateGC.getTime();
        // расширенные атрибуты договора
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // безусловное вычисление даты окончания действия - "Дата окончания договора определяется как «Дата начала» + 1 год"
        GregorianCalendar finishDateGC = new GregorianCalendar();
        Date finishDate = getDateParam(contract.get("FINISHDATE"));
        if (finishDate == null) {
            finishDateGC.setTime(startDate);
            finishDateGC.add(Calendar.YEAR, 1);
            finishDateGC.add(Calendar.DATE, -1);
            finishDateGC.set(Calendar.HOUR_OF_DAY, 23);
            finishDateGC.set(Calendar.MINUTE, 59);
            finishDateGC.set(Calendar.SECOND, 59);
            finishDateGC.set(Calendar.MILLISECOND, 0);
            setOverridedParam(contract, "FINISHDATE", finishDateGC.getTime(), isParamsChangingLogged);
        } else {
            finishDateGC.setTime((Date) datesParser.parseAnyDate(finishDate, Date.class, "FINISHDATE"));
        }

        // безусловное вычисление срока действия договора в днях
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        // в сутках (24*60*60*1000) милисекунд
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
        //contract.put("DURATION", duration);
        if (contract.get("DURATION") == null) {
            setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);
        }
        // список типов объектов - создание нового или выбор (если уже существует в договоре)
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        List<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract == null) {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        }
        /*else {
         insObjGroupList = (List<Map<String, Object>>) insObjGroupListFromContract;
         }*/

        // страхователь - создание нового или выбор (если уже существует в договоре)
        Object insurerFromContractObj = contract.get("INSURERMAP");
        Map<String, Object> insurer;
        if (insurerFromContractObj == null) {
            insurer = new HashMap<String, Object>();
            contract.put("INSURERMAP", insurer);
        } else {
            insurer = (Map<String, Object>) insurerFromContractObj;
        }

        // страхователь - значения по-умолчанию
        setGeneratedParamIfNull(insurer, "PARTICIPANTTYPE", "1", isParamsChangingLogged); // ФЛ
        setGeneratedParamIfNull(insurer, "ISBUSINESSMAN", "0", isParamsChangingLogged); // не ИП
        setGeneratedParamIfNull(insurer, "ISCLIENT", "1", isParamsChangingLogged); // клиент
        setGeneratedParamIfNull(insurer, "CITIZENSHIP", "0", isParamsChangingLogged); // РФ
        setGeneratedParamIfNull(insurer, "GENDER", "0", isParamsChangingLogged); // пол - мужской

        // валюта - значения по-умолчанию
        setGeneratedParamIfNull(contract, "PREMCURRENCYID", "1", isParamsChangingLogged); // рубли
        setGeneratedParamIfNull(contract, "INSAMCURRENCYID", "1", isParamsChangingLogged); // рубли
        setGeneratedParamIfNull(contract, "PAYCURRENCYID", "1", isParamsChangingLogged); // рубли
        setGeneratedParamIfNull(contract, "CURRENCYRATE", "1", isParamsChangingLogged); // рубли

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
        contract.put("PRODCONF", product);

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);
        logger.debug("CONTREXTMAP: " + contractExtValues);

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор (после регистрации продукта в БД)
        /*Map<String, Object> updateContrInsProdStructParams = new HashMap<String, Object>();
        updateContrInsProdStructParams.put("CONTRMAP", contract);
        updateContrInsProdStructParams.put("ISMISSINGSTRUCTSCREATED", true);
        contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", updateContrInsProdStructParams, login, password);*/
        //updateContractInsuranceProductStructure(contract, product, getStringParam(contractExtValues.get("insuranceProgram")), login, password);
    }

    /**
     * Метод для сохранения договора по продукту
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900ContractPrepareToSave(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BMort900ContractPrepareToSave");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        boolean isDataValid = validateSaveParams(contract);

        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }

        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("b2bCorrector1C")) && ((Boolean) params.get("b2bCorrector1C"))) {
                return contract;
            } else if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                logger.debug("after dsB2BMortgageContractPrepareToSaveFixContr");
                return contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                logger.debug("after dsB2BMortgageContractPrepareToSaveFixContr");
                return new HashMap<String, Object>();
            }
        } else {
            logger.debug("after dsB2BMortgageContractPrepareToSaveFixContr");
            return contract;
        }
        // logger.debug("after dsB2BMort900ContractPrepareToSave\n");             
        //return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900ContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BMort900ContractPrepareToSaveFixContr");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        boolean isDataValid = validateSaveParams(contract);

        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParamsFixContr(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }

        logger.debug("after dsB2BMort900ContractPrepareToSaveFixContr\n");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMort900BankStatementsFinalize(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        if (bankStatementsFinalizeThreadCount == 0) {
            bankStatementsFinalizeThreadCount = 1;
            try {
                logger.debug("doBankStatementsFinalize start");
                result = doBankStatementsFinalize(params);
            } finally {
                
                bankStatementsFinalizeThreadCount = 0;
                logger.debug("doBankStatementsFinalize finish\n");
            }
        } else {
            logger.debug("doBankStatementsFinalize already running");
        }
        
        return result;

    }

    private Map<String, Object> doBankStatementsFinalize(Map<String, Object> params) throws Exception {
        logger.debug("");
        logger.debug("Start bank statements finalization...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> bankStatementsParams = new HashMap<String, Object>();
        bankStatementsParams.putAll(params);
        bankStatementsParams.put("STATESYSNAME", B2B_BANKSTATE_INPROCESSQUEUE);

        Map<String, Object> bankStatementsCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateBrowseListByParam", bankStatementsParams, login, password);
        List<Map<String, Object>> bankStatements = WsUtils.getListFromResultMap(bankStatementsCallResult);

        int totalCount = bankStatements.size();
        int current = 1;
        logger.debug(String.format("Found %d bank statements for finalization.", totalCount));

        for (Map<String, Object> bankStatement : bankStatements) {

            logger.debug("");
            logger.debug(String.format("Preparing for finalization %d bank statement (from total of %d found bank statements)...", current, totalCount));

            Map<String, Object> bankStatementFinalizeParams = new HashMap<String, Object>();
            bankStatementFinalizeParams.putAll(bankStatement);
            bankStatementFinalizeParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> bankStatementFinalizeResult = this.callService(THIS_SERVICE_NAME, "dsB2BMort900BankStatementsFinalizeSingleRecord", bankStatementFinalizeParams, login, password);
            bankStatement.putAll(bankStatementFinalizeResult);
            
            try {
                Long bankStatementID = getLongParam(bankStatement.get("BANKSTATEMENTID"));
                Map<String, Object> paramsState = new HashMap<String, Object>();
                paramsState.put("STATEID", BANKCASHFLOW_STATE_ERROR);
                paramsState.put("BANKSTATEMENTID", bankStatementID);
                Map<String, Object> resultCountErrorString = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowCountStateString", paramsState, login, password);
                Map<String, Object> countErrorStringMap = WsUtils.getFirstItemFromResultMap(resultCountErrorString);
                String countErrorString = countErrorStringMap.get("COUNTSTATESTRING").toString();

                Map<String, Object> paramsTypeNewString = new HashMap<String, Object>();
                paramsTypeNewString.put("TYPEMODIFYSTRING", BANKCASHFLOW_TYPEMODIFYSTRING_ADD);
                paramsTypeNewString.put("BANKSTATEMENTID", bankStatementID);
                Map<String, Object> resultCountTypeAddString = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowCountTypeModifyString", paramsTypeNewString, login, password);
                Map<String, Object> countAddStringMap = WsUtils.getFirstItemFromResultMap(resultCountTypeAddString);
                String countAddString = countAddStringMap.get("COUNTTYPEMODIFYSTRING").toString();

                Map<String, Object> paramsTypeUpdateString = new HashMap<String, Object>();
                paramsTypeUpdateString.put("TYPEMODIFYSTRING", BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE);
                paramsTypeUpdateString.put("BANKSTATEMENTID", bankStatementID);
                Map<String, Object> resultCountTypeUpdateString = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowCountTypeModifyString", paramsTypeUpdateString, login, password);
                Map<String, Object> countTypeUpdateStringMap = WsUtils.getFirstItemFromResultMap(resultCountTypeUpdateString);
                String countUpdateString = countTypeUpdateStringMap.get("COUNTTYPEMODIFYSTRING").toString();

                Map<String, Object> paramsCountString = new HashMap<String, Object>();      
                paramsCountString.put("BANKSTATEMENTID", bankStatementID);
                paramsCountString.put("COUNTERRORSTRING", countErrorString);
                paramsCountString.put("COUNTADDSTRING", countAddString);
                paramsCountString.put("COUNTUPDATESTRING", countUpdateString);
                Map<String, Object> resultUpdateCountString;
                resultUpdateCountString = this.callService(THIS_SERVICE_NAME, "dsB2BBankStateUpdateCountString", paramsCountString, login, password);
            } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при добавлении количества строк " + ex.getLocalizedMessage(),
                    "Find error: " + ex.getMessage(),
                    ex
            );
        }
            
            current += 1;

        }

        result.put("BANKSTATEMENTLIST", bankStatements);
        logger.debug("Bank statements finalization finished.");
        logger.debug("");
        return result;
    }

    @WsMethod(requiredParams = {"BANKSTATEMENTID", "STATESYSNAME"})
    public Map<String, Object> dsB2BMort900BankStatementsFinalizeSingleRecord(Map<String, Object> params) throws Exception {

        logger.debug("Start bank statement single record finalization...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> bankCashFlowsParams = new HashMap<String, Object>();
        Long bankStatementID = getLongParam(params.get("BANKSTATEMENTID"));
        logger.debug("BANKSTATEMENTID = " + bankStatementID);
        bankCashFlowsParams.put("BANKSTATEMENTID", bankStatementID);
        bankCashFlowsParams.put("INCLUDEDSYSNAMELIST", BANKCASHFLOW_FINALISATION_INCLUDED_SYSNAMELIST);

        Map<String, Object> unprocessedBankCashFlows = this.callService(THIS_SERVICE_NAME, "dsB2BBankCashFlowBrowseListByParamExForProcessing", bankCashFlowsParams, login, password);
        Long unprocessedCount = getLongParam(unprocessedBankCashFlows.get(TOTALCOUNT));
        logger.debug("Unprocessed cash flow records count for current bank statement: " + unprocessedCount);
        if (unprocessedCount == 0) {
            // нет новых и/или необработанных записей - смена статуса на «Обработан»
            logger.debug("Changing bank statement status...");
            result = bankStatementMakeTrans(params, B2B_BANKSTATE_PROCESSED, login, password);
            //logger.debug("result = bankStatementMakeTrans(params, B2B_BANKSTATE_PROCESSED, login, password);");
        } else {
            // есть новые и/или необработанные записи - статус не меняется
            //result = bankStatementMakeTrans(params, B2B_BANKSTATE_?, " ", login, password);
            logger.debug("Bank statement skipped.");
        }

        logger.debug("Bank statement single record finalization finished.");
        return result;

    }

    private Long bankCashFlowTemplateSelect(Map<String, Object> bankCashFlow, String login, String password) throws Exception {

        logger.debug("Checking current template for this bank cash flow...");

        // проверка текущего шаблона - если уже задан (например, при редактировании детализации платежа пользователем в интерфейсе), то автоматический выбор шаблона пропускается
        Long currentTemplateID = getLongParam(bankCashFlow, "BANKSTATETEMPLATEID");
        logger.debug("Bank cash flow current template id (BANKSTATETEMPLATEID): " + currentTemplateID);
        if ((currentTemplateID == null) || (currentTemplateID == 0L)) {
            logger.debug("Bank cash flow current template is not set - automatic template selecting will be performed.");
        } else {
            logger.debug("Bank cash flow current template is already set - automatic template selecting skipped.");
            return currentTemplateID;
        }

        logger.debug("Template selecting...");

        // todo: загрузка списка шаблонов по расчетному счету получателя из движения денежных средств
        logger.debug("Browsing templates by account...");
        String checkingAccount = getStringParam(bankCashFlow.get("RECIPIENTRSACCOUNT"));
        logger.debug("Аccount: " + checkingAccount);
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("CHECKINGACCOUNT", checkingAccount);
        templateParams.put("ISNOTUSE", 0L);

        Map<String, Object> templatesCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateTemplateBrowseListByParam", templateParams, login, password);
        List<Map<String, Object>> templates = WsUtils.getListFromResultMap(templatesCallResult);
        int templatesCount = templates.size();
        logger.debug(String.format("Found %d templates for this account.", templatesCount));

        Long chosenTemplateID = null;
        if (templatesCount > 0) {
            // выбор из полученных по расчетному счету получателя шаблонов подходящих к детализации назначения платежа

            // Назначение платежа - чтение из поля "PURPOSE"
            //String purposeLine = getStringParam(bankCashFlow.get("PURPOSE"));            
            //String[] purpose = purposeLine.split(";");
            // Назначение платежа - чтение из БД (правильнее, но более ресуроемкая операция)
            Long bankCashFlowID = getLongParam(bankCashFlow.get("BANKCASHFLOWID"));
            String[] purpose = getPurposeArrayByBankCashFlowID(bankCashFlowID, login, password);

            for (Map<String, Object> template : templates) {

                chosenTemplateID = getLongParam(template.get("BANKSTATETEMPLATEID"));
                logger.debug(String.format("Checking template with id (BANKSTATETEMPLATEID) = %d...", chosenTemplateID));

                Map<String, Object> conditionsParams = new HashMap<String, Object>();
                conditionsParams.put("BANKSTATETEMPLATEID", chosenTemplateID);
                Map<String, Object> conditionsCallResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankStateTemplateConditionBrowseListByParam", conditionsParams, login, password);
                List<Map<String, Object>> conditions = WsUtils.getListFromResultMap(conditionsCallResult);

                for (Map<String, Object> condition : conditions) {
                    int blockNum = getLongParam(condition.get("NUM")).intValue();
                    String blockValue = getStringParam(condition.get("VALUE"));
                    logger.debug(String.format("Checking condition - payment purpose block number %d contains value '%s'...", blockNum, blockValue));
                    int blockNumInArray = blockNum - 1;
                    if (blockNumInArray < purpose.length) {
                        String bankCashFlowPurposeBlockValue = purpose[blockNumInArray];
                        if (!bankCashFlowPurposeBlockValue.equals(blockValue)) {
                            chosenTemplateID = null;
                            logger.debug(String.format("Condition is not satisfied - payment purpose block contains '%s' instead.", bankCashFlowPurposeBlockValue));
                        } else {
                            logger.debug("Condition is satisfied.");
                        }
                    } else {
                        chosenTemplateID = null;
                        logger.debug(String.format("Condition is not satisfied - where is no block number %d in payment purpose.", blockNum));
                    }
                }

                if (chosenTemplateID != null) {
                    logger.debug(String.format("Selected template with id (BANKSTATETEMPLATEID) = %d.", chosenTemplateID));
                    break;
                }

            }

            // если шаблон выбран - обновление движения денежных средств (установка выбранного шаблона)
            if (chosenTemplateID != null) {
                Map<String, Object> updateParams = new HashMap<String, Object>();
                //Long bankCashFlowID = getLongParam(bankCashFlow.get("BANKCASHFLOWID"));
                updateParams.put("BANKCASHFLOWID", bankCashFlowID);
                updateParams.put("BANKSTATETEMPLATEID", chosenTemplateID);
                // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
                this.callService(/*B2BPOSWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsB2BBankCashFlowUpdate", updateParams, login, password);
            } else {
                logger.debug("No template selected.");
            }
        }

        logger.debug("Template select finished.");

        return chosenTemplateID;

    }

    private List<Map<String, Object>> doMassCreateBankPurposeDetailQuery(Map<String, Object> params) throws Exception {

        int[] insertResult = this.insertQuery("dsB2BBankPurposeDetailMassCreate", params);

        if (logger.isDebugEnabled()) {
            int totalSavedRows = 0;
            for (int i : insertResult) {
                totalSavedRows = totalSavedRows + i;
            }
            logger.debug("Rows created in DB: " + totalSavedRows);
        }

        return (List<Map<String, Object>>) params.get("rows");
    }

    // сохранение списка элементов детализации назначения платежа в таблицу детализации назначения
    private List<Map<String, Object>> massCreateBankPurposeDetail(List<Map<String, Object>> detailsList, Long bankCashFlowID) throws Exception {

        // генерация идентификаторов записей для массового создания
        ExternalService externalService = this.getExternalService();
        for (Map<String, Object> detailRecord : detailsList) {
            Long generatedBankPurposeDetailID = getLongParam(externalService.getNewId("B2B_BANKPURPOSEDETAIL"));
            detailRecord.put("BANKPURPOSEDETAILID", generatedBankPurposeDetailID);
            logger.debug("BANKPURPOSEDETAILID (generated) = " + generatedBankPurposeDetailID);
            logger.debug("NUM = " + getLongParam(detailRecord.get("NUM")));
            logger.debug("VALUE = " + getStringParam(detailRecord.get("VALUE")));
        }

        // формирование параметров для сохранения списка элементов детализации назначения платежа в таблицу детализации назначения
        Map<String, Object> massCreateParams = new HashMap<String, Object>();
        massCreateParams.put("rows", detailsList);
        massCreateParams.put("totalCount", detailsList.size());
        massCreateParams.put("BANKCASHFLOWID", bankCashFlowID);

        // выполнение запроса
        List<Map<String, Object>> massCreateResult = doMassCreateBankPurposeDetailQuery(massCreateParams);
        logger.debug("massCreateResult = " + massCreateResult);

        return massCreateResult;
    }

    private void createBankPurposeDetails(Long bankCashFlowID, String bankCashFlowPurpose, String login, String password) throws Exception {

        List<Map<String, Object>> detailsList = new ArrayList<Map<String, Object>>();

        String[] bankCashFlowPurposeArray = bankCashFlowPurpose.split(";");

        int num = 0;
        for (int i = 0; i < bankCashFlowPurposeArray.length; i++) {
            num = i + 1; // согласно ФТ нумерация элементов в назначении платежа идет с 1
            String value = bankCashFlowPurposeArray[i];
            Map<String, Object> detail = new HashMap<String, Object>();
            detail.put("NUM", num);
            detail.put("VALUE", value);
            detailsList.add(detail);
        }

        // check last attr
        if (num > 0) {
            if (bankCashFlowPurpose.charAt(bankCashFlowPurpose.length() - 1) == ';') {
                Map<String, Object> detail = new HashMap<String, Object>();
                detail.put("NUM", num + 1);
                detail.put("VALUE", "");
                detailsList.add(detail);
            }
        }

        massCreateBankPurposeDetail(detailsList, bankCashFlowID);

    }

    //<editor-fold defaultstate="collapsed" desc="!только для отладки!">
    // !только для отладки!
    //@WsMethod(requiredParams = {})
    //public Map<String, Object> dsB2BTestAttachContractInPA(Map<String, Object> params) throws Exception {
    //    
    //    String login = params.get(WsConstants.LOGIN).toString();
    //    String password = params.get(WsConstants.PASSWORD).toString();
    //    
    //    params.put(RETURN_AS_HASH_MAP, true);
    //    Map<String, Object> contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);
    //    
    //    logger.debug("Testing attaching contract to PA user account...\n");
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    try {
    //        result = attachContractInPA(contract, login, password);
    //    } catch (Mort900Exception ex) {
    //        logger.debug(ex.getRussianMessage());
    //        logger.debug(ex.getMessage());
    //        logger.debug(ex);
    //        result.put("EXCEPTIONSTR", ex.getMessage());
    //        result.put("EXCEPTIONSTRRUS", ex.getRussianMessage());
    //    } catch (Exception ex)  {
    //        logger.debug(ex);
    //        result.put("EXCEPTIONSTR", ex.getLocalizedMessage());
    //    }
    //    
    //    return result;
    //    
    //}
    //</editor-fold>
    // прикрепление договора в ЛК (с автоматической регистрацией пользователя при необходимости)
    private Map<String, Object> attachContractInPA(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("Attaching created contract to PA user account...");

        // определение идентификатора и номера прикрепляемого договора
        Long contractID = getLongParam(contract, "CONTRID");
        logger.debug("Created contract id (CONTRID) = " + contractID);
        String contractNumber = getStringParam(contract, "CONTRNUMBER");
        logger.debug("Created contract number (CONTRNUMBER) = " + contractNumber);
        if (contractID == null) {
            // идентификатор прикрепляемого договора не найден
            throw new Mort900Exception(
                    "Не удалось определить ИД созданного договора (вероятно, в ходе созания договора возникла неизвестная ошибка)",
                    "Can't found contract id (probably, this caused by unknown error during contract creation)"
            );
        }

        // определение сведений о страхователе из сохраненного черновика договора
        String phoneNumber = "";
        String eMail = "";
        String name = "";
        String surname = "";
        Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
        if (insurer != null) {
            // определение номера мобильного телефона и адреса почты из сохраненного черновика договора
            List<Map<String, Object>> insurerContacts = (List<Map<String, Object>>) insurer.get("contactList");
            if (insurerContacts != null) {
                for (Map<String, Object> insurerContact : insurerContacts) {
                    String contactSysName = getStringParam(insurerContact, "CONTACTTYPESYSNAME");
                    if ("MobilePhone".equals(contactSysName)) {
                        phoneNumber = getStringParam(insurerContact, "VALUE");
                        logger.debug("Insurer's mobile phone number (required for contract attachment to PA user account) from created contract: " + phoneNumber);
                    } else if ("PersonalEmail".equals(contactSysName)) {
                        eMail = getStringParam(insurerContact, "VALUE");
                        if ((null != eMail) && (eMail.equalsIgnoreCase("N"))) {
                            eMail = "";
                        }
                        logger.debug("Insurer's e-mail (required for PA user creation) from created contract: " + eMail);
                    }
                }
            }
            // определение имени и фамилии из сохраненного черновика договора
            name = getStringParam(insurer, "FIRSTNAME");
            logger.debug("Insurer's first name (required for PA user creation) from created contract: " + name);
            surname = getStringParam(insurer, "LASTNAME");
            logger.debug("Insurer's last name (required for PA user creation) from created contract: " + surname);
        }

        if (phoneNumber.isEmpty()) {
            // номера мобильного телефона в сохраненном черновике договора не найден
            throw new Mort900Exception(
                    "В созданном договоре не удалось найти номер мобильного телефона страхователя (требующийся для прикрепления договора в Личном кабинете)",
                    "Insurer's mobile phone number (required for contract attachment to PA user account) not found in created contract"
            );
        }

        // поиск или создание зарегистрированного пользователя
        logger.debug("Searching (or creating if necessary) PA user...");
        Map<String, Object> userParams = new HashMap<String, Object>();
        userParams.put("PHONENUMBER", phoneNumber);
        userParams.put("SURNAME", surname);
        userParams.put("NAME", name);
        //eMail = ""; //!только для отладки!
        userParams.put("EMAIL", eMail);
        logger.debug("PA user info: ");
        if (logger.isDebugEnabled()) {
            for (Map.Entry<String, Object> userParam : userParams.entrySet()) {
                String key = userParam.getKey();
                Object value = userParam.getValue();
                logger.debug("   " + key + " = " + value);
            }
        }
        userParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> paUser = this.callService(PAWS_SERVICE_NAME, "dsPAFindOrCreateRegisteredUser", userParams, login, password);

        if ((paUser == null)) {
            // не удалось найти/создать пользователя
            throw new Mort900Exception(
                    String.format("Не удалось найти (или создать) пользователя Личного кабинета по полученным из созданного черновика договора сведениям (телефон: %s, фамилия: %s, имя: %s, почта: %s)",
                            phoneNumber, surname, name, eMail),
                    String.format("Can't find or create PA user by info from created contract draft (phone: %s, surname: %s, name: %s, e-mail: %s)",
                            phoneNumber, surname, name, eMail)
            );
        } else if ((paUser.get("CREATIONERROR") != null)) {
            // не удалось найти пользователя, а для создания нового не хватило обязательных атрибутов
            throw new Mort900Exception(
                    String.format("Невозможно создать нового пользователя Личного кабинета (телефон: %s, фамилия: %s, имя: %s, почта: %s) для прикрепления созданного договора - предоставлены не все данные пользователя",
                            phoneNumber, surname, name, eMail),
                    String.format("Can't create new PA user (phone: %s, surname: %s, name: %s, e-mail: %s) for created contract attachment - not all requiried user info supplied",
                            phoneNumber, surname, name, eMail)
            );
        } else if (paUser.get("USERID") == null) {
            // не удалось найти/создать пользователя
            throw new Mort900Exception(
                    String.format("Не удалось найти (или создать) пользователя Личного кабинета по полученным из созданного черновика договора сведениям (телефон: %s, фамилия: %s, имя: %s, почта: %s)",
                            phoneNumber, surname, name, eMail),
                    String.format("Can't find or create PA user by info from created contract draft (phone: %s, surname: %s, name: %s, e-mail: %s)",
                            phoneNumber, surname, name, eMail)
            );
        }

        Long paUserID = getLongParam(paUser, "USERID");
        if (logger.isDebugEnabled()) {
            boolean isCreatedNow = getBooleanParam(paUser, "ISCREATEDNOW", false);
            logger.debug(String.format("%s PA user with id (USERID) = %d.", (isCreatedNow ? "Created new" : "Found existing"), paUserID));
        }

        // проверка прикрепляемого договора - не прикреплен ли уже (маловероятно, но проверить)
        logger.debug("Checking if attached contract is already attached...");
        Map<String, Object> paContractParams = new HashMap<String, Object>();
        paContractParams.put("CONTRID", contractID);
        List<Map<String, Object>> attachedContracts = WsUtils.getListFromResultMap(this.callService(PAWS_SERVICE_NAME, "dsPaContractBrowseListByParam", paContractParams, login, password));
        logger.debug("Already attached contracts links list: " + attachedContracts);
        if ((attachedContracts != null) && (!attachedContracts.isEmpty())) {
            // прикрепляемый договора уже прикреплен
            throw new Mort900Exception(
                    String.format("Договор c номером '%s' и ИД '%d' уже включен в состав текущего или другого аккаунта",
                            contractNumber, contractID),
                    String.format("Contract with number '%s' and id '%d' already attached to this or another account",
                            contractNumber, contractID)
            );
        }
        logger.debug("Created contract is not already attached.");

        // прикрепление договора
        logger.debug("Attaching contract...");
        Map<String, Object> attachParams = new HashMap<String, Object>();
        attachParams.put("CONTRID", contractID);
        attachParams.put("CURRENT_USERID", paUserID);
        String smsText = CONTRACT_ATTACHMENT_SMS_TEMPLATE; // todo: формирование текста СМС (возможно, включение в него сведений о созданном договоре?)
        attachParams.put("SMSTEXT", smsText);
        attachParams.put("SMSPHONENUMBER", phoneNumber);
        attachParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> attachResult = this.callService(PAWS_SERVICE_NAME, "dsPAContractValidAndAttachContract", attachParams, login, password);
        logger.debug("Contract attached with result: " + attachResult);
        if ((attachResult == null) || (attachResult.get("PAOBJECTID") == null)) {
            // не удалось прикрепить договор
            throw new Mort900Exception(
                    String.format("Не удалось прикрепить договор c номером '%s' и ИД '%d' к выбранной учетной записи пользователя ЛК (телефон: %s, фамилия: %s, имя: %s, почта: %s, ИД учетной записи: %d)",
                            contractNumber, contractID, phoneNumber, surname, name, eMail, paUserID),
                    String.format("Can't attach contract with number '%s' and id '%d' to selected PA user account (phone: %s, surname: %s, name: %s, e-mail: %s, account id: %d)",
                            contractNumber, contractID, phoneNumber, surname, name, eMail, paUserID)
            );
        }
        logger.debug("Attaching created contract to PA user account finished.");

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("PAUSER", paUser);
        result.put("PACONTRACT", attachResult);

        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankPurposeDetailSave(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        if (params.get("PURPOSELIST") != null) {
            List<Map<String, Object>> purposeDetailList = (List<Map<String, Object>>) params.get("PURPOSELIST");
            logger.debug("dsB2BBankPurposeDetailSave begin");
            for (Map<String, Object> purposeDetailMap : purposeDetailList) {
                Map<String, Object> saveParam = new HashMap<String, Object>();
                Map<String, Object> saveRes = new HashMap<String, Object>();
                if (purposeDetailMap.get("BANKPURPOSEDETAILID") == null) {
                    if ((purposeDetailMap.get("VALUE") == null) || (purposeDetailMap.get("VALUE").toString().isEmpty())) {
                        //none
                        logger.debug("none");

                    } else {
                        //create
                        logger.debug("create: " + saveParam.toString());
                        saveParam.put("BANKCASHFLOWID", purposeDetailMap.get("BANKCASHFLOWID"));
                        saveParam.put("VALUE", purposeDetailMap.get("VALUE"));
                        saveParam.put("NUM", purposeDetailMap.get("NUM"));
                        saveRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankPurposeDetailCreate", saveParam, login, password);
                    }
                } else if ((purposeDetailMap.get("VALUE") == null) || (purposeDetailMap.get("VALUE").toString().isEmpty())) {
                    //remove
                    logger.debug("remove: " + saveParam.toString());
                    saveParam.put("BANKPURPOSEDETAILID", purposeDetailMap.get("BANKPURPOSEDETAILID"));
                    saveRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankPurposeDetailDelete", saveParam, login, password);
                } else {
                    logger.debug("update: " + saveParam.toString());
                    saveParam.put("BANKPURPOSEDETAILID", purposeDetailMap.get("BANKPURPOSEDETAILID"));
                    saveParam.put("BANKCASHFLOWID", purposeDetailMap.get("BANKCASHFLOWID"));
                    saveParam.put("VALUE", purposeDetailMap.get("VALUE"));
                    saveParam.put("NUM", purposeDetailMap.get("NUM"));
                    //update
                    saveRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankPurposeDetailUpdate", saveParam, login, password);
                }
                logger.debug("res: " + saveRes.toString());

            }
        }
        logger.debug("dsB2BBankPurposeDetailSave end");

        return result;
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWID", "TEMPLATEMETHODNAME", "STATESYSNAME", "PURPOSE"})
    public Map<String, Object> dsB2BMort900BankCashFlowsProcessSingleRecord2(Map<String, Object> params) throws Exception {

        logger.debug("Start bank cash flow single record processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {

            result = doBankCashFlowsProcessSingleRecord2(params, login, password);

        } catch (Exception ex) {

            String exLocalizedMessage = ex.getLocalizedMessage();
            logger.warn("Catched exception: " + exLocalizedMessage);
            logger.error("Catched exception call stack: ", ex); // !только для отладки!
            logger.error("Catched exception cause call stack: ", ex.getCause()); // !только для отладки!
            result.put("EXCEPTION", exLocalizedMessage);

            // получение текста ошибки для сохранения в БД
            String errorMessage = getRussianMessageFromException(ex);
            logger.debug("Catched exception info for saving in DB: " + errorMessage);

            // откат текущей транзакции и создание взамен новой
            String transactionError = rollbackCurrentAndBeginNewTransaction();

            // если были проблемы при работе с транзакциями - дополнение описания ошибки для сохранения в БД упоминанием о транзакциях
            if (transactionError != null) {
                errorMessage = String.format("Ошибка при откате изменений: %s. Причина выполнения отката - %s.", transactionError, errorMessage);
                logger.debug("Catched exception info plus transaction error description for saving in DB: " + errorMessage);
            }
            result.put("EXCEPTIONTEXT", errorMessage);

            // перевод статуса в обработанный с ошибкой
            Map<String, Object> transResult = tryTransBankCashFlowToErrorByExternalCall(params, errorMessage, login, password);
            result.putAll(transResult);

        }

        logger.debug("Bank cash flow single record processing finished.");

        return result;

    }

    @WsMethod(requiredParams = {"BANKCASHFLOWLIST"})
    public Map<String, Object> dsB2BMort900BankCashFlowsProcessSingleRecord3(Map<String, Object> params) throws Exception {

        logger.debug("Start bank cash flow list processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {

            result = doBankCashFlowsProcessSingleRecord3(params, login, password);

        } catch (Exception ex) {

            String exLocalizedMessage = ex.getLocalizedMessage();
            logger.warn("Catched exception: " + exLocalizedMessage);
            logger.error("Catched exception call stack: ", ex); // !только для отладки!
            logger.error("Catched exception cause call stack: ", ex.getCause()); // !только для отладки!
            result.put("EXCEPTION", exLocalizedMessage);
            /*
            // получение текста ошибки для сохранения в БД
            String errorMessage = getRussianMessageFromException(ex);
            logger.debug("Catched exception info for saving in DB: " + errorMessage);

            // откат текущей транзакции и создание взамен новой
            String transactionError = rollbackCurrentAndBeginNewTransaction();

            // если были проблемы при работе с транзакциями - дополнение описания ошибки для сохранения в БД упоминанием о транзакциях
            if (transactionError != null) {
                errorMessage = String.format("Ошибка при откате изменений: %s. Причина выполнения отката - %s.", transactionError, errorMessage);
                logger.debug("Catched exception info plus transaction error description for saving in DB: " + errorMessage);
            }
            result.put("EXCEPTIONTEXT", errorMessage);

            // перевод статуса в обработанный с ошибкой
            Map<String, Object> transResult = tryTransBankCashFlowToErrorByExternalCall(params, errorMessage, login, password);
            result.putAll(transResult);
             */
        }

        logger.debug("Bank cash flow single record processing finished.");

        return result;

    }

    private Map<String, Object> doBankCashFlowsProcessSingleRecord2(Map<String, Object> cashFlow, String login, String password) throws Exception {

        Map<String, Object> result;

        // Назначение платежа
        // чтение из поля "PURPOSE"
        //String purposeLine = getStringParam(params.get("PURPOSE"));
        //String[] purpose = purposeLine.split(";");
        // чтение из БД - правильнее, но более ресуроемкая операция
        Long bankCashFlowID = getLongParam(cashFlow.get("BANKCASHFLOWID"));
        List<Map<String, Object>> purposeDetails = getPurposeDetailsByBankCashFlowID(bankCashFlowID, login, password);

        // получение из БД списка объектов учета «Структура назначения платежа банковской выписки»
        Long bankStatementTemplateID = getLongParam(cashFlow.get("BANKSTATETEMPLATEID"));
        // запрос из шаблона обработки банковской выписки списка деталей назначения платежа
        List<Map<String, Object>> purposeTemplateDetails = getPurposeTemplateDetailsByBankStatementTemplateID(bankStatementTemplateID, login, password);

        String templateMethodName = getStringParam(cashFlow.get("TEMPLATEMETHODNAME"));
        logger.debug("Method name from template: " + templateMethodName);

        Map<String, Object> processParams = new HashMap<String, Object>();
        processParams.put("BANKPURPOSEDETAILLIST", purposeDetails);
        processParams.put("BANKSTATETEMPLATEPURPOSEDETAILLIST", purposeTemplateDetails);
        // основные данные банковской выписки
        processParams.put("BANKCASHFLOW", cashFlow);
        processParams.put(RETURN_AS_HASH_MAP, true);
        try {
            Map<String, Object> savedata = this.callService(THIS_SERVICE_NAME, templateMethodName, processParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении данных: " + ex.getLocalizedMessage(),
                    "Data creating error: " + ex.getMessage(),
                    ex
            );
        }

        result = bankCashFlowMakeTrans(cashFlow, B2B_BANKCASHFLOW_PROCESSED, " ", login, password);

        return result;
    }

    private Map<String, Object> doBankCashFlowsProcessSingleRecord3(Map<String, Object> params, String login, String password) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("BANKCASHFLOWLIST");
        if ((cashFlows == null) || (cashFlows.isEmpty())) {
            return result;
        }
        Map<String, Object> cashFlow0 = cashFlows.get(0);
        Long bankStatementTemplateID = getLongParam(cashFlow0.get("BANKSTATETEMPLATEID"));
        String templateMethodName = getStringParam(cashFlow0.get("TEMPLATEMETHODNAME"));
        logger.debug("Method name from template: " + templateMethodName);

        for (Map<String, Object> cashFlow : cashFlows) {
            Long bankCashFlowID = getLongParam(cashFlow.get("BANKCASHFLOWID"));
            List<Map<String, Object>> purposeDetails = getPurposeDetailsByBankCashFlowID(bankCashFlowID, login, password);
            // запрос из шаблона обработки банковской выписки списка деталей назначения платежа
            List<Map<String, Object>> purposeTemplateDetails = getPurposeTemplateDetailsByBankStatementTemplateID(bankStatementTemplateID, login, password);
            cashFlow.put("BANKPURPOSEDETAILLIST", purposeDetails);
            cashFlow.put("BANKSTATETEMPLATEPURPOSEDETAILLIST", purposeTemplateDetails);
        }

        Map<String, Object> processParams = new HashMap<String, Object>();
        processParams.put("BANKCASHFLOWLIST", cashFlows);
        processParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> savedata;
        try {
            //"dsB2BLoadInvestMassProcess"
            savedata = this.callService(THIS_SERVICE_NAME, templateMethodName, processParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении данных: " + ex.getLocalizedMessage(),
                    "Data creating error: " + ex.getMessage(),
                    ex
            );
        }
        // сохранение обработанных cashflow
        if (savedata != null) {
            doBankCashFlowsProcessResult(savedata, login, password);
        }
        result.put("BANKCASHFLOWLIST", savedata);
        return result;
    }

    private void doBankCashFlowsProcessResult(Map<String, Object> params, String login, String password) throws Exception {
        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("BANKCASHFLOWLIST");
        if ((cashFlows == null) || (cashFlows.isEmpty())) {
            return;
        }
        for (Map<String, Object> cashFlow : cashFlows) {
            cashFlow.put("OBJID", cashFlow.get("BANKCASHFLOWID"));
            cashFlow.put("TYPEID", B2B_BANKCASHFLOW_TYPEID);
            if (cashFlow.get("ErrorText") != null) {
                cashFlow.put("STATEID", B2B_BANKCASHFLOW_ERROR_ID);
                cashFlow.put("STATENAME", B2B_BANKCASHFLOW_ERROR);
                cashFlow.put("ERRORTEXT", cashFlow.get("ErrorText"));
            } else {
                cashFlow.put("STATEID", B2B_BANKCASHFLOW_PROCESSED_ID);
                cashFlow.put("STATENAME", B2B_BANKCASHFLOW_PROCESSED);
            }
        }

        Map<String, Object> massParams = new HashMap<String, Object>();
        massParams.put("rows", cashFlows);
        massParams.put("totalCount", cashFlows.size());
        int[] sqlResult;
        sqlResult = this.insertQuery("dsB2BStateMassSave", massParams);
        sqlResult = this.insertQuery("dsB2BBankCashFlowMassSave", massParams);
    }

    // сохранение cashflow
    private List<Map<String, Object>> massCreateBankCashFlow(List<Map<String, Object>> cashFlowList,
            Long bankStatementID, Long templateId, Long userAccountId) throws Exception {
        // генерация идентификаторов записей для массового создания
        ExternalService externalService = this.getExternalService();
        for (Map<String, Object> item : cashFlowList) {
            Long cashFlowID = getLongParam(externalService.getNewId("B2B_BANKCASHFLOW"));
            item.put("BANKCASHFLOWID", cashFlowID);
        }

        BigDecimal createDate = XMLUtil.convertDateToBigDecimal(new Date());
        // формирование параметров для сохранения списка элементов детализации назначения платежа в таблицу детализации назначения
        Map<String, Object> massCreateParams = new HashMap<String, Object>();
        massCreateParams.put("rows", cashFlowList);
        massCreateParams.put("totalCount", cashFlowList.size());
        massCreateParams.put("BANKSTATEMENTID", bankStatementID);
        massCreateParams.put("BANKSTATETEMPLATEID", templateId);
        if (templateId != null) {
            massCreateParams.put("STATEID", B2B_BANKCASHFLOW_INQUEUE_ID);
        } else {
            massCreateParams.put("STATEID", B2B_BANKCASHFLOW_NEW_ID);
        }
        massCreateParams.put("CREATEDATE", createDate);
        massCreateParams.put("CREATEUSERID", userAccountId);

        // выполнение запроса
        List<Map<String, Object>> massCreateResult = doMassCreateBankCashFlowQuery(massCreateParams);
        logger.debug("massCreateResult = " + massCreateResult);

        return massCreateResult;
    }

    private List<Map<String, Object>> doMassCreateBankCashFlowQuery(Map<String, Object> params) throws Exception {

        int[] insertResult = this.insertQuery("dsB2BBankCashFlowMassCreate", params);

        if (logger.isDebugEnabled()) {
            int totalSavedRows = 0;
            for (int i : insertResult) {
                totalSavedRows = totalSavedRows + i;
            }
            logger.debug("Rows created in DB: " + totalSavedRows);
        }

        return (List<Map<String, Object>>) params.get("rows");
    }

    @WsMethod(requiredParams = {"CASHFLOWLIST", "BANKSTATEMENTID"})
    public Map<String, Object> dsB2BMort900CashFlowBatchCreate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("CASHFLOWLIST");
        Long bankStatementID = getLongParam(params.get("BANKSTATEMENTID"));
        Long templateId = getLongParam(params.get("BANKSTATETEMPLATEID"));
        Long userAccountId = getLongParam(params.get("SESSION_USERACCOUNTID"));

        // Сохранение cashflow
        massCreateBankCashFlow(cashFlows, bankStatementID, templateId, userAccountId);
        // Сохранение состояний
        massCreateObjStateCashFlow(cashFlows, templateId, login);
        //Сохранение детализации cashflow
        createBankPurposeBatch(cashFlows);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CASHFLOWLIST", cashFlows);
        return result;
    }

    private void createBankPurposeBatch(List<Map<String, Object>> cashFlowList) throws Exception {

        List<Map<String, Object>> detailsList = new ArrayList<Map<String, Object>>();
        ExternalService externalService = this.getExternalService();
        Long generatedBankPurposeDetailID;

        for (Map<String, Object> item : cashFlowList) {

            Long cashFlowId = getLongParam(item.get("BANKCASHFLOWID"));
            String bankCashFlowPurpose = getStringParam(item.get("description"));
            String[] bankCashFlowPurposeArray = bankCashFlowPurpose.split(";");

            int num = 0;
            for (int i = 0; i < bankCashFlowPurposeArray.length; i++) {
                num = i + 1; // согласно ФТ нумерация элементов в назначении платежа идет с 1
                String value = bankCashFlowPurposeArray[i];
                generatedBankPurposeDetailID = getLongParam(externalService.getNewId("B2B_BANKPURPOSEDETAIL"));
                Map<String, Object> detail = new HashMap<String, Object>();
                detail.put("BANKPURPOSEDETAILID", generatedBankPurposeDetailID);
                detail.put("BANKCASHFLOWID", cashFlowId);
                detail.put("NUM", num);
                detail.put("VALUE", value);
                detailsList.add(detail);
            }

            // check last attr
            if (num > 0) {
                if (bankCashFlowPurpose.charAt(bankCashFlowPurpose.length() - 1) == ';') {
                    generatedBankPurposeDetailID = getLongParam(externalService.getNewId("B2B_BANKPURPOSEDETAIL"));
                    Map<String, Object> detail = new HashMap<String, Object>();
                    detail.put("BANKPURPOSEDETAILID", generatedBankPurposeDetailID);
                    detail.put("BANKCASHFLOWID", cashFlowId);
                    detail.put("NUM", num + 1);
                    detail.put("VALUE", "");
                    detailsList.add(detail);
                }
            }
        }

        // формирование параметров для сохранения списка элементов детализации назначения платежа в таблицу детализации назначения
        Map<String, Object> massCreateParams = new HashMap<String, Object>();
        massCreateParams.put("rows", detailsList);
        massCreateParams.put("totalCount", detailsList.size());

        // выполнение запроса
        doMassCreateBankPurposeBatchQuery(massCreateParams);
    }

    private List<Map<String, Object>> doMassCreateBankPurposeBatchQuery(Map<String, Object> params) throws Exception {

        int[] insertResult = this.insertQuery("dsB2BBankPurposeDetailBatchCreate", params);

        if (logger.isDebugEnabled()) {
            int totalSavedRows = 0;
            for (int i : insertResult) {
                totalSavedRows = totalSavedRows + i;
            }
            logger.debug("Rows created in DB: " + totalSavedRows);
        }

        return (List<Map<String, Object>>) params.get("rows");
    }

    // сохранение состояний cashflow
    private List<Map<String, Object>> massCreateObjStateCashFlow(List<Map<String, Object>> cashFlowList,
            Long templateId, String userName) throws Exception {
        // генерация идентификаторов записей для массового создания
        ExternalService externalService = this.getExternalService();
        for (Map<String, Object> item : cashFlowList) {
            Long stateID = getLongParam(externalService.getNewId("INS_OBJSTATE"));
            item.put("ID", stateID);
            item.put("OBJID", item.get("BANKCASHFLOWID"));
        }

        BigDecimal startDate = XMLUtil.convertDateToBigDecimal(new Date());
        // формирование параметров для сохранения списка элементов детализации назначения платежа в таблицу детализации назначения
        Map<String, Object> massCreateParams = new HashMap<String, Object>();
        massCreateParams.put("rows", cashFlowList);
        massCreateParams.put("totalCount", cashFlowList.size());
        if (templateId != null) {
            massCreateParams.put("STATEID", B2B_BANKCASHFLOW_INQUEUE_ID);
            massCreateParams.put("STATENAME", B2B_BANKCASHFLOW_INQUEUE);
        } else {
            massCreateParams.put("STATEID", B2B_BANKCASHFLOW_NEW_ID);
            massCreateParams.put("STATENAME", B2B_BANKCASHFLOW_NEW);
        }
        massCreateParams.put("TYPEID", B2B_BANKCASHFLOW_TYPEID);
        massCreateParams.put("TYPENAME", B2B_BANKCASHFLOW_TYPENAME);
        massCreateParams.put("STARTDATE", startDate);
        massCreateParams.put("USERNAME", userName);

        // выполнение запроса
        List<Map<String, Object>> massCreateResult = doMassCreateBankCashFlowStateQuery(massCreateParams);
        logger.debug("massStateCreateResult = " + massCreateResult);

        return massCreateResult;
    }

    private List<Map<String, Object>> doMassCreateBankCashFlowStateQuery(Map<String, Object> params) throws Exception {

        int[] insertResult = this.insertQuery("dsB2BStateBatchCreate", params);

        if (logger.isDebugEnabled()) {
            int totalSavedRows = 0;
            for (int i : insertResult) {
                totalSavedRows = totalSavedRows + i;
            }
            logger.debug("Rows created in DB: " + totalSavedRows);
        }

        return (List<Map<String, Object>>) params.get("rows");
    }

}
