package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.cancellation;

import com.bivgroup.services.b2bposws.facade.pos.declaration.utils.MappingHelper;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фасад формирования данных для отчета Растожения
 */
public class B2BReasonChangeDataProviderCancellationFacade extends B2BReasonChangeDataProviderCancellationCustom {
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Сервис формирования данных для изменения "Расторжение"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderCancellation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderCancellation begin");

        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        String error = "";

        // берем заявителя
        Map<String, Object> applicantMap = getMapParam(reportData, "APPLICANTMAP");
        Map<String, Object> insurerMap = getMapParam(reportData, "INSURERMAP");
        // кладем его в получателя
        Map<String, Object> recipientMap = new HashMap<>(applicantMap);

        Map<String, Object> bankDetails = getMapParam(reasonMap, "bankDetailsId_EN");
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        Map<String, Object> bankMap = MappingHelper.bankDetailsMapping2(bankDetails, isNotExistContract);
        if (bankMap.isEmpty()) {
            error = "Не удалось получить сведения о реквизитах получателя!";
        } else {
            recipientMap.put("BANKMAP", bankMap);
        }

        if (error.isEmpty()) {
            reportData.put("RECIPIENTMAP", recipientMap);
        }

        Map<String, Object> insurantMapFromReason = getMapParam(reasonMap, "insurantId_EN");

        if (insurantMapFromReason != null && !insurantMapFromReason.isEmpty()) {
            MappingHelper.requisitesMapping(applicantMap, insurantMapFromReason, isNotExistContract);
            // всегда INSURER для расторжения
            applicantMap.put("TYPESTR", "INSURER");
            MappingHelper.requisitesMapping(insurerMap, insurantMapFromReason, isNotExistContract);
            List<Map<String, Object>> clientContactList = getOrCreateListParam(insurantMapFromReason, "contacts");
            List<Map<String, Object>> contactList = resolveClientContactList(clientContactList);
            applicantMap.put("contactList", contactList);
            insurerMap.put("contactList", contactList);
        } else {
            if (!isNotExistContract) {
                error = "Отсутствует информации о застрахованном в заявлении!";
            }
        }

        String cancellationSysname = getStringParam(reasonMap, "cancellationSysname");
        String cancellationDescription = getStringParam(reasonMap, "cancellationDescription");
        Map<String, Object> reasonReportDataMap = new HashMap<>();
        reasonReportDataMap.put("TYPE", cancellationSysname);
        if (cancellationDescription != null && !cancellationDescription.isEmpty()) {
            reasonReportDataMap.put("OTHERDESCR", cancellationDescription);
        }
        reportData.put(REASON_PARAM_NAME, reasonReportDataMap);

        String program = getStringParam(reportData, "PRODPROGSYSNAME");
        Instant documentDate = getDateParam(reportData.get("DOCUMENTDATE")).toInstant();

        Long repLevel = REPLEVEL_FOR_SPECIFICATION_3;
        if (program.matches("FIRSTCAPITAL|FAMALYASSETS")) {
            // Прекращение_Первый Капитал, Семейный актив_до 11.09.2016
            Instant d2016_09_11 = at2359("2016-09-11");
            if (documentDate.isBefore(d2016_09_11)) {
                repLevel = REPLEVEL_FOR_SPECIFICATION_3_ALT;
            }
        } else if (program.matches("NPR2V1|NPR2V2|NPR2V3|NPR3")) {
            // Прекращение ДСЖ с НПР с 13.10.2014 по 31.12.2014*
            Instant d2014_10_13 = at0000("2014-10-13");
            Instant d2014_12_31 = at2359("2014-12-31");
            if (documentDate.isAfter(d2014_10_13) && documentDate.isBefore(d2014_12_31)) {
                repLevel = REPLEVEL_FOR_SPECIFICATION_3_ALT;
            }
            // Прекращение ДСЖ с ВП до 05.04.15, ДСЖ с НПР с 01.09.14 по 12.10.14 и с 01.01.15 по 05.04.15*
        } else if (program.matches("2_IND|3|4")) {
            // соглашение о расторжении АКТУАЛЬНАЯ* - прекращения по договорам Сетелем, заключенным с 30.07.2013 по 05.11.2015
            if (documentDate.isAfter(at0000("2013-07-30")) && documentDate.isBefore(at2359("2015-11-05"))) {
                repLevel = REPLEVEL_FOR_SPECIFICATION_3_ALT;
            }
            // Заявление форма от 31.05.2016 Сетелем* - прекращения по договорам Сетелем, заключенным с 06.11.2015 по 30.08.2016
            if (documentDate.isAfter(at0000("2015-11-06")) && documentDate.isBefore(at2359("2016-08-30"))) {
                repLevel = REPLEVEL_FOR_SPECIFICATION_3_ALT_2;
            }
        } else if (program.matches("MORTGAGE_CLPBTM|AUTOCREDIT_CLPBTM")) {
            // Защищенный заемщик (ПФки для партнеров)
            //#17981 fix
            repLevel = REPLEVEL_FOR_SPECIFICATION_3;
        }

        reportData.put("REPLEVEL", repLevel);

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        reportData.put("BINDOCTYPESYSNAME", "cancellation_Pf");
        reportData.put("FACTDOCDATE", isNotExistContract ? null : reportData.get("DOCUMENTDATE"));
        formingAttachDocuments(reportData, new ArrayList<>(), isNotExistContract);

        // fixme временно, до тех пор пока в ФТ не будет определен формат номера заявления
        if (reportData.get("id") != null) {
            //reportData.put("APPLNUMBER", String.valueOf(reportData.get("id")));
            reportData.put("APPLDATE", reportData.get("createDate"));
        }

        result.put(REPORT_DATA_PARAM_NAME, reportData);

        logger.debug("dsB2BChangeReasonDataProviderCancellation end");
        return result;
    }

}
