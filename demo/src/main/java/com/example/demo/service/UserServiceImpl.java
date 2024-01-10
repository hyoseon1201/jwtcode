package com.example.demo.service;

import com.example.demo.dto.account.UserRegisterDto;
import com.example.demo.dto.account.UserRegisterResponseDto;
import com.example.demo.dto.account.UserRemovalResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserRegisterResponseDto register(UserRegisterDto userRegisterDTO) {

        User newUser = createNewUser(userRegisterDTO);

        ModelMapper modelMapper = new ModelMapper();
        UserRegisterResponseDto userRegisterResponseDTO = modelMapper.map(newUser, UserRegisterResponseDto.class);

        List<String> roleNames = newUser.getRoleNames();
        userRegisterResponseDTO.setRoleNames(roleNames);

        return userRegisterResponseDTO;
    }

    @Override
    public UserRemovalResponseDTO resign(String username) {

        Optional<User> removalUser = userRepository.findById(username);

        if (removalUser.isPresent()) {
            User user = removalUser.get();
            user.setResign(true);
            userRepository.save(user);

            ModelMapper modelMapper = new ModelMapper();
            UserRemovalResponseDTO userRemovalResponseDTO = modelMapper.map(user, UserRemovalResponseDTO.class);

            return userRemovalResponseDTO;
        } else {
            // User가 존재하지 않는 경우에 대한 예외처리
            throw new NoSuchElementException("해당 User가 존재하지 않습니다.");
        }
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
}
