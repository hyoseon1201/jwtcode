package com.example.demo.service;

import com.example.demo.dto.account.*;
import com.example.demo.entity.Study;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.StudyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final JwtUtil jwtUtil;

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userLoginRequestDto.getUsername());

        if(!passwordEncoder.matches(userLoginRequestDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // UserDetails 객체 안의 username과 authorities를 Map에 넣습니다.
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("password", userDetails.getPassword());
        claims.put("roleNames", userDetails.getAuthorities());

        // 토큰을 생성합니다.
        String accessToken = jwtUtil.generateToken(claims, 10);
        String refreshToken = jwtUtil.generateToken(claims, 60 * 24);

        // 반환
        return new UserLoginResponseDto(userLoginRequestDto.getUsername(), accessToken, refreshToken);
    }

    @Transactional
    @Override
    public UserRegisterResponseDto register(UserRegisterDto userRegisterDTO) {

        User newUser = createNewUser(userRegisterDTO);
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

        User removalUser = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

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
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        if (!user.getEmailCheckToken().equals(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        user.setEmailVerified(true);
        user.setJoinedAt(LocalDateTime.now());
    }

    private User createNewUser(UserRegisterDto userRegisterDTO) {

        if (userRepository.existsById(userRegisterDTO.getUsername())) {
            throw new RuntimeException();
        }

        User newUser = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .nickname(userRegisterDTO.getNickname())
                .phoneNumber(userRegisterDTO.getPhoneNumber())
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
