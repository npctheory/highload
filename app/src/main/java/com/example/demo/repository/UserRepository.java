package com.example.demo.repository;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

@Repository
public class UserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UUID saveUser(User user) {
        UUID profileId = null;
        try (Connection connection = dataSource.getConnection()) {
            String insertSensitiveSql = "INSERT INTO user_account (username, password_hash) VALUES (?, ?)";
            try (PreparedStatement sensitiveStatement = connection.prepareStatement(insertSensitiveSql, Statement.RETURN_GENERATED_KEYS)) {
                sensitiveStatement.setString(1, user.getUsername());
                sensitiveStatement.setString(2, user.getPassword());
                sensitiveStatement.executeUpdate();

                ResultSet generatedKeys = sensitiveStatement.getGeneratedKeys();
                UUID userId = null;
                if (generatedKeys.next()) {
                    userId = (UUID) generatedKeys.getObject(1);
                } else {
                    throw new SQLDataException("Id for account was not generated");
                }

                if (userId != null) {
                    String insertDetailsSql = "INSERT INTO user_profile (account_id, name, surname, date_of_birth, interests, gender, city) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement detailsStatement = connection.prepareStatement(insertDetailsSql, Statement.RETURN_GENERATED_KEYS)) {
                        detailsStatement.setObject(1, userId);
                        detailsStatement.setString(2, user.getName());
                        detailsStatement.setString(3, user.getSurname());
                        detailsStatement.setDate(4, java.sql.Date.valueOf(user.getDateOfBirth()));
                        detailsStatement.setString(5, user.getInterests());
                        detailsStatement.setString(6, user.getGender().name());
                        detailsStatement.setString(7, user.getCity());
                        detailsStatement.executeUpdate();

                        ResultSet generatedProfileKeys = detailsStatement.getGeneratedKeys();
                        if (generatedProfileKeys.next()) {
                            profileId = (UUID) generatedProfileKeys.getObject(1);
                            user.setProfileId(profileId);
                        } else {
                            throw new SQLDataException("Id for profile was not generated");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new BadRequestException("this username is already taken.");
            }
            e.printStackTrace();
        }
        return profileId;
    }
}
