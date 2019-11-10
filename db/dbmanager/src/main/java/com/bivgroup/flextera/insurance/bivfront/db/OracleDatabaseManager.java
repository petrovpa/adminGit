package com.bivgroup.flextera.insurance.bivfront.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleDatabaseManager extends DatabaseManager {

    protected OracleDatabaseManager(Connection connection) {
        super(connection);
    }

    @Override
    protected String[] getCreateSQL(String databaseName, String username, String password) {
        String[] sql = {"CREATE USER " + databaseName + " IDENTIFIED BY " + password,
            "GRANT ALL PRIVILEGES TO " + databaseName, "GRANT SELECT_CATALOG_ROLE TO " + databaseName};
        return sql;
    }

    @Override
    protected String[] getDropSQL(String databaseName) {
        return new String[]{"DECLARE\n" + "CountUser   NUMBER;\n" + "CURSOR sessions_cur IS\n"
                    + "SELECT CAST(sid AS VARCHAR2(50)) as sid, CAST(serial# AS VARCHAR2(50)) serial\n"
                    + "FROM v$session WHERE USERNAME = UPPER('"
                    + databaseName
                    + "') OR USERNAME = LOWER('"
                    + databaseName
                    + "');\n"
                    + "privileges_not_granted EXCEPTION;\n"
                    + "PRAGMA EXCEPTION_INIT (privileges_not_granted, -1952);\n"
                    + "BEGIN\n"
                    + "    SELECT COUNT(1) INTO CountUser FROM ALL_USERS\n"
                    + "     WHERE USERNAME = UPPER('"
                    + databaseName
                    + "') OR USERNAME = LOWER('"
                    + databaseName
                    + "');\n"
                    + "    IF (CountUser > 0) THEN\n"
                    + "    BEGIN\n"
                    + "            EXECUTE IMMEDIATE 'REVOKE ALL PRIVILEGES FROM "
                    + databaseName
                    + "';\n"
                    + "    EXCEPTION\n"
                    + "        WHEN privileges_not_granted THEN NULL;\n"
                    + "    END;\n"
                    + "        FOR sessions_rec IN sessions_cur\n"
                    + "        LOOP\n"
                    + "            EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION ''' || sessions_rec.sid || ',' || sessions_rec.serial || ''' IMMEDIATE';\n"
                    + "        END LOOP;\n"
                    + "        DBMS_LOCK.SLEEP(120);\n"
                    + "        EXECUTE IMMEDIATE 'DROP USER "
                    + databaseName + " CASCADE';\n" + "    END IF;\n" + "END;"};
    }

    @Override
    protected Connection getConnection(String databaseName, String username, String password) throws SQLException {
        String url = getConnection().getMetaData().getURL();
        return DriverManager.getConnection(url, databaseName, password);
    }

    @Override
    public boolean databaseExist(String databaseName) {
        try {
            ResultSet rs = getConnection().createStatement().executeQuery(
                    "SELECT COUNT(*)  FROM ALL_USERS WHERE USERNAME = UPPER('" + databaseName
                    + "') OR USERNAME = LOWER('" + databaseName + "')");
            try {
                if (rs.next()) {
                    return 1 == rs.getInt(1);
                }
                return false;
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
