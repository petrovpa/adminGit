package com.bivgroup.flextera.insurance.bivfront.db;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

public class MSSQLDatabaseManager extends DatabaseManager {

    MSSQLDatabaseManager(Connection connection) {
        super(connection);
    }

    @Override
    protected String[] getDropSQL(String databaseName) {
        return new String[]{"if exists (select 1 from master." + getProperties().getProperty(MASTER_NAME) + ".sysdatabases where name = '" + databaseName
                    + "') BEGIN ALTER DATABASE [" + databaseName
                    + "] SET SINGLE_USER WITH ROLLBACK IMMEDIATE ; drop database " + databaseName + " END"};
    }

    @Override
    protected String[] getCreateSQL(String databaseName, String username, String password) {
        String[] sql = {
            "use master",
            "if not exists (select 1 from master." + getProperties().getProperty(MASTER_NAME) + ".syslogins where name = '" + username + "') exec sp_addlogin '"
            + username + "', '" + password + "'",
            "if not exists (select 1 from master." + getProperties().getProperty(MASTER_NAME) + ".sysdatabases where name = '" + databaseName
            + "') create database " + databaseName,
            "use " + databaseName,
            "if not exists (select 1 from sysusers where name = '" + username
            + "') exec sp_grantdbaccess '" + username + "'",
            "exec sp_addrolemember 'db_owner', '" + username + "'"};
        return sql;
    }

    @Override
    public Connection getConnection(String databaseName, String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public void initDatabase(String databaseName, String username, String password, boolean platform, Writer output) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        Connection cn = getConnection();
        try {
            output.write("use " + databaseName + ";" + System.getProperty("line.separator"));
            createTables(cn, platform, output);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for the database '" + databaseName + "'.", e);
        } catch (IOException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for the database '" + databaseName + "'.", e);
        }
        log.info("initialized database '" + databaseName + "'");
    }

    @Override
    public void initDatabase(String databaseName, String username, String password, boolean platform) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        Connection cn = getConnection();
        try {
            cn.createStatement().executeUpdate("use " + databaseName);
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
                    "select count(*) from master." + getProperties().getProperty(MASTER_NAME) + ".sysdatabases where name = '" + databaseName + "'");
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
