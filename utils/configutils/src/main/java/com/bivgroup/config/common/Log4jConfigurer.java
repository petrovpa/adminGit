package com.bivgroup.config.common;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;

public class Log4jConfigurer {
    private static final Logger logger = Logger.getLogger(Log4jConfigurer.class);

    private static final String DEFAULT_LOG4J_XML = "log4j.xml";
    private static final String DEFAULT_LOG4J_PROPERTIES = "log4j.properties";

    protected Log4jConfigurer() {
    }

    public static void initLogging() {
        try {
            File propertiesFile = new File(DEFAULT_LOG4J_PROPERTIES);
            if (propertiesFile.exists() && propertiesFile.canRead()) {
                PropertyConfigurator.configureAndWatch(DEFAULT_LOG4J_PROPERTIES);
                logger.info("Read log4j configuration from: " + propertiesFile.getCanonicalPath());
            } else {
                File xmlFile = new File(DEFAULT_LOG4J_XML);
                if (xmlFile.exists() && xmlFile.canRead()) {
                    DOMConfigurator.configureAndWatch(DEFAULT_LOG4J_XML);
                    logger.info("Read log4j configuration from: " + xmlFile.getCanonicalPath());
                } else {
                    logger.warn("Can not find log4j configuration file: " + propertiesFile.getCanonicalPath());
                    logger.warn("Can not find log4j configuration file: " + xmlFile.getCanonicalPath());
                    logger.warn("Use default log4j configuration.");
                    logger.warn("You can get log4j.xml configuration file from config-files directory of platform distributive.");
                }
            }
        } catch (Exception var2) {
            logger.error(var2);
        }

    }
}

