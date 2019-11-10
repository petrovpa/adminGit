package com.bivgroup.services.b2bposws.facade.pos.declaration.utils;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.bivgroup.services.b2bposws.system.Constants.B2BPOSWS;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * Фасад содержащий вспомогательные сервисы для работы с изменениями по договору
 */
@BOName("B2BDeclarationOfChangeUtils")
public class B2BDeclarationOfChangeUtilsFacade extends B2BBaseFacade {
    private static final String CONTR_ID_PARAM_NAME = "CONTRID";
    private static final String ERROR_TO_CALC_FINANCIAL_HOLIDAYS_MSG = "Не удалось расчитать даты финансовых каникул";
    private static final String ERROR_CONTRACT_NOT_FOUND = "Не удалось найти договор";
    private static final String ANNULMENT_SYSNAME = "annulment";
    private static final String CANCELLATION_SYSNAME = "cancellation";
    private static final Long REPLEVEL_FOR_SPECIFICATION_3 = 3000L;
    private static final Long REPLEVEL_FOR_SPECIFICATION_3_ALT = 3001L;
    private static final Long REPLEVEL_FOR_SPECIFICATION_3_ALT_2 = 3002L;

    /**
     * Сервис для получения даты годовщины, ближайщей к текущей дате
     *
     * @param params - обязательным параметром является CONTRID - контракт договора
     * @return возвращает Map<String, Object> которая содержит параметр Map<String, Object> Result, в котором находится
     * anniversaryContractDate - дата в строковом виде
     * @throws Exception
     */
    @WsMethod(requiredParams = {CONTR_ID_PARAM_NAME})
    public Map<String, Object> dsB2BGetDateOfNearestAnniversary(Map<String, Object> params) throws Exception {
        logger.debug(String.format("dsB2BDateOfNearestAnniversary begin with params: %s", params));
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        // ИД договора
        Long contractId = getLongParamLogged(params, CONTR_ID_PARAM_NAME);
        Map<String, Object> contract = null;
        String error = "";
        Map<String, Object> contractParams = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        contractParams.put(CONTR_ID_PARAM_NAME, contractId);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        contract = callService(B2BPOSWS, "dsB2BContractBrowseListByParam", contractParams, login, password);
        if (!isCallResultOKAndContainsLongValue(contract, CONTR_ID_PARAM_NAME, contractId)) {
            error = ERROR_CONTRACT_NOT_FOUND;
        } else {
            // получаем дату начала договора
            Date startDate = getDateParam(contract.get("STARTDATE"));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(startDate);
            // Дата подачи заявления на изменения не может быть раньше даты начала действия договора © Саня
            int startDateYear = calendar.get(GregorianCalendar.YEAR) + 1;
            int startDateMonth = calendar.get(GregorianCalendar.MONTH); // месяц годовщины договора
            int startDateDay = calendar.get(GregorianCalendar.DAY_OF_MONTH); // день годовщины договора

            // получаем окончания догвоора
            Date finishDate = getDateParam(contract.get("FINISHDATE"));
            calendar.setTime(finishDate);
            int finisDateYear = calendar.get(Calendar.YEAR);

            // получаем текущую дату и обнуляем время, чтобы оно не мешало при сравнении дат
            Date currentDate = clearTime(new Date());

            Date iterateDate;
            // бежим от даты начала договора + 1 до даты окончания, чтобы получить ближащую дату годовщины договора
            // если текущая дата (сегодняшний день) находится после текущей даты годовщины тогда считаем что это
            // та сама дата и кладем ее в результирующую мапу
            for (int i = startDateYear; i <= finisDateYear; i++) {
                calendar.set(i, startDateMonth, startDateDay, 0, 0, 0);
                iterateDate = calendar.getTime();
                if (currentDate.before(iterateDate)) {
                    String dateStr = (new SimpleDateFormat("dd.MM.yyyy")).format(iterateDate);
                    result.put("anniversaryContractDate", dateStr);
                    break;
                }
            }

            // если ошибок не было, но результирующая мапа пуста, тогда записываем ошибку
            if (result.isEmpty() && !error.isEmpty()) {
                error = String.format("Дата ближайшей годовщины для договора с номером %s отсутствует", contract.get("CONTRNUMBER"));
            }
        }


        // если ошибки есть то записывае в мапу ошибку
        if (!error.isEmpty()) {
            result.put(ERROR, error);
        }

        logger.debug(String.format("dsB2BDateOfNearestAnniversary end with result data: %s", result));
        return result;
    }

