package com.bivgroup.services.b2bposws.facade.pos.sal.custom;

import com.bivgroup.services.b2bposws.system.Constants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("SAL_InvalidPassportsCustom")
public class SAL_InvalidPassportsCustomFacade extends BaseFacade {

    private Object resolveParamByService(String serviceName, String methodName, String fieldName,
            Object fieldValue, String returnFieldName, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put(fieldName, fieldValue);
        Map<String, Object> res = this.callService(serviceName, methodName, params, login, password);
        if ((res != null) && (res.get(returnFieldName) != null)) {
            return res.get(returnFieldName);
        } else {
            return null;
        }
    }

    private String generateSALNote(String docSeries, String docNumber, Map<String, Object> checkRes) {
        return String.format("Исходные данные %s и %s совпадают с %s перечня недействительных паспортов.",
                docSeries, docNumber, checkRes.get("SERIESNUMBER").toString());
    }

    private void writeToSALJournal(String docSeries, String docNumber, Map<String, Object> checkRes,
            String cid, String reference, String login, String password) throws Exception {
        Map<String, Object> journalParams = new HashMap<String, Object>();
        journalParams.put(RETURN_AS_HASH_MAP, "TRUE");
        journalParams.put("EVENTID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindEventBrowseListByParam",
                "SYSNAME", "CheckCustomerWithInvalidPassports", "ID", login, password));
        journalParams.put("SOURCEID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindSourceBrowseListByParam",
                "SYSNAME", "StoreSBI", "ID", login, password));
        String note = generateSALNote(docSeries, docNumber, checkRes);
        journalParams.put("NOTE", note);
        journalParams.put("ISRESOLVED", 0L);
        Map<String, Object> journalRes = this.callService(Constants.B2BPOSWS, "dsSAL_JournalCreate", journalParams, login, password);
        if ((journalRes != null) && (journalRes.get("ID") != null)) {
            // создаем признак события
            Map<String, Object> flagParams = new HashMap<String, Object>();
            flagParams.put("JOURNALID", journalRes.get("ID"));
            flagParams.put("FLAGEVENTID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindFlagEventBrowseListByParam",
                    "SYSNAME", "InvalidPassportsByDocument", "ID", login, password));
            this.callService(Constants.B2BPOSWS, "dsSAL_Journal_FlagCreate", flagParams, login, password);
            // создаем контекст
            Map<String, Object> contextParams = new HashMap<String, Object>();
            contextParams.put("JOURNALID", journalRes.get("ID"));
            contextParams.put("PROPERTYSOURCEID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindContextSourceBrowseListByParam",
                    "SYSNAME", "StoreSBI_Reference", "ID", login, password));
            contextParams.put("VALUE", reference);
            this.callService(Constants.B2BPOSWS, "dsSAL_Journal_ContextCreate", contextParams, login, password);
            contextParams.clear();
            contextParams.put("JOURNALID", journalRes.get("ID"));
            contextParams.put("PROPERTYSOURCEID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindContextSourceBrowseListByParam",
                    "SYSNAME", "StoreSBI_CID", "ID", login, password));
            contextParams.put("VALUE", cid);
            this.callService(Constants.B2BPOSWS, "dsSAL_Journal_ContextCreate", contextParams, login, password);
        }
    }

    @WsMethod(requiredParams = {"DOCSERIES", "DOCNUMBER", "CID", "REFERENCE"})
    public Map<String, Object> dsSAL_InvalidPassportsDoCheck(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String docSeries = params.get("DOCSERIES").toString();
        String docNumber = params.get("DOCNUMBER").toString();
        String cid = params.get("CID").toString();
        String reference = params.get("REFERENCE").toString();
        /*Map<String, Object> nodeParams = new HashMap<String, Object>();
        nodeParams.put(RETURN_AS_HASH_MAP, "TRUE");
        nodeParams.put("ORDERBY", "T.CREATEDATE DESC");
        nodeParams.put("STATESYSNAME", "SAL_INVALIDPASSPORTSNODE_WORK");
        Map<String, Object> nodeRes = this.callService(Constants.B2BPOSWS, "dsSAL_InvalidPassportsNodeBrowseListByParam", nodeParams, login, password);*/
        Map<String, Object> result = new HashMap<String, Object>();
        //if ((nodeRes != null) && (nodeRes.get("ID") != null)) {
            Map<String, Object> checkParams = new HashMap<String, Object>();
            checkParams.put(RETURN_AS_HASH_MAP, "TRUE");
            //paramsAreValid.put("NODEID", nodeRes.get("ID"));
            checkParams.put("SERIESNUMBER", docSeries + docNumber);
            Map<String, Object> checkRes = this.callService(Constants.B2BPOSWS, "dsSAL_InvalidPassportsBrowseListByParam", checkParams, login, password);
            boolean failed = false;
            if (checkRes != null) {
                if (checkRes.get("ID") != null) {
                    result.put("CHECKRES", "INVALIDPASSPORT");
                    failed = true;
                } else {
                    result.put("CHECKRES", "OK");
                }
            } else {
                result.put("CHECKRES", "Ошибка при проверке паспорта");
            }
            // если нашли террориста, необходимо оставить запись в журнале безопасности
            if (failed) {
                writeToSALJournal(docSeries, docNumber, checkRes, cid, reference, login, password);
            }
        //} else {
        //    result.put("CHECKRES", "Ошибка при проверке паспорта");
        //}
        return result;
    }
}
