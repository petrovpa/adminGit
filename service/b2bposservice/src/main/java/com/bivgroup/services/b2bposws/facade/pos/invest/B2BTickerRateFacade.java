package com.bivgroup.services.b2bposws.facade.pos.invest;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@IdGen(entityName="B2B_INVTICKERRATE", idFieldName="INVTICKERRATEID")
@BOName("B2BTickerRate")
public class B2BTickerRateFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERRATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERRATEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BTickerRateCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.insertQuery("dsB2BTickerRateInsert", params);
        result.put("INVTICKERRATEID", params.get("INVTICKERRATEID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERRATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERRATEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERRATEID"})
    public Map<String, Object> dsB2BTickerRateInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.insertQuery("dsB2BTickerRateInsert", params);
        result.put("INVTICKERRATEID", params.get("INVTICKERRATEID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERRATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERRATEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERRATEID"})
    public Map<String, Object> dsB2BTickerRateUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BTickerRateUpdate", params);
        result.put("INVTICKERRATEID", params.get("INVTICKERRATEID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERRATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERRATEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERRATEID"})
    public Map<String, Object> dsB2BTickerRateModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.updateQuery("dsB2BTickerRateUpdate", params);
        result.put("INVTICKERRATEID", params.get("INVTICKERRATEID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERRATEID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERRATEID"})
    public void dsB2BTickerRateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BTickerRateDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERRATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERRATEID - ИД записи</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>TRDATE - дата</LI>
     * <LI>RATEVALUE - цена</LI>
     * <LI>TICKERCODE - код тикера</LI>
     * <LI>TICKERNAME - наименование тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BTickerRateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BTickerRateBrowseListByParam", "dsB2BTickerRateBrowseListByParamCount", params);
        return result;
    }
    
}
