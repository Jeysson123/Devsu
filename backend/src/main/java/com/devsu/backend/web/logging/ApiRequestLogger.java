package com.devsu.backend.web.logging;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Interceptor that logs HTTP request method, URI, response status, and timestamp for API calls. */
@Component
public class ApiRequestLogger implements HandlerInterceptor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String action = request.getMethod() + " " + request.getRequestURI();

        String date = LocalDateTime.now().format(formatter);

        System.out.println(String.format("ACTION: %s | RESULT (CODE): %s | DATE: %s", action, response.getStatus(), date));

        return true;
    }
}