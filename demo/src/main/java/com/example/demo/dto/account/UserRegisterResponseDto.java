package com.example.demo.dto.account;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserRegisterResponseDto {

    private String username;

    private String nickname;

    private String phoneNumber;

    private List<String> roleNames = new ArrayList<>();
}
