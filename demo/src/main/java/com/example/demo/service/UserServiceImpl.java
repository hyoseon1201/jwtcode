package com.example.demo.service;

import com.example.demo.dto.account.UserCreateStudyListResponseDto;
import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemoveResponseDTO;
import com.example.demo.entity.Study;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.StudyRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Transactional
    @Override
    public UserRegisterResponseDto register(UserRegisterDto userRegisterDTO) {

        User newUser = createNewUser(userRegisterDTO);
        generateEmailCheckTokenAndSendEmail(newUser);

        ModelMapper modelMapper = new ModelMapper();
        UserRegisterResponseDto userRegisterResponseDTO = modelMapper.map(newUser, UserRegisterResponseDto.class);

        List<String> roleNames = newUser.getRoleNames();
        userRegisterResponseDTO.setRoleNames(roleNames);

        return userRegisterResponseDTO;
    }

    @Override
    public UserRemoveResponseDTO resign() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ModelMapper modelMapper = new ModelMapper();

        User removalUser = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        removalUser.setResign(true);
        userRepository.save(removalUser);

        return modelMapper.map(removalUser, UserRemoveResponseDTO.class);
    }

    @Override
    public List<UserCreateStudyListResponseDto> findCreateStudyList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ModelMapper modelMapper = new ModelMapper();

        List<Study> studyList = studyRepository.findAllByCreatorUsername(username);

        return studyList.stream()
                .map(study -> modelMapper.map(study, UserCreateStudyListResponseDto.class))
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

        ModelMapper modelMapper = new ModelMapper();
        User newUser = modelMapper.map(userRegisterDTO, User.class);
        newUser.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        newUser.addRole(UserRole.USER);

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
