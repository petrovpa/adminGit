package com.bivgroup.config.common;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;

public class DataSourceFactory {
    public DataSourceFactory() {
    }

    public static DataSource createDataSource(String driverName, String dataSourceUrl, int minCon, int maxCon, String userName, String password) throws ConfigException {
        GenericObjectPool connectionPool = new GenericObjectPool((PoolableObjectFactory) null);
        connectionPool.setMaxIdle(-1);
        connectionPool.setMaxActive(maxCon);
        connectionPool.setMinIdle(1);
        connectionPool.setWhenExhaustedAction((byte) 1);
        connectionPool.setMaxWait(10000L);
        connectionPool.setTestOnBorrow(false);
        connectionPool.setTestOnReturn(false);
        connectionPool.setTestWhileIdle(true);
        connectionPool.setNumTestsPerEvictionRun(4);
        connectionPool.setTimeBetweenEvictionRunsMillis(900000L);

        Driver driver;
        try {
            driver = (Driver) Class.forName(driverName).newInstance();
        } catch (ClassNotFoundException var13) {
            throw new ConfigException("JDBC driver not found: " + driverName, var13);
        } catch (Exception var14) {
            throw new ConfigException("Faild to initialize JDBC driver: " + driverName, var14);
        }

        Properties props = new Properties();
        props.put("user", userName);
        props.put("password", password);
        props.put("fixedString", "true");
        ConnectionFactory connectionFactory = new DriverConnectionFactory(driver, dataSourceUrl, props);
        String validationQuery = resolveValidationQueryFromURL(dataSourceUrl);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, validationQuery, false, false);
        connectionPool.setFactory(poolableConnectionFactory);
        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
        return dataSource;
    }

    private static String resolveValidationQueryFromURL(String jdbcUrl) {
        if (jdbcUrl != null && jdbcUrl.length() != 0) {
            if (jdbcUrl.startsWith("jdbc:oracle")) {
                return "select 1 from dual";
            } else if (jdbcUrl.contains(":sqlserver")) {
                return "select 1";
            } else if (jdbcUrl.contains(":sqlserver")) {
                return "select 1";
            } else if (jdbcUrl.startsWith("jdbc:db2")) {
                return "select 1 from SYSIBM.SYSDUMMY1";
            } else {
                return jdbcUrl.startsWith("jdbc:h2") ? "select 1 from dual" : null;
            }
        } else {
            return null;
        }
    }
}
