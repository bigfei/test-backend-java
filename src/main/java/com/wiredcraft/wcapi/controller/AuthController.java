package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.config.SecurityConfig;
import com.wiredcraft.wcapi.service.UserService;
import net.minidev.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Controller
public class AuthController {
    private final SecurityConfig config;
    private final ClientRegistration registration;

    private UserService userService;

    public AuthController(ClientRegistrationRepository registrations, SecurityConfig config, UserService userService) {
        this.registration = registrations.findByRegistrationId("okta");
        this.config = config;
        this.userService = userService;
    }

    /**
     * Redirect to login page
     * @return redirect url
     */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            userService.syncAuth0User(user);
        }
        return "home";
    }

    public String getManagementApiToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", config.getManagementApiClientId());
        requestBody.put("client_secret", config.getManagementApiClientSecret());
        requestBody.put("audience", "https://dev-wc-1.jp.auth0.com/api/v2/");
        requestBody.put("grant_type", "client_credentials");
        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(
                "https://dev-wc-1.jp.auth0.com/oauth/token",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<HashMap<String, String>>() {}
        );
        HashMap<String, String> result = response.getBody();

        return result.get("access_token");
    }
}
