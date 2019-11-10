package com.bivgroup.querybuilder;

import com.bivgroup.querybuilder.common.idcache.IdCacheManager;
import com.bivgroup.querybuilder.common.idcache.IdObtainedException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ResultIterator;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.query.NamedQuery;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class QueryBuilder {
    public static final String TOTALCOUNT = "TOTALCOUNT";
    public static final String ORDERBY = "ORDERBY";
    public static final String PAGE = "PAGE";
    public static final String ROWSCOUNT = "ROWSCOUNT";
    private static final Logger logger = Logger.getLogger(QueryBuilder.class);

    public QueryBuilder() {
    }

    public static Map<String, Object> getSingleEntity(DataContext context, String namedQuery, Map<String, Object> param) throws Exception {
        List<Map<String, Object>> result = (List<Map<String, Object>>) context.performQuery(namedQuery, param, true);
        return result != null && result.size() > 0 ? result.get(0) : null;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Map<String, Object> getList(DataContext context, String countQuery, String selectQuery, HashMap<String, Object> param) throws Exception {
        return getList(context, countQuery, selectQuery, (Map) param);
    }

    public static Map<String, Object> getList(DataContext context, String countQuery, String selectQuery, Map<String, Object> param) throws Exception {
        try {
            Object count = param.get(ROWSCOUNT);
            Object page = param.containsKey(PAGE) ? param.get(PAGE) : new Integer(0);
            Object orderBy = param.containsKey(ORDERBY) ? param.get(ORDERBY) : "1";
            param.put(ORDERBY, orderBy);
            NamedQuery query = new NamedQuery(selectQuery, param);
            Integer countResult = null;
            List<Map<String, Object>> resultSQL = null;
            int pageRowsCount = count == null ? 0 : new Integer(count.toString());
            if (pageRowsCount == 0) {
                resultSQL = context.performQuery(query);
                countResult = new Integer(((List) resultSQL).size());
            } else {
                int startPosition = pageRowsCount * new Integer(page.toString());
                NamedQuery queryCount = new NamedQuery(countQuery, param);
                List<Map<String, Object>> countResultSQL = context.performQuery(queryCount);
                if (countResultSQL.size() != 0) {
                    ResultIterator iterator = null;

                    try {
                        iterator = context.performIteratedQuery(query);
                        Object num = ((Map) countResultSQL.get(0)).values().iterator().next();
                        countResult = new Integer(num.toString());
                        if (startPosition >= countResult) {
                            startPosition = 0;
                        }

                        resultSQL = new ArrayList<>(pageRowsCount);

                        int i;
                        for (i = 0; i < startPosition && iterator.hasNextRow(); ++i) {
                            iterator.skipRow();
                        }

                        for (i = 0; i < pageRowsCount && iterator.hasNextRow(); ++i) {
                            resultSQL.add((Map<String, Object>) iterator.nextRow());
                        }
                    } finally {
                        try {
                            if (iterator != null) {
                                iterator.close();
                            }
                        } catch (Exception var23) {
                            logger.error(var23);
                        }

                    }
                } else {
                    countResult = new Integer(0);
                }
            }

            Map<String, Object> result = new HashMap(2);
            result.put(TOTALCOUNT, countResult);
            result.put("Result", resultSQL);
            result.put("Status", "OK");
            return result;
        } catch (Exception var25) {
            logger.error(var25);
            throw new Exception(var25);
        }
    }

    public static SQLTemplate preParseSQL(DataContext dataContext, String queryName, Map<String, Object> params, String driverName) throws Exception {
        NamedQuery query = null;
        SQLTemplate sqlTemplate = null;
        DataMap dm = null;

        for (Iterator dmIterator = dataContext.getEntityResolver().getDataMaps().iterator(); dmIterator.hasNext() && sqlTemplate == null; sqlTemplate = (SQLTemplate) dm.getQuery(queryName)) {
            dm = (DataMap) dmIterator.next();
        }

        if (sqlTemplate == null) {
            return null;
        } else {
            String sql = sqlTemplate.getTemplate(driverName);
            sql = parseGetId(dataContext, sql);
            sqlTemplate.setTemplate(driverName, sql);
            return sqlTemplate.queryWithParameters(params);
        }
    }

    private static String parseGetId(DataContext dataContext, String sql) throws Exception {
        String getIdString = "getId(";
        StringBuilder newSql = new StringBuilder(sql.length());

        while (sql.contains("getId(")) {
            int begin = sql.indexOf("getId(") + "getId(".length();
            int end = sql.indexOf(")", begin);
            sql.substring(begin, end - 1);
        }

        return newSql.toString();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int getNewId(String corewsURL, String tableName, int batchSize) throws UnsupportedEncodingException, Exception {
        return getNewIdLong(tableName, batchSize).intValue();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int getNewId(String login, String password, String corewsURL, String tableName, int batchSize) throws UnsupportedEncodingException, Exception {
        return getNewIdLong(tableName, batchSize).intValue();
    }

    public static Long getNewIdLong(String tableName, Integer batchSize) throws IdObtainedException {
        return IdCacheManager.getNextId(tableName, batchSize);
    }
}
