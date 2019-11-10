package com.bivgroup.ws.i900.facade.invest;

import com.bivgroup.ws.i900.facade.Mort900BaseFacade;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("InvestFileLoad")
public class InvestFileLoad extends Mort900BaseFacade {

    private static volatile int investFileLoadProcessingThreadCount = 0;
    private static final String FOLDER_PATH = "INVESTDATA_FOLDER";
    private static final String INVEST_FOLDER_PATH = "inv";
    private static final String INVESTCOUPON_FOLDER_PATH = "invcoupon";
    private static final String INVESTDID_FOLDER_PATH = "did";
    private static final String TICKERRATE_FOLDER_PATH = "quot";

    private static final String INVEST_SYSNAME = "Invest";
    private static final String INVESTCPN_SYSNAME = "InvestCoupon";
    private static final String INVESTDID_SYSNAME = "InvestDID";
    private static final String TICKERRATE_SYSNAME = "TickerRate";

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestFileLoad(Map<String, Object> params) throws Exception {

        if (investFileLoadProcessingThreadCount == 0) {
            investFileLoadProcessingThreadCount = 1;
            try {
                logger.debug("dsB2BInvestFileLoad start");
                String login = params.get(WsConstants.LOGIN).toString();
                String password = params.get(WsConstants.PASSWORD).toString();

                // Получить доступ к upload каталогу
                String uploadPath = Config.getConfig().getParam("uploadPath", "");
                // получить путь к каталогу с инвест доходностью
                String fldInvestCommon = getCoreSettingBySysName(FOLDER_PATH, login, password);
                if ((!fldInvestCommon.isEmpty()) && (!uploadPath.isEmpty())) {
                    // invest
                    Long templateId = getTemplateIdBySysName(INVEST_SYSNAME, login, password);
                    String fldInvest = fldInvestCommon + "/" + INVEST_FOLDER_PATH;
                    if (templateId != null) {
                        doBankStatementCreate(fldInvest, uploadPath, templateId, login, password);
                    }
                    // coupon
                    templateId = getTemplateIdBySysName(INVESTCPN_SYSNAME, login, password);;
                    fldInvest = fldInvestCommon + "/" + INVESTCOUPON_FOLDER_PATH;
                    if (templateId != null) {
                        doBankStatementCreate(fldInvest, uploadPath, templateId, login, password);
                    }
                    // did
                    templateId = getTemplateIdBySysName(INVESTDID_SYSNAME, login, password);;
                    fldInvest = fldInvestCommon + "/" + INVESTDID_FOLDER_PATH;
                    if (templateId != null) {
                        doBankStatementCreate(fldInvest, uploadPath, templateId, login, password);
                    }
                    // tickerrate
                    templateId = getTemplateIdBySysName(TICKERRATE_SYSNAME, login, password);;
                    fldInvest = fldInvestCommon + "/" + TICKERRATE_FOLDER_PATH;
                    if (templateId != null) {
                        doBankStatementCreate(fldInvest, uploadPath, templateId, login, password);
                    }
                }
            } finally {
                investFileLoadProcessingThreadCount = 0;
                logger.debug("dsB2BInvestFileLoad finish\n");
            }
        } else {
            logger.debug("dsB2BInvestFileLoad already running");
        }

        return null;
    }

    private void doBankStatementCreate(String folderPath, String uploadPath, Long templateId, String login, String password) throws Exception {
        // получить все файлы из каталога
        String[] listFile = getFileNameList(folderPath);
        if (listFile == null) {
            return;
        }
        for (String fileName : listFile) {
            String newFileName = UUID.randomUUID().toString() + ".csv";
            // перенести файл в uploadPath с новым именем
            if (moveFile(folderPath + "/" + fileName, uploadPath  + "/" + newFileName)) {
                // создать выписку с файлом
                Map<String, Object> bankStateMap = new HashMap<String, Object>();
                bankStateMap.put(RETURN_AS_HASH_MAP, true);
                bankStateMap.put("TEMPLATEID", templateId);
                bankStateMap.put("FILENAME", fileName);
                bankStateMap.put("FILEPATH", newFileName);
                Map<String, Object> bankStateResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BBankStatementSingle", bankStateMap, login, password);
            }
        }
    }

    private String[] getFileNameList(String dirName) {
        File dir = new File(dirName);
        if (dir.isDirectory()) {

            String[] list = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File f, String s) {
                    return s.endsWith(".csv");
                }
            });
            if (list.length > 0) {
                return list;
            }
        }
        return null;
    }

    private boolean moveFile(String srcFilePath, String dstFilePath) {
        boolean result = false;
        try {
            Path srcPath = Paths.get(srcFilePath);
            Path dstPath = Paths.get(dstFilePath);
            Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            result = true;
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return result;
    }

    private Long getTemplateIdBySysName(String sysName, String login, String password) throws Exception {
        Long result = null;
        Map<String, Object> templMap = new HashMap<String, Object>();
        templMap.put(RETURN_AS_HASH_MAP, true);
        templMap.put("SYSNAME", sysName);
        Map<String, Object> templResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankStateTemplateBrowseListByParam", templMap, login, password);
        if ((templResult != null) && (templResult.get("BANKSTATETEMPLATEID") != null)) {
            result = Long.parseLong(templResult.get("BANKSTATETEMPLATEID").toString());
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBankStatementSingle(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // создать выписку с файлом
        Map<String, Object> bankStateMap = new HashMap<String, Object>();
        bankStateMap.put(RETURN_AS_HASH_MAP, true);
        bankStateMap.put("AUTONUMBERSYSNAME", "B2BBankStateAutoNum");
        bankStateMap.put("DOCDATE", new Date());
        bankStateMap.put("BANKSTATETEMPLATEID", params.get("TEMPLATEID"));
        Map<String, Object> bankStateResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankStateCreateEx", bankStateMap, login, password);
        if ((bankStateResult != null) && (bankStateResult.get("BANKSTATEMENTID") != null)) {
            Map<String, Object> bankStateDocMap = new HashMap<String, Object>();
            bankStateDocMap.put(RETURN_AS_HASH_MAP, true);
            bankStateDocMap.put("BANKSTATEMENTID", bankStateResult.get("BANKSTATEMENTID"));
            bankStateDocMap.put("PRODBINDOCID", 1001L);
            Map<String, Object> bankStateDocResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankStateDocumentCreate", bankStateDocMap, login, password);
            // create attach
            if ((bankStateDocResult != null) && (bankStateDocResult.get("BANKSTATEDOCID") != null)) {
                Map<String, Object> attachParams = new HashMap<String, Object>();
                attachParams.put("OBJID", bankStateDocResult.get("BANKSTATEDOCID"));
                attachParams.put("FILENAME", params.get("FILENAME"));
                attachParams.put("FILEPATH", params.get("FILEPATH"));
                Map<String, Object> attachResult = this.callService(THIS_SERVICE_NAME, "dsB2BBankStateDocument_BinaryFile_createBinaryFileInfo", attachParams, login, password);
            }
        }
        return bankStateResult;
    }
}
