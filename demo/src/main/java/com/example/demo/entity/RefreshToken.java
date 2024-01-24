package com.example.demo.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter @Setter
@RedisHash(value = "refreshToken", timeToLive = 600)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String refreshToken;

    private String username;

    private String password;

    private String expiryDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

}
