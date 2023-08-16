package com.example.mediachat.controller;


import com.example.mediachat.dto.UserDTO;
import com.example.mediachat.service.UserService;
import com.example.mediachat.utils.BaseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponseDTO> register(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.registerAccount(userDTO));
    }
}
