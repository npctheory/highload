package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public class User {

    private UUID id;
    @JsonProperty
    private UUID profileId;
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;
    @JsonProperty
    private String name;
    @JsonProperty
    private String surname;
    @JsonProperty
    private LocalDate dateOfBirth;
    @JsonProperty
    private Gender gender;
    @JsonProperty
    private String interests;
    @JsonProperty
    private String city;
@JsonCreator
    public User(String username, String password, String name, String surname, LocalDate dateOfBirth, Gender gender, String interests, String city) {
        if (username == null || password == null || name == null || surname == null || gender == null) {
            throw new IllegalArgumentException("Username, password, name and surname parameters must be provided to create a user");
        }
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.interests = interests;
        this.city = city;
    }

    public User(UUID profileId, String name, String surname, LocalDate dateOfBirth, Gender gender, String interests, String city) {
        this.profileId = profileId;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.interests = interests;
        this.city = city;
    }

    public User(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getInterests() {
        return interests;
    }

    public String getCity() {
        return city;
    }


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
