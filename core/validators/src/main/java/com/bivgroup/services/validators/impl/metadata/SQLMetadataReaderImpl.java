/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.MetadataReader;

/**
 * Реализация доступа содержит запросы,
 * так как ставилась цель сделать возможным использование библиотеки
 * в любом сервисе без необходимости подтаскивания дополнительных файлов.
 * @author reson
 */
public class SQLMetadataReaderImpl implements MetadataReader{
    private static final Logger logger = Logger.getLogger(SQLMetadataReaderImpl.class.getName());
    private static final String checkQueryText = "SELECT \n" +
"        #result('T.CONDITION', 'java.lang.String', 'CONDITION'),\n" +            
"        #result('T.DATAPROVID', 'java.lang.Long', 'DATAPROVID'),\n" +
"        #result('T.DISCRIMINATOR', 'java.lang.Long', 'DISCRIMINATOR'),\n" +
"        #result('T.ERRORMESSAGE', 'java.lang.String', 'ERRORMESSAGE'),\n" +
"        #result('T.CHECKID', 'java.lang.Long', 'CHECKID'),\n" +
"        #result('T.NAME', 'java.lang.String', 'NAME') \n" +
"FROM \n" +
"      INS_CHECK T \n" +
"#chain('AND' 'WHERE') \n" +
"       #chunk($CHECKID) T.CHECKID = #bind($CHECKID 'NUMERIC') #end \n" +
"       #chunk($CONDITION) T.CONDITION = #bind($CONDITION 'VARCHAR') #end \n" +
"       #chunk($DATAPROVID) T.DATAPROVID = #bind($DATAPROVID 'NUMERIC') #end \n" +
"       #chunk($DISCRIMINATOR) T.DISCRIMINATOR = #bind($DISCRIMINATOR 'NUMERIC') #end \n" +
"       #chunk($ERRORMESSAGE) T.ERRORMESSAGE = #bind($ERRORMESSAGE 'VARCHAR') #end \n" +
"       #chunk($NAME) T.NAME = #bind($NAME 'VARCHAR') #end \n" +
"       AND ((T.ISENABLED is not null) and (T.ISENABLED <> 0))\n" +            
"#end";
    
    private static final String childCheckQueryText = "SELECT \n" +
"        #result('T.CONDITION', 'java.lang.String', 'CONDITION'),\n" +            
"        #result('T.DATAPROVID', 'java.lang.Long', 'DATAPROVID'),\n" +
"        #result('T.DISCRIMINATOR', 'java.lang.Long', 'DISCRIMINATOR'),\n" +
"        #result('CL.CHECKLINKID', 'java.lang.Long', 'CHECKLINKID'),\n" +            
"        #result('T.ERRORMESSAGE', 'java.lang.String', 'ERRORMESSAGE'),\n" +
"        #result('T.CHECKID', 'java.lang.Long', 'CHECKID'),\n" +
"        #result('CL.CHECKLEVEL', 'java.lang.Long', 'CHECKLEVEL'),\n" +
"        #result('T.NAME', 'java.lang.String', 'NAME') \n" +
"FROM \n" +
"   INS_CHECKLINK CL" +     
"   inner join  INS_CHECK T on (T.CHECKID = CL.CHILDID)\n" +
"       "+            
"#chain('AND' 'WHERE') \n" +

"       #chunk($CHECKID) CL.PARENTID = #bind($CHECKID 'NUMERIC') #end \n" +
" and   ((T.ISENABLED is not null) and (T.ISENABLED <> 0))\n" +
" and   ((CL.ISENABLED is not null) and (CL.ISENABLED <> 0))\n" +            
"#end\n" +
" ORDER BY CL.CHECKLINKID";
    
    private static final String childMappingsQueryText = "SELECT \n" +
"        #result('T.CHECKLINKID', 'java.lang.Long', 'CHECKLINKID'),\n" +
"        #result('T.CHILDPARAM', 'java.lang.String', 'CHILDPARAM'),\n" +
"        #result('T.CHECKLINKMAPID', 'java.lang.Long', 'CHECKLINKMAPID'),\n" +
"        #result('T.PARENTPARAM', 'java.lang.String', 'PARENTPARAM'), \n" +
"        #result('T.FORMULA', 'java.lang.String', 'FORMULA') \n" +            
"FROM \n" +
"      INS_CHECKLINK  CL    "+
"      inner join INS_CHECKLINKMAP T on (T.CHECKLINKID = CL.CHECKLINKID)\n" +
"#chain('AND' 'WHERE') \n" +
"       #chunk($CHILDID) CL.CHILDID = #bind($CHILDID 'NUMERIC') #end \n" + 
"       #chunk($PARENTID) CL.PARENTID = #bind($PARENTID 'NUMERIC') #end \n" +
"#end\n"+
" ORDER BY T.CHECKLINKID";
    
