package com.example.demo.controller;

import com.example.demo.dto.study.*;
import com.example.demo.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get-study-list")
    public ResponseEntity<List<StudyListResponseDto>> findStudyList() {

        List<StudyListResponseDto> studyList = studyService.findStudyList();

        return ResponseEntity.status(HttpStatus.OK).body(studyList);
    }

    @GetMapping("/{studyId}/get-study")
    public ResponseEntity<StudyResponseDto> findStudy(@PathVariable Long studyId) {

        StudyResponseDto studyResponseDto = studyService.findStudy(studyId);

        return ResponseEntity.status(HttpStatus.OK).body(studyResponseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{studyId}/update-study")
    public ResponseEntity<StudyUpdateResponseDto> updateStudy(@PathVariable Long studyId, @RequestBody StudyUpdateRequestDto studyUpdateRequestDto) {

        StudyUpdateResponseDto updateStudy = studyService.updateStudy(studyId, studyUpdateRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(updateStudy);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{studyId}/delete-study")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId) {

        studyService.deleteStudy(studyId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{studyId}/join-study")
    public ResponseEntity<StudyJoinResponseDto> joinStudy(@PathVariable Long studyId) {

        StudyJoinResponseDto joinStudy = studyService.joinStudy(studyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(joinStudy);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{studyId}/resign-study")
    public ResponseEntity<Void> resignStudy(@PathVariable Long studyId) {

        studyService.resignStudy(studyId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
