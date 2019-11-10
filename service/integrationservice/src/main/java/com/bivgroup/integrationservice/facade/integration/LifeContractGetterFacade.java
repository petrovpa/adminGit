package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.sberinsur.esb.partner.shema.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@BOName("LifeContractGetter")
public class LifeContractGetterFacade extends IntegrationBaseFacade {

    private static final String FOLDER_PATH = "CONTRACTDATA_FOLDER";
    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetContractList(Map<String, Object> params) throws Exception {

        updateProgramsCache();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long policyId = getLongParam(params, "POLICYID");
        Map<String, Object> result = new HashMap<>();

        try {
            GetObjType got = new GetObjType();

            // TODO: убрать тестовый ид, при пустом ид - не дергать клиентский севрис
            policyId = 14514150L;
            if (params.get("POLICYID") != null) {
                logger.info("LK get Full contr info: " + policyId);
                policyId = Long.valueOf(params.get("POLICYID").toString());
            }
            got.setPolicyId(policyId);

            ListContractType resContrList = callLifePartnerGetContracts(got);
            List<ContractType> ccList = resContrList.getContract();
            for (ContractType contr : ccList) {

                if (contr.getProductName() == null) {
                    if (params.get("PRODPROGSYSNAME") != null) {
                        contr.setProductName(params.get("PRODPROGSYSNAME").toString());
                    }
                }
                Map<String, Object> contrMapNew = new HashMap<>();
                // не безопасно
                //contrMapNew.putAll(params);
                contrMapNew.put("POLICYID",policyId);
                Map<String, Object> contrMap = mapContract(contr, params, login, password);
                updContrStateByFullInfo(contrMap, params, login, password);
                try {
                    updBAByFullInfo(contr, params, login, password);
                } catch (Exception e) {

                }
                if (contrMap == null) {
                    contrMap = new HashMap<>();
                }
                contrMapNew.putAll(contrMap);

                Object prodProgName = params.get("PRODPROGNAME");
                if (prodProgName != null) {
                    contrMapNew.put("PRODPROGNAME", prodProgName);
                }
                Object prodProgSysName = params.get("PRODPROGSYSNAME");
                if (prodProgSysName != null) {
                    contrMapNew.put("PRODPROGSYSNAME", prodProgSysName);
                }

                // получение программы с краткой информации договора
                result.put("CONTRMAP", contrMapNew);

                //this.callService(B2BPOSWS, "", params, login, password);
            }

            //    String goltXML = this.marshall(got, GetObjListType.class);
            //    String contractListRespXML = this.marshall(resContrList, ListContract.class);
            //    result.put("requestStr", goltXML);
            //    result.put("responseStr", contractListRespXML);
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;
    }

    private void updBAByFullInfo(ContractType contr, Map<String, Object> params, String login, String password) throws Exception {
        ListInvestCoverageType lict = contr.getInvestCoverage();
        if (lict != null) {
        List<InvestCoverageType> icList = lict.getInvestCoverage();
        Map<String,Object> extMap = new HashMap<>();
            if ((icList != null) && (!icList.isEmpty())) {
        extMap = processInvestCoverage(extMap, icList, login, password);
        boolean isNeedUpd = false;
        if ((params.get("BASEACTIVE") == null) && (extMap.get("BASEACTIVE") != null)) {
            isNeedUpd = true;
        }
        if ((params.get("BASEACTIVECODE") == null) && (extMap.get("BASEACTIVECODE") != null)) {
            isNeedUpd = true;
        }
        if ((params.get("INVESTCONDITION") == null) && (extMap.get("INVESTCONDITION") != null)) {
            isNeedUpd = true;
        }
        if ((params.get("INVESTCONDITIONCODE") == null) && (extMap.get("INVESTCONDITIONCODE") != null)) {
            isNeedUpd = true;
        }
        if (isNeedUpd) {
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("EXTERNALID", getDoubleParam(params.get("EXTERNALID")).longValue());
            searchParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> searchContrResult = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", searchParams, login, password);
            if (searchContrResult.get("CONTRID") != null) {
                Long prodVerId = getLongParam(searchContrResult.get("PRODVERID"));
                Map<String, Object> configParams = new HashMap<String, Object>();
                configParams.put("PRODVERID", prodVerId);
                Long hbDataVerExtAttrId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "HBDATAVERID"));
                Map<String, Object> contrExtMap = loadContractValues(getLongParam(searchContrResult.get("CONTRID")), hbDataVerExtAttrId, login, password);
                if (contrExtMap != null) {
                    if (contrExtMap.get("CONTREXTID") != null) {
                        if (extMap != null) {
                            contrExtMap.putAll(extMap);
                        }
                        updateContractValues(contrExtMap, login, password);
                    } else {
                        if (extMap != null) {
                            contrExtMap.putAll(extMap);
                        }
                        createContractValues(contrExtMap, login, password);
                    }
                } else {
                    contrExtMap = new HashMap<>();
                    if (extMap != null) {
                        contrExtMap.putAll(extMap);
                    }
                    contrExtMap.put("HBDATAVERID", hbDataVerExtAttrId);
                    contrExtMap.put("CONTRID", searchContrResult.get("CONTRID"));
                    createContractValues(contrExtMap, login, password);
                }

                    }
                }
            }
        }
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetFileContractList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<>();

        try {
            ListContractType resContrList = null;
            String fldContract = getCoreSettingBySysName(FOLDER_PATH, login, password);
            if (!fldContract.isEmpty()) {
                resContrList = doContractFileLoad(fldContract);
            }

            if (resContrList == null) {
                result.put("responseStr", "contract not found");
                result.put("STATUS", "outERROR");
                return result;
            }

            List<Map<String, Object>> retList = new ArrayList<>();
            List<ContractType> ccList = resContrList.getContract();
            logger.error("contract struct start");
            int i = 1;

            for (ContractType contr : ccList) {

                if (contr.getProductName() == null) {
                    if (params.get("PRODPROGSYSNAME") != null) {
                        contr.setProductName(params.get("PRODPROGSYSNAME").toString());
                    }
                }
                Map<String, Object> contrMapNew = new HashMap<>();
                contrMapNew.putAll(params);
                Map<String, Object> contrMap = mapContract2(contr, params, login, password);
//                updContrStateByFullInfo(contrMap, params, login, password);
                contrMapNew.putAll(contrMap);

                Object prodProgName = params.get("PRODPROGNAME");
                if (prodProgName != null) {
                    contrMapNew.put("PRODPROGNAME", prodProgName);
                }
                Object prodProgSysName = params.get("PRODPROGSYSNAME");
                if (prodProgSysName != null) {
                    contrMapNew.put("PRODPROGSYSNAME", prodProgSysName);
                }

                // получение программы с краткой информации договора
                //result.put("CONTRMAP", contrMapNew);
                retList.add(contrMapNew);
                //logger.error("contract:" + i);
                i++;
            }
            logger.error("contract struct finish");

            result.put("CONTRLIST", retList);
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            // stack trace as a string
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;
    }
    
    private void updContrStateByFullInfo(Map<String, Object> contrMap, Map<String, Object> params, String login, String password) {
        String cutContrStateId = getStringParam(params, "STATESYSNAME");
        String fullContrStateId = getStringParam(contrMap, "STATESYSNAME");
        if ((cutContrStateId != null) && (fullContrStateId != null)) {
            if (!cutContrStateId.equalsIgnoreCase(fullContrStateId)) {
                try {
                    if (params.get("POLICYID") != null) {
                        params.put("EXTERNALID", params.get("POLICYID"));
                    }
                    if (params.get("EXTERNALID") != null) {

                        Map<String, Object> searchParams = new HashMap<>();
                        searchParams.put("EXTERNALID", getDoubleParam(params.get("EXTERNALID")).longValue());
                        searchParams.put("ReturnAsHashMap", "TRUE");
                        Map<String, Object> searchContrResult = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", searchParams, login, password);
                        if (searchContrResult.get("CONTRID") != null) {
                            Map<String, Object> contrMapToTrans = new HashMap<>();
                            contrMapToTrans.put("STATESYSNAME", cutContrStateId);
                            contrMapToTrans.put("CONTRID", searchContrResult.get("CONTRID"));
                            contractMakeTrans(contrMapToTrans, fullContrStateId, login, password);
                        }
                    }

                } catch (Exception e) {
                    logger.error("Error make trans while fullContractInfo getting", e);
                    //e.printStackTrace();
                }
            }
        }
    }

