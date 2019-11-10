package com.bivgroup.services.b2bposws.facade.pos.declaration.change.utils;

import com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.B2BReasonChangeDataProviderCustomFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@BOName("ClientManagerDataProvider")
public class ClientManagerDataProviderFacade extends B2BReasonChangeDataProviderCustomFacade {
    private static Map<String, Object> facrAddrType;

    static {
        facrAddrType = new HashMap<>();
        facrAddrType.put("eId", 1005);
        facrAddrType.put("id", 1005);
        facrAddrType.put("name", "Адрес проживания");
        facrAddrType.put("sysname", "FactAddress");
    }

    /**
     * Метод формирование данных о клиенском менеджере для ПФ
     *
     * @return
     */
    @WsMethod()
    public Map<String, Object> formationClientManagerData(Map<String, Object> params) throws Exception {
        Map<String, Object> reportData = getOrCreateMapParam(params, "REPORTDATA");
        Map<String, Object> docReceiptMap = getOrCreateMapParam(params, "docReceiptMap");
        Map<String, Object> clientManager = null, address = null;
        if (!docReceiptMap.isEmpty()) {
            clientManager = getOrCreateMapParam(docReceiptMap, "clientManager");
            //<editor-fold defaultstate="collapsed" desc="TODO: возможно нужно будет перевести в отдельное место, для формировании данных об адресе направления корреспонденции">
            Map<String, Object> interfaceAddress = getOrCreateMapParam(docReceiptMap, "address");
            if (!interfaceAddress.isEmpty()) {
                List<Map<String, Object>> addresses = new ArrayList<>();
                interfaceAddress.put("typeId_EN", facrAddrType);
                addresses.add(interfaceAddress);
                final Map<String, Object> interfaceAddressResolve = resolveClientAddressList(addresses).get(0);
                Map<String, Object> insurerMap = getOrCreateMapParam(reportData, "INSURERMAP");
                List<Map<String, Object>> addressList = getOrCreateListParam(insurerMap, "addressList");
                addressList = addressList.stream().map(item -> {
                    if (getStringParam(item, "ADDRESSTYPESYSNAME").equals("FactAddress")) {
                        return interfaceAddressResolve;
                    } else {
                        return item;
                    }
                }).collect(Collectors.toList());
                insurerMap.put("addressList", addressList);
                address = interfaceAddressResolve;
            }
            //</editor-fold>
        }
        if (clientManager == null || clientManager.isEmpty()) {
            Long clientId = getLongParam(params, "clientId");
            Map<String, Object> findClient = dctFindById(CLIENT_ENTITY_NAME, clientId);
            clientManager = getOrCreateMapParam(findClient, "clientManagerId_EN");
            address = getOrCreateMapParam(clientManager, "addressBankId_EN");
        }
        formationCreateUserMap(reportData, clientManager);
        formationInsBankMap(reportData, address, clientManager);

        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, reportData);
        return result;
    }

    private void formationCreateUserMap(Map<String, Object> reportData, Map<String, Object> clientManager) {
        Map<String, Object> createUser = getOrCreateMapParam(reportData, "CREATEUSERMAP");
        createUser.put("FIRSTNAME", getStringParam(clientManager, "name"));
        createUser.put("MIDDLENAME", getStringParam(clientManager, "patronymic"));
        createUser.put("LASTNAME", getStringParam(clientManager, "surname"));
        createUser.put("PHONE1", getStringParam(clientManager, "tel"));
    }

    private void formationInsBankMap(Map<String, Object> reportData, Map<String, Object> bankAddress, Map<String, Object> clientManager) {
        Map<String, Object> insBankMap = getOrCreateMapParam(reportData, "INSBANKMAP");
        String postalCode = getStringParam(bankAddress, "postcode");
        String addressText = getStringParam(bankAddress, "address");
        if (!postalCode.isEmpty() && addressText.startsWith(postalCode)) {
            addressText = addressText.replaceFirst(postalCode + ", ", "");
        }
        insBankMap.put("address", addressText);
        insBankMap.put("filialNum", getStringParam(clientManager, "bankCode"));
    }
}
