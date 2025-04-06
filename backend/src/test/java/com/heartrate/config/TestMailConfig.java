package com.heartrate.config;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test mail configuration using GreenMail.
 * Following user rules:
 * - Using reusable test infrastructure instead of mocks
 * - Application-level validation
 * - Fail fast approach
 */
@TestConfiguration
@Profile("test")
public class TestMailConfig {

    @Bean
    public GreenMail greenMail() {
        ServerSetup serverSetup = new ServerSetup(3025, "localhost", ServerSetup.PROTOCOL_SMTP);
        GreenMail greenMail = new GreenMail(serverSetup)
                .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"));
        greenMail.start();
        return greenMail;
    }
}
