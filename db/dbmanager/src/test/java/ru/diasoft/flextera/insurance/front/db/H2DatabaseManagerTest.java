package ru.diasoft.flextera.insurance.front.db;

import com.bivgroup.flextera.insurance.bivfront.db.DatabaseManager;
import com.bivgroup.flextera.insurance.bivfront.db.DatabaseManagerException;
import com.bivgroup.flextera.insurance.bivfront.db.H2DatabaseManager;
import static org.junit.Assert.assertTrue;

import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import liquibase.logging.LogFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class H2DatabaseManagerTest {

    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "test";
    private static String DB_NAME = "test";
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String JDBC_URL = "jdbc:h2:mem:";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "sa";
    private Connection connection;
    private H2DatabaseManager manager;
    private int dbNumber = 1;

    public H2DatabaseManagerTest() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
    }

    @Before
    public void setUp() throws Exception {
        dbNumber++;
        DB_NAME = "test" + dbNumber;
        connection = DriverManager.getConnection(JDBC_URL + DB_NAME);
        // приводит к ошибке в ходе компиляции - Error: H2DatabaseManager(java.sql.Connection) has protected access in com.bivgroup.flextera.insurance.bivfront.db.H2DatabaseManager
        /*
        manager = new H2DatabaseManager(connection);
        */
        //System.err.println("connection: " + connection);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
        manager = null;
    }

    @Test
    public void testCreateDatabaseStringStringString() throws DatabaseManagerException {
        // метод ничего не делает, прочто вызываем, чтобы проверить что не ломается
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD);
    }

    @Test
    public void testCreateDatabaseStringStringStringWriter() throws DatabaseManagerException {
        // метод ничего не делает, прочто вызываем, чтобы проверить что не ломается
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD, new OutputStreamWriter(System.out));
    }

    @Test
    public void testDropDatabaseString() throws DatabaseManagerException {
        // метод ничего не делает, прочто вызвыаем, чтобы проверить что не ломается
        manager.dropDatabase(DB_NAME);

    }

    @Test
    public void testDropDatabaseStringWriter() throws DatabaseManagerException {
        // метод ничего не делает, прочто вызвыаем, чтобы проверить что не ломается
        manager.dropDatabase(DB_NAME, new OutputStreamWriter(System.out));
    }

    @Test
    public void initDatabaseStringStringStringWriter() throws DatabaseManagerException {
        Properties p = new Properties();
        p.setProperty("productPrefix", "test");
        p.setProperty("metadataUrl", "c:\test");
        manager.setProperties(p);
        OutputStreamWriter output = new OutputStreamWriter(System.out);
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD, output);
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD); // без этого работать не сможет
        manager.initDatabase(DB_NAME, DB_USER, DB_PASSWORD, false, output);
    }

    @Test
    public void initDatabaseStringStringString() throws DatabaseManagerException {
        Properties p = new Properties();
        p.setProperty("productPrefix", "test");
        p.setProperty("metadataUrl", "c:\test");
        manager.setProperties(p);
        //OutputStreamWriter output = new OutputStreamWriter(System.out);
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD);
        manager.initDatabase(DB_NAME, DB_USER, DB_PASSWORD, false);
    }

    @Test
    public void testValidateDatabase() throws Exception {
        Properties p = new Properties();
        p.setProperty("productPrefix", "test");
        p.setProperty("metadataUrl", "c:\test");
        manager.setProperties(p);
        //OutputStreamWriter output = new OutputStreamWriter(System.out);
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD);
        manager.initDatabase(DB_NAME, DB_USER, DB_PASSWORD, false);

        assertTrue(manager.validateDatabase(DB_NAME));
    }

    @Test
    public void testUpdateDatabase() throws Exception {
        LogFactory.getInstance().getLog().setLogLevel("all", "liquibase.log");
        Properties p = new Properties();
        p.setProperty("productPrefix", "test");
        p.setProperty("metadataUrl", "c:\test");
        manager.setProperties(p);
        //OutputStreamWriter output = new OutputStreamWriter(System.out);
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD);
        manager.initDatabase(DB_NAME, DB_USER, DB_PASSWORD, false);

        manager.updateDatabase(DB_NAME, false);

        assertTrue(manager.validateDatabase(DB_NAME));
    }

    @Test
    public void testUpdateDatabaseWriter() throws Exception {
        Properties p = new Properties();
        p.setProperty("productPrefix", "test");
        p.setProperty("metadataUrl", "c:\test");
        manager.setProperties(p);
        //OutputStreamWriter output = new OutputStreamWriter(System.out);
        manager.createDatabase(DB_NAME, DB_USER, DB_PASSWORD);
        manager.initDatabase(DB_NAME, DB_USER, DB_PASSWORD, false);

        manager.updateDatabase(DB_NAME, false, new OutputStreamWriter(System.out));
    }

    @Test
    public void testGetInstance() throws Exception {
        DatabaseManager dm = DatabaseManager.getInstance(connection);
        assertTrue(dm instanceof H2DatabaseManager);
    }
}
