package com.bivgroup.services.b2bposws.facade.pos.mappers.keyrelations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UnionPersonToParticipantKeyRelations {

    String CRM_INSURER_PARAMNAME = "INSURERMAP";

    // CRM_APPLICANT_PARAMNAME установлен равным "INSURERMAP" для возможности
    // использования соответствий и для договоров и в ходе маппига заявлений на активацию
    String CRM_APPLICANT_PARAMNAME = CRM_INSURER_PARAMNAME;

    String UNION_INSURER_PARAMNAME = "insurer";

    String UNION_CITIZENSHIP_PARAMNAME = "citizenship";
    String UNION_CITIZENSHIP_RUSSIAN_SYSNAME = "russian";
    String UNION_CITIZENSHIP_FOREIGN_SYSNAME = "foreign";

    String CRM_CITIZENSHIP_PARAMNAME = "CITIZENSHIP";
    String CRM_CITIZENSHIP_RUSSIAN_VALUE = "0";
    String CRM_CITIZENSHIP_FOREIGN_VALUE = "1000";

    String OPERATOR_ADD_TO_LIST = "$ADDTOLIST";

    String CITIZENSHIP_RULE =
            UNION_CITIZENSHIP_RUSSIAN_SYSNAME + " > " + CRM_CITIZENSHIP_RUSSIAN_VALUE + "; " +
            UNION_CITIZENSHIP_FOREIGN_SYSNAME + " > " + CRM_CITIZENSHIP_FOREIGN_VALUE;

    String CRM_ADDRESS_LIST_PARAMNAME = "addressList";
    String CRM_APPLICANT_ADDRESS_LIST_PARAMPATH = CRM_INSURER_PARAMNAME + "/" + CRM_ADDRESS_LIST_PARAMNAME;

    List<String[]> UNION_COMMON_MAIN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][] {
            {"sessionToken", "SESSIONID"},
            {"promoCode", "B2BPROMOCODE"},
            {"premValue", "PREMVALUE"},
            {"insAmValue", "INSAMVALUE"}
    });

    List<String[]> INSURER_MAIN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][] {
            // main info
            {"", CRM_INSURER_PARAMNAME}, // для создания мапы CRM_INSURER_PARAMNAME (INSURERMAP)
            {UNION_INSURER_PARAMNAME + "/participantTypeSysName", CRM_INSURER_PARAMNAME + "/PARTICIPANTTYPE", "phys > 1; jur > 2", "1"},
            {UNION_INSURER_PARAMNAME + "/lastName", CRM_INSURER_PARAMNAME + "/LASTNAME"},
            {UNION_INSURER_PARAMNAME + "/firstName", CRM_INSURER_PARAMNAME + "/FIRSTNAME"},
            {UNION_INSURER_PARAMNAME + "/middleName", CRM_INSURER_PARAMNAME + "/MIDDLENAME"},
            {
                    UNION_INSURER_PARAMNAME + "/" + UNION_CITIZENSHIP_PARAMNAME,
                    CRM_INSURER_PARAMNAME + "/" + CRM_CITIZENSHIP_PARAMNAME,
                    CITIZENSHIP_RULE,
                    CRM_CITIZENSHIP_RUSSIAN_VALUE
            },
            {UNION_INSURER_PARAMNAME + "/birthDate", CRM_INSURER_PARAMNAME + "/BIRTHDATE"},
            {UNION_INSURER_PARAMNAME + "/sex", CRM_INSURER_PARAMNAME + "/GENDER", "male > 0; female > 1", "0"},
            {UNION_INSURER_PARAMNAME + "/isInformationSupportAgree", "INFORMSUPPORT", "true > 1; false > 0", "0"}
    });

    List<String[]> INSURER_PASSPORT_RF_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][] {
            // main document - PassportRF
            {"", CRM_INSURER_PARAMNAME + "/documentList"},
            {OPERATOR_ADD_TO_LIST, CRM_INSURER_PARAMNAME + "/documentList", "DOCTYPESYSNAME", "PassportRF"},
            {UNION_INSURER_PARAMNAME + "/documentSeries", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/DOCSERIES"},
            {UNION_INSURER_PARAMNAME + "/documentNumber", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/DOCNUMBER"},
            {UNION_INSURER_PARAMNAME + "/documentIssueDate", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDATE"},
            {UNION_INSURER_PARAMNAME + "/documentIssuedBy", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'PassportRF']/ISSUEDBY"}
    });

    List<String[]> INSURER_PASSPORT_FOREIGN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][] {
            // main document - ForeignPassport
            {"", CRM_INSURER_PARAMNAME + "/documentList"},
            {OPERATOR_ADD_TO_LIST, CRM_INSURER_PARAMNAME + "/documentList", "DOCTYPESYSNAME", "ForeignPassport"},
            {UNION_INSURER_PARAMNAME + "/documentSeries", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'ForeignPassport']/DOCSERIES"},
            {UNION_INSURER_PARAMNAME + "/documentNumber", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'ForeignPassport']/DOCNUMBER"},
            {UNION_INSURER_PARAMNAME + "/documentIssueDate", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'ForeignPassport']/ISSUEDATE"},
            {UNION_INSURER_PARAMNAME + "/documentIssuedBy", CRM_INSURER_PARAMNAME + "/documentList[@DOCTYPESYSNAME = 'ForeignPassport']/ISSUEDBY"}
    });

    List<String[]> INSURER_CONTACTS_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][] {
            // contacts
            {"", CRM_INSURER_PARAMNAME + "/contactList"},
            {OPERATOR_ADD_TO_LIST, CRM_INSURER_PARAMNAME + "/contactList", "CONTACTTYPESYSNAME", "MobilePhone"},
            {UNION_INSURER_PARAMNAME + "/mobilePhone", CRM_INSURER_PARAMNAME + "/contactList[@CONTACTTYPESYSNAME = 'MobilePhone']/VALUE"},
            {OPERATOR_ADD_TO_LIST, CRM_INSURER_PARAMNAME + "/contactList", "CONTACTTYPESYSNAME", "PersonalEmail"},
            {UNION_INSURER_PARAMNAME + "/eMail", CRM_INSURER_PARAMNAME + "/contactList[@CONTACTTYPESYSNAME = 'PersonalEmail']/VALUE"}
    });

    List<String[]> INSURER_REGISTER_ADDRESS_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][] {
            // register address
            {"",                                                               CRM_APPLICANT_ADDRESS_LIST_PARAMPATH},
            {OPERATOR_ADD_TO_LIST,                                             CRM_APPLICANT_ADDRESS_LIST_PARAMPATH, "ADDRESSTYPESYSNAME", "RegisterAddress"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/regionCode",          CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/REGIONKLADR"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/regionName",          CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/REGION"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/cityCode",            CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/CITYKLADR"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/cityName",            CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/CITY"},
            /*
            {UNION_INSURER_PARAMNAME + "/registerAddress/isEmptyStreet",       CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/"},
            */
            {UNION_INSURER_PARAMNAME + "/registerAddress/streetCode",          CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/STREETKLADR"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/streetName",          CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/STREET"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/house",               CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/HOUSE"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/flat",                CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/FLAT"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/postalCode",          CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/POSTALCODE"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/addressText",         CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/ADDRESSTEXT1"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/addressText",         CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/ADDRESSTEXT2"},
            {UNION_INSURER_PARAMNAME + "/registerAddress/addressTextTranslit", CRM_APPLICANT_ADDRESS_LIST_PARAMPATH + "[@ADDRESSTYPESYSNAME = 'RegisterAddress']/ADDRESSTEXT3"}
    });

    List<String[]> FULL_INSURER_COMMON_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(
            INSURER_MAIN_KEY_RELATIONS_LIST,
            INSURER_CONTACTS_KEY_RELATIONS_LIST,
            INSURER_REGISTER_ADDRESS_KEY_RELATIONS_LIST
    );

    List<String[]> INSURER_RUSSIAN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(
            INSURER_PASSPORT_RF_KEY_RELATIONS_LIST
    );

    List<String[]> INSURER_FOREIGN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(
            INSURER_PASSPORT_FOREIGN_KEY_RELATIONS_LIST
    );

    List<String[]> FULL_INSURER_RUSSIAN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(
            FULL_INSURER_COMMON_KEY_RELATIONS_LIST,
            INSURER_RUSSIAN_KEY_RELATIONS_LIST
    );

    List<String[]> FULL_INSURER_FOREIGN_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(
            FULL_INSURER_COMMON_KEY_RELATIONS_LIST,
            INSURER_FOREIGN_KEY_RELATIONS_LIST
    );

    Map<String, Class> FULL_INSURER_KEY_CLASSES = BaseKeyRelations.createClassMap(
            new Object[] {UNION_INSURER_PARAMNAME, HashMap.class},
            new Object[] {CRM_INSURER_PARAMNAME, HashMap.class},
            new Object[] {CRM_APPLICANT_PARAMNAME, HashMap.class},
            new Object[] {"registerAddress", HashMap.class},
            new Object[] {"contactList", ArrayList.class},
            new Object[] {CRM_ADDRESS_LIST_PARAMNAME, ArrayList.class},
            new Object[] {"documentList", ArrayList.class}
    );

}
