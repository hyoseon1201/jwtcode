package com.example.demo.service;

import com.example.demo.dto.study.StudyCreateResponseDto;
import com.example.demo.dto.study.StudyCreateRequestDto;
import com.example.demo.dto.study.StudyRequestDto;
import com.example.demo.dto.study.StudyResponseDto;
import com.example.demo.entity.Study;
import com.example.demo.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;

    @Override
    public StudyCreateResponseDto createStudy(StudyCreateRequestDto studyCreateRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ModelMapper modelMapper = new ModelMapper();

        StudyCreateResponseDto createdStudy = modelMapper.map(studyCreateRequestDto, StudyCreateResponseDto.class);
        createdStudy.setCreatorUsername(username);

        Study studyToSave = modelMapper.map(createdStudy, Study.class);
        studyRepository.save(studyToSave);

        return createdStudy;
    }

    @Override
    public StudyResponseDto findStudy(StudyRequestDto studyRequestDto) {

        Study study = studyRepository.findById(studyRequestDto.getStudyId())
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        ModelMapper modelMapper = new ModelMapper();

        StudyResponseDto studyResponseDto = modelMapper.map(study, StudyResponseDto.class);
        studyResponseDto.setCreatorName(study.getCreator().getUsername());

        return studyResponseDto;
    }
}
