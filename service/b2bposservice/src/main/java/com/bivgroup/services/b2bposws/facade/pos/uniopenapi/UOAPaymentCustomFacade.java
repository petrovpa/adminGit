package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import com.bivgroup.services.b2bposws.facade.pos.uniopenapi.utils.RiskGroup;
import com.bivgroup.services.b2bposws.system.SmsSender;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.bivgroup.services.b2bposws.facade.pos.uniopenapi.UOAConstants.*;

@BOName("UOAPaymentCustom")
public class UOAPaymentCustomFacade extends UOABaseFacade {

    @WsMethod(requiredParams = {"EXTERNALID", "PAYMETHOD"})
    public Map<String, Object> dsB2BPaymentRegistrationUniOpenAPI(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BPaymentRegistrationUniOpenAPI start...");
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        // текст ошибки (будет пуст, если нет ошибок)
        StringBuilder error = new StringBuilder();
        // ИД договора
        // Long contractId = getLongParamLogged(params, "CONTRID");
        // EXTERNALID -> contractId
        String externalId = getStringParamLogged(params, "EXTERNALID");
        Long contractId = getContractIdByExternalId(externalId, error, login, password);
        /*
        Map<String, Object> contract = getContractExtraBriefByExternalIdFromParams(params, error, login, password);
        Long contractId = getLongParamLogged(contract, "CONTRID");
        */

        String payMethod = getStringParamLogged(params, "PAYMETHOD");
        if (payMethod.isEmpty()) {
            error.append("Не указан тип оплаты! ");
        }

        // получение данных по договору
        Map<String, Object> contract = null;
        Long prodConfId = null;
        if (error.length() == 0) {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("CONTRID", contractId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            String serviceName = B2BPOSWS_SERVICE_NAME;
            String methodName = "dsB2BContractBrowseListByParamEx";
            contract = callServiceLogged(serviceName, methodName, contractParams, login, password);
            prodConfId = getLongParamLogged(contract, "PRODCONFID");
            String stateSysName = getStringParamLogged(contract, "STATESYSNAME");
            if ((prodConfId == null) || (stateSysName.isEmpty())) {
                loggerErrorServiceCall(serviceName, methodName, contractParams, contract);
                error.append("Не удалось получить сведения по договору! ");
            } else if (!B2B_CONTRACT_DRAFT.equals(stateSysName)) {
                error.append("По данному договору уже произведена регистрация оплаты! ");
            }
        }

        Double payment = null;
        if (error.length() == 0) {
            Map<String, Object> paymentMap = getPaymentForProduct(
                    getDoubleParam(contract, "PREMVALUE"),
                    contractId, prodConfId, login, password);
            error.append(getStringParam(paymentMap, ERROR));
            payment = getDoubleParam(paymentMap, "PAYMENT");
        }

        String urlPaySuccess = null;
        String urlPayFail = null;
        if (error.length() == 0) {
            if (UNIOPENAPI_PAYMENT_METHOD_ACQUIRING.equals(payMethod)) {
                // получение ссылок для перехода после успеха/провала оплаты
                urlPaySuccess = getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting(
                        contract, URL_PAY_SUCCESS_PARAMNAME, "", error, login, password
                );
                if (error.length() == 0) {
                    urlPayFail = getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting(
                            contract, URL_PAY_FAIL_PARAMNAME, "", error, login, password
                    );
                }
            }
        }

        if (error.length() == 0) {
            boolean makeTrans = false;
            String paymentServiceName = BIVSBERPOSWS_SERVICE_NAME;
            String paymentMethodName = "dsCallPaymentService";
            if (UNIOPENAPI_PAYMENT_METHOD_ACQUIRING.equals(payMethod)) {
                String insurerEMail = getStringParamLogged(contract, "INSUREREMAIL");
                Map<String, Object> paymentParams = new HashMap<>();
                paymentParams.put("PREMCURRENCYID", contract.get("PREMCURRENCYID"));
                paymentParams.put("PAYMENT", payment);
                paymentParams.put("PREMVALUE", contract.get("PREMVALUE"));
                paymentParams.put("EXTERNALID", externalId);
                paymentParams.put("EMAIL", insurerEMail);
                paymentParams.put(IS_SKIP_CLIENT_PROFILE_CHECK, true);
                // Тип оплаты - через внешнюю платежную систему (т. н. merchant / acquiring - https://3dsec.sberbank.ru/payment/merchants/..)
                paymentParams.put(PAYFACT_TYPE_PARAMNAME, PAYFACT_TYPE_ACQUIRING);
                // ссылки для перехода после успеха/провала оплаты
                paymentParams.put(URL_PAY_SUCCESS_PARAMNAME, urlPaySuccess);
                paymentParams.put(URL_PAY_FAIL_PARAMNAME, urlPayFail);
                //
                paymentParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> paymentServiceResult = callServiceLogged(paymentServiceName, paymentMethodName, paymentParams, login, password);
                // при ошибках со стороны платежной системы результат будет иметь вид:
                // {Status=OK, STATUS=CALLSUCCESS, payRes={FORMURL=https://sberbankins.ru&errorMessage=Отсутствует сумма, ERRORMESSAGE=Отсутствует сумма, ERRORCODE=4}, CURRVALID=true}
                logger.debug("paymentServiceResult = " + paymentServiceResult);
                Map<String, Object> payRes = getMapParamLogged(paymentServiceResult, "payRes");
                Long errorCode = getLongParamLogged(payRes, "ERRORCODE");
                String formURL = getStringParam(payRes, "FORMURL");
                if (formURL.isEmpty() || (!BOOLEAN_FLAG_LONG_VALUE_FALSE.equals(errorCode))) {
                    // ИЛИ не возвращена ссылка для перехода
                    // ИЛИ код ошибки, возвращенный платежной системой, свидетельствует о проблеме при регистрации платежа
                    loggerErrorServiceCall(paymentServiceName, paymentMethodName, paymentParams, paymentServiceResult);
                    error.append("Ошибка вызова сервиса оплаты! ");
                } else {
                    // отправка письма со ссылкой на оплату
                    String contractNumber = getStringParam(contract, "CONTRNUMBER");
                    if (!insurerEMail.isEmpty()) {
                        // todo: заменить на полноценный шаблон, когда/если клиент попросит сверстать красивое письмо
                        StringBuilder message = new StringBuilder();
                        message.append("<html>");
                        message.append("<head><meta http-equiv=\"content-type\" content=\"text/html\"/></head>");
                        message.append("<body lang=\"ru-RU\" dir=\"ltr\" style=\"background: transparent\">");
                        message.append("<p>Для оплаты договора № ");
                        message.append(contractNumber);
                        message.append(" перейдите по ссылке <a href=\"").append(formURL).append("\" target=\"_blank\">").append(formURL).append("</a>.</p>");
                        message.append("</body>");
                        message.append("</html>");
                        Map<String, Object> sendParams = new HashMap<>();
                        sendParams.put("CONTRMAP", contract);
                        sendParams.put("EMAILTEXT", message.toString());
                        sendParams.put("ADDRESSLISTSTR", insurerEMail);
                        sendParams.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> eMailSendResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BSendEMailUniOpenAPI", sendParams, login, password);
                        logger.debug("eMailSendResult = " + eMailSendResult);
                    }
                    // отправка смс со ссылкой на оплату
                    String phoneNumber = getStringParam(contract, "INSURERPHONE");
                    if (!phoneNumber.isEmpty()) {
                        String message = String.format("Для оплаты договора № %s перейдите по ссылке %s .", contractNumber, formURL);
                        SmsSender smsSender = new SmsSender();
                        Map<String, Object> smsSendResult = smsSender.sendSms(phoneNumber, message);
                        if (logger.isDebugEnabled()) {
                            loggerDebugPretty(logger, String.format(
                                    "Send sms on number '%s' with message '%s' finished with result",
                                    phoneNumber, message
                            ), smsSendResult);
                        }
                        String smsSendError = getStringParamLogged(smsSendResult, ERROR);
                        if (!smsSendError.isEmpty()) {
                            loggerErrorPretty(logger, String.format(
                                    "Send sms on number '%s' with message '%s' failed (with response text '%s')! Details (send result)",
                                    phoneNumber, message, smsSendError
                            ), smsSendResult);
                        }
                    }
                    // требуется перевод состояния
                    makeTrans = true;
                }
            } else if (UNIOPENAPI_PAYMENT_METHOD_INVOICE.equals(payMethod)) {
                // регистрация плана оплаты и черновика факта оплаты
                Map<String, Object> paymentParams = new HashMap<>();
                paymentParams.put("PREMCURRENCYID", contract.get("PREMCURRENCYID"));
                paymentParams.put("PAYMENT", payment);
                paymentParams.put("PREMVALUE", contract.get("PREMVALUE"));
                paymentParams.put("EXTERNALID", externalId);
                paymentParams.put(IS_SKIP_CLIENT_PROFILE_CHECK, true);
                // Тип оплаты - наличными / через кассу / по платежке / по ПД-4 и т.п.
                paymentParams.put(PAYFACT_TYPE_PARAMNAME, PAYFACT_TYPE_INVOICE);
                paymentParams.put(IS_SKIP_MERCHANT_CALL, true);
                paymentParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> paymentServiceResult = callServiceLogged(paymentServiceName, paymentMethodName, paymentParams, login, password);
                Map<String, Object> planPayMap = getMapParam(paymentServiceResult, "PLANPAYMAP");
                Map<String, Object> factPayMap = getMapParam(paymentServiceResult, "FACTPAYMAP");
                Long planPayId = getLongParam(planPayMap, "PAYID");
                Long factPayId = getLongParam(factPayMap, "PAYFACTID");
                if ((planPayId == null) || (factPayId == null)) {
                    loggerErrorServiceCall(paymentServiceName, paymentMethodName, paymentParams, paymentServiceResult);
                    error.append("Ошибка регистрации оплаты! ");
                } else {
                    // печать ПД-4
                    String repLevelListStr = UNIOPENAPI_REPLEVEL_INVOICE;
                    getAndPrintWithAttachingReportsByRepLevel(contractId, prodConfId, repLevelListStr, login, password);
                    // требуется перевод состояния
                    makeTrans = true;
                }
            } else {
                error.append("Указан неверный тип оплаты! ");
            }

            if (makeTrans) {
                /*
                // смена состояния договора ("Черновик" -> "Предварительная печать (Образец)")
                Map<String, Object> trans1Result = doContractMakeTrans(contract, B2B_CONTRACT_PREPRINTING, error, login, password);
                contract.putAll(trans1Result);
                // смена состояния договора ("Предварительная печать (Образец)" -> "На подписании")
                Map<String, Object> trans2Result = doContractMakeTrans(contract, B2B_CONTRACT_PREPARE, error, login, password);
                contract.putAll(trans2Result);
                */
                // последовательная смена состояния договора ("Черновик" -> "Предварительная печать (Образец)" -> "На подписании")
                List<String> stateSysNameList = Arrays.asList(B2B_CONTRACT_PREPRINTING, B2B_CONTRACT_PREPARE);
                // последовательная смена состояния договора согласно stateSysNameList
                doContractStateChanges(contract, stateSysNameList, error, login, password);
            }
        }
        // результат
        Map<String, Object> result = new HashMap<>();
        if (error.length() > 0) {
            result.put(ERROR, error.toString());
        } else {
            Map<String, Object> objectResultType = new HashMap<>();
            // todo: рассмотреть возможность отказа от хеш-код в пользу GUID из B2B_CONTR.EXTERNALID
            // String externalId = getStringParamLogged(params, "EXTERNALID");
            // objectResultType.put(UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_ID_PARAMNAME, contractId);
            objectResultType.put(UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_ID_PARAMNAME, externalId);
            result.put(UNIOPENAPI_RESULT_CLASS_OBJECT, objectResultType);
        }
        logger.debug("dsB2BPaymentRegistrationUniOpenAPI finished.");
        return result;
    }

    /**
     * Метод получени суммы платежа.
     * Если продукт многолетний, то берется сумма первого периода
     * либо из ближащей секции по договору по дате начала действия секции.
     * Либо суммируется группа рисков. Группировка происходит по
     * дате начала и окончания действия рисков.
     * Если же продукт не многолетний то сумма из договора (PREMVALUE).
     *
     * @param contractPremValue - прмеия по договору, для не многолетних продуктов
     * @param contractId        - идентификатор договора
     * @param prodConfId        - идентификатор конфигурации продукта
     * @param login             - логин для вызова сервисов
     * @param password          - пароль для вызова сервисов
     * @return "словарь", который содержит либо сумму платежа (PAYMENT), либо ошибку (Error)
     * @throws Exception
     */
    private Map<String, Object> getPaymentForProduct(Double contractPremValue, Long contractId, Long prodConfId,
                                                     String login, String password) throws Exception {
        Map<String, Object> prodDefValMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
        // в настройках продукта ищем флаг isLongTerm, чтобы узнать многолетний продукт или нет
        boolean isLongTerm = getBooleanParam(prodDefValMap, "isLongTerm", false);
        String error = "";
        Double payment = contractPremValue;
        if (isLongTerm) {
            Map<String, Object> contractSectionsLoadParams = new HashMap<>();
            contractSectionsLoadParams.put(RETURN_AS_HASH_MAP, true);
            contractSectionsLoadParams.put("PRODCONFID", prodConfId);
            contractSectionsLoadParams.put("CONTRID", contractId);
            Map<String, Object> contractSectionsLoadResult = this.callService(B2BPOSWS_SERVICE_NAME, "loadContractSectionListService",
                    contractSectionsLoadParams, login, password);
            error = getStringParam(contractSectionsLoadResult, ERROR);
            if (error.isEmpty()) {
                List<Map<String, Object>> contractSections = getListParam(contractSectionsLoadResult, "CONTRSECTIONLIST");
                Map<String, Object> section;
                if (contractSections.size() > 1) {
                    // если секций больше чем одна, то сортируем их по дате начала от меньшей к большей и берем сумму из первого элемента отсортированного списка
                    CopyUtils.sortByDateFieldName(contractSections, "STARTDATE");
                    section = contractSections.get(0);
                    payment = getDoubleParam(section, "PREMVALUE");
                } else {
                    // если секция одна, то получем список рисков и группируем их по дате начала и окончания действия риска
                    section = contractSections.get(0);
                    List<Map<String, Object>> insObjGroups = getListParam(section, "INSOBJGROUPLIST");
                    if (insObjGroups != null && !insObjGroups.isEmpty()) {
                        Map<String, Object> insObj = insObjGroups.get(0);
                        List<Map<String, Object>> objects = getListParam(insObj, "OBJLIST");
                        if (objects != null && !objects.isEmpty()) {
                            Map<String, Object> object = objects.get(0);
                            Map<String, Object> contractObjMap = getMapParam(object, "CONTROBJMAP");
                            List<Map<String, Object>> contractRisks = getListParam(contractObjMap, "CONTRRISKLIST");
                            if (contractRisks != null && !contractRisks.isEmpty()) {
                                Map<RiskGroup, Double> map = contractRisks.stream()
                                        // требуется отсортировать список рисков, чтобы вначале оказались риски с максимальной датой окончания
                                        // и минимальной датой начала, тогда максимальный диапазон окажется на самом верху
                                        .sorted((o1, o2) -> {
                                            int sComp = getDateParam(o1.get("STARTDATE")).compareTo(getDateParam(o2.get("STARTDATE")));
                                            if (sComp == 0) {
                                                sComp = getDateParam(o2.get("FINISHDATE")).compareTo(getDateParam(o1.get("FINISHDATE")));
                                            }
                                            return sComp;
                                        })
                                        // группируем и суммируем список рисков по дате начала и дате окончания риска
                                        .collect(Collectors.groupingBy(it -> new RiskGroup(getDateParam(it.get("STARTDATE")), getDateParam(it.get("FINISHDATE"))),
                                                Collectors.summingDouble(foo -> getDoubleParam(foo.get("PREMVALUE"))))
                                        );
                                // полчаем сумму платежа с минимальной датой начала действия риска. Это первый период
                                payment = map.entrySet().stream().min(Comparator.comparing(o -> o.getKey().getStartDate())).map(Map.Entry::getValue).orElse(null);
                            } else {
                                error = "Отсутствует список рисков.";
                            }
                        } else {
                            error = "Отсутствует список объектов.";
                        }
                    } else {
                        error = "Отсутствует список групп страховых объектов.";
                    }
                }
            }
        }
        if (payment == null || payment.equals(0.0)) {
            error = "Неудалось получить сумму платежа первого периода.";
        }
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result.put("PAYMENT", payment);
        } else {
            result.put(ERROR, error);
        }
        return result;
    }

