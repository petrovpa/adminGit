package com.bivgroup.utils.serviceloader;

import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * Класс обертка над {@link java.util.ServiceLoader}
 * для уменьшения дублирования кода
 */
public class DefaultServiceLoader {
    /**
     * Загрузить любую имплемантацию сервиса
     *
     * @param clazz класс сервиса, которые требуется загрузить
     * @param <T>   тип сервиса, которые требуется загрузить
     * @return реализацию сервиса
     */
    public static <T> T loadServiceAny(Class<T> clazz) {
        ServiceLoader<T> impl = ServiceLoader.load(clazz, DefaultServiceLoader.class.getClassLoader());
        T result = null;
        for (T loadedImpl : impl) {
            result = loadedImpl;
            if (result != null) {
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException(
                    "Cannot find implementation for: " + clazz);
        }

        return result;
    }

    /**
     * Загрузить имплемантацию сервиса по имени.
     * Так как имплемантаций в файле может быть указано больше одной,
     * то требуется загружать требуемую разработчику имплемантацию.
     * Требуемая имплемантацию определяется по полному имени класса
     *
     * @param clazz     класс сервиса, которые требуется загрузить
     * @param className полное имя реализации сервиса, которую нужо загрузить
     * @param <T>       тип сервиса, которые требуется загрузить
     * @return реализацию сервиса
     */
    public static <T> T loadServiceByName(Class<T> clazz, String className) {
        ServiceLoader<T> impl = ServiceLoader.load(clazz, DefaultServiceLoader.class.getClassLoader());

        T result = null;
        for (T loadedImpl : impl) {
            if (loadedImpl != null && loadedImpl.getClass().getName().equals(className)) {
                result = loadedImpl;
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException(
                    "Cannot find implementation for: " + clazz + " by name: " + className);
        }

        return result;
    }
}
