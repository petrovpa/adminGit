/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade;

import static com.bivgroup.services.b2bposws.facade.B2BBaseFacade.RISKSUMSHBDATAVERID_PARAMNAME;
import static com.bivgroup.services.b2bposws.facade.B2BBaseFacade.ROWSTATUS_PARAM_NAME;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;

/**
 * Аналог ProductContractCustomFacade для страхования жизни
 * @author Alexandr
 */
@BOName("B2BContractLifeBase")
public class B2BContractLifeBaseFacade extends B2BLifeBaseFacade{
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Обновляет структуру страхового продукта договора на основании сведений о
     * страховом продукте.
     *
     * Eсли выставлен соотвтетсвтующий флаг - то при необходимости добавляет
     * отсутствующие секции/группы/объекты/риски. Если существует связанный с
     * продуктом справочник, хранящий значения сумм по рискам для выбранной
     * программы страхования - то по его данным дополнительно будут обновлены
     * суммы рисков (в CONTRRISKLIST). Если существует связанный с продуктом
     * справочник, хранящий значения показателей для выбранной программы
     * страхования - то по его данным дополнительно будут обновлены показатели
     * договора (CONTREXTMAP).
     *
     * @param b2bContract обновляемый договор
     * @param product полные сведения о страховом продукте
     * @param programCode код выбранной программы страхования (необязательно)
     * @param isMissingStructsCreated флаг создания недостающих элементов
     * структуры страхового продукта договора
     * @param isCreateSections флаг создания секций в структуре договора (иначе
     * работает по старому, верхний элемент в договоре получается группа
     * объектов)
     * @param login логин для вызова других методов веб-сервисов
     * @param password пароль для вызова других методов веб-сервисов
     * @throws Exception
     */
    protected void updateContractInsuranceProductStructure(Map<String, Object> b2bContract, Map<String, Object> product, boolean isCreateSections, String programCode, boolean isMissingStructsCreated, String login, String password) throws Exception {
        Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
        if (prodVerMap != null) {
            // получение полного списка элементов структуры из сведений о продукте
            List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
            if (prodStructs != null) {
                Long productConfigId = Long.valueOf(product.get("PRODCONFID").toString());
                Map<String, Object> hbRes = getRiskSumsAndContrExtValuesFromHandbook(productConfigId, programCode, login, password);
                Double contractPremValue = null;
                if (isCreateSections) {
                    // список секций - выбор (если уже существует в договоре) или создание нового
                    List<Map<String, Object>> sectionList = (List<Map<String, Object>>) b2bContract.get("CONTRSECTIONLIST");
                    Map<String, Map<String, Object>> sectionListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
                    if (sectionList == null) {
                        sectionList = new ArrayList<Map<String, Object>>();
                        b2bContract.put("CONTRSECTIONLIST", sectionList);
                        sectionListAsMapBySysName = new HashMap<String, Map<String, Object>>();
                    } else {
                        sectionListAsMapBySysName = getListAsMapAndFixSysNames(sectionList, "CONTRSECTIONSYSNAME");
                    }
                    // формирование полного списока секций - обновление (уже существующих элементов) сведениями из продукта или дополнение списка новыми (недостающими) элементами
                    List<Map<String, Object>> sectionProdStructs = filterProdStructs(prodStructs, null, DISCRIMINATOR_SECTION);
                    for (Map<String, Object> sectionBean : sectionProdStructs) {
                        String sectionSysName = getStringParam(sectionBean.get("SYSNAME"));
                        Map<String, Object> sectionForContract = sectionListAsMapBySysName.get(sectionSysName);
                        if (sectionForContract == null) {
                            if (isMissingStructsCreated) {
                                sectionForContract = new HashMap<String, Object>();
                                sectionForContract.put("CONTRSECTIONSYSNAME", sectionSysName);
                                sectionForContract.put("INSAMCURRENCYID", b2bContract.get("INSAMCURRENCYID"));
                                sectionForContract.put("INSAMVALUE", b2bContract.get("INSAMVALUE"));
                                sectionForContract.put("PREMCURRENCYID", b2bContract.get("PREMCURRENCYID"));
                                sectionForContract.put("PREMVALUE", b2bContract.get("PREMVALUE"));
                                sectionForContract.put("STARTDATE", b2bContract.get("STARTDATE"));
                                sectionForContract.put("FINISHDATE", b2bContract.get("FINISHDATE"));
                                sectionList.add(sectionForContract);
                                sectionListAsMapBySysName.put(sectionSysName, sectionForContract);
                            } else {
                                continue;
                            }
                        } else {
                            Object currentRowStatus = sectionForContract.get(ROWSTATUS_PARAM_NAME);
                            if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                                sectionForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                            }
                        }
                        sectionForContract.put("PRODSTRUCTID", sectionBean.get("PRODSTRUCTID"));
                        Map<String, Object> contrSectionExtMap;
                        if (sectionForContract.get("CONTRSECTIONEXTMAP") != null) {
                            contrSectionExtMap = (Map<String, Object>) sectionForContract.get("CONTRSECTIONEXTMAP");
                        } else {
                            contrSectionExtMap = new HashMap<String, Object>();
                            sectionForContract.put("CONTRSECTIONEXTMAP", contrSectionExtMap);
                        }
                        contrSectionExtMap.put("HBDATAVERID", sectionBean.get("HBDATAVERID"));
                    }
                    //
                    for (Map<String, Object> sectionBean : sectionList) {
                        Long sectionStructId = Long.valueOf(sectionBean.get("PRODSTRUCTID").toString());
                        Map<String, Object> updRes = updateContractInsuranceProductStructureFromInsObjGroupList(prodStructs, sectionBean, product, hbRes, sectionStructId,
                                programCode, isMissingStructsCreated, login, password);
                        if (updRes.get("contractPremValue") != null) {
                            sectionBean.put("PREMVALUE", Double.valueOf(updRes.get("contractPremValue").toString()));
                            if (contractPremValue == null) {
                                contractPremValue = 0.0;
                            }
                            contractPremValue += Double.valueOf(updRes.get("contractPremValue").toString());
                        }
                    }
                } else {
                    Map<String, Object> updRes = updateContractInsuranceProductStructureFromInsObjGroupList(prodStructs, b2bContract, product, hbRes, null,
                            programCode, isMissingStructsCreated, login, password);
                    if (updRes.get("contractPremValue") != null) {
                        contractPremValue = Double.valueOf(updRes.get("contractPremValue").toString());
                    }
                }
                // если задан справочник сумм, пытаемся из него загрузить показатели по договору
                updateContractExtValues(b2bContract, hbRes, product, contractPremValue);
            }
        }
    }

    private Map<String, Object> getRiskSumsAndContrExtValuesFromHandbook(Long productConfigId, String programCode, String login, String password) throws Exception {
        Map<String, Object> hbRes = null;
        // если не указан код программы страхования, то запрашивать как версию справочника, так и его данные нет необходимости
        if (!programCode.isEmpty()) {
            Map<String, Object> prodDefParams = new HashMap<String, Object>();
            prodDefParams.put("PRODCONFID", productConfigId);
            prodDefParams.put("NAME", RISKSUMSHBDATAVERID_PARAMNAME);
            logger.debug("alarm!!! " + prodDefParams.toString());
            Long sumsHandbookDataVerID = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", prodDefParams, login, password, "VALUE"));
            // если задан справочник с суммами по рискам, нужно его обработать
            if (sumsHandbookDataVerID != null) {
                //Long sumsHBDataVerId = Long.valueOf(prodDefRes.get("VALUE").toString());
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("HBDATAVERID", sumsHandbookDataVerID);
                hbParams.put("insuranceProgram", programCode);
                hbParams.put(RETURN_AS_HASH_MAP, true);
                logger.debug("alarm!!! " + hbParams.toString());

                hbRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbParams, login, password);
                // если не нашли запись справочника по указанной программе, зануляем результат
                if (hbRes.get("HBSTOREID") == null) {
                    hbRes = null;
                }
                if (hbRes != null) {
                    logger.debug("alarm!!! " + hbRes.toString());
                } else {
                    logger.debug("alarm!!! null");
                }
            }
        }
        return hbRes;
    }

    private Map<String, Map<String, Object>> getListAsMapAndFixSysNames(List<Map<String, Object>> list, String correctSysNameKeyName) {
        return getListAsMapAndFixSysNames(list, correctSysNameKeyName, null);
    }

    // исправляет названия ключей, указывающих на системные имена, а также формирует объект для быстрого доступа к элементам списка по системному имени
    private Map<String, Map<String, Object>> getListAsMapAndFixSysNames(List<Map<String, Object>> list, String correctSysNameKeyName, String subMapKeyName) {
        Map<String, Map<String, Object>> listAsMapBySysName = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> element : list) {
            // определение местонахождения системного имени
            // (системное имя может находиться во вложенной в элемент мапе, например, в INSOMJMAP для INSOBJ)
            Map<String, Object> elementSysNameMap;
            if ((subMapKeyName == null) || (subMapKeyName.isEmpty())) {
                elementSysNameMap = element;
            } else {
                elementSysNameMap = (Map<String, Object>) element.get(subMapKeyName);
            }

            Object sysName = elementSysNameMap.get(correctSysNameKeyName);
            if (sysName == null) {
                // ключ, указывающий на системное имя может оказаться таким же как и в PRODSTRUCTS, то есть просто SYSNAME (например, при копировании элемента структуры из данных продукта целиком):
                // в таких случаях требуется дополнительное исправление "переименованием" ключа, отвечающего за системное имя
                // (так как в структуре продукта договора у каждого элемента свой префикс системного имени - INSOBJGROUPSYSNAME, INSOBJSYSNAME и PRODRISKSYSNAME)
                sysName = elementSysNameMap.get("SYSNAME");
                elementSysNameMap.put(correctSysNameKeyName, sysName);
            }
            // дополнение объекта для быстрого доступа к элементам списка текущим элементом
            listAsMapBySysName.put(sysName.toString(), element);
        }
        return listAsMapBySysName;
    }

    private List<Map<String, Object>> filterProdStructs(List<Map<String, Object>> prodStructs, Long parentStructId, Long discriminator) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (prodStructs != null) {
            for (Map<String, Object> bean : prodStructs) {
                boolean fAdd = true;
                if (parentStructId != null) {
                    if ((bean.get("PARENTSTRUCTID") == null) || (Long.valueOf(bean.get("PARENTSTRUCTID").toString()).longValue() != parentStructId.longValue())) {
                        fAdd = false;
                    }
                }
                if (discriminator != null) {
                    if ((bean.get("DISCRIMINATOR") == null) || (Long.valueOf(bean.get("DISCRIMINATOR").toString()).longValue() != discriminator.longValue())) {
                        fAdd = false;
                    }
                }
                if (fAdd) {
                    result.add(bean);
                }
            }
        }
        return result;
    }

    /*
     Аналогична updateContractInsuranceProductStructure, тока обновляется уже с групп объектов,
     при этом parentEntity это либо сам договор, либо его секция (для новых продуктов с секциями)
     */
    protected Map<String, Object> updateContractInsuranceProductStructureFromInsObjGroupList(List<Map<String, Object>> prodStructs,
            Map<String, Object> parentEntity, Map<String, Object> product, Map<String, Object> hbRes, Long insObjGroupParentStructId,
            String programCode, boolean isMissingStructsCreated, String login, String password) throws Exception {
        // список групп объектов - выбор (если уже существует в договоре) или создание нового
        List<Map<String, Object>> groupList = (List<Map<String, Object>>) parentEntity.get("INSOBJGROUPLIST");
        Map<String, Map<String, Object>> groupListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
        if (groupList == null) {
            groupList = new ArrayList<Map<String, Object>>();
            parentEntity.put("INSOBJGROUPLIST", groupList);
            groupListAsMapBySysName = new HashMap<String, Map<String, Object>>();
        } else {
            groupListAsMapBySysName = getListAsMapAndFixSysNames(groupList, "INSOBJGROUPSYSNAME");
        }
        // формирование полного списока типов объектов - обновление (уже существующих элементов) сведениями из продукта или дополнение списка новыми (недостающими) элементами
        List<Map<String, Object>> groupProdStructs = filterProdStructs(prodStructs, insObjGroupParentStructId, DISCRIMINATOR_GROUP);
        for (Map<String, Object> groupBean : groupProdStructs) {
            String groupSysName = getStringParam(groupBean.get("SYSNAME"));
            Map<String, Object> groupForContract = groupListAsMapBySysName.get(groupSysName);
            if (groupForContract == null) {
                if (isMissingStructsCreated) {
                    groupForContract = new HashMap<String, Object>();
                    groupForContract.put("INSOBJGROUPSYSNAME", groupSysName);
                    groupList.add(groupForContract);
                    groupListAsMapBySysName.put(groupSysName, groupForContract);
                } else {
                    continue;
                }
            } else {
                Object currentRowStatus = groupForContract.get(ROWSTATUS_PARAM_NAME);
                if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                    groupForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                }
            }
            groupForContract.put("PRODSTRUCTID", groupBean.get("PRODSTRUCTID"));
            groupForContract.put("HBDATAVERID", groupBean.get("HBDATAVERID"));
        }
        //
        Double contractPremValue = null;
        if (hbRes != null) {
            contractPremValue = 0.0;
        }
        for (Map<String, Object> groupBean : groupList) {
            Long groupStructID = getLongParam(groupBean.get("PRODSTRUCTID"));
            List<Map<String, Object>> objectProdStructs = filterProdStructs(prodStructs, groupStructID, DISCRIMINATOR_OBJECT);
            if ((objectProdStructs != null) && (objectProdStructs.size() > 0)) {
                // список объектов страхования - выбор (если уже существует в элементе списка типов) или создание нового
                List<Map<String, Object>> objList = (List<Map<String, Object>>) groupBean.get("OBJLIST");
                Map<String, Map<String, Object>> objListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
                if (objList == null) {
                    objList = new ArrayList<Map<String, Object>>();
                    groupBean.put("OBJLIST", objList);
                    objListAsMapBySysName = new HashMap<String, Map<String, Object>>();
                } else {
                    objListAsMapBySysName = getListAsMapAndFixSysNames(objList, "INSOBJSYSNAME", "INSOBJMAP");
                }
                for (Map<String, Object> objBean : objectProdStructs) {
                    String objProdSructSysName = getStringParam(objBean.get("SYSNAME"));
                    // объект страхования - выбор (если уже существует в договоре) через объект для быстрого доступа к элементам списка по системному имени ...
                    Map<String, Object> objectForContract = objListAsMapBySysName.get(objProdSructSysName);
                    // ... или создание нового
                    if (objectForContract == null) {
                        if (isMissingStructsCreated) {
                            objectForContract = new HashMap<String, Object>();
                            objList.add(objectForContract);
                            objListAsMapBySysName.put(objProdSructSysName, objectForContract); // для быстрого доступа к элементам списка по системному имени
                        } else {
                            continue;
                        }
                    } else {
                        Object currentRowStatus = objectForContract.get(ROWSTATUS_PARAM_NAME);
                        if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                            objectForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                        }
                    }
                    // обновление (или создание если не существует) INSOBJMAP
                    Map<String, Object> insObjMap = (Map<String, Object>) objectForContract.get("INSOBJMAP");
                    if (insObjMap == null) {
                        insObjMap = new HashMap<String, Object>();
                        insObjMap.put("INSOBJSYSNAME", objProdSructSysName);
                        objectForContract.put("INSOBJMAP", insObjMap);
                    }
                    insObjMap.put("PRODSTRUCTID", objBean.get("PRODSTRUCTID"));
                    insObjMap.put("INSOBJSYSNAME", objBean.get("SYSNAME"));
                    insObjMap.put("HBDATAVERID", objBean.get("HBDATAVERID"));
                    // обновление (или создание если не существует) CONTROBJMAP
                    Map<String, Object> contrObjMap = (Map<String, Object>) objectForContract.get("CONTROBJMAP");
                    if (contrObjMap == null) {
                        contrObjMap = new HashMap<String, Object>();
                        objectForContract.put("CONTROBJMAP", contrObjMap);
                    }
                    contrObjMap.put("CURRENCYID", parentEntity.get("INSAMCURRENCYID"));
                    contrObjMap.put("DURATION", parentEntity.get("DURATION"));
                    contrObjMap.put("STARTDATE", parentEntity.get("STARTDATE"));
                    contrObjMap.put("FINISHDATE", parentEntity.get("FINISHDATE"));
                    contrObjMap.put("PREMCURRENCYID", parentEntity.get("PREMCURRENCYID"));
                    // определение сумм, указанных в самом договоре
                    Double contrObjPremValue = 0.0;
                    Double contrObjInsAmValue = 0.0;
                    if (hbRes == null) {
                        if (parentEntity.get("PREMVALUE") != null) {
                            contrObjPremValue = Double.valueOf(parentEntity.get("PREMVALUE").toString());
                        }
                        if (parentEntity.get("INSAMVALUE") != null) {
                            contrObjInsAmValue = Double.valueOf(parentEntity.get("INSAMVALUE").toString());
                        }
                    }
                    //
                    Long objProdStructId = Long.valueOf(objBean.get("PRODSTRUCTID").toString());
                    List<Map<String, Object>> riskProdStructs = filterProdStructs(prodStructs, objProdStructId, DISCRIMINATOR_RISK);
                    if ((riskProdStructs != null) && (riskProdStructs.size() > 0)) {
                        // список рисков текущего объекта страхования - выбор (если уже существует в элементе списка типов) или создание нового
                        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                        Map<String, Map<String, Object>> riskListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
                        if (riskList == null) {
                            riskList = new ArrayList<Map<String, Object>>();
                            contrObjMap.put("CONTRRISKLIST", riskList);
                            riskListAsMapBySysName = new HashMap<String, Map<String, Object>>();
                        } else {
                            riskListAsMapBySysName = getListAsMapAndFixSysNames(riskList, "PRODRISKSYSNAME");
                        }
                        //List<Map<String, Object>> resRiskList = new ArrayList<Map<String, Object>>(); //todo: вместо создания выбирать уже существующий список рисков, если был передан
                        for (Map<String, Object> riskBean : riskProdStructs) {
                            boolean isRiskWithSums = false;
                            Double insAmValue = null;
                            Double premValue = null;
                            if (hbRes != null) {
                                String riskSysName = riskBean.get("SYSNAME").toString();
                                if ((hbRes.get("INSAM_" + riskSysName) != null) || ((hbRes.get("PREM_" + riskSysName) != null))) {
                                    if (hbRes.get("INSAM_" + riskSysName) != null) {
                                        insAmValue = Double.valueOf(hbRes.get("INSAM_" + riskSysName).toString());
                                        if (insAmValue.doubleValue() > contrObjInsAmValue.doubleValue()) {
                                            contrObjInsAmValue = insAmValue;
                                        }
                                    }
                                    if (hbRes.get("PREM_" + riskSysName) != null) {
                                        premValue = Double.valueOf(hbRes.get("PREM_" + riskSysName).toString());
                                        contractPremValue += premValue;
                                        contrObjPremValue += premValue;
                                    }
                                    isRiskWithSums = true;
                                }
                            } else {
                                if (parentEntity.get("INSAMVALUE") != null) {
                                    insAmValue = Double.valueOf(parentEntity.get("INSAMVALUE").toString());
                                }
                                if (parentEntity.get("PREMVALUE") != null) {
                                    premValue = Double.valueOf(parentEntity.get("PREMVALUE").toString());
                                }
                                isRiskWithSums = true;
                            }
                            if (isRiskWithSums) {
                                String riskSysName = getStringParam(riskBean.get("SYSNAME"));
                                // риск объекта страхования - выбор (если уже существует в договоре) ...
                                Map<String, Object> riskForContract = riskListAsMapBySysName.get(riskSysName);
                                // ... или создание нового
                                if (riskForContract == null) {
                                    if (isMissingStructsCreated) {
                                        riskForContract = new HashMap<String, Object>();
                                        riskForContract.put("PRODRISKSYSNAME", riskSysName);
                                        riskList.add(riskForContract);
                                        riskListAsMapBySysName.put(riskSysName, riskForContract);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    Object currentRowStatus = riskForContract.get(ROWSTATUS_PARAM_NAME);
                                    if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                                        riskForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                                    }
                                }
                                //
                                riskForContract.put("PRODSTRUCTID", riskBean.get("PRODSTRUCTID"));
                                riskForContract.put("PRODRISKSYSNAME", riskBean.get("SYSNAME"));
                                riskForContract.put("CURRENCYID", parentEntity.get("INSAMCURRENCYID"));
                                riskForContract.put("DURATION", parentEntity.get("DURATION"));
                                riskForContract.put("STARTDATE", parentEntity.get("STARTDATE"));
                                riskForContract.put("FINISHDATE", parentEntity.get("FINISHDATE"));
                                riskForContract.put("PREMCURRENCYID", parentEntity.get("PREMCURRENCYID"));
                                if (riskForContract.get("INSAMVALUE") == null) {
                                    riskForContract.put("INSAMVALUE", insAmValue);
                                }
                                if (riskForContract.get("PREMVALUE") == null) {
                                    riskForContract.put("PREMVALUE", premValue);
                                }
                                // риск - расширенные атрибуты
                                // выбор существующих расширенных атрибутов из риска
                                Map<String, Object> riskForContractExtMap = (Map<String, Object>) riskForContract.get("CONTRRISKEXTMAP");
                                if (riskForContractExtMap == null) {
                                    // или создание новых расширенных атрибутов для риска
                                    riskForContractExtMap = new HashMap<String, Object>();
                                    riskForContract.put("CONTRRISKEXTMAP", riskForContractExtMap);
                                }
                                // безусловная установка версии справочника расширенных атрибутов риска
                                riskForContractExtMap.put("HBDATAVERID", riskBean.get("HBDATAVERID"));
                            }
                        }
                    }
                    if ((contrObjMap.get("CONTRRISKLIST") != null) && ((List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST")).size() > 0) {
                        if (contrObjMap.get("PREMVALUE") == null) {
                            contrObjMap.put("PREMVALUE", contrObjPremValue);
                        }
                        if (contrObjMap.get("INSAMVALUE") == null) {
                            contrObjMap.put("INSAMVALUE", contrObjInsAmValue);
                        }
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("contractPremValue", contractPremValue);
        return result;
    }

    private void updateContractExtValues(Map<String, Object> b2bContract, Map<String, Object> hbRes, Map<String, Object> product, Double contractPremValue) throws NumberFormatException {
        // если задан справочник сумм, пытаемся из него загрузить показатели по договору
        if (hbRes != null) {
            List<Map<String, Object>> prodValues = (List<Map<String, Object>>) product.get("PRODVALUES");
            if ((prodValues != null) && (prodValues.size() > 0)) {
                Map<String, Object> contrExtMap = (Map<String, Object>) b2bContract.get("CONTREXTMAP");
                if (contrExtMap != null) {
                    long productConfigID = Long.valueOf(product.get("PRODCONFID").toString());
                    for (Map<String, Object> bean : prodValues) {
                        if (bean.get("PRODCONFID") != null) {
                            long beanProductConfigID = Long.valueOf(bean.get("PRODCONFID").toString());
                            if (beanProductConfigID == productConfigID) {
                                String valueName = getStringParam(bean.get("NAME"));
                                if (!"insuranceProgram".equalsIgnoreCase(valueName)) {
                                    if (hbRes.get(valueName) != null) {
                                        contrExtMap.put(valueName, hbRes.get(valueName));
                                    }
                                }
                                /*else {
                                 // не требуется, код и идентифкатор программы страхования уже определен ранее - в genAdditionalSaveParams
                                 // contrExtMap.put(valueName, getProdProgramIdByProgCode(programCode, Long.valueOf(b2bContract.get("PRODVERID").toString()), login, password));
                                 }*/

                            }
                        }
                    }
                }
            }
        }
        if (contractPremValue != null) {
            b2bContract.put("PREMVALUE", contractPremValue);
        }
    }

}
