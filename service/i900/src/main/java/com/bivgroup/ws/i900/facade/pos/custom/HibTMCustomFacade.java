/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900.facade.pos.custom;

import com.bivgroup.ws.i900.system.Constants;
import com.bivgroup.ws.i900.system.DatesParser;
import com.bivgroup.ws.i900.facade.Mort900BaseFacade;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("HibTMCustom")
public class HibTMCustomFacade extends Mort900BaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из b2bposservice на сервере подписи
    //private static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    // Имя сервиса для вызова методов из paservice
    //private static final String PAWS_SERVICE_NAME = Constants.PAWS;

    // обработчик дат
    private static DatesParser datesParser;

    // флаг подробного протоколирования операций с датами
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    public HibTMCustomFacade() {
        super();
        init();
    }

    private void init() {
        // обработчик дат
        datesParser = new DatesParser();
        // протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
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

    private Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {

        boolean isParamsChangingLogged = logger.isDebugEnabled();

        // идентификатор версии продукта всегда передается в явном виде из dsB2BContrSave
        Long prodVerID = getLongParam(contract.get("PRODVERID"));

        // идентификатор конфигурации продукта должен быть передан в явном виде из dsB2BContrSave
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            // определение идентификатора конфигурации продукта по идентификатору версии, если он не найден во входных параметрах
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
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

        // дата начала действия может быть установлена по правилу в universalProcessPaymentPurpose (в ходе анализа банковской выписки)
        GregorianCalendar startDateGC = new GregorianCalendar();
        Date startDate = getDateParam(contract.get("STARTDATE"));
        if (startDate == null) {
            // условное вычисление даты начала действия (дата начала действия не передана) - "Дата начала договора определяется как «Дата оформления» + 1 день"
            startDateGC.setTime(documentDateGC.getTime());
            startDateGC.add(Calendar.DATE, 1);
            startDate = startDateGC.getTime();
            setGeneratedParam(contract, "STARTDATE", startDate, isParamsChangingLogged);
        } else {
            // дата начала действия установлена по правилу в universalProcessPaymentPurpose (в ходе анализа банковской выписки) и передана в качестве параметра
            startDateGC.setTime(startDate);
        }
        
        // безусловное вычисление даты окончания действия - "Дата окончания договора определяется как «Дата начала» + 2 года"
        GregorianCalendar finishDateGC = new GregorianCalendar();
        finishDateGC.setTime(startDate);
        finishDateGC.add(Calendar.YEAR, 2);
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
        
        // расширенные атрибуты договора
        Map<String, Object> contractExtValues = getOrCreateContractExtMap(contract);
        
        // получение сведений о продукте
        Map<String, Object> product = getOrLoadProduct(contract, login, password);

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);
        logger.debug("CONTREXTMAP: " + contractExtValues);

        // безусловное обновление в мапе договора сведений о программе и страховой суммы по значению премии
        updateSumsAndProgramByPaymentValue(contract, IS_VERBOSE_LOGGING, login, password);

        // формирование структуры секция/тип/объект/риск с длительностью соответствующей одному (первому) оплаченному периоду
        contract = createContractFirstSection(contract, login, password);

        return contract;

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
    public Map<String, Object> dsB2BHibTMContractPrepareToSave(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BHibTMContractPrepareToSave");

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
            result = genAdditionalSaveParams(contract, login, password);
        } else {
            result = contract;
        }

        logger.debug("after dsB2BHibTMContractPrepareToSave\n");

        return result;
    }

}

