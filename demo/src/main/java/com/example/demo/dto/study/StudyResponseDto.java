package com.example.demo.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyResponseDto {

    private Long studyId;

    private String title;

    private String introduction;

    private String creatorName;

    private List<String> memberNames;

    private LocalDateTime createdAt;
}
