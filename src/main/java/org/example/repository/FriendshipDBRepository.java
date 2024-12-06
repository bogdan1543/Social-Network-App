package org.example.repository;

import org.example.domain.FriendRequest;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.validator.FriendshipValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FriendshipDBRepository implements Repository<Integer, Friendship> {

    FriendshipValidator friendshipValidator;

    private final UserDBRepository userRepository;


    public FriendshipDBRepository(UserDBRepository userRepository, FriendshipValidator friendshipValidator) {
        this.friendshipValidator = friendshipValidator;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Friendship> findOne(Integer id) {
        String query = "SELECT * FROM friendships WHERE \"id\" = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Integer idFriend1 = resultSet.getInt("idfriend1");
                Integer idFriend2 = resultSet.getInt("idfriend2");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = null;
                if (resultSet.getTimestamp("friendSince") != null)
                    dateTime = resultSet.getTimestamp("friendSince").toLocalDateTime();
                friendship = new Friendship(idFriend1, idFriend2, dateTime);
                friendship.setId(id);
                friendship.setFriendRequestStatus(FriendRequest.valueOf(status));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendship);
    }

    @Override
    public Iterable<Friendship> findAll() {
        Map<Integer, Friendship> friendships = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer idFriend1 = resultSet.getInt("idfriend1");
                Integer idFriend2 = resultSet.getInt("idfriend2");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = null;
                if (resultSet.getTimestamp("friendSince") != null)
                    dateTime = resultSet.getTimestamp("friendSince").toLocalDateTime();
                Friendship friendship = new Friendship(idFriend1, idFriend2, dateTime);
                friendship.setId(id);
                friendship.setFriendRequestStatus(FriendRequest.valueOf(status));

                friendships.put(friendship.getId(), friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships.values();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        friendshipValidator.validate(entity);
        String query = "INSERT INTO friendships(\"id\", \"idfriend1\", \"idfriend2\", \"status\") VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setInt(1, entity.getId());
            statement.setInt(2, entity.getIdUser1());
            statement.setInt(3, entity.getIdUser2());
            statement.setString(4, entity.getFriendRequestStatus().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Integer id) {
        String query = "DELETE FROM friendships WHERE \"id\" = ?";
        Friendship friendshipToDelete = null;
        friendshipToDelete =  findOne(id).get();
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendshipToDelete);
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        friendshipValidator.validate(entity);
        String query = "UPDATE friendships SET \"status\" = ?, \"friendsince\" = ? WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, entity.getFriendRequestStatus().toString());
            if(entity.getDateTime() == null){
                statement.setTimestamp(2, null);
            }
            else {
                statement.setTimestamp(2, Timestamp.valueOf(entity.getDateTime()));
            }
            statement.setInt(3, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }
}
