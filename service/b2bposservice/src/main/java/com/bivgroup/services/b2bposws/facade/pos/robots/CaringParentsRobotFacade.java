package com.bivgroup.services.b2bposws.facade.pos.robots;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.product.custom.B2BProductEditorCustomFacade.INSPOSWS_SERVICE_NAME;

/**
 * Фасад роботов для продукта "Заботливые родители"
 */
public class CaringParentsRobotFacade extends B2BLifeBaseFacade {
    private static volatile int threadCount = 0;

    /**
     * Сервис для проверки даты акцепта договоров
     * Обрабатывает договора в статусу не подписании (B2B_CONTRACT_PREPARE)
     *
     * @param params
     * @return
     */
    @WsMethod()
    public Map<String, Object> dsCheckAcceptanceDateContractsByPrepareState(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (threadCount == 0) {
            threadCount = 1;
            try {
                logger.debug("dsCheckAcceptanceDateContractsByPrepareState start");
                result = tryCheckAcceptanceDateContracts(params);
            } finally {
                threadCount = 0;
            }
        } else {
            logger.debug("dsCheckAcceptanceDateContractsByPrepareState alreadyRun");
        }
        return result;
    }

    /**
     * Метод подготовки выбора договоров из БД по PRODVERID и STATESYSNAME
     * для последующей одиночной обработки договора.
     * Выбираем только договора в статусе "На подписании" и пока что только
     * для продукта Заботливые родители (PRODVERID=500000)
     *
     * @param params
     * @return
     * @throws Exception
     */
    private Map<String, Object> tryCheckAcceptanceDateContracts(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contractsQueryParams = new HashMap<>();
        // выбираем все договора по продуктам с PRODVERID указаным в PRODVERIDLIST
        // пока что используется только для продукта "Заботливые родители"; PRODVERID=500000
        contractsQueryParams.put("PRODVERIDLIST", "500000");
        // требуется выбрать все договора продуктов в состоянии "На подписании"
        contractsQueryParams.put("STATESYSNAME", "B2B_CONTRACT_PREPARE");
        List<Map<String, Object>> contractList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BContractBrowseListByParamExShort", contractsQueryParams, login, password);

        if (contractList != null) {
            int totalCount = contractList.size();
            int current = 1;
            logger.debug(String.format("Found %d contract statements for processing.", totalCount));

            Map<String, Object> contractStatementProcessResult, contractStatementParams;
            Long contactId;
            for (Map<String, Object> contract : contractList) {
                logger.debug(String.format("\nPreparing for processing %d contract statement (from total of %d found contract statements)...",
                        current, totalCount));
                //INS_OBJSTATEH
                contactId = getLongParam(contract, "CONTRID");

                contractStatementParams = new HashMap<>();
                contractStatementParams.put("CONTRID", contactId);
                contractStatementParams.put(RETURN_AS_HASH_MAP, true);
                try {
                    contractStatementProcessResult = this.callExternalService(B2BPOSWS_SERVICE_NAME,
                            "dsB2BCheckAcceptanceDateContactStatementsProcessSingleRecord", contractStatementParams, login, password);
                    contract.putAll(contractStatementProcessResult);
                } catch (Exception ex) {
                    loggingAnyExceptionDuringContractProcessing(contract, ex);
                }

                current += 1;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("CONTRACTSTATEMENTLIST", contractList);
        return result;
    }

    /**
     * Сервис обработки одного договора
     *
     * @param params <UL>
     *               <LI>CONTRID - идентификатор договора</LI>
     *               </UL>
     * @return
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BCheckAcceptanceDateContactStatementsProcessSingleRecord(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        logger.debug("Start contract statement single record processing...");

        try {
            result = doCheckAcceptanceDateContractStatementsProcessSingleRecord(params, login, password);
        } catch (Exception ex) {
            loggingAnyExceptionDuringContractProcessing(params, ex);
        }

        logger.debug("Bank statement single record processing finished.");

        return result;
    }

    /**
     * Метод логирования процесса обработки договора
     *
     * @param params <UL>
     *               <LI>CONTRID - идентификатор договора</LI>
     *               </UL>
     * @param ex     исключение, которое требуется записать в лог
     */
    private void loggingAnyExceptionDuringContractProcessing(Map<String, Object> params, Exception ex) {
        Long contractId = getLongParam(params, "CONTRID");
        logger.error(
                "Error processing contract with id=" + contractId
                        + " See details in log. Error localized message: " + ex.getLocalizedMessage(),
                ex
        );
    }

    /**
     * Метод обработки одного договора
     * Загружает историю изменений состоянии договора и если она пустая записывает ошибку в лог
     * иначе передает обработку далее
     *
     * @param params   <UL>
     *                 <LI>CONTRID - идентификатор договора</LI>
     *                 </UL>
     * @param login    логин, требуется для вызова сервисов
     * @param password пароль, требуется для вызова сервисов
     * @return
     */
    private Map<String, Object> doCheckAcceptanceDateContractStatementsProcessSingleRecord(Map<String, Object> params,
                                                                                           String login, String password) {
        Map<String, Object> result = new HashMap<>();
        Long contractId = getLongParam(params, "CONTRID");
        List<Map<String, Object>> historyList = loadHistoryStateByContractId(contractId);
        if (historyList.isEmpty()) {
            logger.error("The contract with id=" + contractId + " there is no history. Processing is not possible.");
        } else {
            Map<String, Object> processContract = processingContractByHistoryStates(contractId, historyList, login, password);
            result.putAll(processContract);
        }
        return result;
    }

    /**
     * Метод загрузки истории изменения состояний договора
     *
     * @param contractId идентификатор договора
     * @return список состояний
     */
    private List<Map<String, Object>> loadHistoryStateByContractId(Long contractId) {
        List<Map<String, Object>> historyList = new ArrayList<>();
        try {
            Map<String, Object> stateHistoryQueryParams, historyQueryResult;
            stateHistoryQueryParams = new HashMap<>();
            stateHistoryQueryParams.put("CONTRID", contractId);
            historyQueryResult = this.selectQuery("dsStateHistoryBrowseListByContrAndTypeIDs", stateHistoryQueryParams);
            if (historyQueryResult.get(RESULT) != null) {
                historyList = getListFromResultMap(historyQueryResult);
                // сортируем историю изменений состояний по дате
                historyList.sort(new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return getDoubleParam(o2, "STARTDATE").compareTo(getDoubleParam(o1, "STARTDATE"));
                    }
                });
            }
        } catch (Exception ex) {
            logger.error(
                    "Error getting contract history state by id=" + contractId
                            + ". Error localized message: " + ex.getLocalizedMessage(),
                    ex
            );
        }
        // возвращаем список отсортированный по убыванию
        return historyList;
    }

