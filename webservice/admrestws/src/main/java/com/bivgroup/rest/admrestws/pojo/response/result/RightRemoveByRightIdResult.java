package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-структура результат удаления права по идентификатору (RIGHTID)
 **/
public class RightRemoveByRightIdResult {

    @JsonProperty("RIGHTID")
    private Long rightId;

    public RightRemoveByRightIdResult() {
    }

    public RightRemoveByRightIdResult(Long rightId) {
        this.rightId = rightId;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    @Override
    public String toString() {
        return "RightRemoveByRightIdResult{" +
                "rightId=" + rightId +
                '}';
    }
}
