package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Study {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String introduction;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator",
            referencedColumnName = "username")
    private User creator;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_study",
            joinColumns = @JoinColumn(name = "study_id"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private List<User> studyMembers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "study_tag",
            joinColumns = @JoinColumn(name = "study_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public void update(String introduction, String title) {
        this.introduction = introduction;
        this.title = title;
    }
}