    @WsMethod(requiredParams = {"PREMVALUE", CONTRID_PARAMNAME, PRODCONFID_PARAMNAME})
    public Map<String, Object> dsB2BPaymentGetPaymentForProduct(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Double contractPremValue = getDoubleParam(params, "PREMVALUE");
        Long contractId = getLongParam(params, CONTRID_PARAMNAME);
        Long prodConfId = getLongParam(params, PRODCONFID_PARAMNAME);
        Map<String, Object> result = getPaymentForProduct(
                contractPremValue, contractId, prodConfId, login, password
        );
        return result;
    }

    @WsMethod(requiredParams = {"EXTERNALID", "PAYDATE"})
    public Map<String, Object> dsB2BPaymentCompleteUniOpenAPI(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BPaymentCompleteUniOpenAPI start...");
        logger.debug("dsB2BPaymentCompleteUniOpenAPI params: " + params);

        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        // текст ошибки (будет пуст, если нет ошибок)
        StringBuilder error = new StringBuilder();
        // ИД договора
        /*
        Long contractId = getLongParamLogged(params, "CONTRID");
        if (contractId == null) {
            error.append("Не удалось получить сведения по договору! ");
        }
        */
        // EXTERNALID -> contractId
        Long contractId = getContractIdByExternalIdFromParams(params, error, login, password);
        // данные по договору
        Long contrNodeId = null;
        Long prodConfId = null;
        Map<String, Object> contract = null;
        if (error.length() == 0) {
            // получение данных по договору
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("CONTRID", contractId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contract = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contractParams, login, password);
            // String externalId = getStringParamLogged(contract, "EXTERNALID");
            contrNodeId = getLongParamLogged(contract, "CONTRNODEID");
            prodConfId = getLongParamLogged(contract, "PRODCONFID");
            String stateSysName = getStringParamLogged(contract, "STATESYSNAME");
            if (
                    !isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId) ||
                            (prodConfId == null) || (contrNodeId == null) || (stateSysName.isEmpty())
                    ) {
                error.append("Не удалось получить сведения по договору! ");
            } else if (B2B_CONTRACT_DRAFT.equals(stateSysName)) {
                error.append("По данному договору еще не была произведена регистрация оплаты! ");
            } else if (!B2B_CONTRACT_PREPARE.equals(stateSysName)) {
                error.append("По данному договору все операции по оплате уже были произведены! ");
            }
            // todo: тексты ошибок в константы
        }

