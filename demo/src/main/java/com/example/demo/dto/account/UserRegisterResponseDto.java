package com.example.demo.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterResponseDto {

    private String username;

    private String nickname;

    private String phoneNumber;

    private List<String> roleNames = new ArrayList<>();
}
