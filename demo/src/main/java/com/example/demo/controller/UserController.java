package com.example.demo.controller;

import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemovalResponseDTO;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserRemovalResponseDTO> removalUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserRemovalResponseDTO userRemovalResponseDTO = userService.resign(username);

        return ResponseEntity.status(HttpStatus.OK).body(userRemovalResponseDTO);
    }


}
