package com.wiredcraft.wcapi.config;

import com.wiredcraft.wcapi.controller.LogoutController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();

        // Set up test values using reflection
        ReflectionTestUtils.setField(securityConfig, "domain", "https://dev-wc-1.jp.auth0.com/");
        ReflectionTestUtils.setField(securityConfig, "clientId", "test-client-id");
        ReflectionTestUtils.setField(securityConfig, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(securityConfig, "managementApiClientId", "test-mgmt-client-id");
        ReflectionTestUtils.setField(securityConfig, "managementApiClientSecret", "test-mgmt-client-secret");
        ReflectionTestUtils.setField(securityConfig, "grantType", "client_credentials");
    }

    @Test
    void shouldCreateLogoutSuccessHandler() {
        LogoutSuccessHandler handler = securityConfig.logoutSuccessHandler();

        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(LogoutController.class);
    }

    @Test
    void shouldGetContextPath() {
        given(request.getScheme()).willReturn("https");
        given(request.getServerName()).willReturn("localhost");
        given(request.getServerPort()).willReturn(8080);

        String contextPath = securityConfig.getContextPath(request);

        assertThat(contextPath).isEqualTo("https://localhost:8080");
    }

    @Test
    void shouldGetContextPathWithHttpAndPort80() {
        given(request.getScheme()).willReturn("http");
        given(request.getServerName()).willReturn("example.com");
        given(request.getServerPort()).willReturn(80);

        String contextPath = securityConfig.getContextPath(request);

        assertThat(contextPath).isEqualTo("http://example.com:80");
    }

    @Test
    void shouldGetUserInfoUrl() {
        String userInfoUrl = securityConfig.getUserInfoUrl();

        assertThat(userInfoUrl).isEqualTo("https://dev-wc-1.jp.auth0.com/userinfo");
    }

    @Test
    void shouldGetUsersUrl() {
        String usersUrl = securityConfig.getUsersUrl();

        assertThat(usersUrl).isEqualTo("https://dev-wc-1.jp.auth0.com/api/v2/users");
    }

    @Test
    void shouldGetUsersByEmailUrl() {
        String usersByEmailUrl = securityConfig.getUsersByEmailUrl();

        assertThat(usersByEmailUrl).isEqualTo("https://dev-wc-1.jp.auth0.com/api/v2/users-by-email?email=");
    }

    @Test
    void shouldGetLogoutUrl() {
        String logoutUrl = securityConfig.getLogoutUrl();

        assertThat(logoutUrl).isEqualTo("https://dev-wc-1.jp.auth0.com/v2/logout");
    }

    @Test
    void shouldGetDomain() {
        String domain = securityConfig.getDomain();

        assertThat(domain).isEqualTo("https://dev-wc-1.jp.auth0.com/");
    }

    @Test
    void shouldGetClientId() {
        String clientId = securityConfig.getClientId();

        assertThat(clientId).isEqualTo("test-client-id");
    }

    @Test
    void shouldGetClientSecret() {
        String clientSecret = securityConfig.getClientSecret();

        assertThat(clientSecret).isEqualTo("test-client-secret");
    }

    @Test
    void shouldGetManagementApiClientId() {
        String managementApiClientId = securityConfig.getManagementApiClientId();

        assertThat(managementApiClientId).isEqualTo("test-mgmt-client-id");
    }

    @Test
    void shouldGetManagementApiClientSecret() {
        String managementApiClientSecret = securityConfig.getManagementApiClientSecret();

        assertThat(managementApiClientSecret).isEqualTo("test-mgmt-client-secret");
    }

    @Test
    void shouldGetGrantType() {
        String grantType = securityConfig.getGrantType();

        assertThat(grantType).isEqualTo("client_credentials");
    }

    @Test
    void shouldHandleNullDomain() {
        ReflectionTestUtils.setField(securityConfig, "domain", null);

        String userInfoUrl = securityConfig.getUserInfoUrl();
        assertThat(userInfoUrl).isEqualTo("nulluserinfo");

        String usersUrl = securityConfig.getUsersUrl();
        assertThat(usersUrl).isEqualTo("nullapi/v2/users");
    }
}
