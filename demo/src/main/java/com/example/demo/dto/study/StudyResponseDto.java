package com.example.demo.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyResponseDto {

    private Long studyId;

    private String title;

    private String introduction;

    private String creatorName;

    private List<String> memberNames;
}