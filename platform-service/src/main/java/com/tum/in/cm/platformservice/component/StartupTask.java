package com.tum.in.cm.platformservice.component;

import com.tum.in.cm.platformservice.exception.CustomAlreadyExistsException;
import com.tum.in.cm.platformservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This class executes startup tasks once Spring context has been initialized.
 */
@Component
@Slf4j
public class StartupTask implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Startup tasks starting");
        //TODO: Future - Change mechanism for admin creation - Move to CI system
        String adminEmail = "admin@admin.com";
        String adminPassword = environment.getProperty("auth.admin.password");
        boolean isAdmin = true;
        try {
            userService.attemptRegistration(adminEmail, adminPassword, isAdmin);
        } catch (CustomAlreadyExistsException e) {
            //Ignore exception, admin already exists
        }
    }
}
