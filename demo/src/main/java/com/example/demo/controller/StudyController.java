package com.example.demo.controller;

import com.example.demo.dto.study.StudyCreateResponseDto;
import com.example.demo.dto.study.StudyCreateRequestDto;
import com.example.demo.dto.study.StudyRequestDto;
import com.example.demo.dto.study.StudyResponseDto;
import com.example.demo.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create-study")
    public ResponseEntity<StudyCreateResponseDto> createStudy(@RequestBody StudyCreateRequestDto studyCreateRequestDto) {

        StudyCreateResponseDto createStudy = studyService.createStudy(studyCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createStudy);
    }

    @GetMapping("/get-study")
    public ResponseEntity<StudyResponseDto> findStudy(@RequestBody StudyRequestDto studyRequestDto) {

        StudyResponseDto studyResponseDto = studyService.findStudy(studyRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(studyResponseDto);
    }
}
