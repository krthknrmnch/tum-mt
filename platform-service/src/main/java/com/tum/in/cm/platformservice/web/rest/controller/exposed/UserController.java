package com.tum.in.cm.platformservice.web.rest.controller.exposed;

import com.tum.in.cm.platformservice.component.security.HasAuthorizedAdminRole;
import com.tum.in.cm.platformservice.component.security.JwtBuilder;
import com.tum.in.cm.platformservice.exception.CustomAlreadyExistsException;
import com.tum.in.cm.platformservice.exception.CustomAuthException;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.service.MeasurementService;
import com.tum.in.cm.platformservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.*;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private Environment environment;

    /**
     * Endpoint to request JWT
     */
    @Operation(summary = "Get a JWT token for user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "401", description = LOGIN_FAILED_MSG, content = @Content)})
    @GetMapping(value = "/users/request")
    @Tag(name = "Auth")
    public ResponseEntity<String> requestToken(@RequestHeader(value = "Email") String email,
                                               @RequestHeader(value = "Password") String password) throws CustomAuthException {
        JwtBuilder jwtBuilder = new JwtBuilder();
        String jwt;
        final String JWT_SECRET = environment.getProperty("auth.jwt.secret");
        try {
            if (!userService.existsByEmail(email)) {
                throw new CustomNotFoundException(USER_NOT_FOUND_MSG);
            }
            if (userService.isLoginSuccess(email, password)) {
                if (userService.isAdmin(email)) {
                    jwt = jwtBuilder.buildJwtForAdmin(email, JWT_SECRET);
                } else {
                    jwt = jwtBuilder.buildJwtForUser(email, JWT_SECRET);
                }
            } else {
                throw new CustomAuthException(LOGIN_FAILED_MSG);
            }
        } catch (CustomNotFoundException e) {
            throw new CustomAuthException(LOGIN_FAILED_MSG);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(TOKEN_HEADER, "Bearer " + jwt);
        responseHeaders.set(ACCESS_CONTROL_ALLOW_HEADERS, TOKEN_HEADER);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(SUCCESS_MSG);
    }

    /**
     * Endpoint to register user
     */
    @Operation(summary = "Create a new user with email, password, and isAdmin properties")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "401", description = REGISTRATION_FAILED_MSG, content = @Content)})
    @PostMapping(value = "/users")
    @HasAuthorizedAdminRole
    @SecurityRequirement(name = "Bearer_Authentication")
    @Tag(name = "Admin")
    public ResponseEntity<String> register(@RequestHeader(value = "Email") String email,
                                           @RequestHeader(value = "Password") String password,
                                           @RequestHeader(value = "isAdmin") boolean isAdmin) throws CustomAuthException {
        try {
            userService.attemptRegistration(email, password, isAdmin);
        } catch (CustomAlreadyExistsException e) {
            throw new CustomAuthException(REGISTRATION_FAILED_MSG);
        }
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }

    /**
     * Endpoint to update a User
     */
    @Operation(summary = "Update an existing user with a new password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "401", description = UPDATE_FAILED_MSG, content = @Content)})
    @PutMapping(value = "/users")
    @Tag(name = "User")
    public ResponseEntity<String> updateUser(@RequestHeader(value = "Email") String email,
                                             @RequestHeader(value = "Current-Password") String currentPassword,
                                             @RequestHeader(value = "New-Password") String newPassword) throws CustomAuthException {
        try {
            userService.updateUserByEmail(email, currentPassword, newPassword);
        } catch (CustomNotFoundException e) {
            throw new CustomAuthException(UPDATE_FAILED_MSG);
        }
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }

    /**
     * Endpoint to delete a User
     */
    @Operation(summary = "Delete an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "401", description = DELETE_FAILED_MSG, content = @Content)})
    @DeleteMapping(value = "/users")
    @Tag(name = "User")
    public ResponseEntity<String> deleteUser(@RequestHeader(value = "Email") String email,
                                             @RequestHeader(value = "Password") String password) throws CustomAuthException {
        try {
            userService.deleteByEmail(email, password);
            List<Measurement> measurements = measurementService.listByUserEmail(email);
            for (Measurement measurement : measurements) {
                measurementService.stopMeasurement(measurement);
            }
        } catch (CustomNotFoundException e) {
            throw new CustomAuthException(DELETE_FAILED_MSG);
        }
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }
}
