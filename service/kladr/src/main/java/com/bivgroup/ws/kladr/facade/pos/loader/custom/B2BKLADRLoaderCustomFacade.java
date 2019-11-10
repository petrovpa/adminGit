/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.kladr.facade.pos.loader.custom;

import static com.bivgroup.ws.kladr.system.Constants.*;
import com.bivgroup.ws.kladr.facade.B2BKLADRBaseFacade;
import com.bivgroup.ws.kladr.system.Constants;
import com.bivgroup.ws.kladr.system.DatesParser;
import com.bivgroup.ws.kladr.system.KLADRLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.apache.cayenne.access.DataContext;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
@BOName("B2BKLADRCustom")
public class B2BKLADRLoaderCustomFacade extends B2BKLADRBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из kladr
    private static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;

    private static DatesParser datesParser;

    // флаг подробного протоколирования операций с датами и переопределения параметров
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    // флаг подробного протоколирования вызовов методов веб-сервисов
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_CALLS_LOGGING = logger.isDebugEnabled();

    private static volatile KLADRLoader kladrLoader;

    private static final Object kladrLoaderSync = new Object();
    private static volatile Thread loadThread = null;
    private static volatile int mainImportMethodThreadCount = 0;
    private boolean paused = false;

    private static final String STATUS = "Status";
    private static final String STATUS_OK = "OK";

    //private static final String REFWS_CONFIG = "refws";
    private static final String KLADR_PATH_CORE_SETTING_NAME = "KLADR_FOLDER"; // старое имя параметра, не изменено для сохранения совместимости
    private static final String KLADR_PATH_CONFIG_SETTING_NAME = "kladrFolderPath"; // старое имя параметра, не изменено для сохранения совместимости

    private static final String KLADR_URL_SETTING_NAME_PRIMARY = "KLADR_URL"; // 
    private static final String KLADR_URL_SETTING_NAME_SECONDARY = "kladrURL"; // 
    private static final String KLADR_URL_DEFAULT = "http://www.gnivc.ru/html/gnivcsoft/KLADR/Base.7z"; //
    private static final String KLADR_ARCHIVE_FILE_NAME_DEFAULT = "Base.7z"; //

    private static final int HTTP_RESPONSE_CODE_OK = 200;

    private final SimpleDateFormat commonDateWithMillies = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    private static final String FORCED_DOWNLOAD_PARAM_NAME = "FORCEDOWNLOAD";
    private static final String FORCED_IMPORT_PARAM_NAME = "FORCEIMPORT";

    public B2BKLADRLoaderCustomFacade() {
        super();
        init();
    }

    private void init() {

        logger.debug("B2BKLADRLoaderCustomFacade initialization...");

        // обработчик дат
        datesParser = new DatesParser();
        // обработчик дат - протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);

        logger.debug("B2BKLADRLoaderCustomFacade initialization finished.\n\n");

    }

    /**
     * Деструктор, вызывается при остановке сервиса
     */
    @PreDestroy
    public void myDestroy() {
        if (loadThread != null && loadThread.isAlive()) {
            loadThread.stop();
        }
    }

    private String getSettingValueFromDBOrConfig(String... settingNames) {

        String settingValue = "";

        StringBuilder settingNamesListSB = new StringBuilder();
        for (String settingName : settingNames) {
            settingNamesListSB.append("'").append(settingName).append("', ");
        }
        settingNamesListSB.setLength(settingNamesListSB.length() - 2);
        String settingNamesListStr = settingNamesListSB.toString();

        logger.debug(String.format("Getting from DB (CORE_SETTING) first value for any of settings with names %s...", settingNamesListStr));
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("SETTINGSYSNAMESLIST", settingNamesListStr);
        try {
            Map<String, Object> queryResult = this.selectQuery("getSysSettingBySysName", queryParams);
            logger.debug("getSysSettingBySysName = " + queryResult);
            Map<String, Object> firstFoundSetting = WsUtils.getFirstItemFromResultMap(queryResult);
            if (firstFoundSetting != null) {
                settingValue = getStringParam(firstFoundSetting.get("SETTINGVALUE"));
                logger.debug(String.format("Value: %s", settingValue));
            } else {
                logger.debug("No setting values found in DB.");
            }
        } catch (Exception ex) {
            logger.error("Error while quering settings from DB - configuration file only will be used. Exception details:", ex);
        }

        if (settingValue.isEmpty()) {
            for (String settingName : settingNames) {
                logger.debug(String.format("Getting value of setting '%s' from configuration file...", settingName));
                settingValue = Config.getConfig(THIS_SERVICE_NAME).getParam(settingName, "");
                if (settingValue.isEmpty()) {
                    logger.debug("No setting value found in configuration file.");
                } else {
                    logger.debug(String.format("Value: %s", settingValue));
                    break;
                }
            }
        }

        return settingValue;

    }

    private Map<String, Object> makeSkipResult(String skipText) {
        Map<String, Object> skipResult = new HashMap<String, Object>();
        skipResult.put(RESULT, "ОК");
        skipResult.put("STATUS", "SKIPPED");
        skipResult.put("MESSAGE", skipText);
        logger.info(skipText + ".");
        return skipResult;
    }

    private Map<String, Object> makeErrorResult(String errorText, Exception ex) {
        Map<String, Object> errorResult = new HashMap<String, Object>();
        errorResult.put(RESULT, "ERROR");
        errorResult.put("STATUS", "ERROR");
        errorResult.put("MESSAGE", errorText);
        if (ex == null) {
            logger.error(errorText + "!");
        } else {
            logger.error(errorText + "! Exception details:", ex);
            errorResult.put("EXCEPTION", ex);
        }
        return errorResult;
    }

    private Map<String, Object> makeErrorResult(String errorText) {
        return makeErrorResult(errorText, null);
    }

    //public Map<String, Object> getmassoperprotocol(DefaultedHashMap<String, Object> params) throws Exception {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKLADRImportGetProtocol(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        //result.put(XMLUtil.RET_RESULT, reportStage.getProtocol());
        result.put(XMLUtil.RET_RESULT, kladrLoader.getProtocol());
        return result;
    }

    //public Map<String, Object> progress_status(DefaultedHashMap<String, Object> params) throws Exception {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKLADRImportGetStatus(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        KLADRLoader loader = kladrLoader;
        Thread tmp = loadThread;
        if (tmp != null && !tmp.isAlive()) {
            tmp = null;
            loader = null;
        }
        if (loader == null) {
            result.put("RESULT", "OK");
            result.put("STATUS", "WAIT");
            result.put("STATUSTEXT", /*Messages.getString("REFWSIMPL.WaitStatus", getLocale()));//*/ "WAIT");
            result.put("PERCENTS", 0);
            result.put("RECORDS", 0);
        } else if (this.paused) {
            result.put("RESULT", "OK");
            result.put("STATUS", "PAUSE");
            result.put("STATUSTEXT", /*Messages.getString("REFWSIMPL.PauseStatus", getLocale()));//*/ "PAUSE");
            result.put("PERCENTS", loader.getProgress());
            result.put("RECORDS", loader.getLoadCounter());
        } else {
            result.put("RESULT", "OK");
            result.put("STATUS", "LOADING");
            result.put("STATUSTEXT", /*Messages.getString("REFWSIMPL.LoadingStatus", getLocale()));//*/ "LOADING");
            result.put("PERCENTS", loader.getProgress());
            result.put("RECORDS", loader.getLoadCounter());
        }
        return result;
    }

    /**
     * Прерывание процесса загрузки
     *
     * @param params
     *
     * @return return<UL>
     * </UL>
     *
     * @throws Exception the exception
     */
    //public Map<String, Object> breakmassoper(DefaultedHashMap<String, Object> params) throws Exception {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKLADRImportBreak(Map<String, Object> params) throws Exception {
        logger.info("KLADR: Method dsB2BKLADRImportBreak started...");
        KLADRLoader loader = kladrLoader;
        if (loader != null) {
            loader.cancel();
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, "OK");
        result.put(STATUS, STATUS_OK);
        logger.info("KLADR: Method dsB2BKLADRImportBreak finished.");
        return result;
    }

    /**
     * Приостановление процесса загрузки
     *
     * @param params
     *
     * @return return<UL>
     * </UL>
     *
     * @throws Exception the exception
     */
    //public Map<String, Object> pausemassoper(DefaultedHashMap<String, Object> params) throws Exception {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKLADRImportPause(Map<String, Object> params) throws Exception {
        logger.info("KLADR: Method loadkladr suspended...");
        if (this.loadThread != null && loadThread.isAlive()) {
            this.loadThread.suspend();
            this.paused = true;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, "OK");
        result.put(STATUS, STATUS_OK);
        return result;
    }

    /**
     * Возобновление процесса загрузки
     * <p/>
     * Запускает процесс загрузки, если он был ранее приостановлен.
     *
     * @param params
     *
     * @return return<UL>
     * </UL>
     *
     * @throws Exception the exception
     */
    //public Map<String, Object> resumemassoper(DefaultedHashMap<String, Object> params) throws Exception {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKLADRImportResume(Map<String, Object> params) throws Exception {
        logger.info("KLADR: Method loadkladr resumed...");
        if (this.loadThread != null && loadThread.isAlive()) {
            this.loadThread.resume();
            this.paused = false;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, "OK");
        result.put(STATUS, STATUS_OK);
        return result;
    }

    /**
     * Загрузка адресного классификатора
     * <p/>
     * В случае если загрузка еще не запущена, создает новый поток для загрузки адресов
     * из DBF-файлов. Файлы должны находится в каталоге, указанном в файле *-config.xml
     * kladrFolderPath или в БД в таблице CORE_SETTING с ключем KLADR_FOLDER (будет приоритетней)
     *
     * @param params
     *
     * @return return<UL>
     * </UL>
     *
     * @throws Exception the exception
     */
    //public Map<String, Object> loadkladr(DefaultedHashMap<String, Object> params) throws Exception {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKLADRImportStart(Map<String, Object> params) throws Exception {
        logger.info("KLADR: Method dsB2BKLADRImportStart started...");
        Map<String, Object> result;
        if (mainImportMethodThreadCount == 0) {
            mainImportMethodThreadCount = 1;
            try {
                if (loadThread != null) {
                    if (!loadThread.isAlive()) {
                        loadThread = null;
                        kladrLoader = null;
                    }
                }
                if (loadThread != null) {
                    result = makeErrorResult("KLADR: DBF files import thread already running");
                } else {
                    result = doB2BKLADRImportStart(params);
                }
            } finally {
                mainImportMethodThreadCount = 0;
            }
        } else {
            result = makeErrorResult("KLADR: main import method (doB2BKLADRImportStart) already running");
        }

        logger.debug("KLADR: Method dsB2BKLADRImportStart result: " + result);
        logger.info("KLADR: Method dsB2BKLADRImportStart finished.");
        return result;
    }

    private Map<String, Object> doB2BKLADRImportStart(Map<String, Object> params) throws Exception {

        logger.debug("KLADR: main import method (doB2BKLADRImportStart) started...");
        
        Map<String, Object> result = new HashMap<String, Object>();

        // временный объект для хранения результатов работы методов
        Object checkResult;

        // флаги принудительных операций, управляемых параметрами при вызове метода
        boolean isDownloadForced = getBooleanParam(params, FORCED_DOWNLOAD_PARAM_NAME, false);
        boolean isImportForced = getBooleanParam(params, FORCED_IMPORT_PARAM_NAME, false);

        String kladrFolder = getSettingValueFromDBOrConfig(KLADR_PATH_CORE_SETTING_NAME, KLADR_PATH_CONFIG_SETTING_NAME);
        if (kladrFolder.isEmpty()) {
            return makeErrorResult("Local KLADR files folder not specified");
        }
        kladrFolder = (kladrFolder.endsWith("\\") || kladrFolder.endsWith("/")) ? kladrFolder : kladrFolder + "\\";
        logger.debug(String.format("Selected local KLADR files folder - '%s'.", kladrFolder));

        // Определение полного адреса файла архива со сведениями КЛАДР
        String kladrArchiveURL = getKLADRArchiveURL();

        logger.debug("Processing choosen KLADR archive URL...");
        // формирование URL
        URL url;
        try {
            url = new URL(kladrArchiveURL);
        } catch (Exception ex) {
            return makeErrorResult(String.format("Error in choosen KLADR archive URL (%s)", kladrArchiveURL), ex);
        }
        // Определение имени файла архива со сведениями КЛАДР
        String kladrArchiveFileName = getArchiveFileNameFromURL(kladrArchiveURL);
        logger.debug("Processing KLADR archive choosen URL finished.");

        // дата архива (the number of milliseconds since 01.10.1970 GMT.)
        Long remoteArchiveDateMillies;
        // проверка соединения и получение даты архива
        checkResult = checkUrlConnectionAndGetArchiveDate(url);
        if (checkResult instanceof Long) {
            remoteArchiveDateMillies = (Long) checkResult;
        } else {
            return (Map<String, Object>) checkResult;
        }

        // флаг необходимости выполнения загрузки архива с сайта
        boolean isDownloadNeeded;
        // полное имя локального файла с архивом
        String localArchiveFullName = kladrFolder + kladrArchiveFileName;
        // проверка необходимости выполнения загрузки архива с сайта
        checkResult = checkIsDownloadNeeded(localArchiveFullName, remoteArchiveDateMillies, isDownloadForced, isImportForced);
        // проверка возвращает флаг (если проверка выполнена и определена необходимость загрузки) или мапу с результатом вызова ds-метода (если необходимо немедленное завершение ds-метода)
        if (checkResult instanceof Boolean) {
            isDownloadNeeded = (Boolean) checkResult;
        } else {
            return (Map<String, Object>) checkResult;
        }

        if (isDownloadNeeded) {

            logger.debug("Downloading KLADR archive file...");
            String downloadedFileName = localArchiveFullName;
            File downloadedFile = new File(downloadedFileName);
            try {
                FileUtils.copyURLToFile(url, downloadedFile);
            } catch (Exception ex) {
                return makeErrorResult(String.format("Error while downloading KLADR archive file from URL '%s' to location '%s'", kladrArchiveURL, downloadedFileName), ex);
            }
            if (!downloadedFile.exists()) {
                return makeErrorResult(String.format("Unknown error accessing downloaded KLADR archive file from URL '%s' to location '%s'", kladrArchiveURL, downloadedFileName));
            }
            if (remoteArchiveDateMillies != 0) {
                downloadedFile.setLastModified(remoteArchiveDateMillies);
            }
            logger.debug("Downloading KLADR archive file finished.");

        }

        boolean isImportingNeeded;
        if (isImportForced) {
            // если передан параметр принудительного импорта сведений КЛАДР - то результаты проверок дат и наличия локального архива, а также флаг загрузки файла не учитываются
            isImportingNeeded = isImportForced;
            logger.warn(String.format("Forced import was activated by call parameter ('%s') - KLADR data importing will be executed unconditionally.", FORCED_IMPORT_PARAM_NAME));
        } else {
            // если параметр принудительного импорта сведений КЛАДР не указан - то импорт выполняется только когда загружен свежий архив
            isImportingNeeded = isDownloadNeeded;
        }

        if (isImportingNeeded) {

            // проверка необходимости выполнения распаковки 7z-архива
            // возвращает только флаг необходимости выполнения распаковки и не может приводить к немедленному завершению вызова ds-метода
            boolean isUnpackingNeeded = checkIsUnpackingNeeded(kladrFolder, isDownloadNeeded);

            if (isUnpackingNeeded) {

                // распаковка 7z-архива и проверка (имен по списку) DBF файлов
                Map<String, Object> exitResult = extractAndCheckDBFFiles(kladrFolder, localArchiveFullName);
                // возрващает мапу с результатом вызова ds-метода (если необходимо немедленное завершение вызова ds-метода) или null (если немедленное завершение вызова ds-метода не требуется)
                if ((exitResult != null) && (!exitResult.isEmpty())) {
                    return exitResult;
                }

            }

            // запуск отдельного потока для импорта сведений КЛАДР из DBF-файлов
            result = startKLADRLoadingThread(params, kladrFolder);
        }

        logger.debug("KLADR: main import method (doB2BKLADRImportStart) finished.");
        
        return result;

    }

    // Определение полного адреса файла архива со сведениями КЛАДР
    private String getKLADRArchiveURL() {
        String kladrArchiveURL = getSettingValueFromDBOrConfig(KLADR_URL_SETTING_NAME_PRIMARY, KLADR_URL_SETTING_NAME_SECONDARY);
        if (kladrArchiveURL.isEmpty()) {
            kladrArchiveURL = KLADR_URL_DEFAULT;
            logger.debug(String.format("No KLADR archive URL found in settings, default URL (%s) will be used.", kladrArchiveURL));
        } else {
            logger.debug(String.format("KLADR archive URL found in settings - '%s'.", kladrArchiveURL));
        }
        return kladrArchiveURL;
    }

    // проверка соединения и получение даты архива
    // возвращает флаг (если проверка выполнена и определена необходимость загрузки) или мапу с результатом вызова ds-метода (если необходимо немедленное завершение вызова ds-метода)
    private Object checkUrlConnectionAndGetArchiveDate(URL url) {
        Long remoteArchiveDateMillies;

        logger.debug("Connecting to KLADR archive URL...");
        String kladrArchiveURL = url.toString();
        HttpURLConnection httpConnection;
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            if (httpConnection == null) {
                return makeErrorResult(String.format("Unknown error while connecting to KLADR archive URL (%s)", kladrArchiveURL));
            }
            int responseCode = httpConnection.getResponseCode();
            if (responseCode != HTTP_RESPONSE_CODE_OK) {
                String responseMessage = httpConnection.getResponseMessage();
                return makeErrorResult(String.format("Error while checking connection to KLADR archive URL (%s) - recieved response with code %d and message '%s'", kladrArchiveURL, responseCode, responseMessage));
            }
        } catch (Exception ex) {
            return makeErrorResult(String.format("Error while connecting to KLADR archive URL (%s)", kladrArchiveURL), ex);
        }
        logger.debug("Connecting to KLADR archive URL finished.");

        logger.debug("Getting last-modified information for KLADR archive file on this URL...");
        remoteArchiveDateMillies = httpConnection.getLastModified(); // The result is the number of milliseconds since January 1, 1970 GMT.

        if (remoteArchiveDateMillies == 0) {
            // не определена дата архива на сайте ФГУП ГНИВЦ ФНС - файл будет скачиваться, только если отсутствует локальный
            logger.debug("No last-modified information for KLADR archive file was found on this URL. Downloading/importing will be executed only if local KLADR archive file is absent.");
        } else {
            // определена дата архива на сайте ФГУП ГНИВЦ ФНС - будет выполнено сравнение дат с локальным файлом (если он присутствует)
            Date remoteArchiveDate = new Date(remoteArchiveDateMillies);
            String remoteArchiveDateStr = commonDateWithMillies.format(remoteArchiveDate);
            logger.debug(String.format("Last-modified information for KLADR archive file on this URL: %s (%d milliseconds since 01.01.1970 GMT)", remoteArchiveDateStr, remoteArchiveDateMillies));
        }
        httpConnection.disconnect();
        //logger.debug("Getting last-modified information for KLADR archive file on this URL finished.");

        return remoteArchiveDateMillies;
    }

    // Определение имени файла архива со сведениями КЛАДР
    private String getArchiveFileNameFromURL(String kladrArchiveURL) {
        String kladrArchiveFileName = "";
        int fileNameIndex = kladrArchiveURL.lastIndexOf("/") + 1;
        if (fileNameIndex > 0) {
            kladrArchiveFileName = kladrArchiveURL.substring(fileNameIndex);
            logger.debug(String.format("Founded KLADR archive file name in full URL string - '%s'.", kladrArchiveFileName));
        }
        if (kladrArchiveFileName.isEmpty()) {
            kladrArchiveFileName = KLADR_ARCHIVE_FILE_NAME_DEFAULT;
            logger.debug(String.format("KLADR archive file name not found in full URL string, default file name ('%s') will be used.", kladrArchiveFileName));
        }
        return kladrArchiveFileName;
    }

    // проверка необходимости выполнения распаковки 7z-архива
    // возвращает только флаг необходимости выполнения распаковки и не может приводить к немедленному завершению вызова ds-метода
    private boolean checkIsUnpackingNeeded(String kladrFolderPath, boolean isDownloadingNeeded) {
        boolean isUnpackingNeeded;
        if (isDownloadingNeeded) {
            // если требовалась загрузка файла - то распаковка обязательна
            isUnpackingNeeded = true;
        } else {
            // если не требовалась загрузка файла - то распаковка выполняется только если не найдены нужные DBF
            isUnpackingNeeded = false;
            logger.debug(String.format("Checking requiried local DBF files (%s) at location '%s'...", REQUIRIED_DBF_FILES_NAMES_LIST_STR, kladrFolderPath));
            for (String requiriedDBFFileName : REQUIRIED_DBF_FILES_NAMES_LIST) {
                logger.debug(String.format("Checking requiried local KLADR DBF file '%s'...", requiriedDBFFileName));
                File requiriedDBFFile = new File(kladrFolderPath + requiriedDBFFileName);
                if (requiriedDBFFile.exists()) {
                    logger.debug(String.format("Requiried DBF file '%s' is present.", requiriedDBFFileName));
                } else {
                    logger.debug(String.format("Requiried DBF file '%s' is absent, futher checking skipped.", requiriedDBFFileName));
                    isUnpackingNeeded = true;
                    break;
                }
            }
            if (isUnpackingNeeded) {
                logger.debug("Some of requiried local DBF files are absent - unpacking will be executed.");
            } else {
                logger.debug("All requiried local DBF files are exist - no unpacking will be executed.");
            }
        }
        return isUnpackingNeeded;
    }

    // распаковка 7z-архива и проверка (имен по списку) DBF файлов
    // возрващает мапу с результатом вызова ds-метода (если необходимо немедленное завершение вызова ds-метода) или null (если немедленное завершение вызова ds-метода не требуется)
    private Map<String, Object> extractAndCheckDBFFiles(String kladrFolder, String localArchiveFullName) {
        Map<String, Object> result = null;

        logger.debug("Unpacking KLADR 7z archive file...");

        // проверка на 'org.tukaani.xz' по первому классу, вызывающему исключение при создании объекта SevenZFile
        try {
            Class.forName("org.tukaani.xz.FilterOptions");
        } catch (Exception ex) {
            // 'org.tukaani.xz' не найден (возможно, не включен в WAR файл)
            return makeErrorResult("Checking dependencies error - missing package 'org.tukaani.xz' (requiried by 'org.apache.commons.compress.archivers.sevenz' to unpacking 7z archives)", ex);
        }

        Map<String, Boolean> requiriedDBFFilesNamesCheckList = new HashMap<String, Boolean>();
        for (String requiriedDBFFileName : REQUIRIED_DBF_FILES_NAMES_LIST) {
            requiriedDBFFilesNamesCheckList.put(requiriedDBFFileName, false);
        }

        File localArchiveFile = new File(localArchiveFullName);
        try {
            SevenZFile sevenZFile = new SevenZFile(localArchiveFile);
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                String extractedFileName = entry.getName();
                if (requiriedDBFFilesNamesCheckList.get(extractedFileName) != null) {
                    logger.debug(String.format("Extracting file '%s' to location '%s'...", extractedFileName, kladrFolder));
                    FileOutputStream out = new FileOutputStream(kladrFolder + extractedFileName); // todo: проверить
                    byte[] content = new byte[(int) entry.getSize()];
                    logger.debug("Reading content of file from archive...");
                    sevenZFile.read(content, 0, content.length);
                    logger.debug("Reading content of file from archive is finished.");
                    logger.debug("Writing readed content to file...");
                    out.write(content);
                    out.close();
                    logger.debug("Writing readed content to file is finished, file closed.");
                    logger.debug("File extracted.");
                    requiriedDBFFilesNamesCheckList.put(extractedFileName, true);
                } else {
                    logger.debug(String.format("Presented in archive file '%s' do not supported by KLADR import, this file extraction skipped.", extractedFileName));
                }
                entry = sevenZFile.getNextEntry();
            }
            sevenZFile.close();
        } catch (Exception ex) {
            return makeErrorResult(String.format("Error while trying to unpack 7z archive file located at '%s'", localArchiveFullName), ex);
        }
        logger.debug("Unpacking KLADR 7z archive file finished.");

        logger.debug("Checking unpacked DBF files list...");
        // проверка списка извлеченных файлов
        for (Map.Entry<String, Boolean> requiriedDBFFilesNamesCheck : requiriedDBFFilesNamesCheckList.entrySet()) {
            String fileName = requiriedDBFFilesNamesCheck.getKey();
            Boolean isExtracted = requiriedDBFFilesNamesCheck.getValue();
            if (!isExtracted) {
                return makeErrorResult(String.format("At least one DBF file ('%s') from list of requiried for KLADR import files (%s) is not found at location %s", fileName, fileName, kladrFolder));
            }
        }
        logger.debug("Checking unpacked DBF files list finished - all requiried for KLADR import files was presented in archive and extracted.");

        return result;
    }

    // проверка необходимости выполнения загрузки архива с сайта
    // возвращает флаг (если проверка выполнена и определена необходимость загрузки) или мапу с результатом вызова ds-метода (если необходимо немедленное завершение вызова ds-метода)
    private Object checkIsDownloadNeeded(String localArchiveFullName, long remoteArchiveDateMillies, boolean isDownloadForced, boolean isImportForced) {
        Boolean isDownloadNeeded;
        //String localArchiveFullName = kladrFolderPath + kladrArchiveFileName;
        //File localArchiveFile = null;
        if (isDownloadForced) {
            // если передан параметр принудительной загрузки архива с сайта ФГУП ГНИВЦ ФНС, то проверки дат и наличия локального архива не выполняются
            logger.warn(String.format("Forced downloading was activated by call parameter ('%s') - remote KLADR archive file downloading and data importing will be executed unconditionally.", FORCED_DOWNLOAD_PARAM_NAME));
            isDownloadNeeded = isDownloadForced;
        } else {
            // проверка дат - закачивать и загружать КЛАДР необходимо только если обновился файл с архивом на сайте ФГУП ГНИВЦ ФНС (или отсутствует локальный файл)
            isDownloadNeeded = false;
            logger.debug(String.format("Checking local KLADR archive file ('%s')...", localArchiveFullName));
            File localArchiveFile = new File(localArchiveFullName);
            if (localArchiveFile.exists()) {
                if (remoteArchiveDateMillies != 0) {
                    long localArchiveDateMillies = localArchiveFile.lastModified();
                    Date localArchiveDate = new Date(localArchiveDateMillies);
                    String localArchiveDateStr = commonDateWithMillies.format(localArchiveDate);
                    logger.debug(String.format("Last-modified information for this local KLADR archive file: %s (%d milliseconds since 01.01.1970 GMT)", localArchiveDateStr, localArchiveDateMillies));

                    if (remoteArchiveDateMillies > localArchiveDateMillies) {
                        isDownloadNeeded = true;
                        logger.info("Remote KLADR archive file is newer then local, downloading/importing will be executed.");
                    } else if (isImportForced) {
                        logger.info("Local KLADR archive file is up to date, no downloading needed.");
                    } else {
                        return makeSkipResult("Local KLADR archive file is up to date, no downloading/importing needed");
                    }
                } else if (isImportForced) {
                    logger.info("Remote KLADR archive file date information not found, but local KLADR archive file is exist - no downloading will be executed.");
                } else {
                    return makeSkipResult("Remote KLADR archive file date information not found, but local KLADR archive file is exist - no downloading/importing will be executed");
                }
            } else {
                isDownloadNeeded = true;
                logger.info("No local KLADR archive file found, downloading/importing will be executed.");
            }
        }
        return isDownloadNeeded;
    }

    // запуск отдельного потока для импорта сведений КЛАДР из DBF-файлов
    private Map<String, Object> startKLADRLoadingThread(Map<String, Object> params, String kladrFolder) {
        Map<String, Object> result = new HashMap<String, Object>();
        logger.info("Trying to start KLADR loading thread...");
        synchronized (kladrLoaderSync) {
            if (loadThread != null) {
                if (!loadThread.isAlive()) {
                    loadThread = null;
                    kladrLoader = null;
                }
            }
            if (loadThread == null) {

                // логин и пароль для выполнения вызовов ds-методов сервисов в ходе загрузки КЛАДР
                String login = params.get(WsConstants.LOGIN).toString();
                String password = params.get(WsConstants.PASSWORD).toString();
                
                DataContext dc;
                try {
                    dc = getDataContext();
                } catch (Exception ex) {
                    return makeErrorResult("Error in getting data context for KLADR loader thread", ex);
                }
                DataSource ds;
                try {
                    ds = Config.getConfig(THIS_SERVICE_NAME).getDataSource();                    
                } catch (Exception ex) {
                    return makeErrorResult("Error in getting data source for KLADR loader thread", ex);
                }

                kladrLoader = new KLADRLoader(dc, kladrFolder, login, password, ds);
                loadThread = new Thread(kladrLoader);
                loadThread.start();
                logger.info("KLADR: KLADR loading thread is started.");
                result.put(RESULT, "OK");
                result.put("STATUS", "START");
                result.put("MESSAGE", "KLADR loading thread is started");
            } else {
                logger.info("KLADR: KLADR loading thread is already started.");
                result.put(RESULT, "ERROR");
                result.put("STATUS", "ALREADY STARTED");
                result.put("MESSAGE", "KLADR loading thread is already started");
            }
        }
        return result;
    }

}
