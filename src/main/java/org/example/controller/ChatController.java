package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.domain.UserDate;
import org.example.service.ChatMessages;
import org.example.service.SocialNetwork;
import org.example.utils.events.MessageEntityChangeEvent;
import org.example.utils.events.UserEntityChangeEvent;
import org.example.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<MessageEntityChangeEvent> {
    public TextField messageTextField;

    public Stage currentStage;
    public ListView<Message> messageListView;

    ChatMessages service;
    User from;
    User to;
    ObservableList<Message> model = FXCollections.observableArrayList();

    public void setUserService(ChatMessages service, User from, User to, Stage stage){
        this.service = service;
        this.from = from;
        this.to = to;
        this.currentStage = stage;
        initModel();
        this.service.addObserver(this);
    }

    @FXML
    public void initialize() {
        // Set the custom cell factory
        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Determine alignment and style based on sender
                    boolean isSentByCurrentUser = message.getFrom().equals(from);

                    TextField messageTextField = new TextField(message.getMessage());
                    messageTextField.setEditable(false);
                    messageTextField.setStyle(isSentByCurrentUser
                            ? "-fx-background-color: lightblue; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;"
                            : "-fx-background-color: lightgray; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");

                    HBox messageContainer = new HBox(messageTextField);
                    messageContainer.setSpacing(10);
                    messageContainer.setPrefWidth(messageListView.getWidth());
                    messageContainer.setStyle("-fx-alignment: " + (isSentByCurrentUser ? "CENTER_RIGHT" : "CENTER_LEFT") + ";");

                    setGraphic(messageContainer);
                    setText(null); // Clear default text rendering
                }
            }
        });
        // Set the items for the ListView
        messageListView.setItems(model);
    }


    private void initModel() {
        List<Message> messages = StreamSupport.stream(service.getUserMessages(from, to).spliterator(), false).toList() ;
        System.out.println(messages.size());
        model.setAll(messages);
//        for (Message message : messages) {
//            boolean isSentByCurrentUser = message.getFrom().equals(from);
//
//            TextField messageField = new TextField(message.getMessage());
//            messageField.setEditable(false);
//            messageField.setStyle(isSentByCurrentUser
//                    ? "-fx-background-color: lightblue; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;"
//                    : "-fx-background-color: lightgray; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
//
//            HBox messageContainer = new HBox(messageField);
//            messageContainer.setSpacing(10);
//            messageContainer.setPrefWidth(messageListView.getWidth());
//            messageContainer.setStyle("-fx-alignment: " + (isSentByCurrentUser ? "CENTER_RIGHT" : "CENTER_LEFT") + ";");
//
//            messagesVBox.getChildren().add(messageContainer)};
    }

    public void handleSend(ActionEvent actionEvent) {
        String message = messageTextField.getText();
        if (!message.isBlank() && service != null && from != null && to != null) {
            service.addMessage(new Message(message, from.getId(), to.getId(), LocalDateTime.now()));

//            TextField messageField = new TextField(message);
//            messageField.setEditable(false);
//            messageField.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
//
//            HBox messageContainer = new HBox(messageField);
//            messageContainer.setSpacing(10);
////            messageContainer.setPrefWidth(messagesVBox.getWidth());
//            messageContainer.setStyle("-fx-alignment: CENTER_RIGHT;");
//
////            messagesVBox.getChildren().add(messageContainer);

            messageTextField.clear();
        }
    }

    @Override
    public void update(MessageEntityChangeEvent messageEntityChangeEvent) {
        initModel();
    }
}
