package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public class Profile {
    @JsonProperty
    private UUID id;
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

    public Profile(UUID id, String name, String surname, LocalDate dateOfBirth, Gender gender, String interests, String city) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.interests = interests;
        this.city = city;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
