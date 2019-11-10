package com.bivgroup.service.password.service;

import com.bivgroup.pojo.JsonResult;
import com.bivgroup.pojo.Obj;
import com.bivgroup.service.SoapServiceCaller;
import com.bivgroup.service.UrlencodedServiceCallerInterface;
import com.bivgroup.service.password.common.Constants;
import com.bivgroup.utils.ParamGetter;
import com.bivgroup.utils.RequestWorker;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.service.password.common.Constants.B2BPOSWS;
import static com.bivgroup.service.password.common.Constants.ERROR_PARAMNAME;

@Path("/rest/pass")
public class AccountPassWebServiceGate {
    private UrlencodedServiceCallerInterface urlencodedServiceCaller;
    private SoapServiceCaller soapServiceCaller;
    private RequestWorker requestWorker;

    public AccountPassWebServiceGate() {
        this.urlencodedServiceCaller = DefaultServiceLoader.loadServiceAny(UrlencodedServiceCallerInterface.class);
        this.soapServiceCaller = DefaultServiceLoader.loadServiceAny(SoapServiceCaller.class);
        this.requestWorker = new RequestWorker();
    }

    @POST
    @Path("/dsB2BUserChangePass")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUserChangePass(@FormParam(Constants.FORM_PARAM_NAME) String paramsStr) {
        return this.urlencodedServiceCaller.callExternalService(paramsStr, B2BPOSWS, "dsB2BAccountChangePass");
    }

    @POST
    @Path("/dsB2BAdminCommonResetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminCommonResetPassword(@FormParam(Constants.FORM_PARAM_NAME) String paramStr) {
        return this.urlencodedServiceCaller.callExternalService(paramStr, B2BPOSWS, "dsB2BAdminCommonResetPassword");
    }

    @POST
    @Path("/dsResetPassword")
    @Produces("application/json")
    public Response dsResetPassword(@FormParam("params") String paramsStr) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        Map<String, Object> obj = new HashMap<>();
        String error = "";
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            obj = ParamGetter.getMapParamName(objMap, "obj");
            if (ParamGetter.getStringParam(obj, "LOGIN").isEmpty()) {
                error = "Отсутствует обязательный параметр: логин пользователя.";
            }
            if (ParamGetter.getStringParam(obj, "EMAIL").isEmpty()) {
                error = "Отсутствует обязательный параметр: почта пользователя.";
            }
        }
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result = this.soapServiceCaller.callExternalService(B2BPOSWS, "dsResetPassword", obj);
        } else {
            result.put("Status", "ERROR");
            result.put(ERROR_PARAMNAME, error);
        }
        JsonResult jsonResult = new JsonResult();
        this.requestWorker.serializeJSON(result, jsonResult);
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }
}
