package com.example.demo.controller;

import com.example.demo.dto.account.UserCreateStudyListResponseDto;
import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemoveResponseDTO;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserRegisterResponseDto> generationUser(@RequestBody UserRegisterDto userRegisterDTO) {

        UserRegisterResponseDto userRegisterResponseDTO = userService.register(userRegisterDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRegisterResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/resign")
    public ResponseEntity<UserRemoveResponseDTO> removalUser() {

        UserRemoveResponseDTO userRemoveResponseDTO = userService.resign();

        return ResponseEntity.status(HttpStatus.OK).body(userRemoveResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/created-study-list")
    public ResponseEntity<List<UserCreateStudyListResponseDto>> findCreatedStudies() {

        List<UserCreateStudyListResponseDto> studyList = userService.findCreateStudyList();

        return ResponseEntity.status(HttpStatus.OK).body(studyList);
    }

}
