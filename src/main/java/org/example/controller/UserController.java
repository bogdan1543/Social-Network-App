package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.HelloApplication;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.UserDate;
import org.example.service.SocialNetwork;
import org.example.utils.events.UserEntityChangeEvent;
import org.example.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserController implements Observer<UserEntityChangeEvent> {
    public TextField fieldNume;
    public TextField fieldPrenume;

    SocialNetwork service;
    User user;
    ObservableList<UserDate> model = FXCollections.observableArrayList();



    public void setUserService(SocialNetwork service, User user){
        this.service = service;
        this.user = user;
        initModel();
        this.service.addObserver(this);
    }

    @FXML
    TableView<UserDate> tableView;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<UserDate, LocalDateTime> tableColumnFriendsSince;

    private void initModel() {
        Map<User, LocalDateTime> all_friends= service.getUserFriends(user);
        List<UserDate> all_friends_with_date = all_friends.entrySet()
                .stream()
                .map(entry -> new UserDate(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        model.setAll(all_friends_with_date);
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<>("friendSince"));
        tableColumnFriendsSince.setCellFactory(column -> {
            return  new TableCell<UserDate, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                    }
                }
            };
        });
        tableView.setItems(model);
    }

    public void showUserNonFriendsDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add-friends-view.fxml"));


            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            AddFriendController addFriendController = loader.getController();
            addFriendController.setUserService(service, user, dialogStage);
            System.out.println(user.getFirstName());

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddFriend(ActionEvent actionEvent) {
        showUserNonFriendsDialog(user);
    }

    public void handleDeleteFriend(ActionEvent actionEvent) {
        if (!tableView.getSelectionModel().isEmpty()){
            UserDate friend = tableView.getSelectionModel().getSelectedItem();
            service.removeFriendship(user.getId(), friend.getUser().getId());
            initModel();
            initialize();
        }
    }

    public void handleFriendRequests(ActionEvent actionEvent) {
        showUserFriendRequestsDialog(user);
    }

    private void showUserFriendRequestsDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("friend-requests-view.fxml"));


            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            FriendRequestsController friendRequestsController = loader.getController();
            friendRequestsController.setUserService(service, user, dialogStage);
            System.out.println(user.getFirstName());

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }


    public void handleReload(ActionEvent actionEvent) {
        initialize();
        initModel();
    }
}
