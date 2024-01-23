package com.example.demo.service;

import com.example.demo.dto.account.*;
import com.example.demo.entity.User;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

public interface UserService {

    UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto);

    UserRegisterResponseDto register(UserRegisterDto userRegisterDTO);

    UserRemoveResponseDTO resign();

    List<UserCreateStudyListResponseDto> findCreateStudyList();

    void checkEmailToken(String token, String username);
}
