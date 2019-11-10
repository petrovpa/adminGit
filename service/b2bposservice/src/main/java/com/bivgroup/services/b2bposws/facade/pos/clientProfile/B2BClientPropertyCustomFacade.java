package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import com.bivgroup.core.dictionary.dao.jpa.JPADAOFactory;
import com.bivgroup.crm.Crm2;
import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

@BOName("B2BClientPropertyCustom")
public class B2BClientPropertyCustomFacade extends B2BDictionaryBaseFacade {

    private static final String CLIENT_PROPERTIES_PARAMNAME = "ClientProperties";
    private static final String CLIENT_ID_PARAMNAME = "clientId";

    private final Logger logger = Logger.getLogger(this.getClass());

    // todo: переписать на dctCrudByHierarchy; try catch ничем не отличается от используемого в dctCrudByHierarchy
    @WsMethod(requiredParams = {CLIENT_ID_PARAMNAME, CLIENT_PROPERTIES_PARAMNAME})
    public Map<String, Object> dsB2BClientPropertiesSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientPropertiesSave begin");
        boolean isCallFromGate = isCallFromGate(params);
        Long clientID = getLongParamLogged(params, CLIENT_ID_PARAMNAME);
        List<Map<String, Object>> clientPropertyList = (List<Map<String, Object>>) params.get(CLIENT_PROPERTIES_PARAMNAME);
        List<Map<String, Object>> savedClientPropertyList = null;
        if ((clientPropertyList != null) && (clientID != null)) {
            JPADAOFactory jd = new JPADAOFactory();
            DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Crm2.class));
            try {
                if (isCallFromGate) {
                    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
                    parseDatesBeforeDictionaryCalls(clientPropertyList);
                }
                // Признаки клиента - ClientProperty
                for (Map<String, Object> сlientProperty : clientPropertyList) {
                    if (сlientProperty != null) {
                        Long сlientPropertyClientID = getLongParam(сlientProperty, "clientId");
                        if (!clientID.equals(сlientPropertyClientID)) {
                            сlientProperty.put("clientId", clientID);
                            // setLinkParamWTF(сlientProperty, "clientId", clientID);
                            markAsModified(сlientProperty);
                        }
                        Long propertyTypeID = getLongParam(сlientProperty, "propertyTypeId");
                        if ((propertyTypeID != null) && (сlientProperty.get("propertyTypeId_EN") == null)) {
                            // setLinkParamWTF(сlientProperty, "propertyTypeId", propertyTypeID);
                            сlientProperty.put("propertyTypeId", propertyTypeID);
                        }
                    }
                }
                List<Map> сlientPropertiesListOfMap = new ArrayList<Map>();
                сlientPropertiesListOfMap.addAll(clientPropertyList);
                dc.beginTransaction();
                List savedclientPropertyListTmp = dc.getDAO().crudByHierarchy(CLIENT_PROPERTY_ENTITY_NAME, сlientPropertiesListOfMap);
                dc.commit();
                savedClientPropertyList = dc.processReturnResult(savedclientPropertyListTmp);
                markAllMapsByKeyValue(savedClientPropertyList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            } catch (Exception ex) {
                logger.error("dsB2BClientPropertiesSave caused exception: ", ex);
                dc.rollback();
                throw ex;
            }
        }
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(CLIENT_ID_PARAMNAME, clientID);
        result.put(CLIENT_PROPERTIES_PARAMNAME, savedClientPropertyList);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BClientPropertiesSave end");
        return result;
    }

    @WsMethod(requiredParams = {CLIENT_ID_PARAMNAME})
    public Map<String, Object> dsB2BClientPropertiesLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientPropertiesLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        Long clientID = getLongParamLogged(params, CLIENT_ID_PARAMNAME);

        // Признаки клиента - ClientProperty
        Map<String, Object> clientPropertyParams = new HashMap<String, Object>();
        clientPropertyParams.put("clientId", clientID);
        List<Map<String, Object>> clientPropertyList = dctFindByExample(CLIENT_PROPERTY_ENTITY_NAME, clientPropertyParams, isCallFromGate);
        markAllMapsByKeyValue(clientPropertyList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(CLIENT_PROPERTIES_PARAMNAME, clientPropertyList);
        result.put(CLIENT_ID_PARAMNAME, clientID);
        logger.debug("dsB2BClientPropertiesLoad end");
        return result;
    }

}
