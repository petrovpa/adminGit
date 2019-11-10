package com.bivgroup.imports;

import com.bivgroup.core.aspect.AspectEntityManager;
import com.bivgroup.core.dictionary.dao.hierarchy.ExtendGenericDAO;
import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 *
 * @author aklunok
 */
public class ImportsHierarchyDAO extends HierarchyDAO implements ExtendGenericDAO {

    @PersistenceContext(unitName = "Imports")
    private EntityManager l_em;

    @Resource
    private UserTransaction l_ut;

    @Inject
    private AspectEntityManager l_aem;

    public ImportsHierarchyDAO() {
    }

    @PostConstruct
    public void init() {
        this.setEm(l_em);
        this.setUt(l_ut);
        this.setAem(l_aem);
        initEntityClasses();
    }

    @PreDestroy
    public void cleanUp() {
    }

    @Override
    public void initEntityClasses() {
        // crm
        mapClasses.put("BinaryFile", com.bivgroup.crm.BinaryFile.class);
        mapClasses.put("SMState", com.bivgroup.crm.SMState.class);
        mapClasses.put("SMType", com.bivgroup.crm.SMType.class);
        mapClasses.put("SMTransition", com.bivgroup.crm.SMTransition.class);
        // imports
        mapClasses.put("ImportSession", com.bivgroup.imports.ImportSession.class);
        mapClasses.put("ImportSessionContent", com.bivgroup.imports.ImportSessionContent.class);
        mapClasses.put("ImportSessionContentDepartment", com.bivgroup.imports.ImportSessionContentDepartment.class);
        mapClasses.put("ImportSessionContentManagerContract", com.bivgroup.imports.ImportSessionContentManagerContract.class);
        mapClasses.put("ImportSessionContentManagerDepartment", com.bivgroup.imports.ImportSessionContentManagerDepartment.class);
        mapClasses.put("ImportSessionDepartment", com.bivgroup.imports.ImportSessionDepartment.class);
        mapClasses.put("ImportSessionDepartmentSegment", com.bivgroup.imports.ImportSessionDepartmentSegment.class);
        mapClasses.put("ImportSessionManagerContract", com.bivgroup.imports.ImportSessionManagerContract.class);
        mapClasses.put("ImportSessionManagerDepartment", com.bivgroup.imports.ImportSessionManagerDepartment.class);
        // imports - classifiers
        mapClasses.put("KindImportSessionDepartmentSegment", com.bivgroup.imports.KindImportSessionDepartmentSegment.class);
        mapClasses.put("KindImportSessionDepartmentType", com.bivgroup.imports.KindImportSessionDepartmentType.class);
        mapClasses.put("KindImportSessionManagerPosition", com.bivgroup.imports.KindImportSessionManagerPosition.class);
        mapClasses.put("KindImportSessionContentProcessLogEvent", com.bivgroup.imports.KindImportSessionContentProcessLogEvent.class);
        mapClasses.put("KindImportSessionDepartmentLevel", com.bivgroup.imports.KindImportSessionDepartmentLevel.class);
        mapClasses.put("KindImportSessionFileExceptionRule", com.bivgroup.imports.KindImportSessionFileExceptionRule.class);
        mapClasses.put("KindImportSessionDepartmentFileExceptionRule", com.bivgroup.imports.KindImportSessionDepartmentFileExceptionRule.class);
        // imports - events
        mapClasses.put("ImportSessionContentProcessLogEntry", com.bivgroup.imports.ImportSessionContentProcessLogEntry.class);
        mapClasses.put("ImportSessionEventContractNotFound", com.bivgroup.imports.ImportSessionEventContractNotFound.class);
        mapClasses.put("ImportSessionEventDepartmentBlocked", com.bivgroup.imports.ImportSessionEventDepartmentBlocked.class);
        mapClasses.put("ImportSessionEventDepartmentCreated", com.bivgroup.imports.ImportSessionEventDepartmentCreated.class);
        mapClasses.put("ImportSessionEventDepartmentNotFound", com.bivgroup.imports.ImportSessionEventDepartmentNotFound.class);
        mapClasses.put("ImportSessionEventDepartmentToContractChanged", com.bivgroup.imports.ImportSessionEventDepartmentToContractChanged.class);
        mapClasses.put("ImportSessionEventDepartmentToContractCreated", com.bivgroup.imports.ImportSessionEventDepartmentToContractCreated.class);
        mapClasses.put("ImportSessionEventDepartmentToDepartmentCreated", com.bivgroup.imports.ImportSessionEventDepartmentToDepartmentCreated.class);
        mapClasses.put("ImportSessionEventGroupToContractCreated", com.bivgroup.imports.ImportSessionEventGroupToContractCreated.class);
        mapClasses.put("ImportSessionEventManagerBlocked", com.bivgroup.imports.ImportSessionEventManagerBlocked.class);
        mapClasses.put("ImportSessionEventManagerCreated", com.bivgroup.imports.ImportSessionEventManagerCreated.class);
        mapClasses.put("ImportSessionEventManagerNotFound", com.bivgroup.imports.ImportSessionEventManagerNotFound.class);
        mapClasses.put("ImportSessionEventManagerToContractChanged", com.bivgroup.imports.ImportSessionEventManagerToContractChanged.class);
        mapClasses.put("ImportSessionEventManagerToContractCreated", com.bivgroup.imports.ImportSessionEventManagerToContractCreated.class);
        mapClasses.put("ImportSessionEventManagerToDepartmentChanged", com.bivgroup.imports.ImportSessionEventManagerToDepartmentChanged.class);
        mapClasses.put("ImportSessionEventManagerToDepartmentCreated", com.bivgroup.imports.ImportSessionEventManagerToDepartmentCreated.class);
        mapClasses.put("ImportSessionEventNoChanges", com.bivgroup.imports.ImportSessionEventNoChanges.class);
        mapClasses.put("ImportSessionEventToProcessing", com.bivgroup.imports.ImportSessionEventToProcessing.class);
        mapClasses.put("ImportSessionEventUnknownError", com.bivgroup.imports.ImportSessionEventUnknownError.class);
        // todo: остальные основные события, если потребуется для корректной работы с ними
    }

}
