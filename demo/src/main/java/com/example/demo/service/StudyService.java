package com.example.demo.service;

import com.example.demo.dto.study.*;

import java.util.List;

public interface StudyService {

    StudyCreateResponseDto createStudy(StudyCreateRequestDto studyCreateRequestDto);

    StudyResponseDto findStudy(Long studyId);

    List<StudyListResponseDto> findStudyList();

    StudyUpdateResponseDto updateStudy(Long studyId, StudyUpdateRequestDto studyUpdateRequestDto);

    void deleteStudy(Long studyId);

    StudyJoinResponseDto joinStudy(Long studyId);

    void resignStudy(Long studyId);
}
