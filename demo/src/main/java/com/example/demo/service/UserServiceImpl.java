package com.example.demo.service;

import com.example.demo.dto.account.*;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Study;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.StudyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userLoginRequestDto.getUsername());

        if(!passwordEncoder.matches(userLoginRequestDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("password", userDetails.getPassword());
        claims.put("roleNames", userDetails.getAuthorities());

        String accessToken = JwtUtil.generateToken(claims, 5);
        String refreshToken = JwtUtil.generateToken(claims, 10);

        Long refreshTokenExpiry = JwtUtil.getExpirationDateFromToken(refreshToken);

        Instant refreshTokenExpiryDate = Instant.ofEpochMilli(refreshTokenExpiry);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String formattedExpiryDate = formatter.format(refreshTokenExpiryDate);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .expiryDate(formattedExpiryDate)
                .password(userDetails.getPassword())
                .authorities(userDetails.getAuthorities())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new UserLoginResponseDto(userLoginRequestDto.getUsername(), accessToken, refreshToken);
    }

    @Transactional
    @Override
    public UserRegisterResponseDto register(UserRegisterRequestDto userRegisterRequestDTO) {

        User newUser = createNewUser(userRegisterRequestDTO);
        generateEmailCheckTokenAndSendEmail(newUser);

        List<String> roleNames = newUser.getRoleNames();

        return UserRegisterResponseDto.builder()
                .username(newUser.getUsername())
                .nickname(newUser.getNickname())
                .phoneNumber(newUser.getPhoneNumber())
                .roleNames(roleNames)
                .build();
    }

    @Override
    public UserRemoveResponseDTO resign() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User removalUser = userRepository.getWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다."));

        removalUser.setResign(true);
        userRepository.save(removalUser);

        return UserRemoveResponseDTO.builder()
                .username(removalUser.getUsername())
                .resign(removalUser.isResign())
                .build();
    }

    @Override
    public List<UserCreateStudyListResponseDto> findCreateStudyList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Study> studyList = studyRepository.findAllByCreatorUsername(username);

        return studyList.stream()
                .map(study -> UserCreateStudyListResponseDto.builder()
                        .studyId(study.getId())
                        .title(study.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkEmailToken(String token, String username) {

        User user = userRepository.getWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다."));
        if (!user.getEmailCheckToken().equals(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        user.setEmailVerified(true);
        user.setJoinedAt(LocalDateTime.now());
    }

    private User createNewUser(UserRegisterRequestDto userRegisterRequestDTO) {

        if (userRepository.getWithRoles(userRegisterRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User newUser = User.builder()
                .username(userRegisterRequestDTO.getUsername())
                .password(passwordEncoder.encode(userRegisterRequestDTO.getPassword()))
                .nickname(userRegisterRequestDTO.getNickname())
                .phoneNumber(userRegisterRequestDTO.getPhoneNumber())
                .userRoleList(List.of(UserRole.USER))
                .build();

        return userRepository.save(newUser);
    }

    public void generateEmailCheckTokenAndSendEmail(User newUser) {
        newUser.generateEmailCheckToken();
        sendSignUpConfirmEmail(newUser);
    }

    private void sendSignUpConfirmEmail(User newUser) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newUser.getUsername());
        mailMessage.setSubject("SSA-STUDY 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" +
                newUser.getEmailCheckToken() +
                "&username=" +
                newUser.getUsername());
        javaMailSender.send(mailMessage);
    }
}
