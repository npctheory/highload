package com.example.demo.service;

import com.example.demo.exception.UnauthorizedException;
import com.example.demo.model.User;
import com.example.demo.model.UserCredentials;
import com.example.demo.model.UserToken;
import com.example.demo.repository.AuthRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService{
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository){
        this.authRepository = authRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public UserToken authenticate(UserCredentials userCredentials){
        User user = authRepository.checkCredentials(userCredentials.getUsername());
        if (user != null && passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())) {
            return authRepository.createToken(user.getId());
        }
        else throw new UnauthorizedException("invalid login or password");
    }

    @Transactional
    public Boolean validateToken(UUID token){
        UserToken userToken = new UserToken(token);
        return authRepository.findToken(userToken);
    }
}
