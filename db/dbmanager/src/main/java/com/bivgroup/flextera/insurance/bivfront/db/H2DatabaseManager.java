package com.bivgroup.flextera.insurance.bivfront.db;

import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

public class H2DatabaseManager extends DatabaseManager {

    protected H2DatabaseManager(Connection connection) {
        super(connection);
    }

    @Override
    protected Connection getConnection(String databaseName, String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    protected String[] getCreateSQL(String databaseName, String username, String password) {
        return new String[]{};
    }

    @Override
    protected String[] getDropSQL(String databaseName) {
        return new String[]{};
    }

    @Override
    public void initDatabase(String databaseName, String username, String password, boolean platform, Writer output) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        // use the same connection and avoid closing it
        Connection cn = getConnection();
        try {
            createTables(cn, platform, output);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not generate initialize DDL script for the database '" + databaseName + "'.", e);
        }
        log.info("initialized database '" + databaseName + "'");
    }

    @Override
    public void initDatabase(String databaseName, String username, String password, boolean platform) throws DatabaseManagerException {
        log.finest("initializing database '" + databaseName + "'");
        // use the same connection and avoid closing it
        Connection cn = getConnection();
        try {
            createTables(cn, platform);
        } catch (LiquibaseException e) {
            throw new DatabaseManagerException("Can not initialize database '" + databaseName + "'.", e);
        }
        log.info("initialized database '" + databaseName + "'");
    }

    @Override
    public boolean databaseExist(String databaseName) {
        // всегда возвращаем true потому что БД в памяти создается при коннекте к ней, то есть всегда существует 
        return true;
    }
}
