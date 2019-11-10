package com.bivgroup.services.b2bposws.facade.pos.sal.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.HashMap;
import java.util.Map;
import com.bivgroup.services.b2bposws.system.Constants;
import java.text.SimpleDateFormat;
import java.util.Date;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("SAL_TerroristsCustom")
public class SAL_TerroristsCustomFacade extends B2BBaseFacade {

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

    private String generateSALNoteFIO(String searchName, Date birthDate, Map<String, Object> checkRes) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return String.format("Исходные данные %s и %s совпадают с %s и %s перечня террористов.",
                searchName, df.format(birthDate), checkRes.get("NAME").toString(),
                df.format((Date) parseAnyDate(checkRes.get("BIRTHDATE"), Date.class, "BIRTHDATE")));
    }

    private String generateSALNoteDoc(String docSeries, String docNumber, Map<String, Object> checkRes2) {
        return String.format("Исходные данные %s и %s совпадают с %s и %s перечня террористов.",
                docSeries, docNumber, checkRes2.get("DOCUMENTSERIES").toString(), checkRes2.get("DOCUMENTNUMBER").toString());
    }

    private void writeToSALJournal(String searchName, Date birthDate, String docSeries,
            String docNumber, Map<String, Object> checkRes, Map<String, Object> checkRes2,
            String cid, String reference, boolean fioFailed, boolean docFailed, String login, String password) throws Exception {
        Map<String, Object> journalParams = new HashMap<String, Object>();
        journalParams.put(RETURN_AS_HASH_MAP, "TRUE");
        journalParams.put("EVENTID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindEventBrowseListByParam",
                "SYSNAME", "CheckCustomerWithTerrorists", "ID", login, password));
        journalParams.put("SOURCEID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindSourceBrowseListByParam",
                "SYSNAME", "StoreSBI", "ID", login, password));
        String note = "";
        if (fioFailed) {
            note += generateSALNoteFIO(searchName, birthDate, checkRes);
        }
        if (docFailed) {
            if (!note.isEmpty()) {
                note += '\n';
            }
            note += generateSALNoteDoc(docSeries, docNumber, checkRes2);
        }
        journalParams.put("NOTE", note);
        journalParams.put("ISRESOLVED", 0L);
        Map<String, Object> journalRes = this.callService(Constants.B2BPOSWS, "dsSAL_JournalCreate", journalParams, login, password);
        if ((journalRes != null) && (journalRes.get("ID") != null)) {
            // создаем признаки события
            if (fioFailed) {
                Map<String, Object> flagParams = new HashMap<String, Object>();
                flagParams.put("JOURNALID", journalRes.get("ID"));
                flagParams.put("FLAGEVENTID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindFlagEventBrowseListByParam",
                        "SYSNAME", "TerroristsByNameAndDateOfBirth", "ID", login, password));
                this.callService(Constants.B2BPOSWS, "dsSAL_Journal_FlagCreate", flagParams, login, password);
            }
            if (docFailed) {
                Map<String, Object> flagParams = new HashMap<String, Object>();
                flagParams.put("JOURNALID", journalRes.get("ID"));
                flagParams.put("FLAGEVENTID", resolveParamByService(Constants.B2BPOSWS, "dsSAL_KindFlagEventBrowseListByParam",
                        "SYSNAME", "TerroristsByDocument", "ID", login, password));
                this.callService(Constants.B2BPOSWS, "dsSAL_Journal_FlagCreate", flagParams, login, password);
            }
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

    @WsMethod(requiredParams = {"NAME", "LASTNAME", "BIRTHDATE", "DOCSERIES", "DOCNUMBER", "CID", "REFERENCE"})
    public Map<String, Object> dsSAL_TerroristsDoCheck(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String name = params.get("NAME").toString();
        name = name.replaceAll(" ", "").replaceAll("-", "");
        String lastName = params.get("LASTNAME").toString();
        lastName = lastName.replaceAll(" ", "").replaceAll("-", "");
        String middleName = null;
        if ((params.get("MIDDLENAME") != null) && (!params.get("MIDDLENAME").toString().isEmpty())) {
            middleName = params.get("MIDDLENAME").toString();
            middleName = middleName.replaceAll(" ", "").replaceAll("-", "");
        }
        String searchName = lastName + " " + name;
        if (middleName != null) {
            searchName += " " + middleName;
        }
        Double birthDate = Math.floor((Double) parseAnyDate(params.get("BIRTHDATE"), Double.class, "BIRTHDATE"));

        String docSeries = params.get("DOCSERIES").toString();
        String docNumber = params.get("DOCNUMBER").toString();
        String cid = params.get("CID").toString();
        String reference = params.get("REFERENCE").toString();
        /*Map<String, Object> nodeParams = new HashMap<String, Object>();
        nodeParams.put(RETURN_AS_HASH_MAP, "TRUE");
        nodeParams.put("ORDERBY", "T.CREATEDATE DESC");
        nodeParams.put("STATESYSNAME", "SAL_TERRORISTSNODE_WORK");
        Map<String, Object> nodeRes = this.callService(Constants.B2BPOSWS, "dsSAL_TerroristsNodeBrowseListByParam", nodeParams, login, password);*/
        Map<String, Object> result = new HashMap<String, Object>();
        //if ((nodeRes != null) && (nodeRes.get("ID") != null)) {
            // сначала ищем по ФИО + ДР
            Map<String, Object> checkParams = new HashMap<String, Object>();
            checkParams.put(RETURN_AS_HASH_MAP, "TRUE");
            //paramsAreValid.put("NODEID", nodeRes.get("ID"));
            checkParams.put("NAME", searchName.toUpperCase());
            checkParams.put("BIRTHDATE", birthDate);
            Map<String, Object> checkRes = this.callService(Constants.B2BPOSWS, "dsSAL_TerroristsBrowseListByParam", checkParams, login, password);
            boolean fioFailed = false;
            boolean docFailed = false;
            if (checkRes != null) {
                if (checkRes.get("ID") != null) {
                    result.put("CHECKRES", "TERRORIST");
                    fioFailed = true;
                } else {
                    result.put("CHECKRES", "OK");
                }
                // теперь ищем еще и по документу
                checkParams.clear();
                checkParams.put(RETURN_AS_HASH_MAP, "TRUE");
                //paramsAreValid.put("NODEID", nodeRes.get("ID"));
                checkParams.put("DOCUMENTSERIES", docSeries);
                checkParams.put("DOCUMENTNUMBER", docNumber);
                Map<String, Object> checkRes2 = this.callService(Constants.B2BPOSWS, "dsSAL_TerroristsBrowseListByParam", checkParams, login, password);
                if (checkRes2 != null) {
                    if (checkRes2.get("ID") != null) {
                        result.put("CHECKRES", "TERRORIST");
                        docFailed = true;
                    }
                } else {
                    result.put("CHECKRES", "Ошибка при проверке");
                }
                // если нашли террориста, необходимо оставить запись в журнале безопасности
                if ((fioFailed) || (docFailed)) {
                    writeToSALJournal(searchName, (Date) parseAnyDate(birthDate, Date.class, "BIRTHDATE"),
                            docSeries, docNumber, checkRes, checkRes2, cid, reference, fioFailed, docFailed, login, password);
                }
            } else {
                result.put("CHECKRES", "Ошибка при проверке");
            }
        //} else {
        //    result.put("CHECKRES", "Ошибка при проверке");
        //}
        return result;
    }
}
