package com.example.demo.service;

import com.example.demo.dto.study.*;
import com.example.demo.entity.Study;
import com.example.demo.entity.User;
import com.example.demo.repository.StudyRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        StudyCreateResponseDto createdStudy = StudyCreateResponseDto.builder()
                .title(studyCreateRequestDto.getTitle())
                .introduction(studyCreateRequestDto.getIntroduction())
                .creatorUsername(username)
                .build();

        Study studyToSave = Study.builder()
                .title(createdStudy.getTitle())
                .introduction(createdStudy.getIntroduction())
                .creator(userRepository.getWithRoles(createdStudy.getCreatorUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다.")))
                .createdAt(LocalDateTime.now())
                .build();

        studyRepository.save(studyToSave);

        return createdStudy;
    }

    @Override
    public StudyResponseDto findStudy(Long studyId) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        List<String> memberNames = study.getStudyMembers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        return StudyResponseDto.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .introduction(study.getIntroduction())
                .creatorName(study.getCreator().getUsername())
                .memberNames(memberNames)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public List<StudyListResponseDto> findStudyList() {

        List<Study> studyList = studyRepository.findAll();

        return studyList.stream()
                .map(study -> StudyListResponseDto.builder()
                        .studyId(study.getId())
                        .studyTitle(study.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyUpdateResponseDto updateStudy(Long studyId, StudyUpdateRequestDto studyUpdateRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (!Objects.equals(username, study.getCreator().getUsername())) {
            throw new RuntimeException("해당 스터디의 수정 권한이 없습니다.");
        }

        study.update(studyUpdateRequestDto.getIntroduction(), studyUpdateRequestDto.getTitle());

        return StudyUpdateResponseDto.builder()
                .title(study.getTitle())
                .introduction(study.getIntroduction())
                .creatorUsername(username)
                .build();
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

        User user = userRepository.getWithRoles(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자는 존재하지 않습니다."));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (study.getStudyMembers().contains(user)) {
            throw new RuntimeException("이미 참여한 스터디입니다.");
        }

        study.getStudyMembers().add(user);

        return StudyJoinResponseDto.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .joinUsername(username)
                .build();
    }

    @Override
    @Transactional
    public void resignStudy(Long studyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.getWithRoles(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자는 존재하지 않습니다."));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("해당 스터디는 존재하지 않습니다."));

        if (!study.getStudyMembers().contains(user)) {
            throw new RuntimeException("참여하지 않은 스터디입니다.");
        }

        study.getStudyMembers().remove(user);
    }
}
