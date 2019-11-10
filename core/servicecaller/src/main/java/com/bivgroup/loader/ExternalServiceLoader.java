package com.bivgroup.loader;

import java.util.ServiceLoader;

/**
 * Класс будет удален в следующей версии библиотеки
 * вместо него теперь есть библиотеке serviceloaderutils
 * и класс DefaultServiceLoader
 */
@Deprecated
public class ExternalServiceLoader {
    public static <T> T loadService(Class<T> clazz) {
        ServiceLoader<T> impl = ServiceLoader.load(clazz);

        T result = null;
        for (T loadedImpl : impl) {
            result = loadedImpl;
            if (result != null) {
                break;
            }
        }

        if (result == null) throw new RuntimeException(
                "Cannot find implementation for: " + clazz);

        return result;
    }
}
