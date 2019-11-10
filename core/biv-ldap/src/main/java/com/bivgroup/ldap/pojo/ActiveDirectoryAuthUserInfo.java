package com.bivgroup.ldap.pojo;

public class ActiveDirectoryAuthUserInfo {
    private String activeDirectoryLogin;
    private String userPrincipalName;
    private String password;

    public ActiveDirectoryAuthUserInfo() {

    }

    public ActiveDirectoryAuthUserInfo(String login, String password) {
        this.activeDirectoryLogin = login;
        this.password = password;
    }

    public String getActiveDirectoryLogin() {
        return activeDirectoryLogin != null ? activeDirectoryLogin : "";
    }

    public void setActiveDirectoryLogin(String activeDirectoryLogin) {
        this.activeDirectoryLogin = activeDirectoryLogin;
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserPrincipalName() {
        return userPrincipalName != null ? userPrincipalName : "";
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryAuthUserInfo{" +
                "activeDirectoryLogin='" + activeDirectoryLogin + '\'' +
                ", userPrincipalName='" + userPrincipalName + '\'' +
                '}';
    }
}
