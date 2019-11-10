/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.seatBelt;

import static com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractCustomFacade.getLastElementByAtrrValue;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("SeatBeltCustom")
public class SeatBeltCustomFacade extends ProductContractCustomFacade {

    private final Logger cLogger = Logger.getLogger(SeatBeltCustomFacade.class);
    // Типы выгодоприобретателей
    // 'Застрахованный' (когда CONTREXTMAP.insurerIsInsured == 0) или 'Страхователь (совпадает с ЗЛ)' (когда CONTREXTMAP.insurerIsInsured == 1)
    private static final Long BENEFICIARY_INSURED_TYPEID = 1L;
    // 'По закону'
    private static final Long BENEFICIARY_BY_LAW_TYPEID = 2L;
    // 'Новый'
    private static final Long BENEFICIARY_NORMAL_TYPEID = 3L;
    // 'Страхователь' (когда CONTREXTMAP.insurerIsInsured == 0)
    private static final Long BENEFICIARY_INSURER_TYPEID = 4L;
    private static final String DEF_PROG_NAME = "SB_BASIC";

    // инициализация даты документа
    private GregorianCalendar getOrGenerateDocumentDate(Map<String, Object> contract) {
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if (docDate == null) {
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), cLogger.isDebugEnabled());
        } else {
            //logger.debug("DOCDATE-" + docDate);
            documentDateGC.setTime((Date) parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }
        return documentDateGC;
    }

    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = cLogger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде из b2bContrSave
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }

        // инициализация даты документа
        GregorianCalendar documentDateGC = getOrGenerateDocumentDate(contract);

        contract.put("CONTRNUMBER", getStringParam(contract.get("CONTRPOLSER")) + " " + getStringParam(contract.get("CONTRPOLNUM")));
        // дата начала действия полиса
        // todo: определение метода вычисления даты заменить на анализ SDCALCMETHOD из B2B_PRODDEFVAL

        // определение срока действия в годах
        // todo: получение идентификатора срока действия из продукта (когда будут заполнены B2B_PRODTERM и B2B_TERM)
        Integer durationYears = getIntegerParam(contract.get("TERMID")); // todo: получение действительного срока действия по идентификатору срока (когда будут заполнена B2B_TERM)
        if (durationYears == 0) {
            durationYears = 1;
            contract.put("TERMID", durationYears);
        }
        getFinishDateByStartDateAndTermId(contract, login, password);
        // безусловное вычисление даты окончания действия
        GregorianCalendar finishDateGC = new GregorianCalendar();
        GregorianCalendar startDateGC = new GregorianCalendar();

        startDateGC.setTime((Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE"));
        //startDateGC.setTime(XMLUtil.convertDate(getDoubleParam(contract.get("STARTDATE"))));
        if (contract.get("FINISHDATE") == null) {

            finishDateGC.setTime(startDateGC.getTime());
            finishDateGC.add(Calendar.YEAR, durationYears);
            finishDateGC.add(Calendar.DATE, -1);
            finishDateGC.set(Calendar.HOUR_OF_DAY, 23);
            finishDateGC.set(Calendar.MINUTE, 59);
            finishDateGC.set(Calendar.SECOND, 59);
            finishDateGC.set(Calendar.MILLISECOND, 0);
            setOverridedParam(contract, "FINISHDATE", finishDateGC.getTime(), isParamsChangingLogged);
        } else {
            finishDateGC.setTime((Date) parseAnyDate(contract.get("FINISHDATE"), Date.class, "FINISHDATE"));
            // finishDateGC.setTime((Date) contract.get("FINISHDATE"));
        }
        // безусловное перевычисление срока действия
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        duration += 1; // необходимо прибавить один день, т.к. дата окончания сдвинута на одну секунду в прошлое (и точная длительность получается Х дней без одной секунды)
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        ArrayList<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract == null) {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        } else {
            insObjGroupList = (ArrayList<Map<String, Object>>) insObjGroupListFromContract;
        }

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);

        // расширенные атрибуты договора - выбор (если уже существуют в договоре) или создание новых
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);

        // установка системного имени программы
        String programSysName = DEF_PROG_NAME;
        setOverridedParam(contract, "PRODPROGSYSNAME", programSysName, isParamsChangingLogged);

        // определение идентификатора и кода программы по её системному имени на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "SYSNAME", programSysName);
        Long programID = getLongParam(program.get("PRODPROGID"));
        setOverridedParam(contract, "PRODPROGID", programID, isParamsChangingLogged);
        String programCode = getStringParam(program.get("PROGCODE"));
        setOverridedParam(contract, "PRODPROGCODE", programCode, isParamsChangingLogged);

        // определение страховых суммы и премии по сведениями из программы страхования
        Double programInsAmValue = getDoubleParam(program.get("INSAMVALUE"));
        Double programPremValue = getDoubleParam(program.get("PREMVALUE"));
        setOverridedParam(contract, "INSAMVALUE", programInsAmValue, isParamsChangingLogged);
        setOverridedParam(contract, "PREMVALUE", programPremValue, isParamsChangingLogged);

        // безусловная установка валют договора (для текущего продукта - всегда рубли)
        setOverridedParam(contract, "INSAMCURRENCYID", 1L, isParamsChangingLogged);
        setOverridedParam(contract, "PREMCURRENCYID", 1L, isParamsChangingLogged);

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        boolean isMissingStructsCreated = true;
        updateContractInsuranceProductStructure(contract, product, false, programCode, isMissingStructsCreated, login, password);

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        return contract;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSeatBeltContractPrepareToSave(Map<String, Object> params) throws Exception {

        cLogger.debug("before dsB2BSeatBeltContractPrepareToSave");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        genAdditionalSaveParams(contract, login, password);

        if (contract.get("CONTREXTMAP") != null) {

            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap.get("insurerIsInsured") != null) {
                if ("true".equalsIgnoreCase(getStringParam(contrExtMap.get("insurerIsInsured"))) || "1".equalsIgnoreCase(getStringParam(contrExtMap.get("insurerIsInsured")))) {
                    contrExtMap.put("insurerIsInsured", 1);
                } else {
                    contrExtMap.put("insurerIsInsured", 0);
                }
            }
            if (contrExtMap.get("insurerIsBeneficiary") != null) {
                if ("true".equalsIgnoreCase(getStringParam(contrExtMap.get("insurerIsBeneficiary"))) || "1".equalsIgnoreCase(getStringParam(contrExtMap.get("insurerIsBeneficiary")))) {
                    contrExtMap.put("insurerIsBeneficiary", 1);
                } else {
                    contrExtMap.put("insurerIsBeneficiary", 0);
                }
            }

            List<Map<String, Object>> benefList = null;
            List<Map<String, Object>> memList = null;
            Map<String, Object> insurerMap = (Map<String, Object>) contract.get("INSURERMAP");
            Map<String, Object> insuredMap = (Map<String, Object>) contract.get("INSUREDMAP");
            Map<String, Object> banefMap = (Map<String, Object>) contract.get("BENEFICIARYMAP");
            
            Long insurerId = null;
            if (insurerMap != null) {
                insurerId = getLongParam(insurerMap.get("PARTICIPANTID"));
            }
            Long insuredId = null;
            if (insuredMap != null) {
                insuredId = getLongParam(insuredMap.get("PARTICIPANTID"));
            }
            Long benefId = null;
            if (banefMap != null) {
                benefId = getLongParam(banefMap.get("PARTICIPANTID"));
            }
            boolean isRemoveOldBenef = false;
            if (contract.get("BENEFICIARYLIST") != null) {
                benefList = (List<Map<String, Object>>) contract.get("BENEFICIARYLIST");
                for (Map<String, Object> benmap : benefList) {
               //     if (((benefId != null) && (getLongParam(benmap.get("PARTICIPANTID")).compareTo(benefId) == 0)) || 
               //            ((insuredId != null) && (getLongParam(benmap.get("PARTICIPANTID")).compareTo(insuredId) == 0))){
               //         benmap.put("ROWSTATUS", 2);
                //    } else {
                        benmap.put("ROWSTATUS", 3);
                        isRemoveOldBenef = true;
               //     }
                    // помечаем старых к удалению.
                }
            }
            if (contract.get("MEMBERLIST") != null) {
                memList = (List<Map<String, Object>>) contract.get("MEMBERLIST");
                for (Map<String, Object> memmap : memList) {
                    if ("insured".equalsIgnoreCase(getStringParam(memmap.get("TYPESYSNAME")))) {
                        // застрахованный
                        if ((insuredId != null) && (getLongParam(memmap.get("PARTICIPANTID")).compareTo(insuredId) == 0)) {
                            memmap.put("ROWSTATUS", 2);
                        } else {
                            memmap.put("ROWSTATUS", 3);
                            contract.remove("INSUREDID");
                        }

                    } else if ("insurer".equalsIgnoreCase(getStringParam(memmap.get("TYPESYSNAME")))) {
                        if ((insurerId != null) && (getLongParam(memmap.get("PARTICIPANTID")).compareTo(insurerId) == 0)) {
                            memmap.put("ROWSTATUS", 2);
                        } else {
                            memmap.put("ROWSTATUS", 3);
                        }
                    } else if ("beneficiary".equalsIgnoreCase(getStringParam(memmap.get("TYPESYSNAME")))) {
                        if ((benefId != null) && (getLongParam(memmap.get("PARTICIPANTID")).compareTo(benefId) == 0)) {
                            memmap.put("ROWSTATUS", 2);
                        } else {
                            memmap.put("ROWSTATUS", 3);
                        }
                    } else {
                        memmap.put("ROWSTATUS", 3);
                    }
                    // помечаем старых к удалению.
                }
            }
            List<Map<String, Object>> beneficiaryList = null;
            if (benefList != null) {
                beneficiaryList = benefList;
            } else {
                beneficiaryList = new ArrayList<>();
            }

            // вариант установки страхователя и выгодопреобретателя по умолчанию.
            if ("1".equals(getStringParam(contrExtMap.get("insurerIsInsured")))
                    && "1".equals(getStringParam(contrExtMap.get("insurerIsBeneficiary")))) {

                // заполнить выгодопреобретателей, и страхователя значениями по умолчанию. 
                // страхователь=застрахованный=выгодопреобретатель
                contract.remove("INSUREDMAP");
                contract.put("INSUREDMAP", contract.get("INSURERMAP"));

                Map<String, Object> benefMap = new HashMap<>();
                // т.к. выгодопреобретателем по смерти не может быть погибший страхователь=застрахованный - он по умолчанию по закону.
                if (beneficiaryList.isEmpty() || isRemoveOldBenef) {
                    benefMap.put("TYPEID", BENEFICIARY_BY_LAW_TYPEID);
                    benefMap.put("TYPENAME", "По закону");
                    benefMap.put("INSCOVERID", 3);
                    benefMap.put("RISKSYSNAME", "death");
                    benefMap.put("FULLNAME", "По закону");
                    benefMap.put("PART", 100);
                    beneficiaryList.add(benefMap);
                    Map<String, Object> benefMap1 = new HashMap<>();
                    benefMap1.put("TYPEID", BENEFICIARY_INSURED_TYPEID);
                    benefMap1.put("TYPENAME", "Страхователь (совпадает с ЗЛ)");
                    benefMap1.put("INSCOVERID", 4);
                    benefMap1.put("RISKSYSNAME", "injury");
                    benefMap1.put("FULLNAME", "Страхователь (совпадает с ЗЛ)");
                    benefMap1.put("PART", 100);
                    beneficiaryList.add(benefMap1);
                }
                contract.put("BENEFICIARYLIST", beneficiaryList);
            } else {
                if ("0".equals(getStringParam(contrExtMap.get("insurerIsInsured")))) {
                    // страхователь!= застрахованному застрахованный с формы, выгодопреобретатель по обоим рискам - новое лицо равное застрахованному.
                    contract.put("INSUREDMAP", genFullParticipantMapForSave((Map<String, Object>) contract.get("INSUREDMAP"), login, password));

                    if (beneficiaryList.isEmpty() || isRemoveOldBenef) {
                        Map<String, Object> benefMap = new HashMap<>();
                        benefMap.put("TYPEID", BENEFICIARY_INSURED_TYPEID);
                        benefMap.put("TYPENAME", "Застрахованный");
                        benefMap.put("INSCOVERID", 3);
                        benefMap.put("RISKSYSNAME", "death");
                        benefMap.put("FULLNAME", "Застрахованный");
                        benefMap.put("PART", 100);
                        beneficiaryList.add(benefMap);
                        Map<String, Object> benefMap1 = new HashMap<>();
                        benefMap1.put("TYPEID", BENEFICIARY_INSURED_TYPEID);
                        benefMap1.put("TYPENAME", "Застрахованный");
                        benefMap1.put("INSCOVERID", 4);
                        benefMap1.put("RISKSYSNAME", "injury");
                        benefMap1.put("FULLNAME", "Застрахованный");
                        benefMap1.put("PART", 100);
                        beneficiaryList.add(benefMap1);
                    }
                    contract.put("BENEFICIARYLIST", beneficiaryList);

                } else {
                    // страхователь=застрахованному, выгодопреобретатель по риску смерть - новый введенный с формы.
                    contract.remove("INSUREDMAP");
                    contract.put("INSUREDMAP", contract.get("INSURERMAP"));

                    if (beneficiaryList.isEmpty() || isRemoveOldBenef) {
                        Map<String, Object> benefMap = new HashMap<>();
                        benefMap.put("TYPEID", BENEFICIARY_NORMAL_TYPEID);
                        benefMap.put("TYPENAME", "Новый");
                        benefMap.put("INSCOVERID", 3);
                        benefMap.put("RISKSYSNAME", "death");
                        benefMap.put("FULLNAME", "Новый");
                        benefMap.put("PARTICIPANTMAP", genFullParticipantMapForSave((Map<String, Object>) contract.get("BENEFICIARYMAP"), login, password));
                        //clearPersonIds
                        clearPersonIds(benefMap, "PARTICIPANTMAP");
                        benefMap.put("PART", 100);
                        beneficiaryList.add(benefMap);
                        Map<String, Object> benefMap1 = new HashMap<>();
                        benefMap1.put("TYPEID", BENEFICIARY_INSURED_TYPEID);
                        benefMap1.put("TYPENAME", "Застрахованный");
                        benefMap1.put("INSCOVERID", 4);
                        benefMap1.put("RISKSYSNAME", "injury");
                        benefMap1.put("FULLNAME", "Застрахованный");
                        benefMap1.put("PART", 100);
                        beneficiaryList.add(benefMap1);
                    }
                    contract.put("BENEFICIARYLIST", beneficiaryList);

                }
            }
            //clearPersonIds(contract, "INSUREDMAP");
            //clearPersonIds
        }

        cLogger.debug("after dsB2BSeatBeltContractPrepareToSave");

        return contract;
    }

    public Map<String, Object> dsB2BSeatBeltContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {

        cLogger.debug("before dsB2BSeatBeltContractPrepareToSaveFixContr");

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("b2bCorrector1C")) && ((Boolean) params.get("b2bCorrector1C"))) {
                return contract;
            } else if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                cLogger.debug("after dsB2BHIBContractPrepareToSaveFixContr");
                return contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                cLogger.debug("after dsB2BSeatBeltContractPrepareToSaveFixContr");
                return new HashMap<String, Object>();
            }
        } else {
            cLogger.debug("after dsB2BSeatBeltContractPrepareToSaveFixContr");
            return contract;
        }
    }

    private Map<String, Object> genFullParticipantMapForSave(Map<String, Object> partMap, String login, String password) {
        if (partMap != null) {
        partMap.put("PARTICIPANTTYPE", 1);
        partMap.put("ISBUSINESSMAN", 0);
        partMap.put("ISCLIENT", 1);
        if (partMap.get("CITIZENSHIP") == null) {
            partMap.put("CITIZENSHIP", 0);
        }
        if (partMap.get("GENDER") == null) {
            partMap.put("GENDER", 0);
        }
        if (partMap.get("FIRSTNAME") == null) {
            partMap.put("FIRSTNAME", partMap.get("NAME"));
        }
        if (partMap.get("LASTNAME") == null) {
            partMap.put("LASTNAME", partMap.get("SURNAME"));
        }
        boolean isNeedMakeDocument = false;
        if (partMap.get("documentList") == null) {
            isNeedMakeDocument = true;
        }
        if (partMap.get("documentList") != null) {
            List<Map<String, Object>> docList = (List<Map<String, Object>>) partMap.get("documentList");
            if (docList.isEmpty()) {
                isNeedMakeDocument = true;
            }
        }
        if (isNeedMakeDocument) {
            List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();
            Map<String, Object> docMap = new HashMap<>();
            docMap.put("DOCNUMBER", getStringParam(partMap.get("docNumber")));
            docMap.put("DOCSERIES", getStringParam(partMap.get("docSeries")));

            if (partMap.get("DOCTYPESYSNAME") == null) {
                if (partMap.get("docTypeSysName") != null) {
                    docMap.put("DOCTYPESYSNAME", getStringParam(partMap.get("docTypeSysName")));
                } else {
                    // todo условие по возрасту. если младже 14 лет - тип документа - свидетельство о рождении. 
                    //docMap.put("DOCTYPESYSNAME", "BornCertificate");
                    docMap.put("DOCTYPESYSNAME", "PassportRF");
                }
            } else {
                docMap.put("DOCTYPESYSNAME", getStringParam(partMap.get("DOCTYPESYSNAME")));
            }

            if (partMap.get("ISSUEDATE") != null) {
                docMap.put("ISSUEDATE", getStringParam(partMap.get("ISSUEDATE")));
            } else {
                docMap.put("ISSUEDATE", "01.01.1900");
            }
            docMap.put("ISSUEDBY", getStringParam(partMap.get("ISSUEDBY")));
            docMap.put("ISSUERCODE", getStringParam(partMap.get("ISSUERCODE")));
            docList.add(docMap);
            partMap.put("documentList", docList);
            }
        }
        return partMap;
    }

    private void clearPersonIds(Map<String, Object> map, String personName) {
        if (map.get(personName) != null) {
            Map<String, Object> persMap = (Map<String, Object>) map.get(personName);
            persMap.remove("PARTICIPANTID");
            persMap.remove("PERSONID");
            persMap.remove("PERSONID");
            removeIdsFromList(persMap, "addressList", "ADDRESSID", "PARTICIPANTID");
            removeIdsFromList(persMap, "documentList", "PERSONDOCID", "PERSONID");
            removeIdsFromList(persMap, "contactList", "CONTACTID", "CONTACTPERSONID");
            persMap.put("ROWSTATUS", 1);
        }
    }

    private void removeIdsFromList(Map<String, Object> persMap, String listName, String pkName, String fkName) {
        if (persMap.get(listName) != null) {
            List<Map<String, Object>> objList = (List<Map<String, Object>>) persMap.get(listName);
            for (Map<String, Object> objMap : objList) {
                objMap.remove(pkName);
                objMap.remove(fkName);
                objMap.put("ROWSTATUS", 1);
            }
        }
    }

}