    private Map<String, Object> mapContract(ContractType contr, Map<String, Object> params, String login, String password) throws Exception {
        logger.info("startMapContractFullInfo");
        Map<String, Object> contrMap = new HashMap<>();
        //ContractCut.PolicyProgram
        String programName = contr.getPolicyProgram();
        Route route = PROGRAMS.get(programName);
        if (route == null) {
            logger.error("Programs cache has no '" + programName + "' program;");
            return null;
        }
        route.loadData();
        Long productVersionId = route.getProdVerId();
        if (productVersionId == null) {
            logger.error("LifeContractGetterFacade#mapContract: PROVERID not found to program '" + programName + "'");
        } else {
            contrMap.put("PRODVERID", productVersionId);
        }

        // согласно документу "Маппинг_поля_Продукты CXSD v12.xlsx" программа - выводится в поле "Наименование продукта" в лк
        Map<String, Object> getProdConfIDParams = new HashMap<>();
        Long prodVerId = getLongParam(contrMap, "PRODVERID");
        if (prodVerId != null) {
            getProdConfIDParams.put("PRODVERID", prodVerId);
            getProdConfIDParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> prodConfMap = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password);
            if (prodConfMap != null) {
                if (prodConfMap.get(RESULT) != null) {
                    prodConfMap = (Map<String, Object>) prodConfMap.get(RESULT);
                } else {
                 //   logger.error("getFullInfo: prodconf not found");
                }
            } else {
                logger.error("getFullInfo: prodconf not found");
            }
            Long prodConfId = getLongParam(prodConfMap, "PRODCONFID");
            if (prodConfId != null) {
                //Long prodConfId = (Long) this.callServiceAndGetOneValue(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
                Map<String, Object> prodParam = new HashMap<>();
                prodParam.put("PRODCONFID", prodConfId);
                prodParam.put("HIERARCHY", true);
                prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
                Map<String, Object> prodMap = this.callService(B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
                if (prodMap.get(RESULT) != null) {
                    prodMap = (Map<String, Object>) prodMap.get(RESULT);
                } else {
                    logger.error("getFullInfo: prodStruct browse error");
                }
                Long prodProgId = route.getProdProgId();
                Map<String, Object> prodProgMap = route.getProgramMap();
                contrMap.put("PRODUCTMAP", prodMap);
                if (prodProgId != null) {
                    filterProdMapRisksByProgramm(prodMap, prodProgMap);
                    contrMap.put("PRODPROGID", prodProgId);
                    contrMap.put("PRODPROGNAME", prodProgMap.get("NAME"));
                    contrMap.put("PRODPROGSYSNAME", prodProgMap.get("SYSNAME"));
                    contrMap.put("PROGTYPENAME", prodProgMap.get("NOTE"));
                } else {
                    // если пустой, то мапа просто остается без него.
                }

                Map<String, Object> productMap = (Map<String, Object>) contrMap.get("PRODUCTMAP");

                //Contract.PolicySeries +' ' + Contract.PolicyNumber
                contrMap.put("CONTRPOLSER", contr.getPolicySeries());
                contrMap.put("CONTRPOLNUM", contr.getPolicyNumber());
                //MonthBetween(Contract.PolicyEndDate+1,Contract.PolicyStartDate)
                //Contract.PolicyStartDate
                contrMap.put("STARTDATE", processDate(contr.getPolicyStartDate()));
                //Contract.PolicyEndDate
                contrMap.put("FINISHDATE", processDate(contr.getPolicyEndDate()));
                contrMap.put("MONTHDURATION", yearsBetween(getDateParam(contrMap.get("STARTDATE")), getDateParam(contrMap.get("FINISHDATE"))));

                //Contract.Currency
                contrMap.put("INSAMCURRENCYID", CURRENCYMAP.getKey(contr.getCurrency()));
                contrMap.put("PREMCURRENCYID", CURRENCYMAP.getKey(contr.getCurrency()));
                Map<String, Object> currMap = getCurrencyById(CURRENCYMAP.getKey(contr.getCurrency()), login, password);
                contrMap.put("INSAMCURRENCYNAME", currMap.get("Name"));
                contrMap.put("PREMCURRENCYNAME", currMap.get("Name"));

                ListInvestCoverageType lic = contr.getInvestCoverage();
                Map<String, Object> contrExtMap = new HashMap<>();
                if (lic != null) {
                    List<InvestCoverageType> icList = lic.getInvestCoverage();
                    // исходя из фт - базовый актив, и инвестиционное состояние - должны быть в 1 экземпляре, поэтому, идем по списку, и первое не пустое поле сохраняем
                    // в расширенных атрибутаз.
                    //nvl(InvestCoverage.BaseActive, InvestCoverage.InvestConditions)
                    if (icList != null) {
                        for (InvestCoverageType investCoverage : icList) {
                            if (investCoverage != null) {
                                if (!contrExtMap.containsKey("BASEACTIVE")) {
                                    if ((investCoverage.getBaseActive() != null) && (!investCoverage.getBaseActive().isEmpty())) {
                                        contrExtMap.put("BASEACTIVE", investCoverage.getBaseActive());
                                    } else {
                                        logger.error("getFullInfo: investCoverage.getBaseActive is null");
                                    }
                                } else {
                                    logger.error("getFullInfo: contrExtMap[BASEACTIVE] is null");
                                }
                                if (!contrExtMap.containsKey("BASEACTIVECODE")) {
                                    if ((investCoverage.getBaseActiveCode() != null) && (!investCoverage.getBaseActiveCode().isEmpty())) {
                                        contrExtMap.put("BASEACTIVECODE", investCoverage.getBaseActiveCode());
                                    } else {
                                        logger.error("getFullInfo: investCoverage.getBaseActiveCode is null");
                                    }
                                } else {
                                    logger.error("getFullInfo: contrExtMap[BASEACTIVE] is null");
                                }
                                if (!contrExtMap.containsKey("INVESTCONDITION")) {
                                    if ((investCoverage.getInvestConditions() != null) && (!investCoverage.getInvestConditions().isEmpty())) {
                                        contrExtMap.put("INVESTCONDITIONCODE", investCoverage.getInvestConditions());
                                        Map<String, Object> baseActiveMap = getBaseActiveMap(investCoverage.getInvestConditions(), login, password);
                                        contrExtMap.put("INVESTCONDITION", baseActiveMap.get("NAME"));

                                    } else {
                                        logger.error("getFullInfo: investCoverage.getInvestConditions is null");
                                    }
                                } else {
                                    logger.error("getFullInfo: contrExtMap[INVESTCONDITION] is null");
                                }
                            } else {
                                logger.error("getFullInfo: investCoverage is null");
                            }
                        }
                    } else {
                        logger.error("getFullInfo: getInvestCoverageList return null");
                    }
                } else {
                    logger.error("getFullInfo: getInvestCoverage return null");
                }
                //Contract.WarrantyLevel
                if (contr.getWarrantyLevel() != null) {
                    contrExtMap.put("WARRANTYLEVEL", contr.getWarrantyLevel().doubleValue());
                } else {
                    logger.error("getFullInfo: contr.getWarrantyLevel() is null");
                }
                ListCoverageType lc = contr.getCoverageList();
                if (lc != null) {
                    List<Coverage> cList = lc.getCoverage();
                    Double coverageDetAmountPremSum = 0.0;
                    Double coverageDetInsSum = 0.0;
                    Double periodPayRentSum = 0.0;
                    if (cList != null) {
                        for (Coverage coverage : cList) {
                            if (coverage != null) {
                                String coverageSysname = coverage.getCoverageName();
                                ListCoverageDetType lcd = coverage.getCoverageDetList();
                                List<CoverageDet> cdList = lcd.getCoverageDet();
                                for (CoverageDet coverageDet : cdList) {
                                    if (coverageDet.getAmountAssured() != null) {
                                        Double amountAssured = coverageDet.getAmountAssured().doubleValue();
                                        coverageDetInsSum += amountAssured;
                                        if (coverageSysname.contains("LIFE_OF_TERM")) {
                                            periodPayRentSum += amountAssured;
                                        }
                                    } else {
                                        logger.error("getFullInfo: contr.getCoverageList().getCoverage[].coverage.coverageDet.getAmountAssured() is null");
                                    }

                                    if (coverageDet.getAmountPrem() != null) {
                                        coverageDetAmountPremSum += coverageDet.getAmountPrem().doubleValue();
                                    } else {
                                        logger.error("getFullInfo: contr.getCoverageList().getCoverage[].coverage.coverageDet.getAmountPrem() is null");
                                    }
                                }
                            } else {
                                logger.error("getFullInfo: contr.getCoverageList().getCoverage[].coverage is null");
                            }
                        }
                    } else {
                        logger.error("getFullInfo: contr.getCoverageList().getCoverage is null");
                    }
                    contrExtMap.put("COVDETINSSUM", coverageDetInsSum.doubleValue());
                    contrExtMap.put("COVDETPREMSUM", coverageDetAmountPremSum.doubleValue());
                    contrExtMap.put("PERIODPAYRENTSUM", periodPayRentSum.doubleValue());
                } else {
                    logger.error("getFullInfo: contr.getCoverageList() is null");
                }
                //сумма по [CoverageDet.AmountPrem]

                //Отчет по доходности, столбец Гарантия
                //Отчет по доходности. Столбец Страховая сумма
                //Из файла Отчет по доходности. Столбец Выкупная сумма
                //Contract.PolicyStatus
                Map<String, Object> stateMap = getStateMapByName(contr.getPolicyStatus());
                if (stateMap != null) {
                    contrMap.put("STATEID", stateMap.get("STATEID"));
                    contrMap.put("STATESYSNAME", stateMap.get("STATENAME"));
                } else {
                    contrMap.put("STATEID", getStateIdByName(contr.getPolicyStatus()));
                }
                contrMap.put("STATENAME", contr.getPolicyStatus());

                //Contract.PaymentPeriodicity
                contrMap.put("PAYVARSYSNAME", PERIODICITYMAP.getKey(contr.getPaymentPeriodicity()));
                Map<String, Object> payVarMap = getPayVarIdBySysName(PERIODICITYMAP.getKey(contr.getPaymentPeriodicity()), login, password);
                if (payVarMap.get("PAYVARID") != null) {
                    contrMap.put("PAYVARID", payVarMap.get("PAYVARID"));
                } else {
                    logger.error("getFullInfo: PAYVARID is null");
                }
                if (payVarMap.get("NAME") != null) {
                    contrMap.put("PAYVARNAME", payVarMap.get("NAME"));
                } else {
                    logger.error("getFullInfo: PAYVARNAME is null");
                }

                //Contract.CalculatedFields.RegularPaym
                if (contr.getCalculatedFields() != null) {
                    //Contract.CalculatedFields.NextPayDate
                    if (contr.getCalculatedFields().getNextPayDate() != null) {
                        contrExtMap.put("NEXTPAYDATE", processDate(contr.getCalculatedFields().getNextPayDate()));
                    }
                    if (contr.getCalculatedFields().getRegularPaym() != null) {
                        contrExtMap.put("REGULARPAYM", contr.getCalculatedFields().getRegularPaym().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getRegularPaym() is null");
                    }
                    //Contract.CalculatedFields.PayArrears
                    if (contr.getCalculatedFields().getPayArrears() != null) {
                        contrExtMap.put("PAYARREARS", contr.getCalculatedFields().getPayArrears().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPayArrears() is null");
                    }
                    //Contract.CalculatedFields.PaymentTerm
                    contrExtMap.put("PAYMENTTERM", contr.getCalculatedFields().getPaymentTerm());
                    //Contract.CalculatedFields.SumPaySum
                    if (contr.getCalculatedFields().getSumPaySum() != null) {
                        contrExtMap.put("SUMPAYSUM", contr.getCalculatedFields().getSumPaySum().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getSumPaySum() is null");
                    }
                    //Contract.CalculatedFields.AccumPeriodStartDate +' - '+Contract.CalculatedFields.AccumPeriodEndDate
                    if (contr.getCalculatedFields().getAccumPeriodStartDate() != null) {
                        contrExtMap.put("ACCUMPSTARTDATE", processDate(contr.getCalculatedFields().getAccumPeriodStartDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getAccumPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getAccumPeriodEndDate() != null) {
                        contrExtMap.put("ACCUMPENDDATE", processDate(contr.getCalculatedFields().getAccumPeriodEndDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getAccumPeriodEndDate() is null");
                    }

                    //Contract.CalculatedFields.PayPeriodStartDate+' - '+Contract.CalculatedFields.PayPeriodEndDate
                    if (contr.getCalculatedFields().getPayPeriodStartDate() != null) {
                        contrExtMap.put("PAYPSTARTDATE", processDate(contr.getCalculatedFields().getPayPeriodStartDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPayPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getPayPeriodEndDate() != null) {
                        contrExtMap.put("PAYPENDDATE", processDate(contr.getCalculatedFields().getPayPeriodEndDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPayPeriodEndDate() is null");
                    }

                    //Contract.CalculatedFields.GarPeriodStartDate+' - '+Contract.CalculatedFields.GarPeriodEndDate
                    if (contr.getCalculatedFields().getGarPeriodStartDate() != null) {
                        contrExtMap.put("GARPSTARTDATE", processDate(contr.getCalculatedFields().getGarPeriodStartDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getGarPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getGarPeriodEndDate() != null) {
                        contrExtMap.put("GARPENDDATE", processDate(contr.getCalculatedFields().getGarPeriodEndDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getGarPeriodEndDate() is null");
                    }
//todo: Кол-во лет между датами (Contract.CalculatedFields.PaymPeriodStartDate, Contract.CalculatedFields.PaymPeriodEndDate), округление в большую сторону, т.е. 13 месяцев это 2 года, а 1  месяц это 1 год
                    if ((contr.getCalculatedFields().getPaymPeriodStartDate() != null) && (contr.getCalculatedFields().getPaymPeriodEndDate() != null)) {
                        Date startPaymDate = (Date) processDate(contr.getCalculatedFields().getPaymPeriodStartDate());
                        Date finishPaymDate = (Date) processDate(contr.getCalculatedFields().getPaymPeriodEndDate());
                        contrExtMap.put("PAYPERIODINYEAR", calcYears(startPaymDate, finishPaymDate));
                    }

                    //Contract.CalculatedFields.PaymPeriodStartDate +' - '+ Contract.CalculatedFields.PaymPeriodEndDate
                    if (contr.getCalculatedFields().getPaymPeriodStartDate() != null) {
                        contrExtMap.put("PAYMPSTARTDATE", processDate(contr.getCalculatedFields().getPaymPeriodStartDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPaymPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getPaymPeriodEndDate() != null) {
                        contrExtMap.put("PAYMPENDDATE", processDate(contr.getCalculatedFields().getPaymPeriodEndDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPaymPeriodEndDate() is null");
                    }

                    //Contract.CalculatedFields.RentSum
                    if (contr.getCalculatedFields().getRentSum() != null) {
                        contrExtMap.put("RENTSUM", contr.getCalculatedFields().getRentSum().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getRentSum() is null");
                    }

                    //Contract.CalculatedFields.CurrentPay
                    if (contr.getCalculatedFields().getCurrentPay() != null) {
                        contrExtMap.put("CURRENTPAY", contr.getCalculatedFields().getCurrentPay().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getCurrentPay() is null");
                    }
                    //Contract.CalculatedFields.RemainsToPay
                    if (contr.getCalculatedFields().getRemainsToPay() != null) {
                        contrExtMap.put("REMAINSTOPAY", contr.getCalculatedFields().getRemainsToPay().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getRemainsToPay() is null");
                    }
                } else {
                    logger.error("getFullInfo: contr.getCalculatedFields() is null");
                }

                //Файл Инвестиционная доходность. Столбец ДИД
                //Из файла Инвестиционная доходность. Столбец Гарантированная страховая сумма …
                //Из файла Отчет по доходности. Столбец КУ
                //Contract.PolicyDocDate
                contrMap.put("DOCUMENTDATE", processDate(contr.getPolicyDocDate()));

                memberProcess(contrMap, contr, login, password);
                prodStructProcess(contrMap, productMap, contr, login, password);
                payScheduleProcess(contrMap, contr, login, password);
                payFactProcess(contrMap, contr, login, password);

                //Contract.Coverage.Periodicity покрытия список - по идее это наши риски. периодичность одинаковая, или ее надо хранить в расширенных атрибутах рисков.
                if (contr.getCoverageList() != null) {
                    if (contr.getCoverageList().getCoverage() != null) {
                        if (!contr.getCoverageList().getCoverage().isEmpty()) {
                            if (contr.getCoverageList().getCoverage().get(0) != null) {
                                if (contr.getCoverageList().getCoverage().get(0).getPeriodicity() != null) {
                                    contrExtMap.put("COVPAYVARSYSNAME", PERIODICITYMAP.getKey(contr.getCoverageList().getCoverage().get(0).getPeriodicity()));
                                    Map<String, Object> covPayVarMap = getPayVarIdBySysName(PERIODICITYMAP.getKey(contr.getCoverageList().getCoverage().get(0).getPeriodicity()), login, password);
                                    if (covPayVarMap.get("PAYVARID") != null) {
                                        contrExtMap.put("COVPAYVARID", covPayVarMap.get("PAYVARID"));
                                    } else {
                                        logger.error("getFullInfo: PAYVARID is null");
                                    }
                                    if (covPayVarMap.get("NAME") != null) {
                                        contrExtMap.put("COVPAYVARNAME", covPayVarMap.get("NAME"));
                                    } else {
                                        logger.error("getFullInfo: PAYVARNAME is null");
                                    }

                                }
                            }
                        }
                    }
                }

                //Contract.CoverageDet.AmountAssured (где  CoverageName = ~ LIFE_OF_TERM)
                //Parameter_List.Dict_Credit_Number
                ListParameterType lp = contr.getParameterList();
                if (lp != null) {
                    List<ParameterType> pList = lp.getParameter();
                    if (pList != null) {
                        for (ParameterType parameter : pList) {
                            if (parameter != null) {
                                if ("Dict_Credit_Number".equalsIgnoreCase(parameter.getName())) {
                                    contrExtMap.put("DICTCREDNUMBER", parameter.getValue().toString());
                                }
                            }
                        }
                    }
                }
                contrMap.put("CONTREXTMAP", contrExtMap);
                contrMap.putAll(contrExtMap);
            } else {
                logger.error("getFullInfo: prodconf not found");
            }
//График платежей.Первый взнос
//График платежей.последний взнос (на дату)
//"ThirdParty c Role = LIFE ASSURED
//данное поле необходимо расщифровать, т.к. кроме фио м.б. и другая инфа, на картинке не видно"
//Contract.Thurdparty.FullName
//Contract.Thurdparty.BirthDate
//Contract.Thurdparty.DocumentsType.DocumentFull
//Contract.Thurdparty.ListAddress.FullAddress (где AddressType = REGISTRATION)
//
//Contract.CoverageList.Coverage.CoverageName
//Contract.CoverageList.Coverage.CoveregeDetList.Coverage.AmountAssured+' ' +Contract.CoverageList.Coverage.CoveregeDetList.Coverage.currency / Contract.CoverageList.Coverage.CoveregeDetList.Coverage.AmountAssured+' ' +Contract.CoverageList.Coverage.CoveregeDetList.Coverage.currency (если CoverageDetList >1)
// Где Contract.ThirdPartyList.Thirdparty.ThirdpartyType = BEN
// Contract.ThirdPartyList.Thirdparty.RiskCode
// Contract.ThirdPartyList.Thirdparty.Split
//CoverageList.Coverage.LifeAssureds.LifeAssured
//
//Отчет о доходности (для СмартПолис/Маяк) и "Котировки для ИСЖ купонного"+"Расчеты по купонам" (для СмартПолис купонный/Маяк купонный)
//
//
//устанавливается компанией по рез-там фин года, по году и валюте, в % (2016; RUR; 4%)
//Из файла Инвестиционная доходность Столбец ДИД на 31.12.2015 в рублях
//Файл Инвестиционная доходность Столбец "Гарантированная страховая сумма  (стрховая сумма по основной программе с учетом дохода) "
//
//ОИС
        } else {
            logger.error("getFullInfo: prodver not found");
        }
        logger.info("finishMapContractFullInfo");

        return contrMap;
    }

    private Map<String, Object> getProdProgById(Object prodProgId, Object prodVerId, String login, String password) throws Exception {
        if (prodVerId != null) {
            String prodVerIdStr = prodVerId.toString();
            if (!"0".equals(prodVerIdStr)) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("PRODVERID", prodVerId);
                param.put("PRODPROGID", prodProgId);
                param.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BProductProgramBrowseListByParam", param, login, password);
                if (res.get("PRODPROGID") != null) {
                    return res;
                }
            }
        }
        logger.error("Unknown programm " + prodProgId);
        return null;
    }

    private void filterProdMapRisksByProgramm(Map<String, Object> prodMap, Map<String, Object> prodProgMap) {
        Map<String, Object> prodVerMap = (Map<String, Object>) prodMap.get("PRODVER");
        if (prodVerMap != null) {
            if (prodVerMap.get("PRODSTRUCTS") != null) {
                List<Map<String, Object>> prodStructList = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
                for (Map<String, Object> prodStructMap : prodStructList) {
                    prodStructMap.put("toRemoveByProdProgId", 0L);
                    if (prodStructMap.get("DISCRIMINATOR") != null) {
                        if ("5".equalsIgnoreCase(getStringParam(prodStructMap.get("DISCRIMINATOR")))) {
                            if (prodStructMap.get("PRODPROGID") != null) {
                                if (prodProgMap.get("PRODPROGID") != null) {
                                    if (getStringParam(prodStructMap.get("PRODPROGID")).equalsIgnoreCase(getStringParam(prodProgMap.get("PRODPROGID")))) {
                                        prodStructMap.put("toRemoveByProdProgId", 0L);
                                    } else {
                                        prodStructMap.put("toRemoveByProdProgId", 1L);
                                    }
                                }
                            }
                        }
                    }
                }
                CopyUtils.sortByLongFieldName(prodStructList, "toRemoveByProdProgId");
                List<Map<String, Object>> prodStructFilteredList = CopyUtils.filterSortedListByLongFieldName(prodStructList, "toRemoveByProdProgId", 0L);
                prodVerMap.put("PRODSTRUCTS", prodStructFilteredList);
            }
        }

    }

    private Map<String, Object> getPayVarIdBySysName(String sysName, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SYSNAME", sysName);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BPaymentVariantBrowseListByParam", params, login, password);
        return res;
    }

    private void prodStructProcess(Map<String, Object> contrMap, Map<String, Object> productMap, ContractType contr, String login, String password) {
        List<Map<String, Object>> insObjGroupList = new ArrayList<>();
        if (productMap.get("PRODVER") != null) {
            Map<String, Object> prodverMap = (Map<String, Object>) productMap.get("PRODVER");
            if (prodverMap.get("PRODSTRUCTS") != null) {
                List<Map<String, Object>> prodStrList = (List<Map<String, Object>>) prodverMap.get("PRODSTRUCTS");
                List<Map<String, Object>> memberList = (List<Map<String, Object>>) contrMap.get("MEMBERLIST");
                for (Map<String, Object> prodStrMap : prodStrList) {
                    if (prodStrMap != null) {
                        if (prodStrMap.get("DISCRIMINATOR") != null) {
                            if ("2".equals(prodStrMap.get("DISCRIMINATOR").toString())) {
                                // формируем ТОСЫ.
                                Map<String, Object> tosMap = new HashMap<>();
                                tosMap.putAll(prodStrMap);
                                List<Map<String, Object>> insObjList = new ArrayList<>();
                                if (prodStrMap.get("CHILDS") != null) {
                                    List<Map<String, Object>> prodObjList = (List<Map<String, Object>>) prodStrMap.get("CHILDS");
                                    for (Map<String, Object> prodObjMap : prodObjList) {
                                        Map<String, Object> osMap = new HashMap<>();
                                        Map<String, Object> insObjMap = new HashMap<>();
                                        Map<String, Object> contrObjMap = new HashMap<>();
                                        insObjMap.putAll(prodObjMap);

                                        List<Map<String, Object>> riskList = new ArrayList<>();
                                        Map<String, Object> memberMap = new HashMap<>();
                                        if (prodObjMap.get("CHILDS") != null) {
                                            List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodObjMap.get("CHILDS");
                                            for (Map<String, Object> prodRiskMap : prodRiskList) {
                                                Map<String, Object> riskMap = new HashMap<>();
                                                riskMap.putAll(prodRiskMap);
                                                String riskSysName = getStringParam(riskMap.get("SYSNAME"));
                                                String riskName = getStringParam(riskMap.get("NAME"));
                                                if (!riskSysName.isEmpty()) {
                                                    ListCoverageType lc = contr.getCoverageList();
                                                    if (lc != null) {
                                                        List<Coverage> cList = lc.getCoverage();
                                                        if (cList != null) {
                                                            for (Coverage coverage : cList) {
                                                                if (riskSysName.equalsIgnoreCase(coverage.getCoverageName())) {
                                                                    createMembersMap(riskMap, memberMap, coverage.getLifeAssureds(), coverage.getCoverageDetList());
                                                                    riskMap.put("STARTDATE", processDate(coverage.getPaymentStartDate()));
                                                                    riskMap.put("FINISHDATE", processDate(coverage.getPaymentEndDate()));
                                                                    mappingRisk(coverage.getCoverageDetList(), riskMap);
                                                                    riskList.add(riskMap);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        /*
                                        List<Map<String, Object>> filteredRiskList = riskList;
                                        if ((riskList != null) && (contrMap != null) && (getLongParam(contrMap.get("PRODPROGID")) != null)) {
                                            if (!riskList.isEmpty()) {
                                                Map<String, Object> fistRiskItem = riskList.get(0);
                                                if ((fistRiskItem != null) && (fistRiskItem.get("CHILDS") != null)) {
                                                    List<Map<String, Object>> childList = (List) fistRiskItem.get("CHILDS");
                                                    Long prodProgId = (Long) contrMap.get("PRODPROGID");

                                                    // Фильтруем список рисков по prodprogid
                                                    filteredRiskList = filterRiskListByProdProgId(childList, prodProgId);
                                                }
                                            }
                                        }
                                        contrObjMap.put("CONTRRISKLIST", filteredRiskList);
                                        */
                                        contrObjMap.put("CONTRRISKLIST", riskList);

                                        osMap.put("INSOBJMAP", insObjMap);
                                        osMap.put("CONTROBJMAP", contrObjMap);

                                        insObjList.add(osMap);

                                        contrMap.put("MEMBERSMAP", memberMap);
                                        Map<String, Object> participantMap;
                                        String name;
                                        for (Map<String, Object> item : memberList) {
                                            participantMap = (Map<String, Object>) item.get("PARTICIPANTMAP");
                                            if (participantMap != null) {
                                                name = getStringParam(participantMap.get("BRIEFNAME"));
                                                if (memberMap.containsKey(name)) {
                                                    participantMap.put("RISKLIST", memberMap.get(name));
                                                }
                                            }
                                        }
                                    }
                                }
                                tosMap.put("OBJLIST", insObjList);
                                insObjGroupList.add(tosMap);
                            }
                        }
                    }
                }
            }
        }

        contrMap.put("INSOBJGROUPLIST", insObjGroupList);

    }

    /**
     * Функция фильтрации списка рисков по prodProgId
     *
     * @param riskList   - список рисков
     * @param prodProgId
     * @return
     */
    private List<Map<String, Object>> filterRiskListByProdProgId(List<Map<String, Object>> riskList, Long prodProgId) {
        return riskList.stream().filter(risk -> ((risk.get("PRODPROGID") != null) && (getLongParam(risk.get("PRODPROGID")).equals(prodProgId)))).collect(Collectors.toList());
    }

    private void createMembersMap(Map<String, Object> prodRiskMap, Map<String, Object> memberMap, Coverage.LifeAssureds lifeAssureds, ListCoverageDetType coverageDetList) {
        if (lifeAssureds == null) {
            logger.error("createMembersMap lifeAssureds: coverage.getLifeAssureds() is null");
            return;
        }
        List<Coverage.LifeAssureds.LifeAssured> lifeAssuredsList = lifeAssureds.getLifeAssured();
        if (lifeAssuredsList == null) {
            logger.error("createMembersMap lifeAssuredsList: coverage.getLifeAssureds().getLifeAssured() is null");
            return;
        }
        String riskName = getStringParam(prodRiskMap.get("NAME"));
        String memberName;
        Map<String, Object> riskMap;
        List<Map<String, Object>> riskList;
        boolean exist;
        for (Coverage.LifeAssureds.LifeAssured assured : lifeAssuredsList) {
            memberName = assured.getThirdPartyFullName();
            riskMap = new HashMap<>();
            //assured.getThirdPartyId()
            riskMap.putAll(prodRiskMap);
            mappingRisk(coverageDetList, riskMap);
            if (memberMap.containsKey(memberName)) {
                riskList = (List<Map<String, Object>>) memberMap.get(memberName);
                if (riskList != null) {
                    exist = riskList.stream().anyMatch(new Predicate<Map<String, Object>>() {
                        @Override
                        public boolean test(Map<String, Object> it) {
                            return riskName.equals(getStringParam((it.get("NAME"))));
                        }
                    });
                    if (!exist) {
                        riskList.add(riskMap);
                    }
                }
            } else {
                riskList = new ArrayList<>();
                riskList.add(riskMap);
                memberMap.put(memberName, riskList);
            }
        }
    }

    private void mappingRisk(ListCoverageDetType coverageDetList, Map<String, Object> riskMap) {
        if (coverageDetList != null) {
            if (coverageDetList.getCoverageDet() != null) {
                if (!coverageDetList.getCoverageDet().isEmpty()) {
                    if (coverageDetList.getCoverageDet().get(0).getCurrency() != null) {
                        riskMap.put("CURRENCYID", CURRENCYMAP.getKey(coverageDetList.getCoverageDet().get(0).getCurrency().value()));
                    }
                    if (coverageDetList.getCoverageDet().get(0).getAmountPrem() != null) {
                        riskMap.put("PREMVALUE", coverageDetList.getCoverageDet().get(0).getAmountPrem().doubleValue());
                    }
                    if (coverageDetList.getCoverageDet().get(0).getAmountPrem() != null) {
                        riskMap.put("INSAMVALUE", coverageDetList.getCoverageDet().get(0).getAmountAssured().doubleValue());
                    }
                }
            }
        }
    }

    private void payScheduleProcess(Map<String, Object> contrMap, ContractType contr, String login, String password) {
        ListPaymentSchedulerType lps = contr.getPaymentSchedulerList();
        if (lps != null) {

            /// вероятно ошибка схемы. судя по именам классов - тут должен быть список.
            List<PaymentSchedulerType> psList = lps.getPaymentScheduler();
            if (psList != null) {
                List<Map<String, Object>> payList = new ArrayList<>();
                BigDecimal planAccrualSum = BigDecimal.ZERO;
                BigDecimal factAccrualSum = BigDecimal.ZERO;

                for (PaymentSchedulerType paymentSheduler : psList) {
                    //          PaymentScheduler paymentSheduler = lps.getPaymentScheduler();
                    Map<String, Object> payMap = new HashMap<>();
                    if (paymentSheduler.getSchedulerSum() != null) {
                        payMap.put("AMOUNT", paymentSheduler.getSchedulerSum().doubleValue());
                        planAccrualSum = planAccrualSum.add(paymentSheduler.getSchedulerSum());
                        payMap.put("AMOUNTACCRUAL", planAccrualSum.doubleValue());
                        if (paymentSheduler.getSchedulerCurrency() != null) {
                            payMap.put("AMCURRENCY", paymentSheduler.getSchedulerCurrency().name());
                        }
                    }
                    if (paymentSheduler.getSchedulerDate() != null) {
                        payMap.put("PAYDATE", processDate(paymentSheduler.getSchedulerDate()));
                        try {
                            payMap.put("PAYDATESTR", getFormattedDateStr(processDate(paymentSheduler.getSchedulerDate()), "dd.MM.yyyy"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (paymentSheduler.getPaymentExecutionList() != null) {
                        ListPaymentExecutionType lpet = paymentSheduler.getPaymentExecutionList();
                        if (lpet.getPaymentExecution() != null) {
                            if (!lpet.getPaymentExecution().isEmpty()) {
                                BigDecimal factPaySum = BigDecimal.ZERO;
                                Date lastFactPayDate = null;
                                for (PaymentExecutionType pet : lpet.getPaymentExecution()) {
                                    if (pet.getExecutionSum() != null) {
                                        factPaySum = factPaySum.add(pet.getExecutionSum());
                                    } else {
                                        logger.error("FactPay exist but executionSum is null");
                                    }
                                    if (pet.getPaymentDate() != null) {
                                        if (lastFactPayDate == null) {
                                            lastFactPayDate = processDate(pet.getPaymentDate());
                                        } else if (lastFactPayDate.before(processDate(pet.getPaymentDate()))) {
                                            lastFactPayDate = processDate(pet.getPaymentDate());
                                        }
                                    }
                                }

                                payMap.put("FACTAMOUNT", factPaySum.doubleValue());
                                factAccrualSum = factAccrualSum.add(factPaySum);
                                payMap.put("FACTAMOUNTACCRUAL", factAccrualSum.doubleValue());
                                if (factPaySum.compareTo(paymentSheduler.getSchedulerSum()) >= 0) {
                                    payMap.put("ISFULLPAYD", true);
                                } else {
                                    payMap.put("ISFULLPAYD", false);
                                }
                                payMap.put("PAYFACTDATE", lastFactPayDate);
                                try {
                                    payMap.put("PAYFACTDATESTR", getFormattedDateStr(lastFactPayDate, "dd.MM.yyyy"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }

                    payList.add(payMap);
                }
                contrMap.put("PAYMENTSCHEDULELIST", payList);
            }
        }

    }

    private void payFactProcess(Map<String, Object> contrMap, ContractType contr, String login, String password) {
        ListPaymentType lp = contr.getPayment();
        if (lp != null) {

            /// вероятно ошибка схемы. судя по именам классов - тут должен быть список.
            List<PaymentType> pList = lp.getPayment();
            if (pList != null) {
                List<Map<String, Object>> payList = new ArrayList<>();
                for (PaymentType payment : pList) {
                    Map<String, Object> payMap = new HashMap<>();
                    if (payment.getAmmount() != null) {
                        payMap.put("AMVALUE", payment.getAmmount().doubleValue());
                    }
                    if (payment.getAmmount() != null) {
                        payMap.put("AMCURRENCYID", CURRENCYMAP.getKey(payment.getCurrency().value()));
                    }
                    if (payment.getPaymentDate() != null) {
                        payMap.put("PAYFACTDATE", processDate(payment.getPaymentDate()));
                    }
                    if (payment.getPaymentType() != null) {
                        payMap.put("NOTE", payment.getPaymentType().toString());
                    }
                    payList.add(payMap);
                }
                contrMap.put("PAYMENTLIST", payList);
            }
        }
    }

    private void memberProcess(Map<String, Object> contrMap, ContractType contr, String login, String password) throws Exception {
        ListThirdPartyType ltp = contr.getThirdPartyList();
        if (ltp != null) {
            List<ThirdParty> tpList = ltp.getThirdParty();
            if (tpList != null) {
                List<Map<String, Object>> memberList = new ArrayList<>();
                List<Map<String, Object>> beneficiaryList = new ArrayList<>();
                Map<String, Object> insurerMap = new HashMap<>();
                Map<String, Object> insuredMap = new HashMap<>();
                for (ThirdParty thirdParty : tpList) {
                    if (thirdParty != null) {
                        Map<String, Object> memberMap = new HashMap<>();
                        memberMap.put("TYPESYSNAME", ROLEMAP.getKey(thirdParty.getRole()));
                        Map<String, Object> participantMap = new HashMap<>();

                        processParticipant(participantMap, thirdParty, contr, login, password);

                        memberMap.put("PARTICIPANTMAP", participantMap);
                        if ("beneficiary".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            Map<String, Object> benMap = new HashMap<>();
                            benMap.put("PART", thirdParty.getSplit());
                            benMap.put("INSCOVERID", thirdParty.getRiskCode());
                            benMap.put("RISKCODE", thirdParty.getRiskCode());
                            //INSCOVERID
                            //     PART   
                            //        TYPEID
                            benMap.put("PARTICIPANTMAP", participantMap);
                            beneficiaryList.add(benMap);
                        }
                        if ("insured".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            insuredMap.putAll(participantMap);
                        }
                        if ("insurer".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            insurerMap.putAll(participantMap);
                        }
                        memberList.add(memberMap);
                    }
                }
                contrMap.put("MEMBERLIST", memberList);
                contrMap.put("BENEFICIARYLIST", beneficiaryList);
                contrMap.put("INSURERMAP", insurerMap);
                contrMap.put("INSUREDMAP", insuredMap);

            }
        }
    }

    protected void processParticipant(Map<String, Object> participantMap, ThirdParty thirdParty, ContractType contr, String login, String password) throws Exception {

        participantMap.put("FIRSTNAME", thirdParty.getFirstName());
        participantMap.put("LASTNAME", thirdParty.getLastName());
        participantMap.put("MIDDLENAME", thirdParty.getPatronymic());
        participantMap.put("BRIEFNAME", thirdParty.getFullName());
        participantMap.put("THIRDPARTYID", thirdParty.getThirdPartyId());
        participantMap.put("GENDER", GENDERMAP.getKey(thirdParty.getGender()));

        try {
            participantMap.put("BIRTHDATE", getFormattedDateStr(processDate(thirdParty.getBirthDate()), "dd.MM.yyyy"));
        } catch (Exception e) {
            logger.error("Birthdate member parse error", e);
        }
        participantMap.put("BIRTHPLACE", thirdParty.getBirthPlace());

        participantMap.put("documentList", processDocument(thirdParty.getDocumentsList()));
        participantMap.put("contactList", processContact(thirdParty));
        participantMap.put("addressList", processAddress(thirdParty.getListAddress()));

        participantMap.put("PARTICIPANTTYPE", THIRDPARTYTYPEMAP.getKey(thirdParty.getThirdPartyType()));

        //todo промапить поля в extAttributeList2
        participantMap.put("MaritalStatus", MARITIALMAP.getKey(thirdParty.getMaritalStatus()));
        participantMap.put("Position", thirdParty.getPosition());
        participantMap.put("EmployerName", thirdParty.getEmployerName());
        participantMap.put("education", thirdParty.getOccupation());
        participantMap.put("activityBusinessKind", thirdParty.getFrameReference());
        participantMap.put("OPF", LEGALFORMTYPEMAP.getKey(thirdParty.getLegalFormText()));
        participantMap.put("CITIZENSHIP", thirdParty.getResident());
        participantMap.put("COUNTRYID", getCountryIdByDigitCode(thirdParty.getResident(), login, password));

        participantMap.put("taxResident", thirdParty.getTaxResident());
        participantMap.put("INN", thirdParty.getTin());
        participantMap.put("OGRN", thirdParty.getOgrn());

    }

    private List<Map<String, Object>> processDocument(DocumentsListType documntsList) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (documntsList != null) {
            List<DocumentsType> dtList = documntsList.getDocument();
            if (dtList != null) {
                for (DocumentsType documentType : dtList) {
                    if (documentType != null) {
                        Map<String, Object> docMap = new HashMap<>();
                        docMap.put("DOCTYPESYSNAME", DOCUMENTTYPEMAP.getKey(documentType.getDocumentType()));
                        docMap.put("DOCSERIES", documentType.getDocumentSeries());
                        docMap.put("DOCNUMBER", documentType.getDocumentNumber());
                        try {
                            docMap.put("ISSUEDATE", getFormattedDateStr(processDate(documentType.getDocumentDate()), "dd.MM.yyyy"));
                        } catch (Exception e) {
                            logger.error("Error parse issueDate in member document", e);
                        }
                        docMap.put("ISSUEDBY", documentType.getDocumentInstitution());
                        docMap.put("ISSUERCODE", documentType.getDocumentCodeIns());
                        docMap.put("DESCRIPTION", documentType.getDocumentFull());
                        result.add(docMap);
                    }
                }

            }
        }
        return result;
    }

    private List<Map<String, Object>> processContact(ThirdParty thirdParty) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(getContactMap(thirdParty.getEmail(), "PersonalEmail"));
        result.add(getContactMap(thirdParty.getPhoneHome(), "FactAddressPhone"));
        result.add(getContactMap(thirdParty.getPhoneMobile(), "MobilePhone"));
        result.add(getContactMap(thirdParty.getPhoneWorking(), "WorkAddressPhone"));
        return result;
    }

    private List<Map<String, Object>> processAddress(ListAddressType listAddress) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (listAddress != null) {
            List<Address> aList = listAddress.getAddress();
            if (aList != null) {
                for (Address address : aList) {
                    if (address != null) {
                        Map<String, Object> addressMap = new HashMap<>();
                        addressMap.put("ADDRESSTEXT2", address.getAddressFull());
                        addressMap.put("ADDRESSTEXT1", address.getAddressTxt());
                        addressMap.put("ADDRESSTYPESYSNAME", ADDRESSTYPEMAP.getKey(address.getAddressType()));
                        addressMap.put("STREETKLADR", address.getCladrCode());
                        addressMap.put("KLADR", address.getCladrCode());
                        addressMap.put("COUNTRY", address.getCountry());
                        addressMap.put("REGION", address.getArea());
                        addressMap.put("DISTRICT", address.getDistrict());
                        addressMap.put("CITY", address.getTown());
                        addressMap.put("STREET", address.getStreet());
                        addressMap.put("HOUSE", address.getStreetNr());
                        addressMap.put("BUILD", address.getStreetBuild());
                        addressMap.put("FLAT", address.getStreetFlat());
                        addressMap.put("POSTALCODE", address.getPostCode());
                        result.add(addressMap);
                    }
                }
            }
        }
        return result;
    }

    private Map<String, Object> getContactMap(String value, String contactTypeSysName) {
        Map<String, Object> result = new HashMap<>();
        result.put("CONTACTTYPESYSNAME", contactTypeSysName);
        result.put("VALUE", value);
        return result;
    }

    public static Integer calcYears(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcYears(fromG, toG);
        }
        return result;
    }

    private Map<String, Object> getProdverByProdProgSysName(String policyProgram, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("EXTERNALID", policyProgram);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BProductProgramBrowseListByParam", param, login, password);
        if (res.get("PRODPROGID") != null) {
            return res;
        }
        logger.error("Unknown programm " + policyProgram);
        return null;
    }

    private ListContractType doContractFileLoad(String folderPath) throws Exception {
        // получить все файлы из каталога
        String[] listFile = getFileNameList(folderPath, ".xml");
        if (listFile == null) {
            return null;
    }
        String encoding = "utf-8";
        ListContractType resContrList = null;
        for (String fileName : listFile) {
            InputStream xmlStream = new FileInputStream(folderPath + "/" + fileName);
//            InputStreamReader xmlStream = new InputStreamReader(new FileInputStream(folderPath + "/" + fileName), encoding);
            logger.error("unmarshallroot start");
            resContrList = this.unmarshallroot(ListContractType.class, xmlStream);
            logger.error("unmarshallroot finish");
            break;
        }
        return resContrList;
    }

    private Map<String, Object> mapContract2(ContractType contr, Map<String, Object> params, String login, String password) throws Exception {
        logger.info("startMapContractFullInfo");
        Map<String, Object> contrMap = new HashMap<>();
        //ContractCut.PolicyProgram
        contrMap.put("PRODVERID", getProdverBySysName2(contr.getProductName(), login, password));
        //Map<String, Object> prodProgMap = getProdverByProdProgSysName(contr.getPolicyProgram(), login, password);
        //contrMap.put("PRODVERID", prodProgMap.get("PRODVERID"));

        // согласно документу "Маппинг_поля_Продукты CXSD v12.xlsx" программа - выводится в поле "Наименование продукта" в лк
        if (contrMap.get("PRODVERID") != null) {
            Long prodVerId = Long.parseLong(contrMap.get("PRODVERID").toString());
            Map<String, Object> prodConfMap = getConfigByProdver2(prodVerId, login, password);
            if ((prodConfMap != null) && (prodConfMap.get("PRODCONFID") != null)) {
                Long prodConfId = Long.parseLong(prodConfMap.get("PRODCONFID").toString());
                Map<String, Object> prodMap = getProdByConfId2(prodConfId, login, password);

                Map<String, Object> prodProgMap = null;
                prodProgMap = getProdProgBySysName2(contr.getPolicyProgram(), prodVerId, login, password);

                filterProdMapRisksByProgramm(prodMap, prodProgMap);
                contrMap.put("PRODUCTMAP", prodMap);

                Map<String, Object> productMap = (Map<String, Object>) contrMap.get("PRODUCTMAP");
                //Contract.PolicyProgramm
                //if (prodProgMap == null) {

                contrMap.put("PRODPROGID", prodProgMap.get("PRODPROGID"));
                contrMap.put("PRODPROGNAME", prodProgMap.get("NAME"));
                contrMap.put("PRODPROGSYSNAME", prodProgMap.get("SYSNAME"));
                contrMap.put("PROGTYPENAME", prodProgMap.get("NOTE"));
                //Contract.PolicySeries +' ' + Contract.PolicyNumber
                contrMap.put("CONTRPOLSER", contr.getPolicySeries());
                contrMap.put("CONTRPOLNUM", contr.getPolicyNumber());
                //MonthBetween(Contract.PolicyEndDate+1,Contract.PolicyStartDate)
                //Contract.PolicyStartDate
                contrMap.put("STARTDATE", processDate(contr.getPolicyStartDate()));
                //Contract.PolicyEndDate
                contrMap.put("FINISHDATE", processDate(contr.getPolicyEndDate()));
                contrMap.put("MONTHDURATION", yearsBetween(getDateParam(contrMap.get("STARTDATE")), getDateParam(contrMap.get("FINISHDATE"))));

                //Contract.Currency
                contrMap.put("INSAMCURRENCYID", CURRENCYMAP.getKey(contr.getCurrency()));
                contrMap.put("PREMCURRENCYID", CURRENCYMAP.getKey(contr.getCurrency()));
                Long currencyId = Long.parseLong(contrMap.get("PREMCURRENCYID").toString());
                Map<String, Object> currMap = getCurrencyById2(currencyId, login, password);
                contrMap.put("INSAMCURRENCYNAME", currMap.get("Name"));
                contrMap.put("PREMCURRENCYNAME", currMap.get("Name"));

                ListInvestCoverageType lic = contr.getInvestCoverage();
                Map<String, Object> contrExtMap = new HashMap<>();
                if (lic != null) {
                    List<InvestCoverageType> icList = lic.getInvestCoverage();
                    // исходя из фт - базовый актив, и инвестиционное состояние - должны быть в 1 экземпляре, поэтому, идем по списку, и первое не пустое поле сохраняем
                    // в расширенных атрибутаз.
                    //nvl(InvestCoverage.BaseActive, InvestCoverage.InvestConditions)
                    if (icList != null) {
                        for (InvestCoverageType investCoverage : icList) {
                            if (investCoverage != null) {
                                if (!contrExtMap.containsKey("BASEACTIVE")) {
                                    if ((investCoverage.getBaseActive() != null) && (!investCoverage.getBaseActive().isEmpty())) {
                                        contrExtMap.put("BASEACTIVE", investCoverage.getBaseActive());
                                    } else {
                                        logger.error("getFullInfo: investCoverage.getBaseActive is null");
                                    }
                                } else {
                                    logger.error("getFullInfo: contrExtMap[BASEACTIVE] is null");
                                }
                                if (!contrExtMap.containsKey("BASEACTIVECODE")) {
                                    if ((investCoverage.getBaseActiveCode() != null) && (!investCoverage.getBaseActiveCode().isEmpty())) {
                                        contrExtMap.put("BASEACTIVECODE", investCoverage.getBaseActiveCode());
                                    } else {
                                        logger.error("getFullInfo: investCoverage.getBaseActiveCode is null");
                                    }
                                } else {
                                    logger.error("getFullInfo: contrExtMap[BASEACTIVE] is null");
                                }
                                if (!contrExtMap.containsKey("INVESTCONDITION")) {
                                    if ((investCoverage.getInvestConditions() != null) && (!investCoverage.getInvestConditions().isEmpty())) {
                                        contrExtMap.put("INVESTCONDITIONCODE", investCoverage.getInvestConditions());
                                        Map<String, Object> baseActiveMap = getBaseActiveMap2(investCoverage.getInvestConditions(), login, password);
                                        contrExtMap.put("INVESTCONDITION", baseActiveMap.get("NAME"));

                                    } else {
                                        logger.error("getFullInfo: investCoverage.getInvestConditions is null");
                                    }
                                } else {
                                    logger.error("getFullInfo: contrExtMap[INVESTCONDITION] is null");
                                }
                            } else {
                                logger.error("getFullInfo: investCoverage is null");
                            }
                        }
                    } else {
                        logger.error("getFullInfo: getInvestCoverageList return null");
                    }
                } else {
//                    logger.error("getFullInfo: getInvestCoverage return null");
                }
                //Contract.WarrantyLevel
                if (contr.getWarrantyLevel() != null) {
                    contrExtMap.put("WARRANTYLEVEL", contr.getWarrantyLevel().doubleValue());
                } else {
                    logger.error("getFullInfo: contr.getWarrantyLevel() is null");
                }
                ListCoverageType lc = contr.getCoverageList();
                if (lc != null) {
                    List<Coverage> cList = lc.getCoverage();
                    Double coverageDetAmountPremSum = 0.0;
                    Double coverageDetInsSum = 0.0;
                    Double periodPayRentSum = 0.0;
                    if (cList != null) {
                        for (Coverage coverage : cList) {
                            if (coverage != null) {
                                String coverageSysname = coverage.getCoverageName();
                                ListCoverageDetType lcd = coverage.getCoverageDetList();
                                List<CoverageDet> cdList = lcd.getCoverageDet();
                                for (CoverageDet coverageDet : cdList) {
                                    if (coverageDet.getAmountAssured() != null) {
                                        Double amountAssured = coverageDet.getAmountAssured().doubleValue();
                                        coverageDetInsSum += amountAssured;
                                        if (coverageSysname.contains("LIFE_OF_TERM")) {
                                            periodPayRentSum += amountAssured;
                                        }
                                    } else {
                                        logger.error("getFullInfo: contr.getCoverageList().getCoverage[].coverage.coverageDet.getAmountAssured() is null");
                                    }

                                    if (coverageDet.getAmountPrem() != null) {
                                        coverageDetAmountPremSum += coverageDet.getAmountPrem().doubleValue();
                                    } else {
                                        logger.error("getFullInfo: contr.getCoverageList().getCoverage[].coverage.coverageDet.getAmountPrem() is null");
                                    }
                                }
                            } else {
                                logger.error("getFullInfo: contr.getCoverageList().getCoverage[].coverage is null");
                            }
                        }
                    } else {
                        logger.error("getFullInfo: contr.getCoverageList().getCoverage is null");
                    }
                    contrExtMap.put("COVDETINSSUM", coverageDetInsSum.doubleValue());
                    contrExtMap.put("COVDETPREMSUM", coverageDetAmountPremSum.doubleValue());
                    contrExtMap.put("PERIODPAYRENTSUM", periodPayRentSum.doubleValue());
                } else {
                    logger.error("getFullInfo: contr.getCoverageList() is null");
                }
                //сумма по [CoverageDet.AmountPrem]

                //Отчет по доходности, столбец Гарантия
                //Отчет по доходности. Столбец Страховая сумма
                //Из файла Отчет по доходности. Столбец Выкупная сумма
                //Contract.PolicyStatus
                Map<String, Object> stateMap = getStateMapByName2(contr.getPolicyStatus());
                if (stateMap != null) {
                    contrMap.put("STATEID", stateMap.get("STATEID"));
                    contrMap.put("STATESYSNAME", stateMap.get("STATENAME"));
                } else {
                    contrMap.put("STATEID", getStateIdByName(contr.getPolicyStatus()));
                }
                contrMap.put("STATENAME", contr.getPolicyStatus());

                //Contract.PaymentPeriodicity
                contrMap.put("PAYVARSYSNAME", PERIODICITYMAP.getKey(contr.getPaymentPeriodicity()));
                Map<String, Object> payVarMap = getPayVarIdBySysName2(PERIODICITYMAP.getKey(contr.getPaymentPeriodicity()), login, password);
                if (payVarMap.get("PAYVARID") != null) {
                    contrMap.put("PAYVARID", payVarMap.get("PAYVARID"));
                } else {
                    logger.error("getFullInfo: PAYVARID is null");
                }
                if (payVarMap.get("NAME") != null) {
                    contrMap.put("PAYVARNAME", payVarMap.get("NAME"));
                } else {
                    logger.error("getFullInfo: PAYVARNAME is null");
                }

                //Contract.CalculatedFields.RegularPaym
                if (contr.getCalculatedFields() != null) {
                    //Contract.CalculatedFields.NextPayDate
                    if (contr.getCalculatedFields().getNextPayDate() != null) {
                        contrExtMap.put("NEXTPAYDATE", processDate(contr.getCalculatedFields().getNextPayDate()));
                    }
                    if (contr.getCalculatedFields().getRegularPaym() != null) {
                        contrExtMap.put("REGULARPAYM", contr.getCalculatedFields().getRegularPaym().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getRegularPaym() is null");
                    }
                    //Contract.CalculatedFields.PayArrears
                    if (contr.getCalculatedFields().getPayArrears() != null) {
                        contrExtMap.put("PAYARREARS", contr.getCalculatedFields().getPayArrears().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPayArrears() is null");
                    }
                    //Contract.CalculatedFields.PaymentTerm
                    contrExtMap.put("PAYMENTTERM", contr.getCalculatedFields().getPaymentTerm());
                    //Contract.CalculatedFields.SumPaySum
                    if (contr.getCalculatedFields().getSumPaySum() != null) {
                        contrExtMap.put("SUMPAYSUM", contr.getCalculatedFields().getSumPaySum().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getSumPaySum() is null");
                    }
                    //Contract.CalculatedFields.AccumPeriodStartDate +' - '+Contract.CalculatedFields.AccumPeriodEndDate
                    if (contr.getCalculatedFields().getAccumPeriodStartDate() != null) {
                        contrExtMap.put("ACCUMPSTARTDATE", processDate(contr.getCalculatedFields().getAccumPeriodStartDate()));
                    } else {
//                        logger.error("getFullInfo: contr.getCalculatedFields().getAccumPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getAccumPeriodEndDate() != null) {
                        contrExtMap.put("ACCUMPENDDATE", processDate(contr.getCalculatedFields().getAccumPeriodEndDate()));
                    } else {
//                        logger.error("getFullInfo: contr.getCalculatedFields().getAccumPeriodEndDate() is null");
                    }

                    //Contract.CalculatedFields.PayPeriodStartDate+' - '+Contract.CalculatedFields.PayPeriodEndDate
                    if (contr.getCalculatedFields().getPayPeriodStartDate() != null) {
                        contrExtMap.put("PAYPSTARTDATE", processDate(contr.getCalculatedFields().getPayPeriodStartDate()));
                    } else {
//                        logger.error("getFullInfo: contr.getCalculatedFields().getPayPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getPayPeriodEndDate() != null) {
                        contrExtMap.put("PAYPENDDATE", processDate(contr.getCalculatedFields().getPayPeriodEndDate()));
                    } else {
//                        logger.error("getFullInfo: contr.getCalculatedFields().getPayPeriodEndDate() is null");
                    }

                    //Contract.CalculatedFields.GarPeriodStartDate+' - '+Contract.CalculatedFields.GarPeriodEndDate
                    if (contr.getCalculatedFields().getGarPeriodStartDate() != null) {
                        contrExtMap.put("GARPSTARTDATE", processDate(contr.getCalculatedFields().getGarPeriodStartDate()));
                    } else {
//                        logger.error("getFullInfo: contr.getCalculatedFields().getGarPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getGarPeriodEndDate() != null) {
                        contrExtMap.put("GARPENDDATE", processDate(contr.getCalculatedFields().getGarPeriodEndDate()));
                    } else {
//                        logger.error("getFullInfo: contr.getCalculatedFields().getGarPeriodEndDate() is null");
                    }
//todo: Кол-во лет между датами (Contract.CalculatedFields.PaymPeriodStartDate, Contract.CalculatedFields.PaymPeriodEndDate), округление в большую сторону, т.е. 13 месяцев это 2 года, а 1  месяц это 1 год
                    if ((contr.getCalculatedFields().getPaymPeriodStartDate() != null) && (contr.getCalculatedFields().getPaymPeriodEndDate() != null)) {
                        Date startPaymDate = (Date) processDate(contr.getCalculatedFields().getPaymPeriodStartDate());
                        Date finishPaymDate = (Date) processDate(contr.getCalculatedFields().getPaymPeriodEndDate());
                        contrExtMap.put("PAYPERIODINYEAR", calcYears(startPaymDate, finishPaymDate));
                    }

                    //Contract.CalculatedFields.PaymPeriodStartDate +' - '+ Contract.CalculatedFields.PaymPeriodEndDate
                    if (contr.getCalculatedFields().getPaymPeriodStartDate() != null) {
                        contrExtMap.put("PAYMPSTARTDATE", processDate(contr.getCalculatedFields().getPaymPeriodStartDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPaymPeriodStartDate() is null");
                    }
                    if (contr.getCalculatedFields().getPaymPeriodEndDate() != null) {
                        contrExtMap.put("PAYMPENDDATE", processDate(contr.getCalculatedFields().getPaymPeriodEndDate()));
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getPaymPeriodEndDate() is null");
                    }

                    //Contract.CalculatedFields.RentSum
                    if (contr.getCalculatedFields().getRentSum() != null) {
                        contrExtMap.put("RENTSUM", contr.getCalculatedFields().getRentSum().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getRentSum() is null");
                    }

                    //Contract.CalculatedFields.CurrentPay
                    if (contr.getCalculatedFields().getCurrentPay() != null) {
                        contrExtMap.put("CURRENTPAY", contr.getCalculatedFields().getCurrentPay().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getCurrentPay() is null");
                    }
                    //Contract.CalculatedFields.RemainsToPay
                    if (contr.getCalculatedFields().getRemainsToPay() != null) {
                        contrExtMap.put("REMAINSTOPAY", contr.getCalculatedFields().getRemainsToPay().doubleValue());
                    } else {
                        logger.error("getFullInfo: contr.getCalculatedFields().getRemainsToPay() is null");
                    }
                } else {
                    logger.error("getFullInfo: contr.getCalculatedFields() is null");
                }

                //Файл Инвестиционная доходность. Столбец ДИД
                //Из файла Инвестиционная доходность. Столбец Гарантированная страховая сумма …
                //Из файла Отчет по доходности. Столбец КУ
                //Contract.PolicyDocDate
                contrMap.put("DOCUMENTDATE", processDate(contr.getPolicyDocDate()));

                memberProcess2(contrMap, contr, login, password);
                prodStructProcess(contrMap, productMap, contr, login, password);
                payScheduleProcess(contrMap, contr, login, password);
                payFactProcess(contrMap, contr, login, password);

                //Contract.Coverage.Periodicity покрытия список - по идее это наши риски. периодичность одинаковая, или ее надо хранить в расширенных атрибутах рисков.
                if (contr.getCoverageList() != null) {
                    if (contr.getCoverageList().getCoverage() != null) {
                        if (contr.getCoverageList().getCoverage().get(0) != null) {
                            if (contr.getCoverageList().getCoverage().get(0).getPeriodicity() != null) {
                                contrExtMap.put("COVPAYVARSYSNAME", PERIODICITYMAP.getKey(contr.getCoverageList().getCoverage().get(0).getPeriodicity()));
                                Map<String, Object> covPayVarMap = getPayVarIdBySysName2(PERIODICITYMAP.getKey(contr.getCoverageList().getCoverage().get(0).getPeriodicity()), login, password);
                                if (covPayVarMap.get("PAYVARID") != null) {
                                    contrExtMap.put("COVPAYVARID", covPayVarMap.get("PAYVARID"));
                                } else {
                                    logger.error("getFullInfo: PAYVARID is null");
                                }
                                if (covPayVarMap.get("NAME") != null) {
                                    contrExtMap.put("COVPAYVARNAME", covPayVarMap.get("NAME"));
                                } else {
                                    logger.error("getFullInfo: PAYVARNAME is null");
                                }

                            }
                        }
                    }
                }

                //Contract.CoverageDet.AmountAssured (где  CoverageName = ~ LIFE_OF_TERM)
                //Parameter_List.Dict_Credit_Number
                ListParameterType lp = contr.getParameterList();
                if (lp != null) {
                    List<ParameterType> pList = lp.getParameter();
                    if (pList != null) {
                        for (ParameterType parameter : pList) {
                            if (parameter != null) {
                                if ("Dict_Credit_Number".equalsIgnoreCase(parameter.getName())) {
                                    contrExtMap.put("DICTCREDNUMBER", parameter.getValue().toString());
                                }
                            }
                        }
                    }
                }
                contrMap.put("CONTREXTMAP", contrExtMap);
                contrMap.putAll(contrExtMap);
            } else {
//                logger.error("getFullInfo: prodconf not found");
            }
//График платежей.Первый взнос
//График платежей.последний взнос (на дату)
//"ThirdParty c Role = LIFE ASSURED
//данное поле необходимо расщифровать, т.к. кроме фио м.б. и другая инфа, на картинке не видно"
//Contract.Thurdparty.FullName
//Contract.Thurdparty.BirthDate
//Contract.Thurdparty.DocumentsType.DocumentFull
//Contract.Thurdparty.ListAddress.FullAddress (где AddressType = REGISTRATION)
//
//Contract.CoverageList.Coverage.CoverageName
//Contract.CoverageList.Coverage.CoveregeDetList.Coverage.AmountAssured+' ' +Contract.CoverageList.Coverage.CoveregeDetList.Coverage.currency / Contract.CoverageList.Coverage.CoveregeDetList.Coverage.AmountAssured+' ' +Contract.CoverageList.Coverage.CoveregeDetList.Coverage.currency (если CoverageDetList >1)
// Где Contract.ThirdPartyList.Thirdparty.ThirdpartyType = BEN
// Contract.ThirdPartyList.Thirdparty.RiskCode
// Contract.ThirdPartyList.Thirdparty.Split
//CoverageList.Coverage.LifeAssureds.LifeAssured
//
//Отчет о доходности (для СмартПолис/Маяк) и "Котировки для ИСЖ купонного"+"Расчеты по купонам" (для СмартПолис купонный/Маяк купонный)
//
//
//устанавливается компанией по рез-там фин года, по году и валюте, в % (2016; RUR; 4%)
//Из файла Инвестиционная доходность Столбец ДИД на 31.12.2015 в рублях
//Файл Инвестиционная доходность Столбец "Гарантированная страховая сумма  (стрховая сумма по основной программе с учетом дохода) "
//
//ОИС
        } else {
            logger.error("getFullInfo: prodver not found");
        }
        logger.info("finishMapContractFullInfo");

        return contrMap;
    }

    private void memberProcess2(Map<String, Object> contrMap, ContractType contr, String login, String password) throws Exception {
        ListThirdPartyType ltp = contr.getThirdPartyList();
        if (ltp != null) {
            List<ThirdParty> tpList = ltp.getThirdParty();
            if (tpList != null) {
                List<Map<String, Object>> memberList = new ArrayList<>();
                List<Map<String, Object>> beneficiaryList = new ArrayList<>();
                Map<String, Object> insurerMap = new HashMap<>();
                Map<String, Object> insuredMap = new HashMap<>();
                for (ThirdParty thirdParty : tpList) {
                    if (thirdParty != null) {
                        Map<String, Object> memberMap = new HashMap<>();
                        memberMap.put("TYPESYSNAME", ROLEMAP.getKey(thirdParty.getRole()));
                        Map<String, Object> participantMap = new HashMap<>();

                        processParticipant2(participantMap, thirdParty, contr, login, password);

                        memberMap.put("PARTICIPANTMAP", participantMap);
                        if ("beneficiary".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            Map<String, Object> benMap = new HashMap<>();
                            benMap.put("PART", thirdParty.getSplit());
                            benMap.put("INSCOVERID", thirdParty.getRiskCode());
                            benMap.put("RISKCODE", thirdParty.getRiskCode());
                            //INSCOVERID
                            //     PART   
                            //        TYPEID
                            benMap.put("PARTICIPANTMAP", participantMap);
                            beneficiaryList.add(benMap);
                        }
                        if ("insured".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            insuredMap.putAll(participantMap);
                        }
                        if ("insurer".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            insurerMap.putAll(participantMap);
                        }
                        memberList.add(memberMap);
                    }
                }
                contrMap.put("MEMBERLIST", memberList);
                contrMap.put("BENEFICIARYLIST", beneficiaryList);
                contrMap.put("INSURERMAP", insurerMap);
                contrMap.put("INSUREDMAP", insuredMap);

            }
        }
    }

    protected void processParticipant2(Map<String, Object> participantMap, ThirdParty thirdParty, ContractType contr, String login, String password) throws Exception {

        participantMap.put("FIRSTNAME", thirdParty.getFirstName());
        participantMap.put("LASTNAME", thirdParty.getLastName());
        participantMap.put("MIDDLENAME", thirdParty.getPatronymic());
        participantMap.put("BRIEFNAME", thirdParty.getFullName());
        participantMap.put("THIRDPARTYID", thirdParty.getThirdPartyId());
        participantMap.put("GENDER", GENDERMAP.getKey(thirdParty.getGender()));

        try {
            participantMap.put("BIRTHDATE", getFormattedDateStr(processDate(thirdParty.getBirthDate()), "dd.MM.yyyy"));
        } catch (Exception e) {
            logger.error("Birthdate member parse error", e);
        }
        participantMap.put("BIRTHPLACE", thirdParty.getBirthPlace());

        participantMap.put("documentList", processDocument(thirdParty.getDocumentsList()));
        participantMap.put("contactList", processContact(thirdParty));
        participantMap.put("addressList", processAddress(thirdParty.getListAddress()));

        participantMap.put("PARTICIPANTTYPE", THIRDPARTYTYPEMAP.getKey(thirdParty.getThirdPartyType()));

        //todo промапить поля в extAttributeList2
        participantMap.put("MaritalStatus", MARITIALMAP.getKey(thirdParty.getMaritalStatus()));
        participantMap.put("Position", thirdParty.getPosition());
        participantMap.put("EmployerName", thirdParty.getEmployerName());
        participantMap.put("education", thirdParty.getOccupation());
        participantMap.put("activityBusinessKind", thirdParty.getFrameReference());
        participantMap.put("OPF", LEGALFORMTYPEMAP.getKey(thirdParty.getLegalFormText()));
        participantMap.put("CITIZENSHIP", thirdParty.getResident());
//        participantMap.put("COUNTRYID", getCountryIdByDigitCode(thirdParty.getResident(), login, password));
        participantMap.put("COUNTRYID", "1");

        participantMap.put("taxResident", thirdParty.getTaxResident());
        participantMap.put("INN", thirdParty.getTin());
        participantMap.put("OGRN", thirdParty.getOgrn());

    }

    protected boolean isCallFromGate(Map<String, Object> callParams) {
        boolean isCallFromGate = false;
        if (callParams != null &&
                callParams.get("ISCALLFROMGATE") != null &&
                ((String) callParams.get("ISCALLFROMGATE")).equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

}
