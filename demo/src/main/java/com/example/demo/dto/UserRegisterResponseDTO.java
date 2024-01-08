package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserRegisterResponseDTO {

    private String id;

    private List<String> roleNames = new ArrayList<>();
}
