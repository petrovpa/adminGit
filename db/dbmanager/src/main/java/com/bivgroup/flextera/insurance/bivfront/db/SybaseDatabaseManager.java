package com.bivgroup.flextera.insurance.bivfront.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import liquibase.exception.LiquibaseException;

public class SybaseDatabaseManager extends DatabaseManager {

    SybaseDatabaseManager(Connection connection) {
        super(connection);
    }

    @Override
    protected String[] getDropSQL(String databaseName) {
        return new String[]{"use master",
                    "if exists (select 1 from sysdatabases where name = '" + databaseName + "') drop database " + databaseName};
    }

    @Override
    protected String[] getCreateSQL(String databaseName, String username, String password) {
        String[] sql = {
            "use master",
            "if not exists (select 1 from syslogins where name = '" + username + "') exec sp_addlogin '" + username + "', '" + password + "'",
            "if not exists (select 1 from sysdatabases where name = '" + databaseName + "') create database " + databaseName
            + databaseOptions,
            "USE " + databaseName,
            "exec sp_changedbowner " + username,
            "grant role sa_role TO " + username};
        return sql;
    }

    @Override
    public Connection getConnection(String databaseName, String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public void initDatabase(String databaseName, String username, String password, boolean platform, Writer output) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        //setDBOptions(databaseName);
        Connection cn = getConnection();
        try {
            output.write("use " + databaseName + ";" + System.getProperty("line.separator"));
            createTables(cn, platform, output);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for database '" + databaseName + "'.", e);
        } catch (IOException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for database '" + databaseName + "'.", e);
        }
        log.info("initialized database '" + databaseName + "'");
    }

    @Override
    public void initDatabase(String databaseName, String username, String password, boolean platform) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        //setDBOptions(databaseName);
        Connection cn = getConnection();
        try {
            cn.createStatement().execute("use " + databaseName);
            createTables(cn, platform);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not initialize database '" + databaseName + "'.", e);
        } catch (SQLException e) {
            throw new DatabaseManagerException("Unable to change the database context to the database '" + databaseName + "'.", e);
        }
        // avoid closing connection
        log.info("initialized database '" + databaseName + "'");
    }

    protected void changeDatabaseContext(String databaseName) throws DatabaseManagerException {
        Connection cn = getConnection();
        try {
            cn.createStatement().executeUpdate("use " + databaseName);
        } catch (SQLException e) {
            throw new DatabaseManagerException("Unable to change the database context to the database '" + databaseName + "'.", e);
        }
    }

    protected void changeDatabaseContext(String databaseName, Writer output) throws DatabaseManagerException {
        try {
            output.write("use " + databaseName + ";" + System.getProperty("line.separator"));
        } catch (IOException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for the database '" + databaseName + "'.", e);
        }
    }

    @Override
    public boolean databaseExist(String databaseName) {
        try {
            ResultSet rs = getConnection().createStatement().executeQuery(
                    "select count(*) from sysdatabases where name = '" + databaseName + "'");
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

    private void setDBOptions(final String databaseName) throws SQLException {
        Connection cn = getConnection();
        Statement stmt = cn.createStatement();
        stmt.executeUpdate("use master");
        stmt.executeUpdate("exec sp_dboption \"" + databaseName + "\", \"ddl in tran\", true");
        stmt.close();
    }
}
