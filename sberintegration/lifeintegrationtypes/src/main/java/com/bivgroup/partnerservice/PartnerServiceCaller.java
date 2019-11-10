package com.bivgroup.partnerservice;

import org.apache.log4j.Logger;
import ru.sberinsur.esb.partner.PartnerPortType;
import ru.sberinsur.esb.partner.PartnerPortTypeService;
import ru.sberinsur.esb.partner.shema.*;
import ru.sberinsur.fuse.files.File;
import ru.sberinsur.fuse.files.Folder;
import ru.sberinsur.fuse.files.ShortFileInfo;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class PartnerServiceCaller {


    private PartnerPortType ppt = null;
    protected final Logger logger = Logger.getLogger(this.getClass());

    private void initPartnerService(String partnerServiceURL) {
        URL url = this.getClass().getClassLoader().getResource("partnerService.wsdl");
        PartnerPortTypeService ppts = new ru.sberinsur.esb.partner.PartnerPortTypeService(url);
        ppt = ppts.getPartnerPortTypePort();
        Map<String, Object> ctxt = ((BindingProvider) ppt).getRequestContext();

        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, partnerServiceURL);
        processWsSecurity((BindingProvider) ppt);
    }


    protected void processWsSecurity(BindingProvider proxy) {
        //if (this.isWsSecurityEnabled(serviceName)) {
        List<Handler> handlerChain
                = ((BindingProvider) proxy).getBinding().getHandlerChain();
        handlerChain.add(new UserNameTokenHandler("", ""));
        ((BindingProvider) proxy).getBinding().setHandlerChain(handlerChain);
        //}
    }

    public PartnerServiceCaller(String partnerServiceURL) {
        this.initPartnerService(partnerServiceURL);
    }

    public AnswerImportListType processImportPolicy(ListContractImportType contractList) {
        AnswerImportListType resContrList = ppt.processImportPolicy(contractList);
        return resContrList;
    }

    public ListContractCutType processExportPolicyCut(GetObjListType golt) {
        ListContractCutType resListContractCut = ppt.processExportPolicyCut(golt);
        return resListContractCut;
    }

    public ListContractType processExportPolicy(GetObjType got) {
        ListContractType resListContractCut = ppt.processExportPolicy(got);
        return resListContractCut;
    }
    public ClaimCutType processExportClaimCut(GetObjListType golt) {
        ClaimCutType resListClaimCut = ppt.processExportClaimCut(golt);
        return resListClaimCut;
    }

    public ClaimType processExportClaim(GetObjType got) {
        ClaimType resListClaimCut = ppt.processExportClaim(got);
        return resListClaimCut;
    }
    public AnswerImportListType processImportClaim(ClaimImportType cit) {
        AnswerImportListType answerClaim = ppt.processImportClaim(cit);
        return answerClaim;
    }

    public ChangeCutListType processExportChangeCut(GetChangeList gcl) {
        ChangeCutListType resChangeCutList = ppt.processExportChangeCut(gcl);
        return resChangeCutList;
    }

    public DeleteObjectListType processDeletedObjects(GetDeletedObjects gdo) {
        DeleteObjectListType resDeletedList = ppt.processDeletedObjects(gdo);
        return resDeletedList;
    }
    public ChangeListType processExportChange(GetChange gc) {
        ChangeListType resChangeList = ppt.processExportChange(gc);
        return resChangeList;
    }
    public AnswerImportListType processImportChange(ChangeApplicationListType calt) {
        AnswerImportListType answerChange = ppt.processImportChange(calt);
        return answerChange;
    }

    public ShortFileInfo addFile(Folder folder, File file) {
        ShortFileInfo sfi = ppt.addFile(folder, file);
        return sfi;
    }

    public File getFile(String code) {
        File file = ppt.getFile(code);
        return file;
    }

    public List<AnswerImportType> documentListImport(List<DocumentImportType> dlit) {
        List<AnswerImportType> res = ppt.documentListImport(dlit);
        return res;
    }

    public RegistrationUserAnswer processRegistrationUser(RegistrationUser user) {
        RegistrationUserAnswer resUserReg = ppt.processRegistrationUser(user);
        return resUserReg;
    }

    public void processResponseCut(AnswerImportListType ailt) {
        try {

            ppt.processResponseCut(ailt);
        } catch (Exception ex) {
            logger.error("Error call processResponseCut", ex);
        }

    }

}
