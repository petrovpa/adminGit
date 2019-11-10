package com.bivgroup.sessionutils;

import java.util.List;

public interface UserInfoWrapper {

    List<String> getGroups();

    String getLogin();

    long getLimit();

    void setParam(String s1, String s2);

}
