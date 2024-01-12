package com.example.demo.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyJoinResponseDto {

    private Long studyId;

    private String title;

    private String joinUsername;
}
