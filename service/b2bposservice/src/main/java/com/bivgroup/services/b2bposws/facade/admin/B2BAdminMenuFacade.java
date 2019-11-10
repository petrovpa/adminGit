package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фасад для сущности "Меню администратора"
 *
 * @author ivanovra
 */
@BOName("B2BAdminMenu")
public class B2BAdminMenuFacade extends B2BBaseFacade {

    /**
     * Функция для получения "Типов меню"
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetMenuList(Map<String, Object> params) throws Exception {

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);

        Map<String, Object> customParams = params;
        Map<String, Object> result = null;

        // добавляем ограничение по агентам только если пользователь не "Сотрудник страховой" и не "Робот"
        Object userTypeId = params.get(Constants.SESSIONPARAM_USERTYPEID);
        if ((userTypeId == null) || ((Long.valueOf(userTypeId.toString()) != 1L)
                && ((Long.valueOf(userTypeId.toString())) != 4L))) {
            customParams.put("CP_DEPARTMENTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
        }

        XMLUtil.convertDateToFloat(customParams);
        // Ограничение по орг структуре.
        if (null == params.get("ROOTID")) {
            customParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
            if (null != params.get("USE_SESSION_DEPARTMENTID")) {
                if (params.get("USE_SESSION_DEPARTMENTID").equals(true)) {
                    customParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
                }
            }
        }

        result = this.selectQuery("dsAdminGetMenuList", "dsAdminGetMenuListCount", customParams);

        return result;
    }

    /**
     * Метод для получения списка типов меню
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetMenuTypeList(Map<String, Object> params) throws Exception {

        Map<String, Object> resultRequest = new HashMap<String, Object>();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        if (params.get("ORDERBY") == null) {
            params.put("ORDERBY", "T.NAME ASC");
        }

        // Вызываем функцию у готового фасада для типов меню.
        resultRequest = this.callService(Constants.B2BPOSWS, "dsB2BMenuTypeBrowseListByParam", params, login, password);

        List<Map<String, Object>> menuTypeList = (List<Map<String, Object>>) resultRequest.get(RESULT);

        // Пробегаемся по полученному списку значений , и заменяем имена колонок на свои, 
        // т.к компонент DropDown мапит поля "hid" и "name". (Регистр важен).
        for (Map<String, Object> item : menuTypeList) {
            item.put("hid", item.get("MENUTYPEID"));
            item.remove("MENUTYPEID");
            item.put("name", item.get("NAME"));
            item.remove("NAME");
            item.remove("SYSNAME");
        }

        return resultRequest;
    }

    /**
     * Метод для получения списка Parent menu
     *
     * @param params
     * @return (hid и name)
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetParentMenuList(Map<String, Object> params) throws Exception {

        Map<String, Object> resultRequest = new HashMap<String, Object>();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        if (params.get("ORDERBY") == null) {
            params.put("ORDERBY", "T.NAME ASC");
        }

        if (params.get("MENUID") != null) {
            // Накладываем ограничение, чтобы в список не вернулась запись, у которой было бы MENUID == PARENTMENUID
            String customWhere = "T.MENUID <>" + params.get("MENUID").toString();
            params.put("customWhere", customWhere);
        }

        // Вызываем функцию у готового фасада для типов меню.
        resultRequest = this.selectQuery("dsGetParentMenuList", "dsGetParentMenuListCount", params);

        return resultRequest;
    }

    /**
     * Функция для создания или обновления меню
     * <p>
     * Работа функции зависит от того, что прийдет во входящей мапе. Если мапа
     * прийдет без MENUID, значит это операция создания нового меню. Если мапа
     * пришла с MENUID, значит выполнится операция редактирования (Обновления)
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAdminMenuCreateOrUpdate(Map<String, Object> params) throws Exception {

        Map<String, Object> createMapField = new HashMap<String, Object>();
        Map<String, Object> resultRequest = new HashMap<String, Object>();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        //Перемаппинг полей
        createMapField.put("MENUID", params.get("MENUID"));
        createMapField.put("NAME", params.get("ITEMNAME"));
        createMapField.put("ACTIONURL", params.get("ACTIONURL"));
        createMapField.put("PICTUREURL", params.get("PICTUREURL"));
        createMapField.put("SYSNAME", params.get("ITEMSYSNAME"));
        createMapField.put("MENUTYPEID", params.get("MENUTYPEID"));

        // Обработка для типа вида "Не выбран/указан"
        if (params.get("PARENTMENUID").equals(-1)) {
            createMapField.put("PARENTMENUID", null);
        } else {
            createMapField.put("PARENTMENUID", params.get("PARENTMENUID"));
        }

        createMapField.put("POSITION", params.get("POSITION"));
        createMapField.put("MODULENAME", params.get("MODULENAME"));
        createMapField.put("needTransaction", params.get("needTransaction"));
        createMapField.put("SESSION_USERACCOUNTID", params.get("SESSION_USERACCOUNTID"));
        createMapField.put("INSSYSTEMPASSWORD", params.get("INSSYSTEMPASSWORD"));
        createMapField.put("SESSIONIDFORCALL", params.get("SESSIONIDFORCALL"));
        createMapField.put("INSSYSTEMPASSWORD", params.get("INSSYSTEMPASSWORD"));
        createMapField.put("SESSION_DEPARTMENTID", params.get("SESSION_DEPARTMENTID"));
        createMapField.put("SESSION_USERTYPEID", params.get("SESSION_USERTYPEID"));

        // Если с клиента пришел id, тогда это редактирование, иначе это создание
        if (params.get("MENUID") != null) {
            resultRequest = this.callService(Constants.B2BPOSWS, "dsB2BMenuUpdate", createMapField, login, password);
        } else {
            // Вызываем функцию у готового фасада для типов меню.
            resultRequest = this.callService(Constants.B2BPOSWS, "dsB2BMenuCreate", createMapField, login, password);
        }

        return resultRequest;
    }

    /**
     * Метод для удаления меню
     *
     * @param params Главный параметр MENUID. По этому id будет удалятся запись
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"MENUID"})
    public Map<String, Object> dsB2BAdminMenuDelete(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        //Сначала удаляем все права на это меню
        //result = this.callService(Constants.B2BPOSWS, "dsB2BAdminDeleteMenuRight", params, login, password);
        this.deleteQuery("dsB2BMenuorgstructDeleteByMenuID", params);
        //Затему даляем меню
        result = this.callService(Constants.B2BPOSWS, "dsB2BMenuDelete", params, login, password);

        return result;
    }

}
