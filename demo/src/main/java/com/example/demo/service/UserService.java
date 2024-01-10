package com.example.demo.service;

import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemovalResponseDTO;

public interface UserService {

    UserRegisterResponseDto register(UserRegisterDto userRegisterDTO);

    UserRemovalResponseDTO resign(String username);
}
