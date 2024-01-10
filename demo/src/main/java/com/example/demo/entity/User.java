package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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

    private boolean verify;

    private boolean resign;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRole> userRoleList = new ArrayList<>();

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
}
