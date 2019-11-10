package com.bivgroup.flextera.insurance.bivfront.db;

import com.sybase.jdbc4.jdbc.SybConnection;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.LogLevel;
import liquibase.logging.ext.javautil.JavaUtilLogger;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * DatabaseManager instances allow to create, drop, update and validate platform database.
 * Usage scenarios:
 * <H4>Database validation</H4>
 * <pre><code>
 *  
 *   if (!DatabaseManager.getInstance(connection).validateDatabase()) {
 *       throw new DatabaseIsNotValidException();
 *   }
 * </code></pre> 
 * 
 * <H4>Database update to current version</H4>
 * <pre><code>
 *      DatabaseManager dm = DatabaseManager.getInstance(connection);
 *      dm.setProperties(properties);
 *      // dm.updateDatabase(sqlOutput);
 *      dm.updateDatabase();
 *      
 * </code></pre>
 * 
 * <H4>Create database from scratch</H4>
 * <pre><code>
 *      DatabaseManager dm = DatabaseManager.getInstance(connection); // connection should be powerful enough to drop or create databases
 *      dm.setProperties(properties);
 *      if (needToDrop) {
 *          dm.dropDatabase(databaseName);
 *      }
 *      if (dm.databaseExist(databaseName)) {
 *          // report error
 *      }
 *      dm.createDatabase(databaseName, username, password);
 *      dm.initDatabase(databaseName, username, password);
 *      
 * </code></pre> 
 * 
 * @author 
 *
 */
public abstract class DatabaseManager {

    private static final String COMMAND_VALIDATE = "validate";
    private static final String COMMAND_UPDATE = "update";
    private static final String COMMAND_INIT = "init";
    private static final String COMMAND_CREATE = "create";
    private static final String COMMAND_DROP = "drop";
    private static final String COMMAND_PLATFORM = "platform";
    private static final String COMMAND_STACKTRACE = "stacktrace";
    private static final String CONTEXT_CREATE = "create";
    private static final String CONTEXT_UPDATE = "update";
    private static final String CONTEXT_PLAFORM_CREATE = "platformCreate";
    private static final String CONTEXT_PLAFORM_UPDATE = "platformUpdate";
    public static final String MASTER_NAME = "dbo";
    private final Connection connection;
    private Properties properties;
    /**
     * using java.util.Logger instead of Log4j because liquibase is using it
     */
    public static final Logger log = Logger.getLogger(DatabaseManager.class.getName());
    public static final String VERSION = initVersion();
    public static final String MODULE = getModuleName();
    protected static String databaseOptions = "";

