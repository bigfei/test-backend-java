package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.config.SecurityConfig;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Controller
public class AuthController {
    private final SecurityConfig config;
    private final ClientRegistration registration;

    public AuthController(ClientRegistrationRepository registrations, SecurityConfig config) {
        this.registration = registrations.findByRegistrationId("okta");
        this.config = config;
    }

    @GetMapping("/")
    public String home() {
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
        HashMap<String, String> result = restTemplate.postForObject("https://dev-wc-1.jp.auth0.com/oauth/token", request, HashMap.class);

        return result.get("access_token");
    }
}
