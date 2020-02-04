package com.bivgroup.rest.admrestws.common;

import com.bivgroup.rest.admrestws.pojo.common.ResponseFactory;
import com.fasterxml.jackson.core.JsonParseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionFilterProvider implements ExceptionMapper<Throwable> {
    private final static String PARSE_ERROR_TEXT = "Ошибка в параметрах запроса";
    private final static String GENERAL_ERROR_TEXT = "Ошибка при вызове сервиса";

    public Response toResponse(Throwable e) {
        if (e instanceof JsonParseException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity((ResponseFactory.createErrorResponse(PARSE_ERROR_TEXT))).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity((ResponseFactory.createErrorResponse(GENERAL_ERROR_TEXT))).build();
    }
}