    /**
     * Метод обработки договора по истории изменения его состояний
     * Если договор перешел в состоянии На подписании не из Андеррайтинга,
     * тогда пропускам его обработку и ничего не делаем, если же
     * перешел из Андеррайтинга, тогда переводим его состояние в Черновик
     *
     * @param contractId  идентификатор договора
     * @param historyList список истории изменений состояния договора
     * @param login       логин, требуется для вызова сервисов
     * @param password    пароль, требуется для вызова сервисов
     * @return результат обработки
     */
    private Map<String, Object> processingContractByHistoryStates(Long contractId, List<Map<String, Object>> historyList,
                                                                  String login, String password) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> contractParams = new HashMap<>();
        contractParams.put("CONTRID", contractId);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        try {
            // берем предыдущее состояние, это первый элемент в спискее истории,
            // т.к. в истории не хранится последний переход, т.е. текущее состояние
            Map<String, Object> historyItem = historyList.get(0);
            Map<String, Object> contract = new HashMap<>();
            // если предыдущее состояние было на Андеррайтинг (B2B_CONTRACT_UW) то где требуется проверить дату Акцептования
            if (getStringParam(historyItem, "STATENAME").equalsIgnoreCase("B2B_CONTRACT_UW")) {
                contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalLoad",
                        contractParams, login, password
                );
                Map<String, Object> contractExtMap = getMapParam(contract, "CONTREXTMAP");
                Date acceptDate = getDateParam(contractExtMap.get("acceptDATE"));
                if (acceptDate == null) {
                    logger.error("Contract not with id=" + contractId + " not processing because accept date is null");
                } else {
                    Date currentDate = new Date();
                    if (currentDate.after(acceptDate)) {
                        String fromState = getStringParam(contract, "STATESYSNAME");
                        String toState = "B2B_CONTRACT_DRAFT";
                        Map<String, Object> transitionResult = doStateTransitionContract(fromState, toState, contractId,
                                login, password
                        );
                        result.put("TRANSRES", transitionResult);
                        if (transitionResult != null && !transitionResult.isEmpty()) {
                            contract.put("STATESYSNAME", transitionResult.get("STATESYSNAME"));
                        } else {
                            logger.error(
                                    "Error transition contract with id=" + contractId
                                            + " from state" + fromState + " to " + toState
                                            + ". Details see in logs"
                            );
                        }
                    } else {
                        logger.debug("Contract with id=" + contractId
                                + " not transfer B2B_CONTRACT_DRAFT because accept date before current system date"
                                + currentDate.toString()
                        );
                    }
                }
            } else {
                logger.debug("Contract with id=" + contractId
                        + " not transfer B2B_CONTRACT_DRAFT because it did not trans from B2B_CONTRACT_UW"
                );
            }
            result.putAll(contract);
        } catch (Exception ex) {
            logger.error(
                    "Error getting contract full info by id=" + contractId
                            + ". Error localized message: " + ex.getLocalizedMessage(),
                    ex
            );
        }
        return result;
    }

    /**
     * Метод перевода договора из одного состояния в другое
     * Делает запрос к таблицу CORE_SM_STATE и проверяет возможен ли переход
     * если такой переход не возможен, то возвращаем пустую мапу
     * Если же возможен то по идентификатору договора переводи договор
     * из текущего состояния (fromState) в требуемое (toState)
     *
     * @param fromState  состояние из которого требуется перевести
     * @param toState    состояние в которое требуется перевести
     * @param contractId идентификатор договора
     * @param login      логин, требуется для вызова сервисов
     * @param password   пароль, требуется для вызова сервисов
     * @return результат перевода
     */
    private Map<String, Object> doStateTransitionContract(String fromState, String toState, Long contractId,
                                                          String login, String password) {
        Map<String, Object> transitionOpportunitiesQueryParameters = new HashMap<String, Object>();
        transitionOpportunitiesQueryParameters.put("FROMSTATESYSNAME", fromState);
        transitionOpportunitiesQueryParameters.put("TOSTATESYSNAME", toState);
        String typeSysName = "B2B_CONTRACT";
        transitionOpportunitiesQueryParameters.put("TYPESYSNAME", typeSysName);
        transitionOpportunitiesQueryParameters.put("JOIN_TO_SMTYPE", "TRUE");
        transitionOpportunitiesQueryParameters.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> transitionResult = new HashMap<>();
        Map<String, Object> transQres = new HashMap<>();
        try {
            transQres = this.callService(INSPOSWS_SERVICE_NAME, "dsTransitionsBrowseByParamEx",
                    transitionOpportunitiesQueryParameters, login, password);
        } catch (Exception ex) {
            logger.error(
                    "Error transition contract with id=" + contractId
                            + " from state" + fromState + " to " + toState
                            + "impossible because not transition in CORE_SM_TRANS table."
                            + " Error localized message: " + ex.getLocalizedMessage(),
                    ex
            );
        }
        if (transQres.get("SYSNAME") != null) {
            Map<String, Object> transitionQueryParameters = new HashMap<String, Object>();
            transitionQueryParameters.put("TRANSITIONSYSNAME", transQres.get("SYSNAME"));
            transitionQueryParameters.put("TYPESYSNAME", typeSysName);
            transitionQueryParameters.put("DOCUMENTID", contractId);
            transitionQueryParameters.put("CONTRID", contractId);
            try {
                transitionResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContract_State_MakeTrans",
                        transitionQueryParameters, login, password);
            } catch (Exception ex) {
                logger.error(
                        "Error transition contract with id=" + contractId
                                + " from state" + fromState + " to " + toState
                                + ". Error localized message: " + ex.getLocalizedMessage(),
                        ex
                );
            }
        }
        return transitionResult;
    }
}
