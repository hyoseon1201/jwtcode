package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserRegisterDTO {

    private String id;

    private String password;

    private List<String> roleNames = new ArrayList<>();
}
