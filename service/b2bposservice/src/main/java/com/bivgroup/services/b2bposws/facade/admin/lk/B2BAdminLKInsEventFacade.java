package com.bivgroup.services.b2bposws.facade.admin.lk;

import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фасад для работы с договорами СБСЖ ЛК
 *
 * @author petrovpa
 */
@BOName("B2BAdminLKInsEvent")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminLKInsEventFacade extends B2BAdminLKBaseFacade {

    /**
     * Функция для получения списка договоров
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLkBrowseInsEventListByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsAdminLkBrowseInsEventListByParams", params);
        return result;
    }


    @WsMethod(requiredParams = {"LOSSNOTICEID", "URLPATH"})
    public Map<String, Object> dsB2BAdminLKAllDocCustomBrowseListByParamEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        List<Map<String, Object>> docsList = new ArrayList<>();

        // Загружает все документы приложенные клиентом к заявлению о страховом событии
        Map<String, Object> lossNoticeDocsRes = this.callExternalService(Constants.B2BPOSWS, "dsB2BLossNoticeDocCustomBrowseListByParamEx", params, login, password);
        if (lossNoticeDocsRes.get(ERROR) == null) {
            docsList = (List<Map<String, Object>>) lossNoticeDocsRes.get(RESULT);
        }


        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, docsList);

        return result;
    }

    // Метод для получения файлов к заявлению на убыток(прикрепленные файлы и заявление на выплату)
    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME, DECLARATION_ID_PARAMNAME, URLPATH_PARAMNAME})
    public Map<String, Object> dsB2BAdminLKGetAllAttachedFileWithDeclaration(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        List<Map<String, Object>> resultFilesList = new ArrayList<>();

        // Пути загружать не нужно
        params.put("ISNEEDPROCESSFORUPLOAD", false);

        //Получаем список файлов по зявлению
        params.put("LOSSNOTICEID", params.get(LOSS_NOTICE_ID_PARAMNAME));
        List<Map<String, Object>> resultAttachedFilesOfLossNoticeRes = this.callServiceAndGetListFromResultMapLogged(Constants.B2BPOSWS, "dsB2BLossNoticeDocCustomBrowseListByParamEx", params, login, password);

        if ((resultAttachedFilesOfLossNoticeRes != null) && (!resultAttachedFilesOfLossNoticeRes.isEmpty())) {

            // Такое решение обусловлено тем, что не удалось изменить аспект BinFile, т.к. нет доступа к исходнику
            // Изменить в аспекте условие с T.OBJID = #bind($OBJID на T.OBJID in #bind($OBJID
            for (Map lossNoticeDocItem : resultAttachedFilesOfLossNoticeRes) {
                // Подготавливаем параметры для получения свойств бинарного файла из ins_binfile
                if ((lossNoticeDocItem != null) && (lossNoticeDocItem.get("LOSSNOTICEDOCID") != null) && getLongParam(lossNoticeDocItem.get("LOSSNOTICEDOCID")) != null) {

                    // Загружаем прикрепленные файлы к заявлению
                    Map<String, Object> lossNoticeDocParams = new HashMap<>();
                    lossNoticeDocParams.put("OBJID", getLongParam(lossNoticeDocItem.get("LOSSNOTICEDOCID")));

                    String browseBinFileMethodName = "ds" + LOSS_NOTICE_DOC_ATTACHMENT_FACADE_NAME + "_BinaryFile_BinaryFileBrowseListByParam";
                    List<Map<String, Object>> resultBinaryFileList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, browseBinFileMethodName, lossNoticeDocParams, login, password);

                    if ((resultBinaryFileList != null)) {
                        resultFilesList.addAll(resultBinaryFileList);
                    }
                }
            }
        }

        // Загружаем заявления на выплату
        Map<String, Object> resultAttachedDeclarationOfPaymentRes = this.callExternalService(Constants.B2BPOSWS, "dsB2BLossPaymentClaimBrowseListByLossNoticeId", params, login, password);

        if ((resultAttachedDeclarationOfPaymentRes != null) && (resultAttachedDeclarationOfPaymentRes.get(RESULT) != null)) {
            Map<String, Object> resultAttachedDeclarationOfPaymentMap = (Map<String, Object>) resultAttachedDeclarationOfPaymentRes.get(RESULT);
            if ((resultAttachedDeclarationOfPaymentMap != null) && (resultAttachedDeclarationOfPaymentMap.get("lossPaymentClaimList") != null)) {
                resultFilesList.addAll((List<Map<String, Object>>) resultAttachedDeclarationOfPaymentMap.get("lossPaymentClaimList"));
            }
        }

        // Подготавливаем зашифрованый список файлов для ZIP файла(путь - имя)
        String encryptString = processDocListForUploadZip(resultFilesList, params, login, password);


        Map<String, Object> result = new HashMap<>();

        result.put("ENCRYPTSTRING", encryptString);

        return result;
    }

    // Формирование списка возможных состояний.
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2B_insEventGetStateList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> stateList = new ArrayList<Map<String, Object>>();
        Map<String, Object> cState = new HashMap<>();
        cState.put("NAME", "Черновик");
        cState.put("SYSNAME", "'B2B_LOSSNOTICE_DRAFT'");
        cState.put("ID", 8500);
        stateList.add(cState);
        cState = new HashMap<>();
        cState.put("NAME", "В работе");
        cState.put("SYSNAME", "'B2B_LOSSNOTICE_INWORK'");
        cState.put("ID", 8503);
        stateList.add(cState);
        cState = new HashMap<>();
        cState.put("NAME", "Закрыт");
        cState.put("SYSNAME", "'B2B_LOSSNOTICE_FINAL'");
        cState.put("ID", 8504);
        stateList.add(cState);

        cState = new HashMap<>();
        cState.put("NAME", "Заявление зарегистрировано (В отправке)");
        cState.put("SYSNAME", "'B2B_LOSSNOTICE_SENDING'");
        cState.put("ID", 8501);
        stateList.add(cState);

        cState = new HashMap<>();
        cState.put("NAME", "Заявление зарегистрировано (Отправлено)");
        cState.put("SYSNAME", "'B2B_LOSSNOTICE_SENDED'");
        cState.put("ID", 8502);
        stateList.add(cState);

        result.put(RESULT, stateList);
        return result;
    }

}
