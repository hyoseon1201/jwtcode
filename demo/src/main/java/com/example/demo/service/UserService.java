package com.example.demo.service;

import com.example.demo.dto.account.UserCreateStudyListResponseDto;
import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemoveResponseDTO;

import java.util.List;

public interface UserService {

    UserRegisterResponseDto register(UserRegisterDto userRegisterDTO);

    UserRemoveResponseDTO resign();

    List<UserCreateStudyListResponseDto> findCreateStudyList();
}
