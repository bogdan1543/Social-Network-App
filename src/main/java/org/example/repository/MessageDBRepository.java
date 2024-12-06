package org.example.repository;

import org.example.domain.FriendRequest;
import org.example.domain.Friendship;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.domain.validator.MessageValidator;
import org.example.domain.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MessageDBRepository extends InMemoryRepository<Integer, Message> {

    public MessageDBRepository(MessageValidator validator) {
        super(validator);
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM message ORDER BY date");
            ResultSet resultSet = statement.executeQuery();){

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer fromId = resultSet.getInt("id_from");
                Integer toId = resultSet.getInt("id_to");
                String msg = resultSet.getString("text_message");
                LocalDateTime dateTime = null;
                if (resultSet.getTimestamp("date") != null)
                    dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                Message message = new Message(msg, fromId, toId, dateTime);
                message.setId(id);

                messages.add(message);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message message) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1234/postgres", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO message(text_message, id_from, id_to, date) VALUES (?,?,?,?)");) {

            statement.setString(1, message.getMessage());
            statement.setInt(2, message.getIdFrom());
            statement.setInt(3, message.getIdTo());
            statement.setTimestamp(4, Timestamp.valueOf(message.getDate()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(message);
    }
}
