package com.bivgroup.config;

import com.bivgroup.stringutils.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

    private static final Logger logger = Logger.getLogger(ConfigUtils.class);

    static {
        logger.debug("[ConfigUtils] static init...");
    }

    private ConfigUtils() {
        logger.debug("[ConfigUtils] constructor...");
    }

    private static InputStream getResourceAsStream(String resource) {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(stripped);
        }

        if (stream == null) {
            ConfigUtils.class.getResourceAsStream(resource);
        }

        if (stream == null) {
            stream = ConfigUtils.class.getClassLoader().getResourceAsStream(stripped);
        }

        return stream;
    }

    public static String getPropertyFromResource(String resource, String name) throws IOException {
        String result = null;
        logger.debug(String.format(
                "[ConfigUtils] getPropertyFromResource reading property with name '%s' from resource '%s'...",
                resource, name
        ));
        InputStream inputStream = getResourceAsStream(resource);
        if (inputStream == null) {
            throw new IllegalArgumentException("Not specified file 'project.properties' for the project.");
        } else {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                result = properties.getProperty(name);
            } finally {
                inputStream.close();
            }

            return result;
        }
    }

    public static Properties getPropertiesFromResource(String resource) throws IOException {
        InputStream inputStream = getResourceAsStream(resource);
        if (inputStream == null) {
            throw new IllegalArgumentException("Not specified file 'project.properties' for the project.");
        } else {
            Properties properties = null;
            try {
                properties = new Properties();
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
            return properties;
        }
    }

    public static String getProjectProperty(String propertyName, boolean checkRequired) {
        String result;
        String resourceName = "project.properties";
        logger.debug(String.format(
                "[ConfigUtils] getProjectProperty reading property with name '%s' from resource '%s'...",
                propertyName, resourceName
        ));
        try {
            result = getPropertyFromResource(resourceName, propertyName);
        } catch (IOException ex) {
            logger.error(String.format(
                    "[ConfigUtils] getProjectProperty caused exception while reading property with name '%s' from resource '%s'! Details (exception):", propertyName, resourceName
            ), ex);
            throw new RuntimeException(ex);
        }
        if (checkRequired && StringUtils.isEmpty(result)) {
            logger.error(String.format(
                    "[ConfigUtils] getProjectProperty was unable to read required property with name '%s' from resource '%s'!", propertyName, resourceName
            ));
            throw new IllegalArgumentException("File project.properties property is not set '" + propertyName + "'.");
        } else {
            return result;
        }
    }

    public static String getProjectProperty(String propertyName) {
        return getProjectProperty(propertyName, true);
    }

    public static String getProjectProperty(String propertyName, String defaultValue) {
        String propertyValue = getProjectProperty(propertyName, false);
        return !propertyValue.isEmpty() ? propertyValue : defaultValue;
    }
}
