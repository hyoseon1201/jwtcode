package com.example.demo.service;

import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserRegisterResponseDTO;

public interface UserService {

    UserRegisterResponseDTO register(UserRegisterDTO userRegisterDTO);
}
