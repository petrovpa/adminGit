package com.bivgroup.login.base;

import com.bivgroup.sessionutils.UserInfoWrapper;
import com.bivgroup.utils.ParamGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bivgroup.login.base.common.Constants.USER_SESSION_COUNT_PARAMNAME;

public class ProfileDataWrapper implements UserInfoWrapper {

    private String login;
    private List<String> groups;
    private Map<String, Object> initMap;
    private long sessionCountLimit = 1;

    ProfileDataWrapper(Map<String, Object> user) {
        assert user != null;
        this.initMap = user;
        this.login = "";
        this.groups = new ArrayList<>();

        if (user.get("LOGIN") != null) {
            this.login = user.get("LOGIN").toString();
        }

        List<Map<String, Object>> userGroups = ParamGetter.getListParamName(user, "GROUPS");
        if (userGroups == null) return;
        for (Map<String, Object> group : userGroups) {
            String sysname = (String) group.getOrDefault("SYSNAME", "");
            this.groups.add(sysname);
        }

        long sc = Long.parseLong(user.getOrDefault(USER_SESSION_COUNT_PARAMNAME, "1").toString());
        this.sessionCountLimit = sc == 0L ? 1L : sc;
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setParam(String paramName, String value) {
        this.initMap.put(paramName, value);
    }

    @Override
    public long getLimit() {
        return this.sessionCountLimit;
    }


}
