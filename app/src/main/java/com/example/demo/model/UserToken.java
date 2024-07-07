package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserToken {

    @JsonProperty
    private UUID accessToken;
    @JsonProperty
    private LocalDateTime expirationTime;
    @JsonIgnore
    private final Instant expiration = Instant.now().plusSeconds(86400);

    public UUID getAccessToken() {
        return accessToken;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public UserToken(UUID accessToken) {
        this.accessToken = accessToken;
    }

    public UserToken() {
    }

    public void setAccessToken(UUID accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
