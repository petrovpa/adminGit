package com.bivgroup.rest.departmentservice;

import com.bivgroup.loader.ExternalServiceLoader;
import com.bivgroup.service.UrlencodedServiceCallerInterface;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.bivgroup.rest.common.Constants.B2BPOSWS;

@Path("/rest/department")
public class DepartmentWebServiceGate {
    private final UrlencodedServiceCallerInterface serviceCaller = ExternalServiceLoader.loadService(UrlencodedServiceCallerInterface.class);

    @POST
    @Path("/dsGetDepartmentList")
    @Produces("application/json")
    public Response dsGetDepartmentList(@FormParam("params") String paramStr) {
        return serviceCaller.callExternalService(paramStr, B2BPOSWS, "dsGetDepartmentList");
    }
}
