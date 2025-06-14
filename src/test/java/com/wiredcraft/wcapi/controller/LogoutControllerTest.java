package com.wiredcraft.wcapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class LogoutControllerTest {

    private TestSecurityConfig securityConfig;
    private LogoutController logoutController;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Create mocks
        securityConfig = new TestSecurityConfig();

        // Create LogoutController and inject TestSecurityConfig via reflection
        logoutController = new LogoutController();
        // Since LogoutController uses @Autowired, we need to set the field directly for testing
        try {
            java.lang.reflect.Field configField = LogoutController.class.getDeclaredField("config");
            configField.setAccessible(true);
            configField.set(logoutController, securityConfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject TestSecurityConfig into LogoutController", e);
        }

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        authentication = mock(Authentication.class);

        // Setup common mock behaviors
        given(request.getScheme()).willReturn("http");
        given(request.getServerName()).willReturn("localhost");
        given(request.getServerPort()).willReturn(8080);
    }

    @Test
    void shouldPerformLogoutSuccessfullyWithSession() throws Exception {
        // Arrange
        given(request.getSession()).willReturn(session);
        String expectedLogoutUrl = "https://dev-wc-1.jp.auth0.com/v2/logout?client_id=test-client-id&returnTo=http://localhost:8080";

        // Act
        logoutController.onLogoutSuccess(request, response, authentication);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect(expectedLogoutUrl);
        // Note: Can't verify method calls on TestSecurityConfig since it's not a mock
    }

    @Test
    void shouldPerformLogoutSuccessfullyWithoutSession() throws Exception {
        // Arrange
        given(request.getSession()).willReturn(null);
        String expectedLogoutUrl = "https://dev-wc-1.jp.auth0.com/v2/logout?client_id=test-client-id&returnTo=http://localhost:8080";

        // Act
        logoutController.onLogoutSuccess(request, response, authentication);

        // Assert
        verify(session, never()).invalidate();
        verify(response).sendRedirect(expectedLogoutUrl);
        // Note: Can't verify method calls on TestSecurityConfig since it's not a mock
    }

    @Test
    void shouldHandleLogoutWithDifferentReturnUrl() throws Exception {
        // Arrange
        given(request.getSession()).willReturn(session);
        securityConfig.setTestContextPath("https://example.com");
        String expectedLogoutUrl = "https://dev-wc-1.jp.auth0.com/v2/logout?client_id=test-client-id&returnTo=https://example.com";

        // Act
        logoutController.onLogoutSuccess(request, response, authentication);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect(expectedLogoutUrl);
    }
}
