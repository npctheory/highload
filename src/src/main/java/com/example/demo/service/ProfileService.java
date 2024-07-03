package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.model.Profile;
import com.example.demo.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final AuthService authService;

    public ProfileService(ProfileRepository profileRepository, AuthService authService) {
        this.profileRepository = profileRepository;
        this.authService = authService;
    }

    @Transactional
    public Profile getProfileById(UUID id, UUID token) {
        if (authService.validateToken(token)) {
            Profile profile = profileRepository.getProfileById(id);
            if (profile == null) {
                throw new NotFoundException("Profile not found");
            }
            return profile;
        } else {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    @Transactional
    public List<Profile> searchProfiles(String name, String surname, UUID token) {
        if (authService.validateToken(token)) {
            return profileRepository.searchProfiles(name, surname);
        } else {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }
}

