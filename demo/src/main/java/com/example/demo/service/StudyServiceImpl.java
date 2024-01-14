package com.example.demo.service;

import com.example.demo.dto.study.*;
import com.example.demo.entity.Study;
import com.example.demo.entity.User;
import com.example.demo.repository.StudyRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;

    @Override
    public StudyCreateResponseDto createStudy(StudyCreateRequestDto studyCreateRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ModelMapper modelMapper = new ModelMapper();

        StudyCreateResponseDto createdStudy = modelMapper.map(studyCreateRequestDto, StudyCreateResponseDto.class);
        createdStudy.setCreatorUsername(username);

        Study studyToSave = modelMapper.map(createdStudy, Study.class);
        studyToSave.setCreatedAt(LocalDateTime.now());
        studyRepository.save(studyToSave);

        return createdStudy;
    }

    @Override
    public StudyResponseDto findStudy(Long studyId) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        ModelMapper modelMapper = new ModelMapper();

        StudyResponseDto studyResponseDto = modelMapper.map(study, StudyResponseDto.class);
        studyResponseDto.setCreatorName(study.getCreator().getUsername());

        List<String> memberNames = study.getStudyMembers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        studyResponseDto.setMemberNames(memberNames);

        return studyResponseDto;
    }

    @Override
    public List<StudyListResponseDto> findStudyList() {

        List<Study> studyList = studyRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();

        return studyList.stream()
                .map(study -> modelMapper.map(study, StudyListResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyUpdateResponseDto updateStudy(Long studyId, StudyUpdateRequestDto studyUpdateRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ModelMapper modelMapper = new ModelMapper();

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (!Objects.equals(username, study.getCreator().getUsername())) {
            throw new RuntimeException("해당 스터디의 수정 권한이 없습니다.");
        }

        study.update(studyUpdateRequestDto.getIntroduction(), studyUpdateRequestDto.getTitle());

        // 영속성 컨텍스트에 의해 관리되는 모든 엔티티의 변경 사항을 자동으로 DB에 반영
        // 따라서 save 메서드 호출이 필요 없습니다.

        StudyUpdateResponseDto updatedStudy = modelMapper.map(study, StudyUpdateResponseDto.class);
        updatedStudy.setCreatorUsername(username);

        return updatedStudy;
    }

    @Override
    public void deleteStudy(Long studyId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Study study = studyRepository.findById(studyId)
                        .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (!Objects.equals(username, study.getCreator().getUsername())) {
            throw new RuntimeException("해당 스터디의 수정 권한이 없습니다.");
        }

        studyRepository.deleteById(studyId);
    }

    @Override
    @Transactional
    public StudyJoinResponseDto joinStudy(Long studyId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ModelMapper modelMapper = new ModelMapper();

        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자는 존재하지 않습니다."));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (study.getStudyMembers().contains(user)) {
            throw new RuntimeException("이미 참여한 스터디입니다.");
        }

        study.getStudyMembers().add(user);

        StudyJoinResponseDto joinUser = modelMapper.map(study, StudyJoinResponseDto.class);
        joinUser.setJoinUsername(username);

        return joinUser;
    }

    @Override
    @Transactional
    public void resignStudy(Long studyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자는 존재하지 않습니다."));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (!study.getStudyMembers().contains(user)) {
            throw new RuntimeException("참여하지 않은 스터디입니다.");
        }

        study.getStudyMembers().remove(user);
    }
}
