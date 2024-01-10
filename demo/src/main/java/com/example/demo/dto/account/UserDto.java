package com.example.demo.dto.account;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserDto extends User {

    private String username;

    private String password;

    private List<String> roleNames = new ArrayList<>();

    public UserDto(String username, String password, List<String> roleNames) {
        super(
                username,
                password,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+str)).collect(Collectors.toList()));

        this.username = username;
        this.password = password;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("username", username);
        dataMap.put("password",password);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }

}
