package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.integrationservice.system.Constants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.*;

@BOName("LifeContractCutGetter")
public class LifeContractCutGetterFacade extends IntegrationBaseFacade {
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS; // todo: заменить на импорт
    private static final Long OUTCONTRACTPACKSINGLE = 10L; // todo: заменить на импорт
    private static final Long GETCUTCONTRACTINFO = 20L; // todo: заменить на импорт

    protected static final Map<String, Long> PERIODICITY_MAP;

    static {
        // PeriodicityType.value() -> B2B_PAYVAR.PAYVARID
        PERIODICITY_MAP = new HashMap<>();
        // 103 - "ONE"
        PERIODICITY_MAP.put("ONE", 103L);
        // 101 - "SEM"
        PERIODICITY_MAP.put("SEM", 101L);
        // 205 - "WEEK"
        PERIODICITY_MAP.put("WEEK", 205L);
        // 100 - "ANN"
        PERIODICITY_MAP.put("ANN", 100L);
        // 206 - "DAY"
        PERIODICITY_MAP.put("DAY", 206L);
        // 104 - "MON"
        PERIODICITY_MAP.put("MON", 104L);
        // 102 - "QUA"
        PERIODICITY_MAP.put("QUA", 102L);
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetContractCutList(Map<String, Object> params) throws Exception {

        // логин и пароль полученные в параметрах вызова игнорируются
        // String login = params.get(WsConstants.LOGIN).toString();
        // String password = params.get(WsConstants.PASSWORD).toString();
        updateProgramsCache();
        // логин и пароль для вызова сервисов читаются из конфига (на данный момент - из 'b2bposws-config.xml')
        String login = getLogin();
        String password = getPassword();
        if (DEFAULT_LOGIN.equals(login)) {
            // вероятнее всего, нормальный логин/пароль не удалось прочитать из конфига - очень вероятно, что интеграция будет выдавать исключения и пр.
            logger.error("Selected login for service calls looks like the default one - probably correct login and password are missing in war config file!");
        }

        Map<String, Object> result = new HashMap<>();

        GetObjListType golt = new GetObjListType();
        int packSize = 1;
        int callCount = 1;
        if (params.get("PACKSIZE") != null) {
            packSize = Integer.valueOf(params.get("PACKSIZE").toString()).intValue();
            if (packSize > 100) {
                callCount = packSize / 100;
                callCount = callCount * 2;
                callCount++;
                packSize = 50;
            }
        } else {
            golt.setFrom(parseDate("2017-03-01"));
            golt.setTo(parseDate("2018-01-01"));
        }

        if (isIntegrationInDebugMode()) {
            // !только для отладки!
            String fromDateStr = getStringParam(params, "FROMDATESTR");
            String toDateStr = getStringParam(params, "TODATESTR");
            if (!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
                // !только для отладки!
                golt.setFrom(parseDate(fromDateStr));
                golt.setTo(parseDate(toDateStr));
            }
            // !только для отладки!
            String programListStr = getStringParam(params, "PROGRAMLISTSTR");
            if (!programListStr.isEmpty()) {
                // !только для отладки!
                String[] programListArr = programListStr.split(";");
                if ((programListArr != null) && (programListArr.length > 0)) {
                    ListProgramType programListType = new ListProgramType();
                    programListType.getProgram().addAll(Arrays.asList(programListArr));
                    golt.setProgramList(programListType);
                }
            }
        }

        golt.setRowCount(packSize);

        if (isIntegrationInDebugMode()) {
            // !только для отладки!
            String programName = (String) params.get("PROGRAMNAME");
            ListProgramType lpt = new ListProgramType();
            lpt.getProgram().add(programName);
            golt.setProgramList(lpt);
        }

        try {
            for (int i = 0; i < callCount; i++) {
                ListContractCutType resContrList = callLifePartnerGetContractsCut(golt);
                List<ContractCut> ccList = resContrList.getContract();
                AnswerImportListType ailt = new AnswerImportListType();
                List<AnswerImportType> aitList = ailt.getAnswerImport();

                for (ContractCut contrCut : ccList) {
                    AnswerImportType ait = new AnswerImportType();
                    ait.setSignDeleted(BigInteger.ZERO);
                    try {
                        ait.setPolicyNumber(contrCut.getPolicyNumber());
                        ait.setPolicyId(String.valueOf(contrCut.getPolicyID()));
                        Map<String, Object> contrObj = mapContract(contrCut, login, password);
                        if (contrObj != null) {
                            Map<String, Object> saveRes = saveContract(contrObj, params, login, password);
                            if (saveRes != null) {
                                if ((saveRes.get("CONTRID") != null) && (saveRes.get("CONTRNODEID") != null)) {
                                    ait.setStatus(StatusIntType.SUCCESS);
                                    // договор успешно сохранен, запрашиваем севрис удаляющий его из очереди.
                                } else {
                                    ait.setStatus(StatusIntType.FAIL);
                                    ait.setErr("Ошибка сохранения договора: Договор не создан.");
                                }
                            } else {
                                ait.setStatus(StatusIntType.FAIL);
                                ait.setErr("Ошибка сохранения договора: externalidIdEmpty");
                            }
                        } else {
                            ait.setStatus(StatusIntType.FAIL);
                            ait.setErr("Ошибка сохранения договора: Неизвестный продукт, программа: " + contrCut.getProductName() + " - " + contrCut.getPolicyProgram());
                        }
                    } catch (Exception ex) {
                        ait.setStatus(StatusIntType.FAIL);
                        ait.setErr("Ошибка сохранения договора: 789");
                        ait.setPolicyNumber(contrCut.getPolicyNumber());
                        ait.setPolicyId(String.valueOf(contrCut.getPolicyID()));
                        logger.error("Ошибка сохранения договора: 789", ex);
                    }
                    aitList.add(ait);
                }

                callLifeProcessResponseCut(ailt);
                // ест много процессорного времени
                // String goltXML = this.marshall(golt, GetObjListType.class);
                String goltXML = this.marshall(ailt, AnswerImportListType.class);
                String contractListRespXML = this.marshall(resContrList, ListContractCutType.class);
                // String goltXML = "";
                //  String contractListRespXML = "";
                b2bRequestQueueCreate(goltXML, contractListRespXML, GETCUTCONTRACTINFO, 1000, login, password);
                result.put("requestStr", goltXML);
                result.put("responseStr", contractListRespXML);
                result.put("STATUS", "DONE");
            }
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            result.put("responseStr", sw.toString());
            String goltXML = this.marshall(golt, GetObjListType.class);
            b2bRequestQueueCreate(goltXML, sw.toString(), GETCUTCONTRACTINFO, 1000, login, password);

            result.put("STATUS", "outERROR");
        }
        return result;
    }


    private Map<String, Object> mapContract(ContractCut contrCut, String login, String password) throws Exception {
        Map<String, Object> contrMap = new HashMap<String, Object>();
        long policyID = contrCut.getPolicyID();
        contrMap.put("EXTERNALID", policyID);
        contrMap.put("CONTRPOLSER", contrCut.getPolicySeries());
        contrMap.put("CONTRPOLNUM", contrCut.getPolicyNumber());
        contrMap.put("DOCUMENTDATE", processDate(contrCut.getPolicyDocDate()));
        //if (contrCut.getPolicyNumber().startsWith(contrCut.getPolicySeries())) {
        //contrMap.put("CONTRPOLNUM", contrCut.getPolicyNumber().replaceFirst(contrCut.getPolicySeries(),""));
        //}
        contrMap.put("CONTRNUMBER", contrCut.getPolicyNumber());
        String programName = contrCut.getPolicyProgram();
        Route route = (Route) PROGRAMS.get(programName);
        if (route == null) {
            logger.error("Product and Program unknown " + contrCut.getProductName() + " - " + contrCut.getPolicyProgram());
            return null;
        }

        route.loadData();
        Long productVersionId = route.getProdVerId();
        if (productVersionId == null) {
            logger.error("Error while checking PRODVERID for " + programName);
            return null;
        } else {
            contrMap.put("PRODVERID", productVersionId);
        }
        // находим код программы.
        Long prodProgId = route.getProdProgId();
        if (prodProgId == null) {
            logger.error("Error while checking PRODPROGID for product version = " + productVersionId);
        } else {
            contrMap.put("PRODPROGID", prodProgId);
        }

        contrMap.put("INSAMCURRENCYID", getCurrencyIdBySysName(contrCut.getCurrency()));
        contrMap.put("PREMCURRENCYID", getCurrencyIdBySysName(contrCut.getCurrency()));
        contrMap.put("MEMBERLIST", processMember(contrCut.getThirdPartyList(), login, password));
        Map<String, Object> stateMap = getStateMapByName(contrCut.getPolicyStatus());
        contrMap.put("STATEID", 2000);
        if (stateMap != null) {
            if (stateMap.get("STATEID") != null) {
                contrMap.put("STATEID", stateMap.get("STATEID"));
                contrMap.put("STATESYSNAME", stateMap.get("STATENAME"));
            }
        }
        contrMap.put("CREATEDATE", new Date());
        contrMap.put("CREATEUSERID", 1);
        contrMap.put("STARTDATE", processDate(contrCut.getPolicyStartDate()));
        // #10899 уточняется.
        // для новых горизонтов заменить на  getDateParam(contrExtMap.get("rentEndDATE"))
        //
        contrMap.put("FINISHDATE", processDate(contrCut.getPolicyEndDate()));
        if (contrCut.getAmountAssured() != null) {
            contrMap.put("INSAMVALUE", contrCut.getAmountAssured().doubleValue());
        }
        if (contrCut.getUnpdaidSum() != null) {
            contrMap.put("PREMVALUE", contrCut.getUnpdaidSum().doubleValue());
        }
        ListInvestCoverageType lic = contrCut.getInvestCoverage();
        if (lic != null) {
            List<InvestCoverageType> icList = lic.getInvestCoverage();
            // исходя из фт - базовый актив, и инвестиционное состояние - должны быть в 1 экземпляре, поэтому, идем по списку, и первое не пустое поле сохраняем
            // в расширенных атрибутаз.
            Map<String, Object> contrExtMap = new HashMap<>();
            contrMap.put("CONTREXTMAP", processInvestCoverage(contrExtMap, icList, login, password));
        }
        mapRentAndEtc(contrMap, contrCut);
        return contrMap;
    }

    private void mapRentAndEtc(Map<String, Object> contractMap, ContractCut contractCut) {
        Map<String, Object> contractExtMap = getOrCreateMapParam(contractMap, "CONTREXTMAP");
        // RentSum - Размер ренты
        contractExtMap.put("RENTSUM", contractCut.getRentSum());
        // RentUpperSum - Размер ренты на период повышенных выплат
        contractExtMap.put("RENTUPPERSUM", contractCut.getRentUpperSum());
        // PayPeriodStartDate - Период выплаты ренты дата начала
        contractExtMap.put("PAYPERIODSTARTDATE", processDate(contractCut.getPayPeriodStartDate()));
        // PayPeriodEndDate - Период выплаты ренты дата окончания
        contractExtMap.put("PAYPERIODENDDATE", processDate(contractCut.getPayPeriodEndDate()));
        // UpperPayPeriodEndDate - Дата окончания выплаты повышенной ренты
        contractExtMap.put("UPPERPAYPERIODENDDATE", processDate(contractCut.getUpperPayPeriodEndDate()));
        // CurrentPay - Оплачено на текущую дату
        contractExtMap.put("CURRENTPAY", contractCut.getCurrentPay());
        // PaymentPeriodicity – Периодичность выплаты
        PeriodicityType periodicityType = contractCut.getPaymentPeriodicity();
        if (periodicityType != null) {
            String periodicityTypeValue = periodicityType.value();
            if (periodicityTypeValue != null) {
                // PeriodicityType.value() -> B2B_PAYVAR.PAYVARID
                contractExtMap.put("PAYMENTPERIODICITY", PERIODICITY_MAP.get(periodicityTypeValue));
                contractMap.put("PAYVARID", PERIODICITY_MAP.get(periodicityTypeValue));
            }
        }

        PeriodicityType putPeriodicityType = contractCut.getOutPayPeriodicity();
        if (putPeriodicityType != null) {
            String outPeriodicityTypeValue = putPeriodicityType.value();
            if (outPeriodicityTypeValue != null) {
                // PeriodicityType.value() -> B2B_PAYVAR.PAYVARID
                contractExtMap.put("OUTPAYPERIODICITY", PERIODICITY_MAP.get(outPeriodicityTypeValue));
            }
        }
    }

    private Object getCurrencyIdBySysName(String currency) {
        return CURRENCYMAP.getKey(currency);
    }

    private Object processMember(ContractCut.ThirdPartyList thirdPartyList, String login, String password) {
        List<ContractCut.ThirdPartyList.ThirdParty> tpl = thirdPartyList.getThirdParty();
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (ContractCut.ThirdPartyList.ThirdParty thirdParty : tpl) {
            thirdParty.getThirdPartyId();
            Map<String, Object> memberMap = new HashMap<>();
            memberMap.put(MEMBER_TYPE_SYSNAME_FIELDNAME, ROLEMAP.getKey(thirdParty.getRole()));
            memberMap.put(MEMBER_THIRDPARTYID_FIELDNAME, Double.valueOf(thirdParty.getThirdPartyId()).longValue());
            // "HashCode (на контрагенте) – хэш ФИОДУЛДР" (Хешированное значение ФИОДУЛДР)
            memberMap.put(MEMBER_HASHCODE_FIELDNAME, thirdParty.getHashCode());
            memberList.add(memberMap);
        }
        return memberList;
    }

    private Map<String, Object> saveContract(Map<String, Object> contrMap, Map<String, Object> params, String login, String password) throws Exception {
        // вызывать мега универсальный сервис сохранения договоров, ради сохранения краткой инфы - смысла нет.
        // если потребуется сохранить поля, из структуры договора, проще добавить в расширенные атрибуты.
        //1. попытатся найти договор по externalid. если нашли - вызывать update,
        Map<String, Object> searchParams = new HashMap<>();

        if (contrMap.get("EXTERNALID") != null) {
            searchParams.put("EXTERNALID", getDoubleParam(contrMap.get("EXTERNALID")).longValue());
            searchParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> searchContrResult = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", searchParams, login, password);
            Long existedContractId = getLongParam(searchContrResult, "CONTRID");
            if (existedContractId != null) {
                // в бд уже сохранен данный договор. обновляем его.
                List<Map<String, Object>> memberList = (List<Map<String, Object>>) contrMap.get("MEMBERLIST");
                contrMap.remove("MEMBERLIST");
                if ((contrMap.get("PRODVERID") != null) && (!"0".equals(contrMap.get("PRODVERID").toString()))) {
                    contrMap.put("CONTRID", existedContractId);
                    contrMap.put("FROMSTATESYSNAME", searchContrResult.get("STATESYSNAME"));

                    updateContract(contrMap, login, password);
                    // TODO: при необходимости обновлять мемберов. (если в мембере может произойти замена персоны страхователя, 
                    // то надо выплнить отвязку договора от лк старого страхователя)
                    // при следующем логине, или регистрации нового страхователя в лк - ему привяжется этот договор.
                    Map<String, Object> memberSearchParams = new HashMap<>();
                    memberSearchParams.put("CONTRID", existedContractId);
                    List<Map<String, Object>> oldMemberList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS, "dsB2BMemberBrowseListByParam", memberSearchParams, login, password);

                    // удалить всех старых мемберов
                    Map<String, Object> memberDelParam = new HashMap<>();
                    memberDelParam.put("CONTRID", existedContractId);
                    Map<String, Object> memberDelRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BMemberDeleteByContrId", memberDelParam, login, password);
                    // удалить связи с договором из sd_shared_contract
                    Map<String, Object> sharedContrDelParam = new HashMap<>();
                    sharedContrDelParam.put("contractId", existedContractId);
                    Map<String, Object> sharedDelRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUnattach", sharedContrDelParam, login, password);
//
//                    // создать новый мемберов.
//                    boolean isInsurerExist = false;
//                    if (oldMemberList != null) {
//                        for (Map<String, Object> oldMemberMap : oldMemberList) {
//
//                            String oldMemberTypeSysName = getStringParam(oldMemberMap, MEMBER_TYPE_SYSNAME_FIELDNAME);
//                            if ("insurer".equalsIgnoreCase(oldMemberTypeSysName)) {
//                                isInsurerExist = true;
//                            }
//                            Long oldMemberId = getLongParam(oldMemberMap, "MEMBERID");
//                            Map<String,Object>
//                            if (oldMemberId == null) {
//                                // если по какой то причине отсутствует ИД существующего в БД мембера,
//                                // то дальнейшая проверка с целью обновления мембера не имеет смысла
//                                continue;
//                            }
//                            Long oldMemberThirdPartyId = getLongParam(oldMemberMap, MEMBER_THIRDPARTYID_FIELDNAME);
//                            // поиск соответствия существующего в БД мембера мемберу пришедшему из ОИС
//                            for (Map<String, Object> memberMap : memberList) {
//                                String memberTypeSysName = getStringParam(memberMap, MEMBER_TYPE_SYSNAME_FIELDNAME);
//                                Long memberThirdPartyId = getLongParam(memberMap, MEMBER_THIRDPARTYID_FIELDNAME);
//                                boolean isThirdPartyIdsEqual = (memberThirdPartyId != null) && (memberThirdPartyId.equals(oldMemberThirdPartyId));
//                                boolean isTypeSysNamesEqual = (!memberTypeSysName.isEmpty()) && (memberTypeSysName.equalsIgnoreCase(oldMemberTypeSysName));
//                                if (isThirdPartyIdsEqual && isTypeSysNamesEqual) {
//                                    // у пришедшего из ОИС мембера ThirdPartyId и тип не пустые И совпадают с соответствующими у существующего в БД мембера
//                                    // следовательно, это один и тот же мембер -  следует проверить обновляемые (те, которые могут измениться или новые) поля
//                                    boolean isUpdateNeeded = false;
//                                    // "HashCode (на контрагенте) – хэш ФИОДУЛДР" (Хешированное значение ФИОДУЛДР)
//                                    // HashCode - новое поле, у имеющихся в БД мемберов может быть еще не заполнено, в этом случае следует выполнить обновление
//                                    String oldMemberHashCode = getStringParam(oldMemberMap, MEMBER_HASHCODE_FIELDNAME);
//                                    String memberHashCode = getStringParam(memberMap, MEMBER_HASHCODE_FIELDNAME);
//                                    if (!memberHashCode.equals(oldMemberHashCode)) {
//                                        isUpdateNeeded = true;
//                                    }
//                                    // todo: при необходимости - добавить еще обновляемых/новых полей для проверки
//                                    if (isUpdateNeeded) {
//                                        // обновляемые (те, которые могут измениться или новые) поля содержат отличающиеся значения
//                                        // следует выполнить обновление данных мембера
//                                        memberMap.put("MEMBERID", oldMemberId); // обязательный параметр при обновлении - проверен на null ранее
//                                        memberMap.put("CONTRID", existedContractId);
//                                        callService(B2BPOSWS, "dsB2BMemberUpdate", memberMap, logger.isDebugEnabled(), login, password);
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (!isInsurerExist) {
                    for (Map<String, Object> memberMap : memberList) {
//                            String memberTypeSysName = getStringParam(memberMap, MEMBER_TYPE_SYSNAME_FIELDNAME);
//                            if ("insurer".equalsIgnoreCase(memberTypeSysName)) {
                        memberMap.put("CONTRID", existedContractId);
                        this.callService(B2BPOSWS, "dsB2BMemberCreate", memberMap, login, password);
                        //  }
                    }
                    //}
                    //
                    processContrExtMap(contrMap, searchContrResult, login, password);
                    /*  for (Map<String, Object> memberMap : memberList) {
                    memberMap.put("CONTRID", searchContrResult.get("CONTRID"));
                    Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BMemberCreate", memberMap, login, password);
                }*/
                }
                return searchContrResult;
            } else {

                List<Map<String, Object>> memberList = (List<Map<String, Object>>) contrMap.get("MEMBERLIST");
                contrMap.remove("MEMBERLIST");
                if ((contrMap.get("PRODVERID") != null) && (!"0".equals(contrMap.get("PRODVERID").toString()))) {
                    Map<String, Object> contrnodeMap = createContractNode(login, password);
                    contrMap.put("CONTRNODEID", contrnodeMap.get("CONTRNODEID"));
                    contrnodeMap.put("EXTERNALID", contrMap.get("EXTERNALID"));
                    contrnodeMap.put("CONTRID", createContract(contrMap, login, password));
                    Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
                    if (contrExtMap == null) {
                        contrExtMap = new HashMap<String, Object>();
                    }
                    contrExtMap.put("CONTRID", contrnodeMap.get("CONTRID"));
                    Long prodVerId = getLongParam(contrMap.get("PRODVERID"));
                    Map<String, Object> configParams = new HashMap<String, Object>();
                    configParams.put("PRODVERID", prodVerId);
                    Long hbDataVerExtAttrId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "HBDATAVERID"));

                    contrExtMap.put("HBDATAVERID", hbDataVerExtAttrId);
                    createContractValues(contrExtMap, login, password);

                    updateContractNode(contrnodeMap, login, password);

                    Map<String, Object> userInfo = findDepByLogin(login, password);
                    // продавца мы не ищем, в B2B не используется старый механизм продавцов (хотя поле в договоре осталось)
        /* Long sellerId = getSellerId(userInfo, login, password);
         contract.put("SELLERID", sellerId); */
                    Map<String, Object> accountFindParams = new HashMap<String, Object>();
                    accountFindParams.put("LOGIN", login);
                    accountFindParams.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> accountFindResult = this.callService(
                            Constants.ADMINWS, "admAccountFind",
                            accountFindParams, login, password
                    );

                    Map<String, Object> rightsParams = new HashMap<>();
                    rightsParams.put("CONTRID", contrnodeMap.get("CONTRID"));
                    rightsParams.put("USERACCOUNTID", 48010);
                    if (accountFindResult.get(RESULT) != null) {
                        List<Map<String, Object>> accList = (List<Map<String, Object>>) accountFindResult.get(RESULT);
                        if (!accList.isEmpty()) {
                            accountFindResult = accList.get(0);
                            if (accountFindResult.get("USERACCOUNTID") != null) {
                                rightsParams.put("USERACCOUNTID", accountFindResult.get("USERACCOUNTID"));
                            }
                        }
                    }
                    rightsParams.put("UPDATETEXT", "Создан договор");
                    rightsParams.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    Map<String, Object> rightsres = this.callService(B2BPOSWS, "dsB2BContractCreateOrgStructAndHist", rightsParams, login, password);

                    if (contrnodeMap.get("CONTRID") != null) {
                        //TODO  добавить перевод состояния в нужное через аспект состояний.
                        if (contrMap.get("STATESYSNAME") != null) {
                            // состояние определилось. пробуем перевести в него договор после создания.
                            String fromStateSysName = "B2B_CONTRACT_DRAFT";

                            String toStateSysName = contrMap.get("STATESYSNAME").toString();
                            contrMap.put("CONTRID", contrnodeMap.get("CONTRID"));
                            contrMap.put("STATESYSNAME", fromStateSysName);
                            contrMap.put("CHANGETYPE", "INSERTING");

                            contractMakeTrans(contrMap, toStateSysName, login, password);

                        }
                    }

                    for (Map<String, Object> memberMap : memberList) {
                        memberMap.put("CONTRID", contrnodeMap.get("CONTRID"));
                        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BMemberCreate", memberMap, login, password);
                    }
                    return contrnodeMap;
                }
            }
        }
        return null;
    }

    private void processContrExtMap(Map<String, Object> contrMap, Map<String, Object> searchContrResult, String login, String password) throws Exception {
        Long prodVerId = getLongParam(contrMap.get("PRODVERID"));

        Map<String, Object> configParams = new HashMap<String, Object>();
        Map<String, Object> contrExtParam = (Map<String, Object>) contrMap.get("CONTREXTMAP");
        configParams.put("PRODVERID", prodVerId);
        Long hbDataVerExtAttrId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "HBDATAVERID"));
        Map<String, Object> newcontrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
        if (newcontrExtMap == null) {
            newcontrExtMap = new HashMap<>();
        }
        if (searchContrResult.get("CONTRID") != null) {
            // проверяем существование extMap если есть - обновляем иначе создаем
            Map<String, Object> contrExtMap = loadContractValues(getLongParam(searchContrResult.get("CONTRID")), hbDataVerExtAttrId, login, password);
            if (contrExtMap != null) {
                if (contrExtMap.get("CONTREXTID") != null) {
                    if (contrExtParam != null) {
                        newcontrExtMap.putAll(contrExtParam);
                    }
                    newcontrExtMap.put("CONTREXTID", contrExtMap.get("CONTREXTID"));
                    newcontrExtMap.put("HBDATAVERID", hbDataVerExtAttrId);
                    newcontrExtMap.put("CONTRID", searchContrResult.get("CONTRID"));
                    updateContractValues(newcontrExtMap, login, password);
                } else {
                    newcontrExtMap = new HashMap<>();
                    if (contrExtParam != null) {
                        newcontrExtMap.putAll(contrExtParam);
                    }
                    newcontrExtMap.put("HBDATAVERID", hbDataVerExtAttrId);
                    newcontrExtMap.put("CONTRID", searchContrResult.get("CONTRID"));
                    createContractValues(newcontrExtMap, login, password);
                }
            }

        } else {
            if (newcontrExtMap == null) {
                newcontrExtMap = new HashMap<>();
            }
            if (contrExtParam != null) {
                newcontrExtMap.putAll(contrExtParam);
            }
//            newcontrExtMap.putAll(contrExtParam);
            newcontrExtMap.put("HBDATAVERID", hbDataVerExtAttrId);
            newcontrExtMap.put("CONTRID", contrMap.get("CONTRID"));
            createContractValues(newcontrExtMap, login, password);
        }
    }

    private Map<String, Object> createContractNode(String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("RVERSION", 0L);
        params.put("LASTVERNUMBER", 0L);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BContractNodeCreate", params, login, password);
        if (res.get(RESULT) != null) {
            return (Map<String, Object>) res.get(RESULT);
        }
        logger.debug("createContractNode end");
        return null;
    }

    private Object createContract(Map<String, Object> contrMap, String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrMap);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callExternalService(B2BPOSWS, "dsB2BFastContractCreate", params, login, password);
        if (res.get("CONTRID") != null) {
            return res.get("CONTRID");
        }

        logger.debug("createContractNode end");
        return null;
    }

    private Object createContractExt(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrExtMap);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BFastContractExtensionCreate", params, login, password);
        if (res.get("CONTRID") != null) {
            return res.get("CONTRID");
        }
        logger.debug("createContractNode end");
        return null;
    }


    private Object updateContract(Map<String, Object> contrMap, String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();

        params.putAll(contrMap);
        //TODO: убрать из мапы stateid
        params.remove("STATEID");
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BFastContractUpdate", params, login, password);
        if (res.get("CONTRID") != null) {
            //TODO: добавить перевод состояния.
            if ((contrMap.get("STATESYSNAME") != null) && (contrMap.get("FROMSTATESYSNAME") != null)) {
                // состояние определилось. пробуем перевести в него договор после создания.
                String fromStateSysName = contrMap.get("FROMSTATESYSNAME").toString();
                ;
                String toStateSysName = contrMap.get("STATESYSNAME").toString();
                if (!fromStateSysName.equalsIgnoreCase(toStateSysName)) {
                    //contrMap.put("CONTRID",res.get("CONTRID"));
                    contrMap.put("STATESYSNAME", fromStateSysName);
                    contractMakeTrans(contrMap, toStateSysName, login, password);
                }
            }

            return res.get("CONTRID");
        }
        logger.debug("createContractNode end");
        return null;
    }

    private void updateContractNode(Map<String, Object> contrnodeMap, String login, String password) throws Exception {
        this.callService(B2BPOSWS, "dsB2BContractNodeUpdate", contrnodeMap, login, password);
    }

}
