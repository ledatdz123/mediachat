package com.example.mediachat.service;

import com.example.mediachat.dto.UserDTO;
import com.example.mediachat.utils.BaseResponseDTO;

public interface UserService {
    BaseResponseDTO registerAccount(UserDTO userDTO);
}
