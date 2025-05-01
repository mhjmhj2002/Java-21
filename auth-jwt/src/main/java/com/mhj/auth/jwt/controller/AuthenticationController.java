package com.mhj.auth.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mhj.auth.jwt.dto.LoginUserDto;
import com.mhj.auth.jwt.dto.RegisterUserDto;
import com.mhj.auth.jwt.entity.User;
import com.mhj.auth.jwt.response.LoginResponse;
import com.mhj.auth.jwt.service.AuthenticationService;
import com.mhj.auth.jwt.service.JwtService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse
        		.builder()
        		.token(jwtToken)
        		.expiresIn(jwtService.getExpirationTime())
        		.build();

        return ResponseEntity.ok(loginResponse);
    }
}