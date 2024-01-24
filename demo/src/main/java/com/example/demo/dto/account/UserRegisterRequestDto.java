package com.example.demo.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDto {

    @Email
    private String username;

    @Size(min = 5, max = 12)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9!@#$%^&*?=+~]+$")
    private String password;

    @Size(min = 1, max = 7)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]+$")
    private String nickname;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phoneNumber;

    private List<String> roleNames = new ArrayList<>();
}
