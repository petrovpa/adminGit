package com.bivgroup.integrationservice.facade.integration;

import ru.diasoft.services.inscore.facade.RowStatus;
import ru.sberinsur.esb.partner.shema.ASalesServOnlineType;
import ru.sberinsur.esb.partner.shema.ThirdParty;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bivgroup.integrationservice.facade.integration.LifeLKUserRegistratorFacade.normalizePhoneNumber;

public class PPOOnlineHelper {

    private static final String PPO_ONLINE_FIELDNAME = "ppoOnlineType";
    private static final String PREV_PPO_ONLINE_FIELDNAME = "prevPpoOnlineType";
    private static final String MOBILE_PHONE_HB_SYSNAME = "MobilePhone";

    static Boolean phonesExists(ThirdParty party, Map<String, Object> client) {
        int phonesCounter = 0;
        if (party.getPhoneMobile() != null && party.getPhoneMobile().trim().length() != 0) {
            phonesCounter++;
        }
        List<Map<String, Object>> contacts = (List<Map<String, Object>>) client.get("contacts");
        List<Map<String, Object>> mobileContacts = contacts
                .stream()
                .filter((Map<String, Object> contact) -> new MobileContactFilter().test(contact)).collect(Collectors.toList());
        return ((phonesCounter + mobileContacts.size()) > 0);
    }

    public static void turnOnPPoOnline(Map<String, Object> profile, String ppoType) {
        profile.put(PPO_ONLINE_FIELDNAME, ppoType);
        profile.put(PREV_PPO_ONLINE_FIELDNAME, ppoType);
    }

    public static void turnOffPPoOnline(Map<String, Object> profile) {
        profile.put(PPO_ONLINE_FIELDNAME, ASalesServOnlineType.NONE.value());
    }

    static void processPhoneNumbers(ThirdParty party, Map<String, Object> clientProfile) {

        String phoneMobile = Optional.ofNullable(party.getPhoneMobile()).orElse(null);
        Map<String, Object> client = (Map<String, Object>) clientProfile.get("clientId_EN");

        if (phoneMobile != null) {
            phoneMobile = normalizePhoneNumber(phoneMobile);
            List<Map<String, Object>> contacts = (List<Map<String, Object>>) client.get("contacts");
            // надо пройтись по контактам и найти где у нас валяется контакт из ППО по полю value
            String finalPhoneMobile = phoneMobile;
            Map<String, Object> complimentaryContact = contacts.stream().filter((Map<String, Object> contact) -> new CompPhoneFilter(finalPhoneMobile).test(contact)).findFirst().orElse(null);
            if (complimentaryContact != null) {
                setContactAsPpoPrimary(complimentaryContact);
                turnOnPPoOnline(clientProfile, ASalesServOnlineType.YES_EMPLOYEE.value());
            } else { //применяем флаг isPpoPrimary для первого попавшегося, например для того, который уже isPrimary
                Map<String, Object> primaryContact = contacts.stream().filter((Map<String, Object> contact) -> new PrimaryContactFilter().test(contact)).findFirst().orElse(null);
                if (primaryContact != null) {
                    setContactAsPpoPrimary(primaryContact);
                    turnOnPPoOnline(clientProfile, ASalesServOnlineType.YES_EMPLOYEE.value());
                }
            }
        }
    }

    private static void setContactAsPpoPrimary(Map<String, Object> contact) {
        contact.put("isPpoPrimary", 1);
        contact.put("isPrimary", 1);
        if (contact.containsKey("id")) {
            contact.put(RowStatus.ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
        } else {
            contact.put(RowStatus.ROWSTATUS_PARAM_NAME, RowStatus.INSERTED.getId());
        }
    }

    private static class PrimaryContactFilter implements Predicate {

        @Override
        public boolean test(Object o) {
            Map<String, Object> contact = (Map<String, Object>) o;
            String isPs = contact.getOrDefault("isPrimary", "0").toString();
            int isP = Integer.parseInt(isPs);
            return isP == 1;
        }
    }

    private static class MobileContactFilter implements Predicate {

        @Override
        public boolean test(Object o) {
            Map<String, Object> contact = (Map<String, Object>) o;
            Map<String, Object> typeIdEn = Optional.ofNullable((Map<String, Object>) contact.get("typeId_EN")).orElse(null);
            if (typeIdEn != null) {
                String sysName = (String) typeIdEn.get("sysname");
                if (sysName.equalsIgnoreCase(MOBILE_PHONE_HB_SYSNAME)) return true;
            }
            return false;
        }
    }


    // фильтр комплиментарных контактов
    private static class CompPhoneFilter implements Predicate {

        private final String phone;

        public CompPhoneFilter(String phone) {
            this.phone = phone;
        }

        @Override
        public boolean test(Object contact) {
            Map<String, Object> contactMap = (Map<String, Object>) contact;
            String value = Optional.ofNullable((String) contactMap.get("value")).orElse("").trim();
            return value.equalsIgnoreCase(phone);
        }
    }


}
