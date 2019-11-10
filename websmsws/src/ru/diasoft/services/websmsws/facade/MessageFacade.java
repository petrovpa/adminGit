package ru.diasoft.services.websmsws.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.NamedQuery;

import ru.diasoft.utils.XMLUtil;

/**
 * @entity {@name SMS-сообщение} {@domain SMS}
 * @author dlekhanov
 * @description SMS-сообщение, фасад таблицы SMS_MESSAGE
 */
public class MessageFacade {

    private DataContext dataContext;

    public MessageFacade(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    /**
     * @throws Exception 
     * @description Поиск сообщения по идентификатору
     * 
     * @tables SMS_MESSAGE
     * 
     * @inparam {@table SMS_MESSAGE} {@field MESSAGEID}
     * 
     * @outparams {@table table SMS_MESSAGE}
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> messageFindById(Map<String, Object> params) throws Exception {
        if(params.get("MESSAGEID") == null || "".equals(params.get("MESSAGEID"))){
            throw new Exception("Missing required parameter: MESSAGEID");
        }
        
        List<Map<String, Object>> result = getDataContext().performQuery(new NamedQuery("messageFindById", params));
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     * @description Поиск списка сообщений. В случае если ни один параметр не передан, будет возвращен весь список.
     * 
     * @notes Для того чтобы метод работал в Lookup'е, нужно передать параметры
     * PAGE и/или ROWSCOUNT (не равными null). 
     *
     * @tables SMS_MESSAGE
     *
     * @inparam {@table SMS_MESSAGE} {@except MESSAGEID}
     *
     * @outparamsInList
     * @outparams {@table SMS_MESSAGE}
     */
    public Map<String, Object> messageFindListByParams(Map<String, Object> params) throws Exception {
        if(params.get("SRCNUMBER") != null){
            params.put("SRCNUMBER", "%" + params.get("SRCNUMBER")+ "%");
        }
        if(params.get("DSTNUMBER") != null){
            params.put("DSTNUMBER", "%" + params.get("DSTNUMBER")+ "%");
        }
        
        XMLUtil util = new XMLUtil();
        util.convertDateToFloat(params);
        
        return ru.diasoft.services.common.QueryBuilder.getList(
                getDataContext(), "messageFindListByParamsCount", "messageFindListByParams", params);
    }

    public DataContext getDataContext() {
        return dataContext;
    }

}