    protected DatabaseManager(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection is null");
        }
        this.connection = connection;
    }

    private static String initVersion() {
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("com/bivgroup/flextera/insurance/bivfront/db/version");
        } catch (MissingResourceException e) {
            rb = ResourceBundle.getBundle("com/bivgroup/flextera/insurance/bivfront/db/version");
        }
        return rb.getString("version");
    }

    private static String getModuleName() {
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("com/bivgroup/flextera/insurance/bivfront/db/version");
        } catch (MissingResourceException e) {
            rb = ResourceBundle.getBundle("com/bivgroup/flextera/insurance/bivfront/db/version");
        }
        return rb.getString("module");
    }

    protected Liquibase getLiquibase(Connection connection) throws LiquibaseException  {

        Liquibase liquibase = new Liquibase("rootChangeLog.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
        if (properties != null) {
            Enumeration e = properties.propertyNames();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                liquibase.setChangeLogParameter(name, properties.getProperty(name));
            }
        }
        return liquibase;
    }

    protected abstract String[] getCreateSQL(String databaseName, String username, String password);

    protected abstract String[] getDropSQL(String databaseName);

    /**
     * creates database  
     * @throws DatabaseManagerException
     */
    public void createDatabase(String databaseName, String username, String password) throws DatabaseManagerException {
        log.finest("creating database '" + databaseName + "'");
        String[] sql = getCreateSQL(databaseName, username, password);
        for (int i = 0; i < sql.length; i++) {
            Statement stmt;
            try {
                stmt = getConnection().createStatement();
            } catch (SQLException e) {
                throw new DatabaseManagerException("Can not create database '" + databaseName + "'. " + e.getMessage(), e);
            }
            try {
                stmt.executeUpdate(sql[i]);
            } catch (SQLException e) {
                if (!e.getClass().getSimpleName().contains("SybSQLWarning")) {
                    throw new DatabaseManagerException("Can not create database '" + databaseName + "'. " + e.getMessage(), e);
                }
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        log.info("created database '" + databaseName + "'");
    }

    /**
     * Generates database creation SQL script and writes it into supplied <i>output</i>
     * @param databaseName
     * @param username
     * @param password
     * @param output
     * @throws DatabaseManagerException
     */
    public void createDatabase(String databaseName, String username, String password, Writer output) throws DatabaseManagerException {
        log.finest("creating database '" + databaseName + "'");
        String[] sql = getCreateSQL(databaseName, username, password);
        try {
            for (int i = 0; i < sql.length; i++) {
                output.write(sql[i] + ";" + System.getProperty("line.separator"));
            }
            output.flush();
        } catch (IOException e) {
            throw new DatabaseManagerException("Can not generate create DDL script for database '" + databaseName + "'.", e);
        }
        log.info("created database '" + databaseName + "'");
    }

    /**
     * initializes database (e.g. creates tables, indexes, etc)
     * @param databaseName
     * @param username
     * @param password
     * @param platform
     * @throws DatabaseManagerException
     */
    public void initDatabase(String databaseName, String username, String password, boolean platform) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        Connection userConnection;
        try {
            userConnection = getConnection(databaseName, username, password);
        } catch (SQLException e) {
            throw new DatabaseManagerException("Unable to connect to database '" + databaseName + "': username = '" + username + "', password = '" + password + "'. " + e.getMessage(), e);
        }
        try {
            createTables(userConnection, platform);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not initialize database '" + databaseName + "'.", e);
        } finally {
            try {
                userConnection.close();
            } catch (SQLException e) {
            }
        }
        log.info("initialized database '" + databaseName + "'");
    }

    /**
     * generates database initialization SQL script and writes it to supplied <i>output</i>
     * @param databaseName
     * @param username
     * @param password
     * @param platform
     * @param output
     * @throws DatabaseManagerException
     */
    public void initDatabase(String databaseName, String username, String password, boolean platform, Writer output) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        Connection userConnection;
        try {
            userConnection = getConnection(databaseName, username, password);
        } catch (SQLException e) {
            throw new DatabaseManagerException("Unable to connect to database '" + databaseName + "': username = '" + username + "', password = '" + password + "'. " + e.getMessage(), e);
        }
        try {
            createTables(userConnection, platform, output);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for database '" + databaseName + "'. " + e.getMessage(), e);
        } finally {
            try {
                userConnection.close();
            } catch (SQLException e) {
            }
        }
        log.info("initialized database '" + databaseName + "'");
    }

    protected void createTables(Connection userConnection, boolean platform, Writer output) throws  LiquibaseException {
        if (!platform) {
            getLiquibase(userConnection).update(CONTEXT_CREATE, output);
            getLiquibase(getConnection()).update(CONTEXT_UPDATE, output);
        }
        getLiquibase(userConnection).update(CONTEXT_PLAFORM_CREATE, output);
        getLiquibase(getConnection()).update(CONTEXT_PLAFORM_UPDATE, output);
    }

    protected void createTables(Connection userConnection, boolean platform) throws  LiquibaseException {
        if (!platform) {
            getLiquibase(userConnection).update(CONTEXT_CREATE);
            getLiquibase(getConnection()).update(CONTEXT_UPDATE);
        }
        getLiquibase(userConnection).update(CONTEXT_PLAFORM_CREATE);
        getLiquibase(getConnection()).update(CONTEXT_PLAFORM_UPDATE);
    }

    /**
     * drops given database.
     * @throws DatabaseManagerException
     */
    public void dropDatabase(String databaseName) throws DatabaseManagerException {
        log.finest("dropping database '" + databaseName + "'");
        String[] sql = getDropSQL(databaseName);
        for (int i = 0; i < sql.length; i++) {
            Statement stmt;
            try {
                stmt = getConnection().createStatement();
            } catch (SQLException e) {
                throw new DatabaseManagerException("Can not drop database '" + databaseName + "'. " + e.getMessage(), e);
            }
            try {
                stmt.executeUpdate(sql[i]);
            } catch (SQLException e) {
                if (!e.getClass().getSimpleName().contains("SybSQLWarning")) {
                    throw new DatabaseManagerException("Can not drop database '" + databaseName + "'. " + e.getMessage(), e);
                }
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        log.info("dropped database '" + databaseName + "'");
    }

    /**
     * Generated SQL script for dropping database <i>databaseName</i> and writes it into supplied <i>output</i>
     * @param databaseName
     * @param output
     * @throws DatabaseManagerException
     */
    public void dropDatabase(String databaseName, Writer output) throws DatabaseManagerException {
        log.finest("dropping database '" + databaseName + "'");
        String[] sql = getDropSQL(databaseName);
        try {
            for (int i = 0; i < sql.length; i++) {
                output.write(sql[i] + ";" + System.getProperty("line.separator"));
            }
            output.flush();
        } catch (IOException e) {
            throw new DatabaseManagerException("Can not generate drop DDL script for database '" + databaseName + "'.", e);
        }
        log.info("dropped database '" + databaseName + "'");
    }

    /**
     * updates database to the current version.
     * @throws DatabaseManagerException
     */
    public void updateDatabase(String databaseName, boolean platform) throws DatabaseManagerException {
        log.finest("updating database to current version");
        changeDatabaseContext(databaseName);
        if (!platform) {
            try {
                getLiquibase(getConnection()).update(CONTEXT_UPDATE);
            } catch (LiquibaseException e) {
                throw new DatabaseManagerException("Can not update database for module '" + MODULE + "'. " + e.getMessage(), e);
            }
        }
        try {
            getLiquibase(getConnection()).update(CONTEXT_PLAFORM_UPDATE);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not update platform tables for module '" + MODULE + "'. " + e.getMessage(), e);
        }
        log.info("updated database to version " + VERSION);
    }

    /**
     * Generates SQL update script to make the database up to date.
     * The script will be written into supplied <i>output</i>
     * @param output
     * @throws DatabaseManagerException
     */
    public void updateDatabase(String databaseName, boolean platform, Writer output) throws DatabaseManagerException {
        log.finest("updating database to current version");
        changeDatabaseContext(databaseName, output);
        if (!platform) {
            try {
                getLiquibase(getConnection()).update(CONTEXT_UPDATE, output);
            } catch (LiquibaseException e) {
                throw new DatabaseManagerException("Can not generate update script for module '" + MODULE + "'.", e);
            }
        }
        try {
            getLiquibase(getConnection()).update(CONTEXT_PLAFORM_UPDATE, output);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not generate platform update script for module '" + MODULE + "'.", e);
        }
        log.info("updated database to version " + VERSION);
    }

    /**
     * Checks the database state and version.
     * @return false if the database is not up to date. 
     * @throws DatabaseManagerException
     */
    public boolean validateDatabase(String databaseName) throws DatabaseManagerException {
        // check that DB has all updates we have
        List<ChangeSet> unrunChangeSets;
        changeDatabaseContext(databaseName);
        try {
            unrunChangeSets = getLiquibase(getConnection()).listUnrunChangeSets(new Contexts(CONTEXT_UPDATE));
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not retrieve ChangeSet list for module '" + MODULE + "'.", e);
        }
        String dbVersion = getDatabaseVersion();
        log.info("The database version is " + dbVersion + " (expected " + VERSION + ")");
        boolean result = ((unrunChangeSets == null) || (unrunChangeSets.size() == 0));
        if (result) {
            log.info("The database contains all necessary updates");
        } else {
            log.info("The database is missing " + unrunChangeSets.size() + " updates");
        }
        // check that DB version is not greater than ours
        return result && VERSION.equals(getDatabaseVersion());
    }

    public Connection getConnection() {
        return connection;
    }

    protected void changeDatabaseContext(String databaseName) throws DatabaseManagerException {
    }

    protected void changeDatabaseContext(String databaseName, Writer output) throws DatabaseManagerException {
    }

    /**
     * 
     * @return database version if any, otherwise returns "UNKNOWN"
     */
    public String getDatabaseVersion() {

        String sql = "SELECT ID FROM DATABASECHANGELOG WHERE AUTHOR='" + MODULE + "_version' ORDER BY DATEEXECUTED DESC";
        try {
            ResultSet rs = getConnection().createStatement().executeQuery(sql);
            try {
                if (rs.next()) {
                    return rs.getString(1);
                }
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            log.throwing(DatabaseManager.class.getName(), "getDatabaseVersion. " + e.getMessage(), e);
        } finally {
        }
        return "UNKNOWN";
    }

    /**
     * checks whether the database <i>databaseName</i> exist.
     * @param databaseName
     * @return true if database exists, false otherwise
     */
    public abstract boolean databaseExist(String databaseName);

    protected abstract Connection getConnection(String databaseName, String username, String password)
            throws SQLException;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static DatabaseManager getInstance(Connection connection) throws DatabaseManagerException {
        setLoggingLevel("info", null);
        try {
            DatabaseMetaData dmd = connection.getMetaData();
            String productName = dmd.getDatabaseProductName();
            if ("H2".equals(productName)) {
                log.finer("selected H2DatabaseManager");
                return new H2DatabaseManager(connection);
            } else if ("Oracle".equals(productName)) {
                log.finer("selected OracleDatabaseManager");
                return new OracleDatabaseManager(connection);
            } else if ("Microsoft SQL Server".equals(productName)) {
                log.finer("selected MSSQLDatabaseManager");
                return new MSSQLDatabaseManager(connection);
            } else if ("sql server".equals(productName) || productName.contains("Adaptive Server")) {
                log.finer("selected SybaseDatabaseManager");
                return new SybaseDatabaseManager(connection);
            } else if ("ASE".equalsIgnoreCase(productName)) {
                log.finer("selected SybaseDatabaseManager");
                return new SybaseDatabaseManager(connection);
            } else {
                throw new DatabaseManagerException("Database " + productName + " " + dmd.getDatabaseProductVersion()
                        + " is not supported");
            }
        } catch (SQLException e) {
            throw new DatabaseManagerException(e);
        }
    }

    private static LogLevel getLiquiBaseLogLevel(String level){
        //all|finest|finer|fine|info|warning|severe|off
        //
        LogLevel result = null;
        if ("all".equalsIgnoreCase(level) || "finest".equalsIgnoreCase(level)|| "finer".equalsIgnoreCase(level)){
            result = LogLevel.DEBUG;
        }else{
            if ("fine".equalsIgnoreCase(level) || "info".equalsIgnoreCase(level)){
                result = LogLevel.INFO;
            } else {
                if ("warning".equalsIgnoreCase(level)){
                    result = LogLevel.WARNING;
                } else {
                    if ("severe".equalsIgnoreCase(level)){
                        result = LogLevel.SEVERE;
                    } else {
                        result = LogLevel.OFF;
                    }
                }
            }
        }
        return result;
    }
    private static Level getLogLevel(String level){
        return Level.parse(level.toUpperCase());
    }
    public static void setLoggingLevel(String level, String logFile) {
        LogFactory logFactory = LogFactory.getInstance();
        logFactory.getLog().setLogLevel(getLiquiBaseLogLevel(level));        
        log.setLevel(getLogLevel(level));
        if (logFile != null) {
            Handler fH;
            try {
                fH = new FileHandler(logFile);
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot open log file " + logFile + ". Reason: " + e.getMessage());
            }
            fH.setFormatter(new SimpleFormatter());
            log.addHandler(fH);
            log.setUseParentHandlers(false);
            liquibase.logging.Logger logger = LogFactory.getInstance().getLog();
            if (logger instanceof JavaUtilLogger){
                ((JavaUtilLogger)logger).addHandler(fH);
                ((JavaUtilLogger)logger).setUseParentHandlers(false);
            }
        }
    }

    public static void usage(String message) {
        String usage = "Usage: \n"
                + "java "
                + DatabaseManager.class.getName()
                + " [commands] [options]\n"
                + "Commands:\n"
                + "\t-d or drop - drop database\n"
                + "\t-c or create - create database\n"
                + "\t-i or init - initialize the database\n"
                + "\t-u or update - update the database to current version. \n"
                + "\t-p or platform - update only the platform database data to current version. \n"
                + "\t-v or validate - validate the database. This is default command.\n"
                + "\t-s or stacktrace - show exeption stack trace.\n"
                + "Options:\n"
                + "\t--url=<url> - JDBC URL of the database to connect to\n"
                + "\t--driver=<driverClass> - JDBC driver class name\n"
                + "\t--username=<username> - username to connect to the database (default: current user)\n"
                + "\t--password=<password> - password for username (default: <username>)\n"
                + "\t--admin=<adminUsername> - database administrator user name\n"
                + "\t--adminPassword=<adminPassword> - database administrator password\n"
                + "\t--database=<databaseName> - the name of the database to drop or to cretae\n"
                + "\t--sqlFile=<file> - dump SQL script to <file> instead of executing it\n"
                + "\t--sqlFileEncoding=<encoding> - encoding for SQL script file, default is UTF-8\n"
                + "\t--logLevel=<all|finest|finer|fine|info|warning|severe|off> - set logging level\n"
                + "\t--logFile=<file> - specify the file, where to drite log. By default the logs are output to console\n"
                + "\t-Dmastername=<sys> - имя схемы для MSSQL, необходимо указать если имя отлично от sys, в базе master(например --mastername=dbo)"
                + "\t-D<propertyName>=<propertyValue> - define property with given name and value\n";

        System.out.println(message);
        System.out.println(usage);
        System.exit(1);
    }

    /**
     * 
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // parse command line
        boolean drop = false, create = false, init = false, update = false, platform = false, validate = true;
        boolean stacktrace = false;
        Properties p = new Properties();
        Properties options = new Properties();
        Writer sqlFile = null;

        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("dbmanager");
            Enumeration<String> keys = rb.getKeys();
            while (keys.hasMoreElements()) {
                String name = (String) keys.nextElement();
                if (name.startsWith("-D")) {
                    p.setProperty(name, rb.getString(name));
                } else {
                    options.setProperty(name, rb.getString(name));
                }
            }
        } catch (MissingResourceException e) {
        }

        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i]) || COMMAND_DROP.equals(args[i])) {
                drop = true;
                validate = false;
            } else if ("-c".equals(args[i]) || COMMAND_CREATE.equals(args[i])) {
                create = true;
                validate = false;
            } else if ("-i".equals(args[i]) || COMMAND_INIT.equals(args[i])) {
                init = true;
                validate = false;
            } else if ("-u".equals(args[i]) || COMMAND_UPDATE.equals(args[i])) {
                update = true;
                validate = false;
            } else if ("-p".equals(args[i]) || COMMAND_PLATFORM.equals(args[i])) {
                platform = true;
            } else if ("-v".equals(args[i]) || COMMAND_VALIDATE.equals(args[i])) {
                validate = true;
            } else if ("-s".equals(args[i]) || COMMAND_STACKTRACE.equals(args[i])) {
                stacktrace = true;
            } else if (args[i].startsWith("--")) {
                int index = args[i].indexOf('=');
                if (index > 0) {
                    String name = args[i].substring(2, index);
                    String value = args[i].substring(index + 1);
                    options.setProperty(name, value);
                }
            } else if (args[i].startsWith("-D")) {
                int index = args[i].indexOf('=');
                if (index > 0) {
                    String name = args[i].substring(2, index);
                    String value = args[i].substring(index + 1);
                    p.setProperty(name, value);
                }
            } else {
                usage("Invalid option: " + args[i]);
            }
        }

        // validate options:
        if (validate) {
            drop = false;
            create = false;
            init = false;
            update = false;
        }
        if (update) {
            drop = false;
            create = false;
        }
        if (!options.containsKey("username")) {
            usage("username option is not specified");
        }
        if ((drop || create) && !options.containsKey("admin")) {
            usage("drop or create commands require admin user name to be specified");
        }
        if ((drop || create) && !options.containsKey("database")) {
            usage("drop or create commands require database name to be specified");
        }
        if (!options.containsKey("url")) {
            usage("JDBC URL for connecting to database is not specified");
        }
        if (create && init && options.containsKey("sqlFile")) {
            usage("SQL initialization script can be generated only for existing database. You need to run DatabaseManager several times:\n\t1. Dump SQL for creating database.\n\t2.Actually create the database\n\t3.Dump SQL for initializing the database.");
        }
        if (!options.containsKey("password")) {
            options.setProperty("password", options.getProperty("username"));
        }
        if (!p.containsKey(MASTER_NAME)) {
            p.setProperty(MASTER_NAME, "dbo");
        }

        // do the work
        setLoggingLevel(options.getProperty("logLevel", "info"), options.getProperty("logFile"));
        try {
            if (options.containsKey("driver")) {
                Class.forName(options.getProperty("driver"));
            } else {
                // preload all known drivers
                //Class.forName("org.h2.Driver");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to load a database driver." + e.getMessage());
            if (stacktrace) {
                e.printStackTrace();
            }
            System.exit(5);
        }

        String cnUserName = options.getProperty("admin");
        String cnPassword = options.getProperty("adminPassword");
        String username = options.getProperty("username");
        String password = options.getProperty("password");
        if (cnUserName == null || update || validate) {
            cnUserName = username;
            cnPassword = password;
        }
        if (options.containsKey("sqlFile")) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(options.getProperty("sqlFile"));
                sqlFile = new OutputStreamWriter(fos, options.getProperty("sqlFileEncoding", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.err.println("Used unsupported еncoding." + e.getMessage());
                if (stacktrace) {
                    e.printStackTrace();
                }
                System.exit(6);
            } catch (FileNotFoundException e) {
                System.err.println("The file '" + options.getProperty("sqlFile") + "' cannot be opened.");
                if (stacktrace) {
                    e.printStackTrace();
                }
                System.exit(7);
            }
        }

        Locale.setDefault(Locale.ENGLISH);

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(options.getProperty("url"), cnUserName, cnPassword);
            log.finest("connected to the database " + connection.getMetaData().getURL());
        } catch (SQLException ex) {
            System.err.println("Unable to connect to server " + options.getProperty("url") + ". " + ex.getMessage());
            if (stacktrace) {
                ex.printStackTrace();
            }
            System.exit(4);
        }
        try {
            DatabaseManager dm = DatabaseManager.getInstance(connection);
            dm.setProperties(p);
            String databaseName = options.getProperty("database");
            if (options.containsKey("dataDevice")) {
                databaseOptions += (" on " + options.get("dataDevice"));
                if (options.containsKey("dataDeviceSize")) {
                    databaseOptions += ("='" + options.get("dataDeviceSize") + "'");
                }
            }
            if (options.containsKey("logDevice")) {
                databaseOptions += (" log on " + options.get("logDevice"));
                if (options.containsKey("logDeviceSize")) {
                    databaseOptions += ("='" + options.get("logDeviceSize") + "'");
                }
            }
            if (drop) {
                if (sqlFile != null) {
                    dm.dropDatabase(databaseName, sqlFile);
                } else {
                    dm.dropDatabase(databaseName);
                }
            }
            if (create) {
                if (sqlFile != null) {
                    dm.createDatabase(databaseName, username, password, sqlFile);
                } else {
                    if (dm.databaseExist(databaseName)) {
                        System.err.println("Can not create database '" + databaseName + "', because it already exist.");
                        System.exit(2);

                    }
                    dm.createDatabase(databaseName, username, password);
                }
            }
            if (init) {
                if (sqlFile != null) {
                    dm.initDatabase(databaseName, username, password, platform, sqlFile);
                } else {
                    dm.initDatabase(databaseName, username, password, platform);
                }
            }
            if (update) {
                if (sqlFile != null) {
                    dm.updateDatabase(databaseName, platform, sqlFile);
                } else {
                    dm.updateDatabase(databaseName, platform);
                }
            }
            if (validate) {
                if (!dm.validateDatabase(databaseName)) {
                    System.out.println("The database is not up to date");
                    System.exit(3);
                }
                System.out.println("The database is valid. Database version: " + VERSION);
            }
        } catch (Exception ex) {
            if (stacktrace) {
                throw ex;
            } else {
                System.err.println(ex.getMessage());
                System.exit(9);
            }
        } finally {
            connection.close();
            if (sqlFile != null) {
                sqlFile.close();
            }
        }
        log.finer("exiting gracefully");
    }
}
