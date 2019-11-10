package com.bivgroup.rest.admrestws.pojo.request.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountFilter extends Filter {
    private Long userAccountId;

    public AccountFilter() {
        super();
    }

    public AccountFilter(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public AccountFilter(String sysname, String operation, Integer accessMode, Integer anyValue, String values,
                         String keys, Long userAccountId) {
        super(sysname, operation, accessMode, anyValue, values, keys);
        this.userAccountId = userAccountId;
    }

    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }
}
