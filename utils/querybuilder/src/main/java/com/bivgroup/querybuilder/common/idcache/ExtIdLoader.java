package com.bivgroup.querybuilder.common.idcache;

import com.bivgroup.config.Config;
import com.bivgroup.config.common.ConfigException;
import com.bivgroup.xmlutil.XmlUtil;
import com.bivgroup.xmlutil.exception.XmlUtilException;
import com.bivgroup.xmlutil.interfaces.ServiceUtil;

import java.util.HashMap;
import java.util.Map;

public class ExtIdLoader {
    private static final String corewsUrl = Config.getConfig().getParam("corews", "[corews]");
    private static ServiceUtil xmlUtil;
    private static Boolean initialized = false;
    private static final Object monitor = new Object();

    private ExtIdLoader() {
    }

    public static final void init() throws ConfigException {
        xmlUtil = new XmlUtil();
        initialized = true;
    }

    public static final void init(ServiceUtil serviceUtil) throws ConfigException {
        xmlUtil = serviceUtil;
        initialized = true;
    }

    public static Long load(String tableName, Integer bufferSize) throws XmlUtilException, ConfigException {
        if (!initialized) {
            Object var2 = monitor;
            synchronized (monitor) {
                if (!initialized) {
                    init();
                }
            }
        }

        Map<String, Object> params = new HashMap<>(2, 1.0F);
        params.put("TABLENAME", tableName);
        params.put("BATCHSIZE", bufferSize);
        Map<String, Object> map = xmlUtil.callService(corewsUrl, "getnewid", params);
        return ((Number) map.get("Result")).longValue();
    }
}
