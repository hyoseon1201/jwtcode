package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    @ManyToMany(mappedBy = "tags")
    private List<User> users;

    @ManyToMany(mappedBy = "tags")
    private List<Study> studies;

}
