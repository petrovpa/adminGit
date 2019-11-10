package com.bivgroup.core.audit.rest;

import com.bivgroup.core.audit.Audit;
import com.bivgroup.core.audit.AuditImpl;
import com.bivgroup.core.audit.annotation.AuditBean;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Priority(1)
@Provider
public class AuditBindingFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        AuditBean auditingClass = resourceInfo.getResourceClass().getAnnotation(AuditBean.class);
        AuditBean auditingMethod = resourceInfo.getResourceMethod().getAnnotation(AuditBean.class);
        if (auditingClass != null || auditingMethod != null) {
            ResteasyProviderFactory.pushContext(Audit.class, new AuditImpl());
        }
    }
}
