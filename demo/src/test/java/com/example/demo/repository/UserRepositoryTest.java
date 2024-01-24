package com.example.demo.repository;

import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRegisterRequestData_Save_ReturnSavedUserData() {

        // given - precondition or setup
        User user = User.builder()
                .username("testuser@email.com")
                .password("qwer1234!")
                .nickname("testuser")
                .phoneNumber("01012345678")
                .build();

        // when - action or the behaviour that we are going test

        // then - verify the output
    }
}