package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentials {

    @JsonProperty
    private String username;
    @JsonProperty
    private String password;


    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
