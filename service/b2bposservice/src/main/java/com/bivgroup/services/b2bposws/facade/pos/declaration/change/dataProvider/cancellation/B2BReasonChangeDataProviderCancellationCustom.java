package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.cancellation;

import com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.B2BReasonChangeDataProviderCustomFacade;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class B2BReasonChangeDataProviderCancellationCustom extends B2BReasonChangeDataProviderCustomFacade {
    protected static final Long REPLEVEL_FOR_SPECIFICATION_3 = 3000L;
    protected static final Long REPLEVEL_FOR_SPECIFICATION_3_ALT = 3001L;
    protected static final Long REPLEVEL_FOR_SPECIFICATION_3_ALT_2 = 3002L;
    protected static final String DOCUMENT_NAME_IDENTITY = "Копия документа, удостоверяющего личность";
    protected static final String DOCUMENT_NAME_CONTRACT = "Копия Договора страхования";

    protected Instant at0000(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    protected Instant at2359(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
    }

    /**
     * Метод формирования списка прилагаемых документов
     * TODO: пока что формируем его константно
     *
     * @param reportData
     * @param attachDocList
     * @param isNotExistContract
     */
    protected void formingAttachDocuments(Map<String, Object> reportData, List<Map<String, Object>> attachDocList,
                                          boolean isNotExistContract) {
        List<Map<String, Object>> reportAttachDocList = getOrCreateListParam(reportData, "attachDocList");
        String documentSelect = isNotExistContract ? FALSE_STR_VALUE : TRUE_STR_VALUE;
        Map<String, Object> item = new HashMap<>();
        item.put("name", DOCUMENT_NAME_IDENTITY);
        item.put("isSelect", documentSelect);
        reportAttachDocList.add(item);
        item = new HashMap<>();
        item.put("name", DOCUMENT_NAME_CONTRACT);
        item.put("isSelect", documentSelect);
        reportAttachDocList.add(item);
    }
}
