package com.example.mediachat.service.impl;

import com.example.mediachat.dto.UserDTO;
import com.example.mediachat.entity.Role;
import com.example.mediachat.entity.User;
import com.example.mediachat.exception.BaseException;
import com.example.mediachat.repository.RoleRepository;
import com.example.mediachat.repository.UserRepository;
import com.example.mediachat.service.UserService;
import com.example.mediachat.utils.BaseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public BaseResponseDTO registerAccount(UserDTO userDTO) {
        BaseResponseDTO response = new BaseResponseDTO();

        validateAccount(userDTO);

        User user = insertUser(userDTO);

        try {
            userRepository.save(user);
            response.setCode(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Create account successfully");
        } catch (Exception e) {
            response.setCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
            response.setMessage("Service unavailable");
        }
        return response;
    }

    private User insertUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(userDTO.getRole()));
        user.setRoles(roles);
        return user;
    }

    private void validateAccount(UserDTO userDTO) {
        //validate null data
        if (ObjectUtils.isEmpty(userDTO)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Data must not empty");
        }

        //validate duplicate username
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (!ObjectUtils.isEmpty(user)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Username had existed");
        }


        //validate role
        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        if (!roles.contains(userDTO.getRole())) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid role");
        }
    }
}

