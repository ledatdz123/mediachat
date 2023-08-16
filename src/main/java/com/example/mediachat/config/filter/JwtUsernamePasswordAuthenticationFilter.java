package com.example.mediachat.config.filter;

import com.example.mediachat.dto.LoginRequest;
import com.example.mediachat.dto.LoginResponseDTO;
import com.example.mediachat.jwt.JwtConfig;
import com.example.mediachat.jwt.JwtService;
import com.example.mediachat.service.security.UserDetailsCustom;
import com.example.mediachat.utils.BaseResponseDTO;
import com.example.mediachat.utils.HelperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager manager,
                                                   JwtConfig jwtConfig,
                                                   JwtService jwtService){
        super(new AntPathRequestMatcher(jwtConfig.getUrl(), "POST"));
        setAuthenticationManager(manager);
        this.objectMapper = new ObjectMapper();
        this.jwtService = jwtService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        log.info("Start attempt to authentication");
        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        log.info("End attempt to authentication");

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword(),
                        Collections.emptyList()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userDetailsCustom);
        LoginResponseDTO responseDTO=new LoginResponseDTO();
        responseDTO.setJwt(accessToken);
        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
        log.info("End success authentication: {}", accessToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage(failed.getLocalizedMessage());

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
        return;
    }


}
