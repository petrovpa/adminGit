package com.bivgroup.xmlutil.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoftServiceErrorsListException extends XmlUtilException {
    private static final MessageFormat format = new MessageFormat("Error occured while calling service {0}!\nFaultCodes={1}, FaultMessages=\"{2}\"\nErrorStack:\n=====================================================\n{3}");
    private final String serviceCall;
    private final List<String> faultCodes;
    private final List<String> faultMessages;
    private final List<String> errorDescriptions;

    public SoftServiceErrorsListException(String serviceCall, List<String> faultCodes, List<String> faultMessages, List<String> errorDescriptions) {
        super(format.format(new Object[]{serviceCall, faultCodes, faultMessages, errorDescriptions}));
        this.serviceCall = serviceCall;
        this.faultCodes = new ArrayList(faultCodes);
        this.faultMessages = new ArrayList(faultMessages);
        this.errorDescriptions = new ArrayList(errorDescriptions);
    }

    public String getServiceCall() {
        return this.serviceCall;
    }

    public List<String> getFaultCodes() {
        return this.faultCodes;
    }

    public String getFaultCode(int i) {
        return (String)this.faultCodes.get(i);
    }

    public List<String> getFaultMessages() {
        return this.faultMessages;
    }

    public String getFaultMessage(int i) {
        return (String)this.faultMessages.get(i);
    }

    public List<String> getErrorDescriptions() {
        return this.errorDescriptions;
    }

    public String getErrorDescription(int i) {
        return (String)this.errorDescriptions.get(i);
    }

    public List<Map<String, Object>> getErrorData() {
        List<Map<String, Object>> result = new ArrayList();

        for(int i = 0; i < this.faultCodes.size(); ++i) {
            HashMap<String, Object> resultRow = new HashMap();
            resultRow.put("Result", "ERROR");
            resultRow.put("Status", "ERROR");
            resultRow.put("lastErrorCode", this.faultCodes.get(i));
            resultRow.put("lastErrorMessage", this.faultMessages.get(i));
            resultRow.put("lastErrorDescription", this.errorDescriptions.get(i));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            this.printStackTrace(pw);
            pw.flush();
            pw.close();

            try {
                sw.close();
            } catch (IOException var7) {
                ;
            }

            resultRow.put("lastErrorException", sw.getBuffer().toString());
            resultRow.put("FAULTCODE", this.faultCodes.get(i));
            resultRow.put("FAULTMESSAGE", this.faultMessages.get(i));
            resultRow.put("ErrorDescription", this.errorDescriptions.get(i));
        }

        return result;
    }
}
