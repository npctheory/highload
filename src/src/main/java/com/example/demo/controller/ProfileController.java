package com.example.demo.controller;

import com.example.demo.exception.UnauthorizedException;
import com.example.demo.model.Profile;
import com.example.demo.service.ProfileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable UUID id, @RequestHeader("Authorization") UUID token) {
        Profile profile = profileService.getProfileById(id, token);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Profile>> searchProfiles(@RequestParam String name, @RequestParam String surname, @RequestHeader("Authorization") UUID token) {
        List<Profile> profiles = profileService.searchProfiles(name, surname, token);
        return ResponseEntity.ok(profiles);
    }
}

