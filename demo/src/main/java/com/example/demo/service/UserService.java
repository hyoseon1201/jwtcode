package com.example.demo.service;

import com.example.demo.dto.account.*;

import java.util.List;

public interface UserService {

    UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto);

    UserRegisterResponseDto register(UserRegisterRequestDto userRegisterRequestDTO);

    UserRemoveResponseDTO resign();

    List<UserCreateStudyListResponseDto> findCreateStudyList();

    void checkEmailToken(String token, String username);
}
