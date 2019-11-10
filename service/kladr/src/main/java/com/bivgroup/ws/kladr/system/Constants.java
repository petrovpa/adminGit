/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.kladr.system;

/**
 *
 * @author ilich
 */
public class Constants {

    public static final String INSPRODUCTWS = "insproductws";
    public static final String VALIDATORSWS = "validatorsws";
    public static final String INSTARIFICATORWS = "instarificatorws";
    public static final String INSPOSWS = "insposws";
    public static final String BIVSBERPOSWS = "bivsberposws";
    public static final String B2BPOSWS = "b2bposws";
    public static final String BIVPOSWS = "bivposws";
    public static final String WEBSMSWS = "websmsws";
    public static final String CRMWS = "crmws";
    public static final String SMWS = "smws";
    public static final String REFWS = "refws";
    public static final String ADMINWS = "adminws";
    public static final String COREWS = "corews";
    public static final String SIGNWS = "signws";
    public static final String INSUNDERWRITINGWS = "insunderwritingws";
    public static final String LIBREOFFICEREPORTSWS = "libreofficereportsws";
    public static final String SIGNBIVSBERPOSWS = "signbivsberposws";
    public static final String SIGNB2BPOSWS = "signb2bposws";
    public static final String SIGNWEBSMSWS = "signwebsmsws";

    public static final String FILEB2BPOSWS = "fileb2bposws";
    
    public static final String SESSIONPARAM_USERACCOUNTID = "SESSION_USERACCOUNTID";
    public static final String SESSIONPARAM_USERTYPEID = "SESSION_USERTYPEID";
    public static final String SESSIONPARAM_DEPARTMENTID = "SESSION_DEPARTMENTID";
    
    // константы для КЛАДР
    public static final String ALTNAMES_FILE = "ALTNAMES.DBF";
    public static final String HOUSE_FILE = "DOMA.DBF";
    //public static final String FLAT_FILE = "FLAT.DBF";
    public static final String KLADR_FILE = "KLADR.DBF";
    public static final String SOCRBASE_FILE = "SOCRBASE.DBF";
    public static final String STREET_FILE = "STREET.DBF";
        
    public static final String[] REQUIRIED_DBF_FILES_NAMES_LIST = {
        ALTNAMES_FILE,
        HOUSE_FILE,
        //FLAT_FILE,
        KLADR_FILE,
        SOCRBASE_FILE,
        STREET_FILE
    };
    
    public static final String REQUIRIED_DBF_FILES_NAMES_LIST_STR;
    
    static {
        StringBuilder listSB = new StringBuilder();
        for (String requiriedDBFFileName : REQUIRIED_DBF_FILES_NAMES_LIST) {
            listSB.append("'").append(requiriedDBFFileName).append("', ");
        }
        listSB.setLength(listSB.length() - 2);
        REQUIRIED_DBF_FILES_NAMES_LIST_STR = listSB.toString();
    }
    
}