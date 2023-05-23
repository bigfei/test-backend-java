package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.config.SecurityConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LogoutController implements LogoutSuccessHandler {

    @Autowired
    private SecurityConfig config;

    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        if (req.getSession() != null) {
            req.getSession().invalidate();
        }
        String returnTo = config.getContextPath(req);
        String logoutUrl = config.getLogoutUrl() + "?client_id=" + config.getClientId() + "&returnTo=" +returnTo;
        res.sendRedirect(logoutUrl);
    }
}