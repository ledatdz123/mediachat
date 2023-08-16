package com.example.mediachat;

import com.example.mediachat.repository.RoleRepository;
import com.example.mediachat.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

@SpringBootApplication
public class MediachatApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediachatApplication.class, args);
	}
}
