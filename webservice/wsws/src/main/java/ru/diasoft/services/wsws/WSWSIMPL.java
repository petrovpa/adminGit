package ru.diasoft.services.wsws;

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
//@javax.jws.WebService(wsdlLocation = "WEB-INF/wsdl/WSWS.wsdl",endpointInterface = "ru.diasoft.services.wsws.WSWSPORTTYPE")
@WebService(serviceName = "wsws", portName = "WSWSPORT", endpointInterface = "ru.diasoft.support.WSWSPORTTYPE", targetNamespace = "http://support.diasoft.ru", wsdlLocation = "WEB-INF/wsdl/WSWS.wsdl")
@HandlerChain(file = "handler-chain.xml")
public class WSWSIMPL extends WsImpl {

    public WSWSIMPL() {
        super("wsws");
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