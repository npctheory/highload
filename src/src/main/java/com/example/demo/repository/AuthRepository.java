package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.UserToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class AuthRepository{

    private final DataSource dataSource;

    public AuthRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public User checkCredentials(String username){
        try (Connection connection = dataSource.getConnection()) {
            String selectUserSQL = "SELECT id, username, password_hash FROM user_account WHERE username = ?";
            try (PreparedStatement userStatement = connection.prepareStatement(selectUserSQL)) {
                userStatement.setString(1, username);
                try (ResultSet resultSet = userStatement.executeQuery()) {
                    if (resultSet.next()) {
                        UUID id = (UUID) resultSet.getObject("id");
                        String hashedPassword = resultSet.getString("password_hash");
                        return new User(id, username, hashedPassword);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public UserToken createToken(UUID userId){
        UserToken userToken = new UserToken();
        Timestamp expirationTimestamp = Timestamp.from(userToken.getExpiration());
        try (Connection connection = dataSource.getConnection()) {
            String insertTokenSql = "INSERT INTO access_token (user_id, expiration_timestamp) VALUES (?, ?)";
            try (PreparedStatement tokenStatement = connection.prepareStatement(insertTokenSql, Statement.RETURN_GENERATED_KEYS)) {
                tokenStatement.setObject(1, userId);
                tokenStatement.setTimestamp(2, expirationTimestamp);
                tokenStatement.executeUpdate();

                ResultSet generatedKeys = tokenStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userToken.setAccessToken((UUID) generatedKeys.getObject(3));
                    LocalDateTime timestamp = generatedKeys.getObject("expiration_timestamp", LocalDateTime.class);;
                    userToken.setExpirationTime(timestamp);
                    return userToken;
                } else {
                    throw new SQLDataException("Id for account was not generated");
                }
            }
    }
        catch (SQLException e) {}
return null;
}

    @Transactional
    public Boolean findToken(UserToken userToken) {
        String sql = "SELECT 1 FROM access_token WHERE token = ? AND expiration_timestamp < ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setObject(1, userToken.getAccessToken());
            preparedStatement.setTimestamp(2, Timestamp.from(userToken.getExpiration()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
