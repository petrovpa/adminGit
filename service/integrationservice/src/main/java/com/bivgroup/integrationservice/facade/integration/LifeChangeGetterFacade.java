package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import ru.diasoft.rsa.beanmaputils.BeanToMapMapper;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BOName("LifeChangeGetter")
public class LifeChangeGetterFacade extends IntegrationBaseFacade {
    public static final int B2B_LOSSNOTICE_SENDING = 8501;
    public static final int B2B_LOSSNOTICE_SENDED = 8502;
    private static final String LOSS_NOTICE_MAP_PARAMNAME = "LOSSNOTICE" + "MAP";
    private static final String LOSS_NOTICE_ID_PARAMNAME = "lossNoticeId";
    private static final String LOSS_NOTICE_STATEID_PARAMNAME = "stateId";
    private static final String LOSS_NOTICE_DOCFOLDER1C_PARAMNAME = "docFolder1C";
    private static final String LOSS_NOTICE_EXTERNAL_ID_PARAMNAME = "externalId";

    @WsMethod(requiredParams = {"CHANGEID", "CHANGETYPE"})
    public Map<String, Object> dsLifeIntegrationGetChange(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            GetChange gc = new GetChange();
            long changeId = 23012400;//979763782
            String changeType = "CHANGE_ASSET";
            if (params.get("CHANGEID") != null) {
                changeType = getStringParam(params.get("CHANGETYPE"));
                logger.info("LK get Full contr info: " + params.get("CHANGEID").toString());
                changeId = Long.valueOf(params.get("CHANGEID").toString()).longValue();
            }
            gc.setChangeId(changeId);
            gc.setChangeType(changeType);
            ChangeListType resChangeList = callLifePartnerGetChanges(gc);
            List<ChangeType> ccList = resChangeList.getChange();
            List<Map<String,Object>> changeList = new ArrayList<Map<String,Object>>();

            for (ChangeType change : ccList) {
                Map<String, Object> claimMap = BeanToMapMapper.mapBeansToMap(change);
                //Map<String, Object> claimMap = mapClaim(claim, login, password);
                changeList.add(claimMap);

            }
            result.put("CHANGEFULLLIST", changeList);
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetChanges call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            // stack trace as a string
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;
    }


    private List<Map<String, Object>> getChangeList(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put(LOSS_NOTICE_STATEID_PARAMNAME, B2B_LOSSNOTICE_SENDING);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BLossNoticeLoadByStateId", searchParam, login, password);
        if (res.get(RESULT) != null) {
            return (List<Map<String, Object>>) res.get(RESULT);
        }
        return null;
    }




    private void mappingChange(Map<String, Object> lossNoticeMap, Map<String, Object> params, ChangeApplicationListType cit, String login, String password) {

    }
    
}
