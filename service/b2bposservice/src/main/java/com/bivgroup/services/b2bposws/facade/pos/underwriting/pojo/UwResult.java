package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

import com.bivgroup.core.dictionary.dao.jpa.RowStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UwResult {

    Long id;

    List<UwRiskDetail> uwRiskDetails = new ArrayList<>();

    public Map<String, Object> toEntity() {
        Map<String, Object> entity = new HashMap<>();
        entity.put("id", this.getId());
        List<Map<String, Object>> details = new ArrayList<>();
        for (UwRiskDetail uwRiskDetail : getUwRiskDetails()) {
            Map<String, Object> objectMap = uwRiskDetail.toEntity();
            details.add(objectMap);
        }
        entity.put("details", details);
        entity.put("rowStatus", RowStatus.MODIFIED.getId());
        return entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UwRiskDetail> getUwRiskDetails() {
        return uwRiskDetails;
    }

    public void setUwRiskDetails(List<UwRiskDetail> uwRiskDetails) {
        this.uwRiskDetails = uwRiskDetails;
    }
}
