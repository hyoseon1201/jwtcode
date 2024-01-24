package com.example.demo.controller;

import com.example.demo.dto.account.*;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(userLoginResponseDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserRegisterResponseDto> generationUser(@RequestBody UserRegisterRequestDto userRegisterRequestDTO) {

        UserRegisterResponseDto userRegisterResponseDTO = userService.register(userRegisterRequestDTO);

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

    @GetMapping("/check-email-token")
    public ResponseEntity<Boolean> checkEmailToken(@RequestParam(value = "token") String token, @RequestParam(value = "username") String username) {
        userService.checkEmailToken(token, username);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }


}
