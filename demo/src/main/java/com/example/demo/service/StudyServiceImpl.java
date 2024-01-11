package com.example.demo.service;

import com.example.demo.dto.study.StudyCreateDto;
import com.example.demo.entity.Study;
import com.example.demo.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    @Override
    public StudyCreateDto createStudy(StudyCreateDto studyCreateDto) {

        ModelMapper modelMapper = new ModelMapper();
        Study mappedStudy = modelMapper.map(studyCreateDto, Study.class);
        Study savedStudy = studyRepository.save(mappedStudy);

        return modelMapper.map(savedStudy, StudyCreateDto.class);
    }
}
