package com.bivgroup.rest.journalservice;

import com.bivgroup.loader.ExternalServiceLoader;
import com.bivgroup.service.UrlencodedServiceCallerInterface;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.bivgroup.rest.common.Constants.B2BPOSWS;

@Path("/rest/journal")
public class JournalWebServiceGate {
    private final UrlencodedServiceCallerInterface serviceCaller = ExternalServiceLoader.loadService(UrlencodedServiceCallerInterface.class);

    @POST
    @Path("/dsB2B_GetUserParams")
    @Produces("application/json")
    public Response dsB2B_GetUserParams(@FormParam("params") String paramsStr) {
        return serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsB2B_GetUserParams");
    }

    @POST
    @Path("/dsB2B_SetUserParams")
    @Produces("application/json")
    public Response dsB2B_SetUserParams(@FormParam("params") String paramsStr) {
        return serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsB2B_SetUserParams");
    }

    @POST
    @Path("/dsB2B_DeleteUserParams")
    @Produces("application/json")
    public Response dsB2B_DeleteUserParams(@FormParam("params") String paramsStr) {
        return serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsB2B_DeleteUserParams");
    }

    @POST
    @Path("/dsB2B_JournalCustomBrowseListByParam")
    @Produces("application/json")
    public Response dsB2B_JournalCustomBrowseListByParam(@FormParam("params") String paramsStr) {
        return serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsB2B_JournalCustomBrowseListByParam");
    }

    @POST
    @Path("/dsB2B_JournalDataBrowseListByParamWrapper")
    @Produces("application/json")
    public Response dsB2B_JournalDataBrowseListByParamWrapper(@FormParam("params") String paramsStr) {
        return serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsB2B_JournalDataBrowseListByParamWrapper");
    }
}
