package com.bivgroup.core.audit.rest;

import com.bivgroup.core.audit.ResultOperation;

import javax.ws.rs.container.ContainerResponseContext;

@FunctionalInterface
public interface AuditAnalysisResult {
    /**
     * Анализ результата операции
     *
     * @param responseContext объект из которого можно получить ответ сервера
     * @return результат анализа
     */
    ResultOperation analysisOperationResult(ContainerResponseContext responseContext);
}
