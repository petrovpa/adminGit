package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("OpenApiDataProvider")
public class OpenApiDataProviderFacade extends B2BLifeBaseFacade {

    public Map<String, Object> dsB2BOpenApiPrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> docDataCall = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BSberLifePrintDocDataProvider", params, login, password);

        if (docDataCall.get("Status").equals("OK")) {
            Map<String, Object> reportData = getMapParam(docDataCall, "Result");
            Date documentDate = getDateParam(reportData.get("DOCUMENTDATE"));
            Locale russianLocale = new Locale("ru");
            DateFormatSymbols russianDateFormatSymbols = DateFormatSymbols.getInstance(russianLocale);
            russianDateFormatSymbols.setMonths(MONTHS_FOR_STRING_DATE);
            SimpleDateFormat dateFormatterMonth = new SimpleDateFormat("«dd» MMMMM yyyy", russianLocale);
            dateFormatterMonth.setDateFormatSymbols(russianDateFormatSymbols);
            reportData.put("DOCUMENTDATEMONTHLYSTR", dateFormatterMonth.format(documentDate));

            reportData.put("PAYMENTVALUE", reportData.get("PREMVALUE"));
            // FIXME: раскомментить когда будет понятно из какого поля брать
            //reportData.put("INSTARIFVALUE", "");
            //reportData.put("FIRSTPAYDATE", "");

            Map<String, Object> contrExtMap = getMapParam(reportData, "CONTREXTMAP");
            if (contrExtMap != null) {
                contrExtMap.put("CREDITCONTRNUM", contrExtMap.get("creditContractNumber"));
                contrExtMap.put("CREDITCONTRDATE", contrExtMap.get("creditContractDATE"));
            }
        }

        return docDataCall;
    }
}
