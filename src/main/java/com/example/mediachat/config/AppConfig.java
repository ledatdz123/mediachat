package com.example.mediachat.config;

import com.example.mediachat.config.filter.CustomAuthenticationProvider;
import com.example.mediachat.config.filter.JwtTokenAuthenticationFilter;
import com.example.mediachat.config.filter.JwtUsernamePasswordAuthenticationFilter;
import com.example.mediachat.exception.CustomAccessDeniedHandler;
import com.example.mediachat.jwt.JwtConfig;
import com.example.mediachat.jwt.JwtService;
import com.example.mediachat.service.security.UserDetailsServiceCustom;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    JwtConfig jwtConfig;


    @Autowired
    private JwtService jwtService;

    @Bean
    public JwtConfig jwtConfig(){
        return new JwtConfig();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceCustom();
    }

    @Autowired
    public void configGlobal(final AuthenticationManagerBuilder auth){
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        builder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());

        AuthenticationManager manager = builder.build();

        http
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1/auth/**",
                        "/v2/api-docs",
                        "/v3/api-docs/**",
                        "/v2/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                ).permitAll()
                .requestMatchers("/account/**").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/user/**").hasAuthority("USER")
                .anyRequest().authenticated()
                .and()
                .authenticationManager(manager)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        ((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                )
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .addFilterBefore(new JwtUsernamePasswordAuthenticationFilter(manager, jwtConfig, jwtService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig, jwtService), UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}

