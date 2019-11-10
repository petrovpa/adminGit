package com.bivgroup.rest.menuservice;

import com.bivgroup.service.UrlencodedServiceCallerInterface;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.bivgroup.rest.common.Constants.B2BPOSWS;
import static com.bivgroup.rest.common.Constants.FORM_PARAM_NAME;

@Path("/rest/menu")
public class MenuWebServiceGate {
    private final UrlencodedServiceCallerInterface serviceCaller;

    public MenuWebServiceGate() {
        this.serviceCaller = DefaultServiceLoader.loadServiceAny(UrlencodedServiceCallerInterface.class);
    }

    /**
     * С учетом профильного права
     */
    @POST
    @Path("/dsB2BMenuBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMenuBrowseEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return this.serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsMenuBrowseListByParam");
    }

    /**
     * Полное дерево меню
     */
    @POST
    @Path("/dsLoadFullMenu")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsLoadFullMenu(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return this.serviceCaller.callExternalService(paramsStr, B2BPOSWS, "dsLoadFullMenu");
    }
}
