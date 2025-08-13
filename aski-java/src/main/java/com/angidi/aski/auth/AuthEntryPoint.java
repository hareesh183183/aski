package com.angidi.aski.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject json = new JSONObject();
        json.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        json.put("message", "Unauthorized");
        json.put("path", request.getServletPath());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json.toString());
    }
}
