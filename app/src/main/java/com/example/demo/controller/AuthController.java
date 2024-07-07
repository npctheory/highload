package com.example.demo.controller;

import com.example.demo.model.UserCredentials;
import com.example.demo.model.UserToken;
import com.example.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserToken> login(@RequestBody UserCredentials userCredentials){
        UserToken userToken = authService.authenticate(userCredentials);
        if (userToken != null) {
            return ResponseEntity.ok(userToken);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
