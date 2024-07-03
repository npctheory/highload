package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, UUID>> userRegister(@RequestBody User user) {
        userService.registerUser(user);
        Map<String, UUID> response = new HashMap<>();
        if (user.getProfileId() != null) {
            response.put("profileId", user.getProfileId());
            return ResponseEntity.ok(response);
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}
