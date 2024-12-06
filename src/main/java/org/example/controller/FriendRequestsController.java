package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.domain.FriendRequest;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.service.SocialNetwork;
import org.example.utils.events.UserEntityChangeEvent;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<UserEntityChangeEvent> {
    SocialNetwork service;
    Stage currentStage;
    User user;
    ObservableList<User> model = FXCollections.observableArrayList();


    public void setUserService(SocialNetwork service, User user, Stage stage){
        this.service = service;
        this.user = user;
        this.currentStage = stage;
        initModel();
        this.service.addObserver(this);
    }

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;

    private void initModel() {
        List<User> all_user_friend_requests = StreamSupport.stream(service.getUserFriendRequests(user).spliterator(), false).toList();

        model.setAll(all_user_friend_requests);
    }

    @FXML
    public void initialize() {
        tableView.setItems(null);
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);

    }
    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void handleAccept(ActionEvent actionEvent) {
        if(!tableView.getSelectionModel().isEmpty()){
            User sender = tableView.getSelectionModel().getSelectedItem();
            Friendship friendship = service.getFriendshipById(user.getId(), sender.getId());
            service.respondFriendRequest(friendship, FriendRequest.ACCEPTED);
        }
    }

    public void handleReject(ActionEvent actionEvent) {
        if(!tableView.getSelectionModel().isEmpty()){
            User sender = tableView.getSelectionModel().getSelectedItem();
            Friendship friendship = service.getFriendshipById(user.getId(), sender.getId());
            service.respondFriendRequest(friendship, FriendRequest.REJECTED);
        }
    }

    public void handleGoBack(ActionEvent actionEvent) {
        currentStage.close();
    }
}
