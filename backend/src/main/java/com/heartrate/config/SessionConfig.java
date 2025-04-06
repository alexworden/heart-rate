package com.heartrate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Session configuration for the application.
 * Following user rules:
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@Configuration
@EnableJdbcHttpSession(
    maxInactiveIntervalInSeconds = 1800, // 30 minutes
    tableName = "SPRING_SESSION"
)
public class SessionConfig {

    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean secure;

    @Value("${server.servlet.session.cookie.http-only:true}")
    private boolean httpOnly;

    @Value("${server.servlet.session.cookie.same-site:strict}")
    private String sameSite;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setUseSecureCookie(secure);
        serializer.setUseHttpOnlyCookie(httpOnly);
        serializer.setSameSite(sameSite);
        return serializer;
    }
}
