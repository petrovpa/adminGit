package com.bivgroup.services.b2bposws.facade.admin.lk;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


@BOName("B2BAdminLK")
public class B2BAdminLKBaseFacade extends B2BBaseFacade {

    protected static final String KIND_DECLARATION = "com.bivgroup.termination.KindDeclaration";
    // Типы состояний для сущностей
    protected static final Long TYPEID_STATE_CONTRACT = 2000L;
    protected static final Long TYPEID_STATE_MESSAGE = 9200L;
    // Уровни отчета
    protected static final Long PROD_REP_LVL_DID = 9010L;
    // Запросы для детальной информации
    protected static final String DETAIL_FOR_INVEST_CAPITAL_NOT_COUPON_PROD = "dsB2BInvestMaxDateBrowseListByParam"; // "Инвестирование капитала (не купонные продукты)"
    protected static final String DETAIL_FOR_INVEST_CAPITAL_COUPON_PROD = "dsB2BInvestCouponMaxDateBrowseListByParam"; // "Инвестирование капитала (купонные продукты)"
    protected static final String DETAIL_FOR_ACCUMULATE_OF_FUNDS_PROD = "dsB2BInvestDIDMaxDateBrowseListByParam"; // Накопление средств
    // Обязательные параметры
    protected static final String LOSS_NOTICE_ID_PARAMNAME = "lossNoticeId";
    protected static final String DECLARATION_ID_PARAMNAME = "DECLARATIONID";
    protected static final String URLPATH_PARAMNAME = "URLPATH";
    // Имя фасада для загрузки файлов из ins_binfile
    protected static final String LOSS_NOTICE_DOC_ATTACHMENT_FACADE_NAME = "B2BLossNoticeDoc";
    // Программы
    protected static final List<String> investCapitalNotCouponProdList = Arrays.asList(
            "SMART_POLICY",
            "LIGHTHOUSE"
    );
    protected static final List<String> investCapitalCouponProdList = Arrays.asList(
            "SMART_POLICY_ILIK",
            "SMART_POLICY_RB_ILIK"
    );
    protected static final List<String> accumulateOfFundsProdList = Arrays.asList(
            "FIRSTCAPITAL",
            "FIRSTCAPITAL_RB-FCC0",
            "FAMALYASSETS",
            "FAMALYASSETS_RB-FCC0",
            "EDUCATION_PLAN",
            "SBI-SAL_PROGRAM",
            "FAMALY_FOND",
            "LIFE_PLAN",
            "STRAGIC_INVESTOR"
    );
    protected static String KIND_CHANGEREASON = "com.bivgroup.termination.KindChangeReason";
    protected static String KEYFIELD = "sysname";
    protected static String NAMEFIELD = "name";

    /**
     * Функция конвертирования результата типа list в map (example: callRes.get(RESULT) type of List)
     *
     * @param callRes - результат вызова внешнего веб-метода
     * @return Возвращает готовую маппу
     */
    protected Map<String, Object> convertCallResListToMap(Map<String, Object> callRes) {

        Map<String, Object> result = null;

        if ((callRes != null) && (callRes.get(RESULT) != null)) {
            List<Map<String, Object>> callListRes = (List<Map<String, Object>>) callRes.get(RESULT);
            if (callListRes.size() > 0) {

                result = callListRes.get(0);
            }
        }

        return result;
    }

}