    private static final String dataProviderQueryText = "SELECT \n" +
"        #result('T.DISCRIMINATOR', 'java.lang.Long', 'DISCRIMINATOR'),\n" +
"        #result('T.DATAPROVID', 'java.lang.Long', 'DATAPROVID'),\n" +
"        #result('T.METHODNAME', 'java.lang.String', 'METHODNAME'),\n" +
"        #result('T.NAME', 'java.lang.String', 'NAME'),\n" +
"        #result('T.SERVICENAME', 'java.lang.String', 'SERVICENAME') \n" +
"FROM \n" +
"      INS_DATAPROV T \n" +
"#chain('AND' 'WHERE') \n" +
"       #chunk($DATAPROVID) T.DATAPROVID = #bind($DATAPROVID 'NUMERIC') #end \n" +
"       #chunk($DISCRIMINATOR) T.DISCRIMINATOR = #bind($DISCRIMINATOR 'NUMERIC') #end \n" +
"       #chunk($METHODNAME) T.METHODNAME = #bind($METHODNAME 'VARCHAR') #end \n" +
"       #chunk($NAME) T.NAME = #bind($NAME 'VARCHAR') #end \n" +
"       #chunk($SERVICENAME) T.SERVICENAME = #bind($SERVICENAME 'VARCHAR') #end \n" +
"#end";
    
    private static final String serviceDataProviderFieldsQueryText = "SELECT \n" +
"        #result('T.DATAPROVID', 'java.lang.Long', 'DATAPROVID'),\n" +
"        #result('T.FIELDTYPE', 'java.lang.String', 'FIELDTYPE'),\n" +
"        #result('T.DATAPROVFIELDID', 'java.lang.Long', 'DATAPROVFIELDID'),\n" +
"        #result('T.ISLIST', 'java.lang.Integer', 'ISLIST'),\n" +
"        #result('T.ISMAINFIELD', 'java.lang.Integer', 'ISMAINFIELD'),\n" +            
"        #result('T.KIND', 'java.lang.Long', 'KIND'),\n" +
"        #result('T.NAME', 'java.lang.String', 'NAME') \n" +
"FROM \n" +
"      INS_DATAPROVFIELD T \n" +
"#chain('AND' 'WHERE') \n" +
"       #chunk($DATAPROVFIELDID) T.DATAPROVFIELDID = #bind($DATAPROVFIELDID 'NUMERIC') #end \n" +
"       #chunk($DATAPROVID) T.DATAPROVID = #bind($DATAPROVID 'NUMERIC') #end \n" +
"       #chunk($FIELDTYPE) T.FIELDTYPE = #bind($FIELDTYPE 'VARCHAR') #end \n" +
"       #chunk($ISLIST) T.ISLIST = #bind($ISLIST 'INTEGER') #end \n" +
"       #chunk($ISMAINFIELD) T.ISMAINFIELD = #bind($ISMAINFIELD 'INTEGER') #end \n" +            
"       #chunk($KIND) T.KIND = #bind($KIND 'NUMERIC') #end \n" +
"       #chunk($NAME) T.NAME = #bind($NAME 'VARCHAR') #end \n" +
"#end ";
    
    private DataContext context = null;

    public SQLMetadataReaderImpl(DataContext value) {
        this.context = value;
    }
    
    
    
    
    private List<Map<String,Object>> selectQuery(String queryText,Map<String,Object> params){
        logger.debug("try to get datamap from EntityResolver");
        DataMap dataMap = (DataMap) context.getEntityResolver().getDataMaps().iterator().next();
        logger.debug(dataMap.getName());
        
        SQLTemplate query = new SQLTemplate(dataMap, queryText);
        query.setCachePolicy(SQLTemplate.NO_CACHE);
        query.setFetchingDataRows(true);
        query.setParameters(params);
        return context.performQuery(query);        
    }
    
    
    private Map<String, Object> getCheckByParams(Map<String, Object> params){
        List<Map<String,Object>> resultList  = selectQuery(checkQueryText, params);
        Map<String,Object> result = null;
        if ((resultList != null) && (resultList.size()>0)){
            result = resultList.get(0);
        }
        return result;       
    }
    
    @Override
    public Map<String, Object> getCheckByName(String name) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("NAME", name);
        return getCheckByParams(params);
    }

    @Override
    public Map<String, Object> getCheckById(Long checkId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("CHECKID", checkId);
        return getCheckByParams(params);
    }

    @Override
    public List<Map<String, Object>> getChildChecks(Long complexCheckId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("CHECKID", complexCheckId);
        return selectQuery(childCheckQueryText, params);       
    }

    @Override
    public List<Map<String, Object>> getChildCheckMappings(Long complexCheckId, Long childCheckId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("PARENTID", complexCheckId);
        if (childCheckId != null) 
            params.put("CHILDID", complexCheckId);        
        return selectQuery(childMappingsQueryText, params);       
    }

    @Override
    public Map<String, Object> getDataProviderById(Long dataproviderId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("DATAPROVID", dataproviderId); 
        List<Map<String,Object>> resultList  = selectQuery(dataProviderQueryText, params);
        Map<String,Object> result = null;
        if ((resultList != null) && (resultList.size()>0)){
            result = resultList.get(0);
        }
        List<Map<String,Object>> fields = this.getListDataProviderFields(dataproviderId);
        result.put("FIELDS", fields);
        return result;        
    }

    @Override
    public List<Map<String, Object>> getListDataProviderFields(Long dataproviderId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("DATAPROVID", dataproviderId);
        return selectQuery(serviceDataProviderFieldsQueryText, params);        
    }
    
    
}
