package com.bivgroup.service;

import com.bivgroup.service.common.IsNeedLogging;

import java.text.DecimalFormat;
import java.util.Map;

public abstract class SoapServiceCaller implements IsNeedLogging {
    private static final String LOGIN = "INSSYSTEMLOGIN";
    private static final String PASSWORD = "INSSYSTEMPASSWORD";

    public abstract Map<String, Object> callExternalService(String moduleName, String methodName, Map<String, Object> params,
                                                            String login, String password);

    public Map<String, Object> callExternalService(String moduleName, String methodName, Map<String, Object> params) {
        String login = getStringParam(params.remove(LOGIN));
        String password = getStringParam(params.remove(PASSWORD));
        if ((login.isEmpty()) || (password.isEmpty())) {
            login = getLogin();
            password = getPassword();
        }
        return callExternalService(moduleName, methodName, params, login, password);
    }

    private String getStringParam(Object bean) {
        if (bean == null) {
            return "";
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.parseDouble(bean.toString()));
        } else {
            return bean.toString();
        }
    }

    public abstract String getLogin();

    public abstract String getPassword();

}
