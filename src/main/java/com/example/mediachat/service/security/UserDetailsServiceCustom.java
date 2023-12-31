package com.example.mediachat.service.security;

import com.example.mediachat.entity.User;
import com.example.mediachat.exception.BaseException;
import com.example.mediachat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;

import java.util.stream.Collectors;

public class UserDetailsServiceCustom implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserDetailsCustom userDetailsCustom = getUserDetails(email);

        if(ObjectUtils.isEmpty(userDetailsCustom)){
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid username or password!");
        }

        return userDetailsCustom;
    }

    private UserDetailsCustom getUserDetails(String email){
        User user = userRepository.findByEmail(email);

        if(ObjectUtils.isEmpty(user)){
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid username or password!");
        }

        return new UserDetailsCustom(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName()))
                        .collect(Collectors.toList())
        );
    }
}

