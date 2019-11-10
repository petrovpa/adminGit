package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BReasonChangeDataProviderHolder")
public class B2BReasonChangeDataProviderHolderFacade extends B2BReasonChangeDataProviderPersonCustomFacade {
    private static final String NEWINSURER_PERSON_TYPE = "NEWINSURER"; // новый страхователь

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Сервис формирования данных для изменения "Страхователя"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderHolderChange(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderFinancialVacation begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(reasonReportDataMap, "CHANGEINSURER");
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        Map<String, Object> insurantPersonMap = getOrCreateMapParam(reportData, isVipSegment ? "PERSONMAP" : "CHANGEBENEFDATAMAP");
        insurantPersonMap.put("personType", NEWINSURER_PERSON_TYPE);
        Map<String, Object> insurantMap = getOrCreateMapParam(reasonMap, "insurantId_EN");
        reasonReportDataMap.put("chgInsInfo", generateFioByPersonMap(insurantMap));
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        updateReportPersonMapByClientMap(insurantPersonMap, insurantMap, isNotExistContract, login, password);
        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        String showFormStr = isVipSegment ? "FORM1IND" : "FORM2";
        showFormMap.put(showFormStr, TRUE_STR_VALUE);
        if (isVipSegment) {
            showFormMap.put("DECL", TRUE_STR_VALUE);
        }
        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);
        logger.debug("dsB2BChangeReasonDataProviderFinancialVacation end");
        return params;
    }

    private String generateFioByPersonMap(Map<String, Object> personMap) {
        StringBuilder sb = new StringBuilder();
        String lastName = getStringParam(personMap, "surname");
        if (!lastName.isEmpty()) {
            sb.append(lastName).append(' ');
        }
        String name = getStringParam(personMap, "name");
        if (!name.isEmpty()) {
            sb.append(name).append(' ');
        }
        String middleName = getStringParam(personMap, "patronymic");
        if (!middleName.isEmpty()) {
            sb.append(middleName);
        } else {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
