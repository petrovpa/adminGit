package com.bivgroup.login.base.pojo;

public class AuthUserInfo {
    private String login;
    private String b2bLogin;
    private String password;
    private String projectName;
    private Long userAccountId;
    private String hash;
    private Long departmentId;
    private Long userTypeId;
    private boolean isNeedChangePwd;

    public AuthUserInfo() {

    }

    public AuthUserInfo(String login, String password, String projectName, Long userAccountId, boolean isNeedChangePwd) {
        this.login = login;
        this.password = password;
        this.projectName = projectName;
        this.userAccountId = userAccountId;
        this.isNeedChangePwd = isNeedChangePwd;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Long userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getB2bLogin() {
        return b2bLogin;
    }

    public void setB2bLogin(String b2bLogin) {
        this.b2bLogin = b2bLogin;
    }

    public boolean isNeedChangePwd() {
        return isNeedChangePwd;
    }

    public void setNeedChangePwd(boolean needChangePwd) {
        isNeedChangePwd = needChangePwd;
    }

}
