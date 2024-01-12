package com.example.demo.security;

import com.example.demo.dto.account.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.getWithRoles(username);

        if (user == null) {
            throw new UsernameNotFoundException("해당 유저가 존재하지 않습니다.");
        }

        if (user.isResign()) {
            throw new DisabledException("해당 유저는 비활성화 상태입니다.");
        }

        return new UserDto(
                user.getUsername(),
                user.getPassword(),
                user.getUserRoleList()
                        .stream()
                        .map(Enum::name).collect(Collectors.toList())
        );
    }
}
