package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.config.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Test implementation of SecurityConfig for use in unit tests.
 * This avoids the need to mock SecurityConfig, which causes issues with Java 24 and Byte Buddy.
 */
public class TestSecurityConfig extends SecurityConfig {

    private String testContextPath = "http://localhost:8080";
    private String testLogoutUrl = "https://dev-wc-1.jp.auth0.com/v2/logout";
    private String testClientId = "test-client-id";
    private String testManagementApiClientId = "test-management-client-id";
    private String testManagementApiClientSecret = "test-management-client-secret";

    @Override
    public String getContextPath(HttpServletRequest request) {
        if (testContextPath != null) {
            return testContextPath;
        }
        // Default behavior - reconstruct from request
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    @Override
    public String getLogoutUrl() {
        return testLogoutUrl;
    }

    @Override
    public String getClientId() {
        return testClientId;
    }

    @Override
    public String getManagementApiClientId() {
        return testManagementApiClientId;
    }

    @Override
    public String getManagementApiClientSecret() {
        return testManagementApiClientSecret;
    }

    // Allow tests to override values
    public void setTestContextPath(String contextPath) {
        this.testContextPath = contextPath;
    }

    public void setTestLogoutUrl(String logoutUrl) {
        this.testLogoutUrl = logoutUrl;
    }

    public void setTestClientId(String clientId) {
        this.testClientId = clientId;
    }

    public void setTestManagementApiClientId(String clientId) {
        this.testManagementApiClientId = clientId;
    }

    public void setTestManagementApiClientSecret(String clientSecret) {
        this.testManagementApiClientSecret = clientSecret;
    }
}
