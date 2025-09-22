package com.aurora.ledger.infrastructure.bootstrap;

import com.aurora.ledger.application.service.UserService;
import com.aurora.ledger.domain.user.UserRegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initializes development data when 'dev' profile is active.
 * Uses UserService to register the user so PasswordEncoder is applied.
 */
@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevDataInitializer.class);

    private final UserService userService;

    public DevDataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            String login = "joao.silva";
            String document = "35060268870";
            String password = "SenhaForte123!";

            // Attempt to register  UserService will validate and encode password
            UserRegistrationRequest req = new UserRegistrationRequest("Joao Silva", document, login, password);
            if (req.isValid()) {
                try {
                    userService.registerUser(req);
                    logger.info("DevDataInitializer: test user created: {}", login);
                } catch (Exception e) {
                    // If user exists or any other error, log and continue
                    logger.warn("DevDataInitializer: could not create user (maybe exists): {}  {}", login, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("DevDataInitializer error", e);
        }
    }
}










