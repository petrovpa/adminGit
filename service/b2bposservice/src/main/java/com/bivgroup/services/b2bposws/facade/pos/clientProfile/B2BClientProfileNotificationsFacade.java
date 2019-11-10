package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.crm.ClientProfileNotification;
import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BOName("B2BClientProfileNotifications")
public class B2BClientProfileNotificationsFacade extends B2BDeclarationBaseFacade {

    private static final String NOTIFICATION_ID_PARAMNAME = "id";
    private static final String NOTIFICATION_MAP_PARAMNAME = "NOTIFICATIONMAP";
    private static final String TRANSITION_PARAMNAME = "SMTRANSITION";
    private final Logger logger = Logger.getLogger(this.getClass());
    // перечень доступных полей класса уведомлений
    private static String NOTIFICATION_FIELDNAMES = "id,clientProfileId,stateId,text,objectId,description";

    /*
     * Load notification by ID
     */

    @WsMethod(requiredParams = NOTIFICATION_MAP_PARAMNAME)
    public Map<String, Object> dsB2BClientProfileNotificationCreate(Map<String, Object> params) throws Exception {

        Map<String, Object> newNotification = createParams();

        if (params.get(NOTIFICATION_MAP_PARAMNAME) != null) {
            newNotification = (Map<String, Object>) params.get(NOTIFICATION_MAP_PARAMNAME); // если нужно создать на основе уже заданных полей
            newNotification = remapParamsByFieldNames(newNotification);
        }

        Map<String, Object> result = createParams();

        Map<String, Object> newNotificationWithId = dctCrudByHierarchy(CLIENT_PROFILE_NOTIFICATION_ENTITY_NAME, newNotification);
        result.put(RESULT, newNotificationWithId);

        return result;

    }

    @WsMethod(requiredParams = {NOTIFICATION_ID_PARAMNAME})
    public Map<String, Object> dsB2BClientProfileNotificationLoad(Map<String, Object> params) throws Exception {

        logger.debug("dsB2BClientProfileNotificationLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";

        Long notificationId = getLongParam(params, NOTIFICATION_ID_PARAMNAME);
        if (notificationId == null) {
            error = "Не указан идентификатор загружаемого уведомления!";
        }

        Map<String, Object> notification = null;
        Map<String, Object> result = createParams();
        notification = dctFindById(CLIENT_PROFILE_NOTIFICATION_ENTITY_NAME, notificationId, isCallFromGate);
        if (notification != null) {
            result.put(RESULT, notification);
        } else {
            result.put(ERROR, error);
        }

        logger.debug("dsB2BClientProfileNotificationLoad end");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BClientProfileNotificationLoadByParams(Map<String, Object> params) throws Exception {

        logger.debug("dsB2BClientProfileNotificationLoadByParams begin");
        boolean isCallFromGate = isCallFromGate(params);

        List<Map<String, Object>> notifications = null;
        Map<String, Object> loadParams = remapParamsByFieldNames(params);
        notifications = dctFindByExample(CLIENT_PROFILE_NOTIFICATION_ENTITY_NAME, loadParams, isCallFromGate);
        Map<String, Object> result = createParams();
        if (notifications != null) {
            result.put(RESULT, notifications);
        } else {
            result.put(ERROR, "Не удалось загрузить список уведомлений");
        }

        logger.debug("dsB2BClientProfileNotificationLoadByParams end");
        return result;
    }

    @WsMethod(requiredParams = {NOTIFICATION_MAP_PARAMNAME})
    public Map<String, Object> dsB2BClientProfileNotificationSave(Map<String, Object> params) throws Exception {

        boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        Map<String, Object> notification = getMapParam(params, NOTIFICATION_MAP_PARAMNAME);

        if (notification.isEmpty()) {
            error = "Отсутствует оповещение";
        }

        Map<String, Object> saveResult = null;
        if (error.isEmpty()) {
            saveResult = dctCrudByHierarchy(CLIENT_PROFILE_NOTIFICATION_ENTITY_NAME, notification, isCallFromGate);
        }

        if (error.isEmpty()) {
            return B2BResult.ok(saveResult);
        } else {
            return B2BResult.error(error);
        }

    }

    @WsMethod(requiredParams = {TRANSITION_PARAMNAME, NOTIFICATION_MAP_PARAMNAME})
    public Map<String, Object> dsB2BClientProfileNotificationTrans(Map<String, Object> params) throws Exception {

        boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        Map<String, Object> notification = getMapParam(params, NOTIFICATION_MAP_PARAMNAME);

        if (notification == null) {
            error = "Не задано оповещение";
        }

        Long notifyId = getLongParam(notification, NOTIFICATION_ID_PARAMNAME);

        if (notifyId == null) {
            error = "Не указан id уведомления";
        }

        String transitionName = getStringParam(params, TRANSITION_PARAMNAME);

        if (transitionName.isEmpty()) {
            error = "Не задан переход";
        }

        Map<String, Object> result = createParams();

        if (error.isEmpty()) {
            dctMakeTransition(CLIENT_PROFILE_NOTIFICATION_ENTITY_NAME, notifyId, transitionName);
            result.put(RESULT, RET_STATUS_OK);
        }
        return result;
    }

    private Map<String, Object> createParams() {
        return new HashMap<>();
    }

    private Map<String, Object> remapParamsByFieldNames(Map<String, Object> params) {

        Map<String, Object> output = createParams();
        Map<String, String> fieldnames = new HashMap<>();

        for (Field f : ClientProfileNotification.class.getDeclaredFields()) {
            fieldnames.put(f.getName().toLowerCase(), f.getName());
        }

        for (String paramName : params.keySet()) {
            if (params.get(paramName) != null) {
                if (fieldnames.get(paramName.toLowerCase()) != null) {
                    output.put(fieldnames.get(paramName.toLowerCase()), params.get(paramName));
                } else {
                    output.put(paramName, params.get(paramName));
                }
            }
        }
        return output;
    }
}
