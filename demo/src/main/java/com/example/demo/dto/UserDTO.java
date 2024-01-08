package com.example.demo.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserDTO extends User {

    private String id;

    private String password;

    private List<String> roleNames = new ArrayList<>();

    public UserDTO(String id, String password, List<String> roleNames) {
        super(
                id,
                password,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+str)).collect(Collectors.toList()));

        this.id = id;
        this.password = password;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("id", id);
        dataMap.put("password",password);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }

}