        Map<String, Object> factPayment = null;
        Long factPaymentId = null;
        if (error.length() == 0) {
            // получение текущих фактических платежей (dsB2BPaymentFactBrowseListByParam)
            Map<String, Object> factPaymentParams = new HashMap<>();
            factPaymentParams.put("CONTRNODEID", contrNodeId);
            factPaymentParams.put(PAYFACT_TYPE_PARAMNAME, PAYFACT_TYPE_INVOICE);
            // factPaymentParams.put(PAYFACT_STATE_SYSNAME_PARAMNAME, PAYFACT_STATE_SYSNAME_SUCCESS);
            String methodName = "dsB2BPaymentFactBrowseListByParam";
            List<Map<String, Object>> factPaymentList = callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, methodName, factPaymentParams, login, password);
            String factPaymentError = "";
            if (factPaymentList != null) {
                if (factPaymentList.isEmpty()) {
                    factPaymentError = "По данному договору не было зарегистрировано оплаты, требующей подтверждения! ";
                } else if (factPaymentList.size() > 1) {
                    factPaymentError = "Не удалось получить однозначные сведения о регистрации оплаты по данному договору! ";
                } else {
                    factPayment = factPaymentList.get(0);
                    factPaymentId = getLongParam(factPayment, "PAYFACTID");
                }
            }
            if ((factPayment == null) || (factPaymentId == null)) {
                factPaymentError = "Не удалось получить сведения о регистрации оплаты по данному договору! ";
            }
            if (!factPaymentError.isEmpty()) {
                logger.error(String.format(
                        "Fact payment check by calling %s (with params = %s) error - '%s'! Details (list from result): %s",
                        methodName, factPaymentParams, factPaymentError, factPaymentList
                ));
                error.append(factPaymentError);
            }
        }

        if (error.length() == 0) {
            String factPaymentSysName = getStringParamLogged(factPayment, PAYFACT_STATE_SYSNAME_PARAMNAME);
            if (!PAYFACT_STATE_SYSNAME_DRAFT.equals(factPaymentSysName)) {
                error.append("Hо данному договору не обнаружено зарегистрированных платежей, требующих подтверждения! ");
            }
        }

        if (error.length() == 0) {
            // обновление платежа - параметры
            Map<String, Object> factPaymentUpdateParams = new HashMap<>();
            factPaymentUpdateParams.put("PAYFACTID", factPaymentId);
            factPaymentUpdateParams.put("PAYFACTDATE", new Date());
            factPaymentUpdateParams.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_SUCCESS);
            factPaymentUpdateParams.put("NOTE", "");
            factPaymentUpdateParams.put(RETURN_AS_HASH_MAP, true);
            /*
            // todo: уточнить необходимость заполнения NOTE данными из плана оплаты
            String paymentNoteCode = getStringParam(planPayment,"PAYID") + getStringParam(factPaymentId);
            factPaymentUpdateParams.put("NOTE", paymentNoteCode);
            */
            // обновление платежа - вызов
            String methodName = "dsB2BPaymentFactUpdate";
            Map<String, Object> factPaymentUpdateResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, methodName, factPaymentUpdateParams, login, password);
            if (!isCallResultOKAndContainsLongValue(factPaymentUpdateResult, "PAYFACTID", factPaymentId)) {
                logger.error(String.format(
                        "Unable to update fact payment by calling %s (with params = %s)! Details (call result): %s",
                        methodName, factPaymentUpdateParams, factPaymentUpdateResult
                ));
                error.append("Не удалось сохранить сведения о подтверждении платежа! ");
            }
        }

        if (error.length() == 0) {
            // смена состояния договора ("Предварительная печать (Образец)" -> "На подписании")
            /*
            Map<String, Object> trans1Result = doContractMakeTrans(contract, B2B_CONTRACT_PREPARE, error, login, password);
            contract.putAll(trans1Result);
            */
            // смена состояния договора ("На подписании" -> "Подписан")
            Map<String, Object> trans2Result = doContractMakeTrans(contract, B2B_CONTRACT_SG, error, login, password);
            contract.putAll(trans2Result);
        }

        if (error.length() == 0) {
            // печать полиса
            String repLevelList = UNIOPENAPI_REPLEVEL_POLICY_SIGNED;
            // получение и печать ПФ по значениям REPLEVEL с прикреплением к договору
            getAndPrintWithAttachingReportsByRepLevel(contractId, prodConfId, repLevelList, login, password);
        }

        Map<String, Object> result = new HashMap<>();
        if (error.length() > 0) {
            result.put(ERROR, error.toString());
        } else {
            Map<String, Object> objectResultType = new HashMap<>();
            // todo: рассмотреть возможность отказа от хеш-код в пользу GUID из B2B_CONTR.EXTERNALID
            String externalId = getStringParamLogged(params, "EXTERNALID");
            // objectResultType.put(UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_ID_PARAMNAME, contractId);
            objectResultType.put(UNIOPENAPI_RESULT_CLASS_OBJECT_OBJECT_ID_PARAMNAME, externalId);
            result.put(UNIOPENAPI_RESULT_CLASS_OBJECT, objectResultType);
        }

        logger.debug("dsB2BPaymentCompleteUniOpenAPI finished.");
        return result;
    }

    @WsMethod(requiredParams = {"EXTERNALID"})
    public Map<String, Object> dsB2BContractPaymentStateUniOpenAPI(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractPaymentStateUniOpenAPI start...");
        logger.debug("dsB2BContractPaymentStateUniOpenAPI params: " + params);

        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        // текст ошибки (будет пуст, если нет ошибок)
        StringBuilder error = new StringBuilder();
        // ИД договора
        /*
        Long contractId = getLongParamLogged(params, "CONTRID");
        if (contractId == null) {
            // todo: тексты ошибок в константы
            error.append("Не удалось получить сведения по договору! ");
        }
        */
        // EXTERNALID -> contractId
        Long contractId = getContractIdByExternalIdFromParams(params, error, login, password);
        Long contrNodeId = null;
        Long prodConfId = null;
        String stateSysName = null;
        Map<String, Object> contract = null;
        if (error.length() == 0) {
            // получение данных по договору
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("CONTRID", contractId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contract = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contractParams, login, password);
            contrNodeId = getLongParamLogged(contract, "CONTRNODEID");
            prodConfId = getLongParamLogged(contract, "PRODCONFID");
            stateSysName = getStringParamLogged(contract, "STATESYSNAME");
            if (
                    !isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId) ||
                            (contrNodeId == null) || (prodConfId == null) || (stateSysName.isEmpty())
                    ) {
                // todo: тексты ошибок в константы
                error.append("Не удалось получить сведения по договору! ");
            } else if (B2B_CONTRACT_DRAFT.equals(stateSysName)) {
                error.append("По данному договору еще не была произведена регистрация оплаты! ");
            }
        }

        Map<String, Object> factPayment = null;
        Long factPaymentId = null;
        if (error.length() == 0) {
            // получение текущих фактических платежей (dsB2BPaymentFactBrowseListByParam)
            Map<String, Object> factPaymentParams = new HashMap<>();
            factPaymentParams.put("CONTRNODEID", contrNodeId);
            String methodName = "dsB2BPaymentFactBrowseListByParam";
            List<Map<String, Object>> factPaymentList = callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, methodName, factPaymentParams, login, password);
            String factPaymentError = "";
            if (factPaymentList != null) {
                if (factPaymentList.isEmpty()) {
                    factPaymentError = "По данному договору еще не была произведена регистрация оплаты! ";
                } else if (factPaymentList.size() > 1) {
                    factPaymentError = "Не удалось получить однозначные сведения о регистрации оплаты по данному договору! ";
                } else {
                    factPayment = factPaymentList.get(0);
                    factPaymentId = getLongParam(factPayment, "PAYFACTID");
                }
            }
            if ((factPayment == null) || (factPaymentId == null)) {
                factPaymentError = "Не удалось получить сведения об оплате по данному договору! ";
            }
            if (!factPaymentError.isEmpty()) {
                logger.error(String.format(
                        "Fact payment check by calling %s (with params = %s) error - '%s'! Details (list from result): %s",
                        methodName, factPaymentParams, factPaymentError, factPaymentList
                ));
                error.append(factPaymentError);
            }
        }

        String payState = null;
        if (error.length() == 0) {
            String factPaymentSysName = getStringParamLogged(factPayment, PAYFACT_STATE_SYSNAME_PARAMNAME);
            // todo: мапа вместо if-ов
            if (PAYFACT_STATE_SYSNAME_DRAFT.equals(factPaymentSysName)) {
                payState = UNIOPENAPI_PAY_STATE_WAIT;
            } else if (PAYFACT_STATE_SYSNAME_SUCCESS.equals(factPaymentSysName)) {
                payState = UNIOPENAPI_PAY_STATE_SUCCESS;
            } else if (PAYFACT_STATE_SYSNAME_FAIL.equals(factPaymentSysName)) {
                payState = UNIOPENAPI_PAY_STATE_FAIL;
            }
            if (payState == null) {
                error.append("Не удалось определить состояние оплаты по данному договору! ");
            }
        }

        /*
        // todo: возможно, не требуется - проверить
        if (error.length() == 0) {
            // смена состояния договора ("Предварительная печать (Образец)" -> "На подписании")
            Map<String, Object> trans1Result = doContractMakeTrans(contract, B2B_CONTRACT_PREPARE, error, login, password);
            contract.putAll(trans1Result);
            // смена состояния договора ("На подписании" -> "Подписан")
            Map<String, Object> trans2Result = doContractMakeTrans(contract, B2B_CONTRACT_SG, error, login, password);
            contract.putAll(trans2Result);
        }
        */

        if (error.length() == 0) {
            if (UNIOPENAPI_PAY_STATE_SUCCESS.equals(payState)) {
                if (B2B_CONTRACT_PREPARE.equals(stateSysName)) {
                    // смена состояния договора ("На подписании" -> "Подписан")
                    doContractMakeTrans(contract, B2B_CONTRACT_SG, error, login, password);
                    if (error.length() == 0) {
                        // если смена состояния завершилась успешно - печать оригинала полиса
                        String repLevelList = UNIOPENAPI_REPLEVEL_POLICY_SIGNED;
                        // получение и печать ПФ по значениям REPLEVEL с прикреплением к договору
                        getAndPrintWithAttachingReportsByRepLevel(contractId, prodConfId, repLevelList, login, password);
                    }
                }
                // todo: еще ветки 'else if' если/когда потребуется обрабатывать после оплаты договоры в иных состояних (например, требующих больше переходов)
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (error.length() > 0) {
            result.put(ERROR, error.toString());
        } else {
            Map<String, Object> paymentInfoType = new HashMap<>();
            paymentInfoType.put("payState", payState);
            result.put(UNIOPENAPI_RESULT_CLASS_PAYMENT_STATE, paymentInfoType);
        }

        logger.debug("dsB2BContractPaymentStateUniOpenAPI finished.");
        return result;
    }
}
