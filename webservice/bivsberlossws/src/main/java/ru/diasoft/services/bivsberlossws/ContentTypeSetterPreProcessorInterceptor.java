/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.diasoft.services.bivsberlossws;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.spi.metadata.ResourceMethod;


@Provider
@ServerInterceptor
public class ContentTypeSetterPreProcessorInterceptor implements
		PreProcessInterceptor {

	public ServerResponse preProcess(HttpRequest request,
			ResourceMethod method) throws Failure, WebApplicationException {
		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
				"*/*; charset=UTF-8");
		return null;
	}

    public ServerResponse preProcess(HttpRequest hr, ResourceMethodInvoker rmi) throws Failure, WebApplicationException {
		hr.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
				"*/*; charset=UTF-8");
            return null;
    }

}