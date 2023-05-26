package com.wiredcraft.wcapi.config;

import com.wiredcraft.wcapi.controller.LogoutController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Value(value = "${okta.oauth2.issuer}")
    private String domain;

    @Value(value = "${okta.oauth2.clientId}")
    private String clientId;

    @Value(value = "${okta.oauth2.clientSecret}")
    private String clientSecret;

    @Value(value = "${com.auth0.managementApi.clientId}")
    private String managementApiClientId;

    @Value(value = "${com.auth0.managementApi.clientSecret}")
    private String managementApiClientSecret;

    @Value(value = "${com.auth0.managementApi.grantType}")
    private String grantType;

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutController();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/", "/index.html", "*.ico", "*.css", "*.js").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults()))
                .logout((logout) ->
                        logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler()).permitAll()
                );
        return http.build();
    }

    public String getContextPath(HttpServletRequest request) {
        String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return path;
    }


    public String getUserInfoUrl() {
        return getDomain() + "userinfo";
    }

    public String getUsersUrl() {
        return getDomain() + "api/v2/users";
    }

    public String getUsersByEmailUrl() {
        return getDomain() + "api/v2/users-by-email?email=";
    }

    public String getLogoutUrl() {
        return getDomain() + "v2/logout";
    }

    public String getDomain() {
        return domain;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getManagementApiClientId() {
        return managementApiClientId;
    }

    public String getManagementApiClientSecret() {
        return managementApiClientSecret;
    }

    public String getGrantType() {
        return grantType;
    }
}
