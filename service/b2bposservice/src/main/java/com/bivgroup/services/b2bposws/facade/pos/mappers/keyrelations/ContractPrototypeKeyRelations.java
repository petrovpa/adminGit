package com.bivgroup.services.b2bposws.facade.pos.mappers.keyrelations;
// todo: сменить имя пакета на более лаконичное, например *.mappers.rules или т.п. (после сведения веток)

import java.util.*;

public interface ContractPrototypeKeyRelations {

    String INTERFACE_INSURER_PARAMNAME = "INSURER";
    String INTERFACE_INSURER_DOC_PARAMNAME = INTERFACE_INSURER_PARAMNAME + "DOC";

    String CRM_INSURER_PARAMNAME = "INSURERMAP";

    String CRM_CITIZENSHIP_PARAMNAME = "CITIZENSHIP";
    String CRM_CITIZENSHIP_RUSSIAN_VALUE = "0";
    String CRM_CITIZENSHIP_FOREIGN_VALUE = "1000";

    String OPERATOR_ADD_TO_LIST = "$ADDTOLIST";

    String CITIZENSHIP_RULE =
            CRM_CITIZENSHIP_RUSSIAN_VALUE + " > " + CRM_CITIZENSHIP_RUSSIAN_VALUE + "; " +
            CRM_CITIZENSHIP_RUSSIAN_VALUE + " > " + CRM_CITIZENSHIP_FOREIGN_VALUE;

    List<String[]> INSURER_MAIN_KEY_RELATIONS_LIST = createKeyRelationList(new String[][] {
            // main info
            {"", CRM_INSURER_PARAMNAME}, // для создания мапы CRM_INSURER_PARAMNAME (INSURERMAP)
            {INTERFACE_INSURER_PARAMNAME + "PARTICIPANTTYPE", CRM_INSURER_PARAMNAME + "/PARTICIPANTTYPE", "1 > 1; 2 > 2", "1"},
            {INTERFACE_INSURER_PARAMNAME + "LASTNAME", CRM_INSURER_PARAMNAME + "/LASTNAME"},
            {INTERFACE_INSURER_PARAMNAME + "FIRSTNAME", CRM_INSURER_PARAMNAME + "/FIRSTNAME"},
            {INTERFACE_INSURER_PARAMNAME + "MIDDLENAME", CRM_INSURER_PARAMNAME + "/MIDDLENAME"},
            {
                    INTERFACE_INSURER_PARAMNAME + CRM_CITIZENSHIP_PARAMNAME,
                    CRM_INSURER_PARAMNAME + "/" + CRM_CITIZENSHIP_PARAMNAME,
                    CITIZENSHIP_RULE,
                    CRM_CITIZENSHIP_RUSSIAN_VALUE
            },
            {INTERFACE_INSURER_PARAMNAME + "BIRTHDATE", CRM_INSURER_PARAMNAME + "/BIRTHDATE"},
            {INTERFACE_INSURER_PARAMNAME + "GENDER", CRM_INSURER_PARAMNAME + "/GENDER", "0 > 0; 1 > 1", "0"}
    });

