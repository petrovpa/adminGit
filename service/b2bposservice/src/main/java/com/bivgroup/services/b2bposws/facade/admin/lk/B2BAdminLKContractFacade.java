package com.bivgroup.services.b2bposws.facade.admin.lk;

import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

/**
 * Фасад для работы с договорами СБСЖ ЛК
 *
 * @author ivanovra
 */
@BOName("B2BAdminLKContract")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminLKContractFacade extends B2BAdminLKBaseFacade {

    private String getCallDetailMethod(Map<String, Object> contrMap) {
        String dsCallDetailMethod = "";
        if (contrMap.get("PRODPROGSYSNAME") != null) {
            String prodProgSysName = (String) contrMap.get("PRODPROGSYSNAME");
            if (investCapitalNotCouponProdList.contains(prodProgSysName)) {
                dsCallDetailMethod = DETAIL_FOR_INVEST_CAPITAL_NOT_COUPON_PROD;
            }
            if (investCapitalCouponProdList.contains(prodProgSysName)) {
                dsCallDetailMethod = DETAIL_FOR_INVEST_CAPITAL_COUPON_PROD;
            }
            if (accumulateOfFundsProdList.contains(prodProgSysName)) {
                dsCallDetailMethod = DETAIL_FOR_ACCUMULATE_OF_FUNDS_PROD;
            }
        }

        return dsCallDetailMethod;
    }

    /**
     * Функция для получения списка договоров
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLkBrowseContractListByParams(Map<String, Object> params) throws Exception {

        params.put("CHECKEXISTPROFRULE", true);
        params.put("LKVISIBLE", 1);
        Map<String, Object> result = selectQuery("dsAdminLkBrowseContractListByParams", params);

        return result;
    }

    /**
     * Функция формирования ДиД отчета для администратора ЛК
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "CONTRNUMBER", "PRODCONFID"})
    public Map<String, Object> dsB2BAdminLKCreateDIDReportByParams(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = null;
        String errorMessage = "Не удалось сформировать отчет!";

        // Получаем данные по договору Дид
        params.put("REPLEVEL", PROD_REP_LVL_DID);
        Map<String, Object> reportDataRes = this.callExternalService(Constants.B2BPOSWS, "dsB2BProductReportBrowseListByParamEx", params, login, password);
        Map<String, Object> reportDataMap = convertCallResListToMap(reportDataRes);

        if ((reportDataMap != null) && (reportDataMap.size() > 0)) {
            // Добавим расширение
            reportDataMap.put("REPORTFORMATS", ".pdf");

            //Подготавливаем параметры для печати
            Map<String, Object> paramsForPrint = new HashMap<>();
            paramsForPrint.put("REPORTDATA", reportDataMap);
            paramsForPrint.put("CONTRID", params.get("CONTRID"));
            paramsForPrint.put("PRODCONFID", params.get("PRODCONFID"));
            paramsForPrint.put("NEEDREPRINT", "TRUE");
            paramsForPrint.put("ISNEEDSIGN", false);

            Map<String, Object> printDocumentsRes = this.callExternalService(Constants.SIGNB2BPOSWS, "dsB2BPrintDocuments", paramsForPrint, login, password);

            if ((printDocumentsRes != null) && (printDocumentsRes.get(RESULT) != null)) {
                result = printDocumentsRes;
            }
        } else {
            errorMessage = "Данный тип продукта не поддерживает отчет по ДиД";
        }

        // Если в результате ничего не лежит, значит, что отчет не сформирован
        if (result == null) {
            result = new HashMap<>();
            result.put("STATUS", "ERROR");
            result.put(ERROR, errorMessage);
        }

        return result;
    }

    /**
     * Метод для получения полной информации по договору (включая и детальную).
     *
     * @param params принимает набор обязательных параметров.
     * @return возвращает найденные поля
     */
    @WsMethod(requiredParams = {"POLICYID", "CONTRNUMBER"})
    public Map<String, Object> dsB2BAdminLKBrowseContractFullInfoByParam(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // Возвращаемый результат
        Map<String, Object> result = null;

        // Получаем данные из интеграции
        Map<String, Object> integrationCallRes = this.callExternalService(Constants.B2BPOSWS, "dsLifeIntegrationGetContractList", params, login, password);

        if ((integrationCallRes != null) && (integrationCallRes.get(RESULT) != null)) {
            Map<String, Object> integrationMap = (Map<String, Object>) integrationCallRes.get(RESULT);

            // Интеграция возвращает не list, как все, а мапу
            if ((integrationMap != null) && (integrationMap.get("STATUS") != null)
                    && (integrationMap.get("STATUS").equals("DONE")) && (integrationMap.get("CONTRMAP") != null)) {

                Map<String, Object> contrMap = (Map<String, Object>) integrationMap.get("CONTRMAP");

                String dsCallMethod = this.getCallDetailMethod(contrMap);

                if (!dsCallMethod.isEmpty()) {

                    Map<String, Object> investDetailParams = new HashMap<>();
                    investDetailParams.put("CONTRNUMBER", params.get("CONTRNUMBER"));

                    // Выбираем данные для детальной информации по инвестиционному доходу
                    Map<String, Object> investDetailCallRes = this.callExternalService(Constants.B2BPOSWS, dsCallMethod, investDetailParams, login, password);
                    Map<String, Object> investDetailMap = convertCallResListToMap(investDetailCallRes);

                    if (investDetailMap != null) {
                        contrMap.putAll(investDetailMap);
                    }
                }

                result = new HashMap<>();
                result.put(RESULT, integrationMap);
                result.put("STATUS", "OK");
            }
        }

        if (result == null) {
            result = new HashMap<>();
            result.put(ERROR, "Не удалось получить детальную информацию для договора");
            result.put("STATUS", ERROR);
        }

        parseDates(result, String.class);

        return result;
    }

    /**
     * Дата провайдер для получения списка состояний договора в ЛК системе
     *
     * @param params
     * @return возвращает маппу доступных состояний
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLKBrowseContractStateListByParams(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<>();
        params.put("TYPEID", TYPEID_STATE_CONTRACT);
        result = selectQuery("dsAdminLKBrowseStateListByParams", params);

        return result;
    }

    /**
     * Дата провайдер для получения списка продуктов в ЛК системе
     *
     * @param params
     * @return возвращает мапу доступных продуктов
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLKBrowseProductListByParams(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = null;

        Map<String, Object> queryParams = params;
        queryParams.put("LKVISIBLE", 1);

        result = selectQuery("dsAdminLKBrowseProductListByParams", queryParams);

        if (result == null) {
            result = new HashMap<>();
            result.put("STATUS", ERROR);
            result.put(ERROR, "Не удалось получить список продуктов!!!");
        }

        return result;
    }

}
