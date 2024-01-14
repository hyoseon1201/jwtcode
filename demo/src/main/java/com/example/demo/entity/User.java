package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "username")
public class User {

    @Id
    private String username;

    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    private boolean resign;

    private boolean emailVerified;

    private boolean social;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String location;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRole> userRoleList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_tag",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public List<String> getRoleNames() {
        return userRoleList
                .stream()
                .map(UserRole::name)
                .collect(Collectors.toList());
    }

    public void addRole(UserRole userRole) {
        userRoleList.add(userRole);
    }

    public boolean hasRole(UserRole userRole) {
        return userRoleList.contains(userRole);
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

}
