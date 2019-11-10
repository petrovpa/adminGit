package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import java.util.HashMap;
import java.util.Map;

public class UOAConstants {

    public static final String UNIOPENAPI_PAYMENT_METHOD_ACQUIRING = "acquiring";
    public static final String UNIOPENAPI_PAYMENT_METHOD_INVOICE = "invoice";

    /** UniOpenAPI: REPLEVEL = 111100 - полис образец */
    public static final String UNIOPENAPI_REPLEVEL_POLICY_DRAFT = "111100";
    /** UniOpenAPI: REPLEVEL = 111200 - платежка (ПД4) */
    public static final String UNIOPENAPI_REPLEVEL_INVOICE = "111200";
    /** UniOpenAPI: REPLEVEL = 111300 - полис */
    public static final String UNIOPENAPI_REPLEVEL_POLICY_SIGNED = "111300";
    /** UniOpenAPI: REPLEVEL = 111400 - заявление */
    public static final String UNIOPENAPI_REPLEVEL_APPLICATION = "111400";
    /** UniOpenAPI: REPLEVEL = 111500 - правила */
    public static final String UNIOPENAPI_REPLEVEL_RULES = "111500";
    /** UniOpenAPI: REPLEVEL = 111600 - приложения к правилам */
    public static final String UNIOPENAPI_REPLEVEL_RULES_APPENDIX = "111600";

    /** UniOpenAPI: POLICY - полис (черновик или подписанный) */
    public static final String UNIOPENAPI_FILE_TYPE_POLICY = "POLICY";
    /** UniOpenAPI: PD_4 - ПД-4 (платежка) */
    public static final String UNIOPENAPI_FILE_TYPE_PD_4 = "PD_4";
    /** UniOpenAPI: APPLICATION - заявление */
    public static final String UNIOPENAPI_FILE_TYPE_APPLICATION = "APPLICATION";

    /** ru.sberbankins.scheme.ws.base.ObjectType */
    public static final String UNIOPENAPI_RESULT_CLASS_OBJECT = "ru.sberbankins.scheme.ws.base.ObjectType";
    /** ru.sberbankins.scheme.ws.base.ObjectType.objectID */
    public static final String UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_ID_PARAMNAME = "objectID";
    /** ru.sberbankins.scheme.ws.base.ObjectType.objectNumber */
    public static final String UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_NUMBER_PARAMNAME = "objectNumber";
    /** ru.sberbankins.scheme.ws.base.ObjectType */
    public static final String UNIOPENAPI_RESULT_CLASS_CONTRACT = UNIOPENAPI_RESULT_CLASS_OBJECT;
    /** ru.sberbankins.scheme.ws.base.ObjectType.objectID */
    public static final String UNIOPENAPI_RESULT_CLASS_CONTRACT_CONTRACT_ID_PARAMNAME = UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_ID_PARAMNAME;
    /** ru.sberbankins.scheme.ws.base.ObjectType.objectNumber */
    public static final String UNIOPENAPI_RESULT_CLASS_CONTRACT_CONTRACT_NUMBER_PARAMNAME = UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_NUMBER_PARAMNAME;
    /** ru.sberbankins.scheme.ws.loader.PaymentInfoType */
    public static final String UNIOPENAPI_RESULT_CLASS_PAYMENT_STATE = "ru.sberbankins.scheme.ws.loader.PaymentInfoType";
    /** ru.sberbankins.scheme.ws.loader.FileListType */
    public static final String UNIOPENAPI_RESULT_CLASS_FILES = "ru.sberbankins.scheme.ws.loader.FileListType";

    /** Параметр ответа payState может принимать следующие значения: wait – ожидается оплата */
    public static final String UNIOPENAPI_PAY_STATE_WAIT = "WAIT";
    /** Параметр ответа payState может принимать следующие значения: success – оплата завершена успешно */
    public static final String UNIOPENAPI_PAY_STATE_SUCCESS = "SUCCESS";
    /** Параметр ответа payState может принимать следующие значения: fail – отказ в оплате */
    public static final String UNIOPENAPI_PAY_STATE_FAIL = "FAIL";

    /** UNIOPENAPI_CONTRACT_CREATE_STATE */
    public static final String UNIOPENAPI_CONTRACT_CREATE_STATE_PARAMNAME = "UNIOPENAPI_CONTRACT_CREATE_STATE";

    /** CONTRACT_CREATE_STATE_PARAMNAME */
    public static final String CONTRACT_CREATE_STATE_PARAMNAME = "CONTRCREATESTATE";

    /** Имя константы продукта (B2B_PRODDEFVAL.NAME), которая отвечает за префикс серии в номере договора при создании через OpenAPI */
    public static final String UNIOPENAPI_CONTRACT_SERIES_PREFIX_PRODDEFVAL_NAME = "UNIOPENAPI_CONTRSERIESPREFIX";

    public static final Map<String, String> UNIOPENAPI_FILE_TYPE_BY_REPLEVEL;

    static {
        UNIOPENAPI_FILE_TYPE_BY_REPLEVEL = new HashMap<>();
        UNIOPENAPI_FILE_TYPE_BY_REPLEVEL.put(UNIOPENAPI_REPLEVEL_POLICY_DRAFT, UNIOPENAPI_FILE_TYPE_POLICY);
        UNIOPENAPI_FILE_TYPE_BY_REPLEVEL.put(UNIOPENAPI_REPLEVEL_POLICY_SIGNED, UNIOPENAPI_FILE_TYPE_POLICY);
        UNIOPENAPI_FILE_TYPE_BY_REPLEVEL.put(UNIOPENAPI_REPLEVEL_INVOICE, UNIOPENAPI_FILE_TYPE_PD_4);
        UNIOPENAPI_FILE_TYPE_BY_REPLEVEL.put(UNIOPENAPI_REPLEVEL_APPLICATION, UNIOPENAPI_FILE_TYPE_APPLICATION);
    }

}
