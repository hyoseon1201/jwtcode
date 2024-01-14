package com.example.demo.service;

import com.example.demo.dto.account.UserCreateStudyListResponseDto;
import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemoveResponseDTO;
import com.example.demo.entity.User;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

public interface UserService {

    UserRegisterResponseDto register(UserRegisterDto userRegisterDTO);

    UserRemoveResponseDTO resign();

    List<UserCreateStudyListResponseDto> findCreateStudyList();

    void checkEmailToken(String token, String username);
}
