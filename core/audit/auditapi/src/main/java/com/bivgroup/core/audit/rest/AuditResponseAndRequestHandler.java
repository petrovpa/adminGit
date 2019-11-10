package com.bivgroup.core.audit.rest;

import com.bivgroup.core.audit.AuditParameters;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * Интерфейс анализа запроса и ответа вызова rest-сервиса
 */
public interface AuditResponseAndRequestHandler {

    /**
     * Метод разбора и получения входящих параметрова аудита
     *
     * @param request         информация о запросе, откуда можно получить #{@link javax.ws.rs.FormParam} и #{@link javax.ws.rs.HeaderParam}
     * @param uriInfo         информация о строке запроса из которой можно получить #{@link javax.ws.rs.QueryParam} и {@link javax.ws.rs.PathParam}
     * @param auditParameters ссылка на объект параметров аудита, в которые можно добавлять
     *                        значение, которые требуется заудитить
     * @return параметры, которые требуется сохранить в document аудит
     */
    Map<String, Object> getRequest(HttpRequest request, UriInfo uriInfo, AuditParameters auditParameters);

    /**
     * Метод обработки ответа сервер
     *
     * @param responseContext объект из которого можно получить ответ сервера
     * @param auditParameters ссылка на объект параметров аудита, в которые можно добавлять
     *                        значение, которые требуется заудитить
     * @return параметры которые требуется сохранить в document аудита
     */
    Map<String, Object> getResponse(ContainerResponseContext responseContext, AuditParameters auditParameters);

    /**
     * Метод установки информации об методе. Для обработки аннотаций и прочего.
     *
     * @param resourceInfo информация об методе #{@link ResourceInfo}
     */
    void setResourceInfo(ResourceInfo resourceInfo);
}