    /**
     * Сервис для получения дат финансовых каникул
     *
     * @param params - обязательным параметром является CONTRID - контракт договора
     * @return возвращает Map<String, Object> которая содержит параметры Map<String, Object> Result, в котором находитятся даты
     * financialHolydaysStartDate - дата начала финансовых каникул в строковом виде
     * financialHolydaysFinishDate - дата начала финансовых каникул в строковом виде
     * @throws Exception
     */
    @WsMethod(requiredParams = {CONTR_ID_PARAM_NAME})
    public Map<String, Object> dsB2BGetDatesOfFinancialHolidays(Map<String, Object> params) throws Exception {
        logger.debug(String.format("dsB2BGetDatesOfFinancialHolidays begin with params: %s", params));
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        String errorMessage = "";
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> queryParams = params;

        // Расчет дат ведется на основе ближайшей даты годовщины
        queryParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> nearestAnniversaryDatesMap = this.callService(B2BPOSWS,
                "dsB2BGetDateOfNearestAnniversary", queryParams, login, password);

        errorMessage = ERROR_CONTRACT_NOT_FOUND;
        // Если нашли нужный нам договор
        if (isCallResultOK(nearestAnniversaryDatesMap)) {

            errorMessage = ERROR_TO_CALC_FINANCIAL_HOLIDAYS_MSG;
            if (nearestAnniversaryDatesMap.containsKey("anniversaryContractDate")) {
                errorMessage = "";
                resMap.put("anniversaryContractDate", nearestAnniversaryDatesMap.get("anniversaryContractDate"));
                resMap.put("financialHolydaysStartDate", nearestAnniversaryDatesMap.get("anniversaryContractDate"));

                // Вычисляем дату конца финансовых каникул
                Calendar gCalendar = new GregorianCalendar();
                gCalendar.setTime(new SimpleDateFormat("dd.MM.yyyy").parse((String) nearestAnniversaryDatesMap.get("anniversaryContractDate")));
                gCalendar.set(Calendar.YEAR, gCalendar.get(Calendar.YEAR) + 1);

                String financialHolydaysFinishDate = (new SimpleDateFormat("dd.MM.yyyy")).format(gCalendar.getTime());
                resMap.put("financialHolydaysFinishDate", financialHolydaysFinishDate);
            }
        }

        if (!errorMessage.isEmpty()) {
            resMap.clear();
            resMap.put(ERROR, ERROR_TO_CALC_FINANCIAL_HOLIDAYS_MSG);
        }

        logger.debug(String.format("dsB2BGetDatesOfFinancialHolidays end with result data: %s", resMap));
        return resMap;
    }

