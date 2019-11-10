package com.bivgroup.querybuilder.common.idcache;

import com.bivgroup.config.Config;

import java.util.HashMap;
import java.util.Map;

public class IdCacheManager {
    private static IdCacheManager instance;
    private static final Map<String, IdCacheItem> idCache = new HashMap();
    private static Integer DEFAULT_BUFFER_SIZE = 1000;
    private static Integer bufferSize;

    private IdCacheManager() {
        Integer bufSize = Integer.parseInt(Config.getConfig().getParam("cacheIdBufferSize", DEFAULT_BUFFER_SIZE.toString()));
        if (bufSize > DEFAULT_BUFFER_SIZE) {
            bufferSize = bufSize;
        }

    }

    public static Long getNextId(String tableName, Integer quantity) throws IdObtainedException {
        if (instance == null) {
            Class var2 = IdCacheManager.class;
            synchronized(IdCacheManager.class) {
                if (instance == null) {
                    instance = new IdCacheManager();
                }
            }
        }

        if (!idCache.containsKey(tableName)) {
            IdCacheManager var7 = instance;
            synchronized(instance) {
                if (!idCache.containsKey(tableName)) {
                    idCache.put(tableName, new IdCacheItem(tableName, bufferSize));
                }
            }
        }

        return ((IdCacheItem)idCache.get(tableName)).getNextId(quantity);
    }

    public static Long getNextId(String tableName) throws IdObtainedException {
        return getNextId(tableName, 1);
    }

    static {
        bufferSize = DEFAULT_BUFFER_SIZE;
    }
}
