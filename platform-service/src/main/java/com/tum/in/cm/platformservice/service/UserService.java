package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomAlreadyExistsException;
import com.tum.in.cm.platformservice.exception.CustomAuthException;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.user.User;
import com.tum.in.cm.platformservice.repository.primary.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.tum.in.cm.platformservice.util.Constants.*;
import static org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256;

/**
 * User service containing business logic for managing users.
 * Passwords are encrypted and stored with PBKDF2 encryption using HmacSHA256.
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Environment environment;

    public User findByEmail(String email) throws CustomNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new CustomNotFoundException(USER_NOT_FOUND_MSG);
        }
        return user;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null && user.isAdmin();
    }

    public User insert(User user) throws CustomAlreadyExistsException {
        if (existsByEmail(user.getEmail())) {
            throw new CustomAlreadyExistsException(USER_ALREADY_EXISTS_MSG);
        }
        userRepository.insert(user);
        log.info("Inserted User with Email: " + user.getEmail());
        return user;
    }

    public void update(User updatedUser) throws CustomNotFoundException {
        User user = this.findByEmail(updatedUser.getEmail());
        updatedUser.setId(user.getId());
        userRepository.save(updatedUser);
        log.info("Updated User with Email: " + updatedUser.getEmail());
    }

    /**
     * Method attempts to update existing user.
     * Password is encrypted.
     */
    public void updateUserByEmail(String email, String currentPassword, String newPassword) throws CustomNotFoundException, CustomAuthException {
        User user = this.findByEmail(email);
        final String PBKDF2_SALT = environment.getProperty("auth.encryption.salt");
        final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(PBKDF2_SALT, PBKDF2_SALT_LENGTH, PBKDF2_ITERATIONS, PBKDF2WithHmacSHA256);
        if (isLoginSuccess(email, currentPassword)) {
            pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
            user.setPassword(pbkdf2PasswordEncoder.encode(newPassword));
            this.update(user);
        } else {
            throw new CustomAuthException(LOGIN_FAILED_MSG);
        }
    }

    /**
     * Method attempts to create new user.
     * Password is encrypted.
     */
    public void attemptRegistration(String email, String password, boolean isAdmin) throws CustomAlreadyExistsException {
        final String PBKDF2_SALT = environment.getProperty("auth.encryption.salt");
        final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(PBKDF2_SALT, PBKDF2_SALT_LENGTH, PBKDF2_ITERATIONS, PBKDF2WithHmacSHA256);
        pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
        User user = new User();
        user.setEmail(email);
        user.setPassword(pbkdf2PasswordEncoder.encode(password));
        user.setAdmin(isAdmin);
        this.insert(user);
    }

    /**
     * Method attempts to check if email and password combination exists.
     */
    public boolean isLoginSuccess(String email, String password) throws CustomNotFoundException {
        final String PBKDF2_SALT = environment.getProperty("auth.encryption.salt");
        final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(PBKDF2_SALT, PBKDF2_SALT_LENGTH, PBKDF2_ITERATIONS, PBKDF2WithHmacSHA256);
        pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
        User user = this.findByEmail(email);
        return pbkdf2PasswordEncoder.matches(password, user.getPassword());
    }

    public void deleteByEmail(String email, String password) throws CustomNotFoundException, CustomAuthException {
        if (isLoginSuccess(email, password)) {
            userRepository.deleteByEmail(email);
            log.info("Deleted User with Email: " + email);
        } else {
            throw new CustomAuthException(DELETE_FAILED_MSG);
        }
    }

    //Used in tests
    public void deleteAll() {
        userRepository.deleteAll();
        log.info("Deleted All Users");
    }
}
