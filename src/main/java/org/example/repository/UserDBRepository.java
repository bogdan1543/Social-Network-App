package org.example.repository;

import org.example.domain.User;
import org.example.domain.validator.UserValidator;

import java.sql.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class UserDBRepository implements Repository<Integer, User> {

    UserValidator userValidator;

    public UserDBRepository(UserValidator userValidator) {
        this.userValidator = userValidator;
    }


    @Override
    public Optional<User> findOne(Integer id) {
        String query = "select * from users WHERE \"id\" = ?";
        User user = null;
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("nume");
                String lastName = resultSet.getString("prenume");
                user = new User(firstName, lastName);
                user.setId(id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        HashMap<Integer, User> users = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                String prenume = resultSet.getString("prenume");
                User user = new User(nume, prenume);
                user.setId(id);

                users.put(user.getId(), user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users.values();
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        String query = "INSERT INTO users(\"id\", \"nume\", \"prenume\") VALUES (?,?,?)";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Integer id) {

        String query = "DELETE FROM users WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        User userToDelete = null;
        for (User user : findAll()) {
            if (Objects.equals(user.getId(), id)) {
                userToDelete = user;
            }
        }
        return Optional.ofNullable(userToDelete);
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }
}