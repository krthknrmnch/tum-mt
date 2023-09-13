package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomAlreadyExistsException;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tum.in.cm.platformservice.util.Constants.USER_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userService.deleteAll();
    }

    @AfterEach
    public void teardown() {
        userService.deleteAll();
    }

    //Base Test
    @Test
    public void testInsert() throws CustomAlreadyExistsException {
        User user = new User();
        user.setEmail("Test");
        User resultUser = userService.insert(user);
        assertThat(resultUser.getEmail()).isEqualTo("Test");
    }

    //Base Test
    @Test
    public void testNotFoundException() {
        assertThatThrownBy(() -> {
            User user = userService.findByEmail("Non_Existent_Id");
        }).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_MSG);
    }
}
