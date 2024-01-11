package com.example.demo.service;

import com.example.demo.dto.study.StudyCreateResponseDto;
import com.example.demo.dto.study.StudyCreateRequestDto;
import com.example.demo.dto.study.StudyRequestDto;
import com.example.demo.dto.study.StudyResponseDto;

public interface StudyService {

    StudyCreateResponseDto createStudy(StudyCreateRequestDto studyCreateRequestDto);

    StudyResponseDto findStudy(StudyRequestDto studyRequestDto);
}