    List<String[]> INSURER_PASSPORT_RF_KEY_RELATIONS_LIST = createKeyRelationList(new String[][] {
            // main document - PassportRF
            {"", CRM_INSURER_PARAMNAME + "/documentList"},
            {OPERATOR_ADD_TO_LIST, CRM_INSURER_PARAMNAME + "/documentList", "DOCTYPESYSNAME", "PassportRF"},
            {INTERFACE_INSURER_DOC_PARAMNAME + "SERIES", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/DOCSERIES"},
            {INTERFACE_INSURER_DOC_PARAMNAME + "NUMBER", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/DOCNUMBER"},
            {INTERFACE_INSURER_DOC_PARAMNAME + "ISSUEDATE", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDATE"},
            {INTERFACE_INSURER_DOC_PARAMNAME + "ISSUEDBY", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDBY"}
    });

    List<String[]> INSURER_FULL_KEY_RELATIONS_LIST = createKeyRelationList(
            INSURER_MAIN_KEY_RELATIONS_LIST,
            INSURER_PASSPORT_RF_KEY_RELATIONS_LIST
    );

    Map<String, Class> INSURER_FULL_KEY_CLASSES = createClassMap(
            new Object[] {CRM_INSURER_PARAMNAME, HashMap.class},
            new Object[] {"documentList", ArrayList.class}
    );

    // маппинг обогащения прототипа договора по содержимому 1C
    List<String[]> CONTRACT_PROTOTYPE_CONTENT_TO_CONTRACT_KEY_RELATIONS_LIST = createKeyRelationList(new String[][]{
            {"CONTRNUMBER", "CONTRNUMBER"},
            {"SIGNDATE", "SIGNDATE"},
            {"SIGNDATETIME", "SIGNDATETIME"},
            {"STARTDATE", "STARTDATE"},
            {"STARTDATETIME", "STARTDATETIME"},
            {"FINISHDATE", "FINISHDATE"},
            {"FINISHDATETIME", "FINISHDATETIME"},
            // {"INSURERTYPEPERSON", ""},
            {"INSURERSURNAME", CRM_INSURER_PARAMNAME + "/LASTNAME"},
            {"INSURERNAME", CRM_INSURER_PARAMNAME + "/FIRSTNAME"},
            {"INSURERPATRONYMIC", CRM_INSURER_PARAMNAME + "/MIDDLENAME"},
            {"INSURERBIRTHDATE", CRM_INSURER_PARAMNAME + "/BIRTHDATE"},
            // {"INSURERBIRTHDATETIME", CRM_INSURER_PARAMNAME + "/BIRTHDATETIME"},
            {"", CRM_INSURER_PARAMNAME + "/documentList"},
            // {"INSURERDOCUMENTTYPEID", ""},
            {OPERATOR_ADD_TO_LIST, CRM_INSURER_PARAMNAME + "/documentList", "DOCTYPESYSNAME", "PassportRF"},
            {"INSURERDOCUMENTNO", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/DOCNUMBER"},
            {"INSURERDOCUMENTSERIES", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/DOCSERIES"},
            {"INSURERDOCUMENTISSUEDATE", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDATE"},
            // {"INSURERDOCUMENTISSUEDATETIME", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDATETIME"},
            {"INSURERDOCUMENTAUTHORITY", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDBY"}
    });

    Map<String, Class> CONTRACT_PROTOTYPE_CONTENT_TO_CONTRACT_KEY_CLASSES = createClassMap(
            new Object[] {CRM_INSURER_PARAMNAME, HashMap.class},
            new Object[] {"documentList", ArrayList.class}
    );

    // todo: общие статические методы из *To*KeyRelations вынести в отдельный класс/интерфейс (после сведения веток)

    static Map<String, Class> createClassMap(Object[]... classRuleList) {
        Map<String, Class> classMap = new HashMap<String, Class>();
        for (Object[] classRule : classRuleList) {
            if ((classRule != null) && (classRule.length == 2) && (classRule[0] instanceof String) && (classRule[1] instanceof Class)) {
                classMap.put((String) classRule[0], (Class) classRule[1]);
            }
        }
        return classMap;
    }

    static List<String[]> createKeyRelationList(String[][] keyRelationArray) {
        List<String[]> keyRelationList = new ArrayList<String[]>();
        Collections.addAll(keyRelationList, keyRelationArray);
        return keyRelationList;
    }

    static List<String[]> createKeyRelationList(List<String[]>... keyRelationListArray) {
        List<String[]> totalKeyRelationList = new ArrayList<String[]>();
        for (List<String[]> keyRelationList: keyRelationListArray) {
            totalKeyRelationList.addAll(keyRelationList);
        }
        return totalKeyRelationList;
    }


}
