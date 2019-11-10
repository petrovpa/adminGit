/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900.facade.pos.custom;

import com.bivgroup.ws.i900.facade.Mort900BaseFacade;
import com.bivgroup.ws.i900.system.Constants;
import com.bivgroup.ws.i900.system.DatesParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;

/**
 *
 * @author kkulkov
 */
@BOName("Mort900Custom")
public class Cib900CustomFacade extends Mort900BaseFacade {

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
    private static final String SYSNAME_MORTGAGETM = "B2B_MORTGAGE_TELEMARKETING"; // Пролонгация ипотеки через ТМ

    private static DatesParser datesParser;
    // флаг подробного протоколирования операций с датами
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

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
                if (prodProgId == null) {
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
                                            contract.put("PRODPROGID", prodProgId);
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

    public Cib900CustomFacade() {
        super();
        init();
    }

    private void init() {
        datesParser = new DatesParser();
        // протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
    }

    /**
     * Метод для сохранения договора по продукту
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCib900ContractPrepareToSave(Map<String, Object> params) throws Exception {

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

        logger.debug("after dsB2BMort900ContractPrepareToSave\n");

        return result;
    }

}
