package com.bivgroup.config;

import com.bivgroup.config.common.ConfigException;
import com.bivgroup.config.common.Log4jConfigurer;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);
    public static final String KERBEROS_CONFIG = "kerberosConfig";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String PARAMETER_USER_NAME = "userName";
    public static final String PARAMETER_MAX_CON = "maxCon";
    public static final String PARAMETER_MIN_CON = "minCon";
    public static final String PARAMETER_DATA_SOURCE_URL = "dataSourceUrl";
    public static final String PARAMETER_JDBC_DRIVER = "jdbcDriver";
    public static final String[] DATABASE_CONNECTION_PARAMETERS = new String[]{
            PARAMETER_JDBC_DRIVER, PARAMETER_DATA_SOURCE_URL, PARAMETER_MIN_CON, PARAMETER_MAX_CON, PARAMETER_USER_NAME
    };
    private final String serviceName;
    private Map<String, Object> params = new HashMap<>();
    private static final transient Map<String, Config> inst = new HashMap<>();
    private static final transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private SimpleDateFormat dateFormat;
    public static final Map<String, Object> dataSources = new ConcurrentHashMap<>();

    static {
        logger.debug("Config static init...");
    }

    public SimpleDateFormat getDateFormat() {
        if (this.dateFormat == null) {
            this.dateFormat = new SimpleDateFormat(this.getParam("dateFormat", "dd.MM.yyyy"));
        }

        return this.dateFormat;
    }

    public static Config getConfig(String serviceName) {
        logger.debug("getConfig...");
        lock.readLock().lock();

        Config var2;
        try {
            Config conf = inst.get(serviceName);
            if (conf != null) {
                var2 = conf;
                logger.debug("getConfig finished.");
                return var2;
            }

            lock.readLock().unlock();
            lock.writeLock().lock();

            try {
                conf = inst.get(serviceName);
                if (conf == null) {
                    Log4jConfigurer.initLogging();
                    conf = new Config(serviceName);
                    inst.put(serviceName, conf);
                    var2 = conf;
                    logger.debug("getConfig finished.");
                    return var2;
                }

                var2 = conf;
            } catch (Exception ex) {
                logger.error("getConfig exception:", ex);
                throw ex;
            } finally {
                lock.readLock().lock();
                lock.writeLock().unlock();
            }
        } catch (Exception ex) {
            logger.error("getConfig exception:", ex);
            throw ex;
        } finally {
            lock.readLock().unlock();
        }
        logger.debug("getConfig finished.");
        return var2;
    }

    public static Config getConfig() {
        return getConfig("common");
    }

    private Config(String serviceName) {
        this.serviceName = serviceName;
        this.commonInit();
        this.init();
        logger.info("reading config" + serviceName + " = " + this.params);
    }

    private void init() {
        logger.debug("Config init...");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File("config" + this.serviceName + "-dir.txt"));
            fileOutputStream.write(("this is config directory for Diasoft platform for " + this.serviceName).getBytes());
            fileOutputStream.close();
        } catch (Exception ex) {
            logger.warn("Config init exception:", ex);
        }

        fileOutputStream = null;
        File configFile = new File(this.serviceName + "-config.xml");
        if (!configFile.exists()) {
            logger.warn("Configuration file " + configFile.getAbsolutePath() + " not found!");
        } else {
            try {
                Document document = (new SAXBuilder()).build(configFile);
                Element root = document.getRootElement();

                for (Object element : root.getChildren()) {
                    Element el = (Element) element;
                    parseElement(this.params, el);
                }
            } catch (Exception var8) {
                logger.error("Faild to parse Configuration file " + configFile.getAbsolutePath(), var8);
            }

        }
        logger.debug("Config init finished.");
    }

    public static void parseElement(Map<String, Object> param, Element root) {
        if (root.getChildren().size() > 0) {
            Map<String, Object> p = new HashMap(root.getChildren().size());

            for (Object element : root.getChildren()) {
                Element el = (Element) element;
                parseElement(p, el);
            }

            param.put(root.getName(), p);
        } else {
            param.put(root.getName(), root.getValue().trim());
        }

    }

    private void commonInit() {
        Document document = null;
        File configFile = new File("common-config.xml");

        try {
            document = (new SAXBuilder()).build(configFile);
            Element root = document.getRootElement();

            for (Object element : root.getChildren()) {
                Element el = (Element) element;
                parseElement(this.params, el);
            }

            if (this.params.containsKey(KERBEROS_CONFIG)) {
                try {
                    if (logger.isDebugEnabled()) {
                        System.setProperty("sun.security.krb5.debug", "true");
                    }

                    Map<String, String> props = (Map<String, String>) this.params.get(KERBEROS_CONFIG);
                    System.setProperty("java.security.krb5.realm", props.get("realm"));
                    System.setProperty("java.security.krb5.kdc", props.get("kdc"));
                    System.setProperty("java.security.krb5.serviceprincipal", props.get("serviceprincipal"));
                    if (props.get("serviceoid") != null) {
                        System.setProperty("java.security.krb5.serviceoid", props.get("serviceoid"));
                    }

                    System.setProperty("java.security.auth.login.config", "./jaas.conf");
                    System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
                } catch (SecurityException var7) {
                    logger.fatal("setProperty doesn't allowed her, you should use -D for service config");
                    throw new RuntimeException(var7);
                }
            }
        } catch (FileNotFoundException var8) {
            logger.error("Common configuration file " + configFile.getAbsolutePath() + " not found!", var8);
        } catch (JDOMException | IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }

    }

    public String getParam(String key, String defaultValue) {
        Object object = this.params.get(key);
        return object == null ? defaultValue : (String) object;
    }

    public Map<String, Object> getAllParams() {
        return this.params;
    }

    /*public DataSource getDataSource() throws ConfigException {
        try {
            return this.getDataSource(this.params);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new ConfigException(ex);
        }
    }*/

    /*public DataSource getDataSource(String alias) throws ConfigException {
        try {
            if (!(this.params.get(alias) instanceof Map)) {
                throw new ConfigException(alias + " is not a Map");
            } else {
                Map<String, Object> param = (Map<String, Object>) this.params.get(alias);
                return this.getDataSource(param);
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new ConfigException(ex);
        }
    }

    public DataSource getDataSource(Map<String, Object> params) throws ConfigException {
        this.checkRequiredConfigParam(params, PARAMETER_JDBC_DRIVER);
        this.checkRequiredConfigParam(params, PARAMETER_DATA_SOURCE_URL);
        this.checkRequiredConfigParam(params, PARAMETER_MIN_CON);
        this.checkRequiredConfigParam(params, PARAMETER_MAX_CON);
        this.checkRequiredConfigParam(params, PARAMETER_USER_NAME);
        this.checkRequiredConfigParam(params, PARAMETER_PASSWORD);

        try {
            DataSource connectionPool = DataSourceFactory.createDataSource(
                    (String) params.get(PARAMETER_JDBC_DRIVER),
                    (String) params.get(PARAMETER_DATA_SOURCE_URL),
                    Integer.parseInt((String) params.get(PARAMETER_MIN_CON)),
                    Integer.parseInt((String) params.get(PARAMETER_MAX_CON)),
                    (String) params.get(PARAMETER_USER_NAME),
                    (String) params.get(PARAMETER_PASSWORD)
            );
            logDataSourceInfo(connectionPool);
            return connectionPool;
        } catch (Exception ex) {
            throw new ConfigException("Faild to create DataSource for " + this.serviceName, ex);
        }
    }*/

    private void checkRequiredConfigParam(Map<String, Object> confParams, String paramKey) throws ConfigException {
        if (confParams.get(paramKey) == null) {
            throw new ConfigException("No needed parameter: " + paramKey + " in config for " + this.serviceName + " \n config params are: " + confParams);
        }
    }

    /*private static void logDataSourceInfo(DataSource connectionPool) {
        StringBuilder logMasg = new StringBuilder();
        logMasg.append("\t *** Data source \t").append(connectionPool);
        Connection con = null;

        try {
            con = connectionPool.getConnection();
            DatabaseMetaData meta = con.getMetaData();
            logMasg.append("\n\t *** URL: \t\t").append(meta.getURL());
            logMasg.append("\n\t *** User Name: \t").append(meta.getUserName());
            logMasg.append("\n\t *** Catalog: \t\t").append(con.getCatalog());
            logMasg.append("\n\t *** DBMS name: \t").append(meta.getDatabaseProductName());
            logMasg.append("\n\t *** DBMS version: \t").append(meta.getDatabaseProductVersion());
            logMasg.append("\n\t *** JDBC name: \t").append(meta.getDriverName());
            logMasg.append("\n\t *** JDBC version: \t").append(meta.getDriverVersion());
            logMasg.append("\n\t *** Transaction Isolation: \t").append(con.getTransactionIsolation());
        } catch (SQLException ex) {
            logger.error("Faild to collect DataSourceInfo", ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    logger.error(ex.getLocalizedMessage(), ex);
                }
            }
        }

        System.out.println(logMasg.toString());
        if (logger.isInfoEnabled()) {
            logger.info(logMasg.toString());
        }

    }*/

    public void checkRequiredParams(String[] requiredParams) throws ConfigException {
        if (requiredParams != null) {
            for (String paramName : requiredParams) {
                Object paramValue = this.params.get(paramName);
                if (paramValue == null || "".equals(paramValue.toString().trim())) {
                    throw new ConfigException("Parameter " + paramName + " is is not specified neither in the " + this.serviceName + "-config.xml nor common-config.xml");
                }
            }

        }
    }

    public static Map<String, Object> getVersion(URL holder) {
        HashMap<String, Object> result = new HashMap<>();
        String appServerHome = holder.getFile();
        logger.debug("Server home folder: " + appServerHome);
        File manifestFile = new File(appServerHome.substring(0, appServerHome.indexOf("WEB-INF")) + "META-INF/MANIFEST.MF");
        FileInputStream stream = null;

        try {
            Manifest mf = new Manifest();
            stream = new FileInputStream(manifestFile);
            mf.read(stream);
            Attributes atts = mf.getMainAttributes();

            for (Object key : atts.keySet()) {
                result.put(key.toString(), atts.getValue(key.toString()));
            }
        } catch (Exception var17) {
            logger.error(var17.getLocalizedMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception var16) {
                    logger.error(var16.getLocalizedMessage());
                }
            }

        }

        return result;
    }

    /*public static DataSource getNamedDataSource(String name) throws ConfigException {
        DataSource ds = (DataSource) dataSources.get(name);
        if (ds == null) {
            Map var2 = dataSources;
            synchronized (dataSources) {
                ds = (DataSource) dataSources.get(name);
                if (ds == null) {
                    ds = getConfig(name).getDataSource();
                    dataSources.put(name, ds);
                }
            }
        }

        return ds;
    }*/
}

