package com.bivgroup.core.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.config.exception.ConfigException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AuditImpl implements Audit {
    private Logger logger = LogManager.getLogger(AuditImpl.class.getName());
    private static final Marker AUDIT_MARKER = MarkerManager.getMarker("Audit");
    private static final Marker WORK_ERROR_MARKER = MarkerManager.getMarker("work_error");
    private static final String LOGIN_PARAMETER_NAME = "login";
    private static final String SYSTEM_PARAMETER_NAME = "system";
    private static final String OPERATION_PARAMETER_NAME = "operation";
    private static final String USER_ACCOUNT_ID_PARAMETER_NAME = "userAccountId";
    private static final String CUSTOM_MESSAGE_ACCOUNT_ID_PARAMETER_NAME = "customMessage";
    private static final String INPUT_DOCUMENT_ID_PARAMETER_NAME = "inputDocumentId";
    private static final String OUTPUT_DOCUMENT_ID_PARAMETER_NAME = "outputDocumentId";
    private static final String IP_INFO_PARAMETER_NAME = "ipInfo";
    private static final String RESULT_STATUS_PARAMETER_NAME = "resultStatus";
    private static final String MARKER_PARAMETER_NAME = "marker";

    @Override
    public void audit(String operation, ResultOperation resultStatus, String login, Long userAccountId, AuditIpInfo ipInfo, String message) {
        Map<String, Object> messageMap = new HashMap<>();
        if (ipInfo != null) {
            messageMap.put(IP_INFO_PARAMETER_NAME, ipInfo);
        }
        messageMap.put(MARKER_PARAMETER_NAME, AUDIT_MARKER.getName());
        messageMap.put(SYSTEM_PARAMETER_NAME, getSystemName());
        messageMap.put(LOGIN_PARAMETER_NAME, login);
        messageMap.put(OPERATION_PARAMETER_NAME, operation);
        messageMap.put(RESULT_STATUS_PARAMETER_NAME, resultStatus.getResultName());
        messageMap.put(USER_ACCOUNT_ID_PARAMETER_NAME, userAccountId);
        messageMap.put(CUSTOM_MESSAGE_ACCOUNT_ID_PARAMETER_NAME, message);
        logger.info(AUDIT_MARKER, convertObjectToJson(messageMap));
    }

    @Override
    public <P> void audit(String operation, ResultOperation resultStatus, String login, Long userAccountId, AuditIpInfo ipInfo, String message, P params, List<Obfuscator<P>> obfuscators) {
        Map<String, Object> messageMap = new HashMap<>();
        if (ipInfo != null) {
            messageMap.put(IP_INFO_PARAMETER_NAME, ipInfo);
        }
        messageMap.put(MARKER_PARAMETER_NAME, AUDIT_MARKER.getName());
        messageMap.put(LOGIN_PARAMETER_NAME, login);
        messageMap.put(SYSTEM_PARAMETER_NAME, getSystemName());
        messageMap.put(OPERATION_PARAMETER_NAME, operation);
        messageMap.put(RESULT_STATUS_PARAMETER_NAME, resultStatus.getResultName());
        messageMap.put(USER_ACCOUNT_ID_PARAMETER_NAME, userAccountId);
        messageMap.put(CUSTOM_MESSAGE_ACCOUNT_ID_PARAMETER_NAME, message);
        messageMap.put("parameters", copyAndObfuscateObject(params, obfuscators));
        logger.info(AUDIT_MARKER, convertObjectToJson(messageMap));
    }

    @Override
    public <P, D> void audit(String operation, ResultOperation resultStatus, String login, Long userAccountId,
                             AuditIpInfo ipInfo, String message, Long inputDocumentId, Long outputDocumentId, P parameters, D document,
                             List<Obfuscator<P>> parametersObfuscators, List<Obfuscator<D>> documentObfuscators) {
        Map<String, Object> messageMap = new HashMap<>();
        if (ipInfo != null) {
            messageMap.put(IP_INFO_PARAMETER_NAME, ipInfo);
        }
        messageMap.put(MARKER_PARAMETER_NAME, AUDIT_MARKER.getName());
        messageMap.put(LOGIN_PARAMETER_NAME, login);
        messageMap.put(SYSTEM_PARAMETER_NAME, getSystemName());
        messageMap.put(OPERATION_PARAMETER_NAME, operation);
        messageMap.put(USER_ACCOUNT_ID_PARAMETER_NAME, userAccountId);
        messageMap.put(RESULT_STATUS_PARAMETER_NAME, resultStatus.getResultName());
        messageMap.put(CUSTOM_MESSAGE_ACCOUNT_ID_PARAMETER_NAME, message);
        messageMap.put(INPUT_DOCUMENT_ID_PARAMETER_NAME, inputDocumentId);
        messageMap.put(OUTPUT_DOCUMENT_ID_PARAMETER_NAME, outputDocumentId);
        if (parameters != null) {
            messageMap.put("parameters", copyAndObfuscateObject(parameters, parametersObfuscators));
        }
        if (document != null) {
            messageMap.put("document", copyAndObfuscateObject(document, documentObfuscators));
        }
        logger.info(AUDIT_MARKER, convertObjectToJson(messageMap));
    }

    @Override
    public <P, D> void audit(AuditParameters auditParameters, P parameters, D document, List<Obfuscator<P>> parametersObfuscators,
                             List<Obfuscator<D>> documentObfuscators) {
        this.audit(auditParameters.getOperation(), auditParameters.getResultStatus(), auditParameters.getLogin(), auditParameters.getUserAccountId(),
                auditParameters.getIpInfo(), auditParameters.getMessage(), auditParameters.getInputDocumentId(), auditParameters.getOutputDocumentId(), parameters, document,
                parametersObfuscators, documentObfuscators);
    }

    @Override
    public <P, D> void audit(AuditParameters auditParameters, P parameters, D document, Class<Obfuscator>[] parametersClassObfuscators,
                             Class<Obfuscator>[] documentClassObfuscators) {
        this.audit(auditParameters, parameters, document, createListObfuscator(parametersClassObfuscators), createListObfuscator(documentClassObfuscators));
    }

    private <T> List<Obfuscator<T>> createListObfuscator(Class<Obfuscator>[] obfuscators) {
        List<Obfuscator<T>> result = new ArrayList<>();
        if (obfuscators != null) {
            for (Class<? extends Obfuscator> clazz : obfuscators) {
                try {
                    Constructor<? extends Obfuscator> ctor = clazz.getConstructor();
                    Obfuscator obfuscator = ctor.newInstance();
                    result.add(obfuscator);
                } catch (NoSuchMethodException e) {
                    logger.error(WORK_ERROR_MARKER, "При получении конструктора класса {} произошла ошибка: {}", clazz, e.fillInStackTrace());
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    logger.error(WORK_ERROR_MARKER, "При создании объекта класса {} произошла ошибка: {}", clazz, e.fillInStackTrace());
                }
            }
        }
        return result;
    }

    /**
     * Метод преобразования объекта в json строку
     *
     * @param item объект, который требуется преобразовать
     * @param <T>  тип объекта
     * @return объект в json строке
     */
    private <T> String convertObjectToJson(T item) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonDocument = null;
        try {
            if (item != null) {
                jsonDocument = mapper.writeValueAsString(item);
            }
        } catch (JsonProcessingException e) {
            jsonDocument = "{'Error': 'невозможно пробрзаровать объект в json строку'}";
            logger.error(WORK_ERROR_MARKER, "При конвертации объекта {} в строку в формате json произошла ошибка: {}", item, e.fillInStackTrace());
        }
        return jsonDocument;
    }

    /**
     * Метод копирования объекта и применение обфускации над объектом
     *
     * @param item        объект, над которым надо провести обфускацию
     * @param obfuscators список обфускаторов, которые нужно применить над объектом
     * @param <T>         тип объекта
     * @return строка в json формате
     */
    private <T> T copyAndObfuscateObject(T item, List<Obfuscator<T>> obfuscators) {
        T copyItem = copyObject(item);
        obfuscateObject(copyItem, obfuscators);
        return copyItem;
    }

    /**
     * Метод обфускации объекта
     *
     * @param item        объект, над которым требуется провести обфускацию
     * @param obfuscators список обфускаторов, которые требуется применить над объектом
     * @param <T>         тип объекта
     */
    private <T> void obfuscateObject(T item, List<Obfuscator<T>> obfuscators) {
        if (item != null && obfuscators != null) {
            for (Obfuscator<T> obfuscator : obfuscators) {
                if (obfuscator != null) {
                    item = obfuscator.obfuscate(item);
                }
            }
        }
    }

    /**
     * Метод копирование объекта
     *
     * @param item объект, которые требуется копировать
     * @param <T>  тип объекта, который требуется копировать
     * @return ссылка на скопированный объект
     */
    @SuppressWarnings("unchecked")
    private <T> T copyObject(T item) {
        T copyItem = null;
        Class<?> itemClazz = item.getClass();
        Class<?> constructorClazz = itemClazz;
        if (item instanceof Map) {
            constructorClazz = Map.class;
        }
        if (item instanceof Collection) {
            constructorClazz = Collection.class;
        }
        try {
            Constructor<?> copyConstructor = itemClazz.getConstructor(constructorClazz);
            copyItem = (T) copyConstructor.newInstance(item);
        } catch (NoSuchMethodException e) {
            logger.error(WORK_ERROR_MARKER, "При получении конструктора класса {} произошла ошибка: {}", itemClazz, e.fillInStackTrace());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error(WORK_ERROR_MARKER, "При копировании объекта класса {} произошла ошибка: {}", itemClazz, e.fillInStackTrace());
        }
        return copyItem;
    }

    private String systemName = "none";
    public static final String COREWS = "corews";
    //  private DataContext context = null;
    private static final String coreSettingSystemNameGetter = "select SETTINGVALUE from CORE_SETTING where SETTINGSYSNAME = 'SystemSysName' and ROWNUM = 1";

    private String getSystemName() {
        if ("none".equalsIgnoreCase(systemName)) {
            DataSource dataSource = null;
            Connection conn = null;
            Statement statement = null;
            ResultSet rs = null;
            try {
                dataSource = Config.getConfig(COREWS).getDataSource();
                conn = dataSource.getConnection();
                statement = conn.createStatement();
                statement.execute(coreSettingSystemNameGetter);
                rs = statement.getResultSet();
                rs.next();
                systemName = rs.getString(1);
                return systemName;
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("fail getting system name from CORE_SETTING", e);
            } catch (ConfigException e) {
                e.printStackTrace();
                logger.error("fail getting datasourceName from common-config.xml", e);
            } finally {
                try {
                    rs.close();
                } catch (Exception e) { /* ignored */ }
                try {
                    statement.close();
                } catch (Exception e) { /* ignored */ }
                try {
                    conn.close();
                } catch (Exception e) { /* ignored */ }
                dataSource = null;
            }
        }
        return systemName;
    }

    DataSource getJNDIDataSource(String driverName) throws Exception {
        String DATASOURCE_CONTEXT = driverName;
        Connection result = null;
        try {
            Context initialContext = new InitialContext();
            //cast is necessary
            DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
            if (datasource != null) {
                return datasource;
            }
        } catch (Exception e) {
            throw new Exception("Faild to create DataSource for getting system name for audit", e);
        }
        return null;
    }
}
