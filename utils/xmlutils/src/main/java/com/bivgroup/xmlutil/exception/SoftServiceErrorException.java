package com.bivgroup.xmlutil.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SoftServiceErrorException extends XmlUtilException {
    private static final MessageFormat format = new MessageFormat("Error occured while calling service {0}!\nFaultCode={1}, FaultMessage=\"{2}\"\nErrorStack:\n=====================================================\n{3}");
    private final String serviceCall;
    private final String faultCode;
    private final String faultMessage;
    private final String errorDescription;

    public SoftServiceErrorException(String serviceCall, String faultCode, String faultMessage, String errorDescription) {
        super(format.format(new Object[]{serviceCall, faultCode, faultMessage, errorDescription}));
        this.serviceCall = serviceCall;
        this.faultCode = faultCode;
        this.faultMessage = faultMessage;
        this.errorDescription = errorDescription;
    }

    public String getServiceCall() {
        return this.serviceCall;
    }

    public String getFaultCode() {
        return this.faultCode;
    }

    public String getFaultMessage() {
        return this.faultMessage;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public Map<String, Object> getErrorData() {
        Map<String, Object> result = new HashMap();
        result.put("Result", "ERROR");
        result.put("Status", "ERROR");
        result.put("lastErrorCode", this.faultCode);
        result.put("lastErrorMessage", this.faultMessage);
        result.put("lastErrorDescription", this.errorDescription);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        pw.flush();
        pw.close();

        try {
            sw.close();
        } catch (IOException var5) {
            ;
        }

        result.put("lastErrorException", sw.getBuffer().toString());
        result.put("FAULTCODE", this.faultCode);
        result.put("FAULTMESSAGE", this.faultMessage);
        result.put("ErrorDescription", this.errorDescription);
        return result;
    }
}

