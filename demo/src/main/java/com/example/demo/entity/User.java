package com.example.demo.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    private String id;

    private String password;

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
}
