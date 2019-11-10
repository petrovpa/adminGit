package com.bivgroup.services.b2bposws.system.files;

import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.config.Config;

public interface SeaweedsGetters {

    String CONFIG_SERVICE_NAME = Constants.B2BPOSWS;

    default String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(CONFIG_SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    default boolean isUseSeaweedFS() {
        return getUseSeaweedFS().equalsIgnoreCase("TRUE");
    }

    default String getSeaweedFSUrl() {
        Config config = Config.getConfig(CONFIG_SERVICE_NAME);
        String SeaweedFSUrl = config.getParam("SEAWEEDFSURL", "");
        return SeaweedFSUrl;
    }

}
