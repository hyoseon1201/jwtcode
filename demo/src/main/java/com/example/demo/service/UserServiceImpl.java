package com.example.demo.service;

import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserRegisterResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserRegisterResponseDTO register(UserRegisterDTO userRegisterDTO) {
        User newUser = createNewUser(userRegisterDTO);

        ModelMapper modelMapper = new ModelMapper();
        UserRegisterResponseDTO userRegisterResponseDTO = modelMapper.map(newUser, UserRegisterResponseDTO.class);

        List<String> roleNames = newUser.getRoleNames();
        userRegisterResponseDTO.setRoleNames(roleNames);

        return userRegisterResponseDTO;
    }

    private User createNewUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsById(userRegisterDTO.getId())) {
            throw new RuntimeException();
        }

        ModelMapper modelMapper = new ModelMapper();

        User newUser = modelMapper.map(userRegisterDTO, User.class);

        newUser.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        newUser.addRole(UserRole.USER);

        return userRepository.save(newUser);
    }
}