    /**
     * Сервис анализа даты начала действия договора для инициирования либо Расторжения либо Аннулирования договора
     *
     * @param params <UL>
     *               <LI>CONTRID - идентификатор договора</LI>
     *               </UL>
     * @return <UL>
     * <LI>cancellationType - наименование Расторжения либо Аннулирования договора</LI>
     * <LI>error - текст ошибки, в случае, если неудалось получить договор или дата начала действия в договоре отсутствует</LI>
     * </UL>
     * @throws Exception
     */
    @WsMethod(requiredParams = {CONTR_ID_PARAM_NAME})
    public Map<String, Object> dsB2BGetCancellationTypeByContrId(Map<String, Object> params) throws Exception {
        logger.debug(String.format("dsB2BGetCancellationTypeForContract begin with params: %s", params));
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String error = "";
        // ИД договора
        Long contractId = getLongParamLogged(params, CONTR_ID_PARAM_NAME);
        Map<String, Object> contract = null;
        Map<String, Object> contractParams = new HashMap<>();
        contractParams.put(CONTR_ID_PARAM_NAME, contractId);
        contractParams.put("ISHIDDENCONTRACT", false);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        contract = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParamExShort", contractParams, login, password);
        if (!isCallResultOKAndContainsLongValue(contract, CONTR_ID_PARAM_NAME, contractId)) {
            error = ERROR_CONTRACT_NOT_FOUND;
        }

        String cancellationType = "";
        Date startDate = null;
        String contractStateSysname = "";
        if (error.isEmpty()) {
            contractStateSysname = getStringParam(contract.get("STATESYSNAME"));
            //Если договор находится в статусе "Проект", то возвращаем "аннулирование"
            if (contractStateSysname.equals("Проект")) {
                cancellationType = ANNULMENT_SYSNAME;
            } else {
                // получаем дату начала договора
                startDate = clearTime(getDateParam(contract.get("STARTDATE")));
                if (startDate == null) {
                    error = "У договора отсутствует дата начала действия";
                } else {
                    Long prodConfId = getLongParam(contract, "PRODCONFID");
                    if (prodConfId == null) {
                        error = "У продукта отсутствует конфигурация.";
                    } else {
                        Map<String, Object> prodDefValueQuery = new HashMap<>();
                        prodDefValueQuery.put("PRODCONFID", prodConfId);
                        prodDefValueQuery.put("NAME", "contrStartDateOffset");
                        prodDefValueQuery.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> prodDefValueQueryResult = this.callExternalServiceLogged(B2BPOSWS_SERVICE_NAME,
                                "dsB2BProductDefaultValueBrowseListByParam",
                                prodDefValueQuery, login, password);
                        Long contrStartDateOffset = getLongParam(prodDefValueQueryResult.get("VALUE"));
                        // если не указана смещение даты начала действия договора, тогда прибавляем 15 дней согласно задаче #12487
                        if (contrStartDateOffset == null) {
                            contrStartDateOffset = 15L;
                        }

                        startDate = addDayToDate(startDate, contrStartDateOffset.intValue());
                        // получаем текущую дату и обнуляем время, чтобы оно не мешало при сравнении дат
                        Date currentDate = clearTime(new Date());
                        if (currentDate.before(startDate)) {
                            cancellationType = ANNULMENT_SYSNAME;
                        } else {
                            cancellationType = CANCELLATION_SYSNAME;
                        }
                    }
                }
            }
        }


        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result.put("cancellationType", cancellationType);
        } else {
            result.put(ERROR, error);
        }
        logger.debug(String.format("dsB2BGetCancellationTypeForContract end with result data: %s", result));
        return result;
    }

    private Date addDayToDate(Date date, Integer offset) {
        Date result = date;
        if (date != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(GregorianCalendar.DAY_OF_YEAR, offset);
            result = calendar.getTime();
        }
        return result;
    }

    private Date clearTime(Date date) {
        Date result = date;
        if (date != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(GregorianCalendar.HOUR, 0);
            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.set(GregorianCalendar.SECOND, 0);
            calendar.set(GregorianCalendar.MILLISECOND, 0);
            result = calendar.getTime();
        }
        return result;
    }

    /**
     * Сервис проверки возможности печати документов Расторжения или Аннулирования договора
     *
     * @param params <UL>
     *               <LI>CONTRID - идентификатор договора</LI>
     *               </UL>
     * @return <UL>
     * <LI>isPossibleCancellation - true - если печать возможна; false - если печатать нечего</LI>
     * </UL>
     * @throws Exception
     */
    @WsMethod(requiredParams = {CONTR_ID_PARAM_NAME})
    public Map<String, Object> dsB2BCheckIsPossibleAnnulmentOrCancellation(Map<String, Object> params)
            throws Exception {
        logger.debug(String.format("dsB2BCheckIsPossibleAnnulmentOrCancellation begin with params: %s", params));
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        /*
        TODO: Возможно потребуется в будущем, когда потребуется для одного типа иметь возможность печатать ПФ, а для другого нет
        Map<String, Object> typeSysnameQueryParams = new HashMap<>(params);
        typeSysnameQueryParams.put(RETURN_AS_HASH_MAP, true);
        String typeSysname = getStringParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                "dsB2BGetCancellationTypeByContrId", typeSysnameQueryParams,
                login, password, "cancellationType")
        );*/
        String error = "";
        Map<String, Object> prodVerQueryParams = new HashMap<>(params);
        prodVerQueryParams.put("ISHIDDENCONTRACT", false);
        Long contractProdVerId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                "dsB2BContractBrowseListByParamExShort", prodVerQueryParams,
                login, password, "PRODVERID")
        );
        if (contractProdVerId == null) {
            error = "Невозможно получить продукт для данного договора.";
        }

        Map<String, Object> contrShortQueryParams = new HashMap<>(params);
        contrShortQueryParams.put("ISHIDDENCONTRACT", false);
        contrShortQueryParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contrShortQueryResult = this.callExternalService(B2BPOSWS_SERVICE_NAME,
                "dsB2BContractBrowseListByParamExShort", contrShortQueryParams, login, password);
        Long prodConfId = getLongParam(contrShortQueryResult, "PRODCONFID");
        if (prodConfId == null) {
            error = "У продукта отсутствует конфигурация.";
        }

        boolean isPossibleCancellation = true;
        if (error.isEmpty()) {
            Map<String, Object> repListQueryParams = new HashMap<>();
            repListQueryParams.put("REPLEVELLIST", REPLEVEL_FOR_SPECIFICATION_3 + ","
                    + REPLEVEL_FOR_SPECIFICATION_3_ALT + "," + REPLEVEL_FOR_SPECIFICATION_3_ALT_2
            );
            repListQueryParams.put("PRODCONFID", prodConfId);
            List<Map<String, Object>> repList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BProductReportBrowseListByParamEx",
                    repListQueryParams, login, password
            );
            if (repList == null || repList.isEmpty()) {
                isPossibleCancellation = false;
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result.put("isPossibleCancellation", isPossibleCancellation);
        } else {
            result.put(ERROR, error);
        }
        logger.debug(String.format("dsB2BCheckIsPossibleAnnulmentOrCancellation end with result data: %s", result));
        return result;
    }
}
