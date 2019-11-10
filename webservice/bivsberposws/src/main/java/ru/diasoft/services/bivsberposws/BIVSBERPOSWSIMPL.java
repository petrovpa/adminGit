package ru.diasoft.services.bivsberposws;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import ru.diasoft.services.inscore.WsImpl;
import ru.diasoft.support.ContextData;
import ru.diasoft.support.DSCALLFAULT_Exception;
import ru.diasoft.utils.exception.XMLParseException;


/**
 * This is the service blank implementation created by SOA system group Adjust
 * description of this service properly
 *
 * @author SOA system group
 */
//@javax.jws.WebService(wsdlLocation = "WEB-INF/wsdl/BIVSBERPOSWS.wsdl",endpointInterface = "ru.diasoft.services.bivsberposws.BIVSBERPOSWSPORTTYPE")
@WebService(serviceName = "bivsberposws", portName = "BIVSBERPOSWSPORT", endpointInterface = "ru.diasoft.support.BIVSBERPOSWSPORTTYPE", targetNamespace = "http://support.diasoft.ru", wsdlLocation = "WEB-INF/wsdl/BIVSBERPOSWS.wsdl")
@HandlerChain(file = "handler-chain.xml")
public class BIVSBERPOSWSIMPL extends WsImpl {

    public BIVSBERPOSWSIMPL() {
        super("bivsberposws");
    }

    public  void dscallasync(String commandtext, String commanddata, ContextData contextdata){
        dscallasync(commandtext, commanddata);
    }
    public  String dscall(String commandtext, String commanddata, ContextData contextdata) throws DSCALLFAULT_Exception{
        try {
            return this.dscall(commandtext, commanddata);
        } catch (XMLParseException ex) {
            throw new DSCALLFAULT_Exception("dscall exception", null, ex);
        }
    }    

}