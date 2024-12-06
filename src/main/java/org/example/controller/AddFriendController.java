package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddFriendController implements Observer<UserEntityChangeEvent> {

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
        List<User> all_users = StreamSupport.stream(service.getUsers().spliterator(), false).toList();
        // Lista utilizatorilor care NU sunt prieteni
        List<User> nonFriends = new ArrayList<>();
        List<User> all_friends_and_pending = new ArrayList<>();
        for (Friendship f : service.getFriendships()){
            if (f.getFriendRequestStatus() == FriendRequest.REJECTED)
                continue;
            else if (Objects.equals(user.getId(), f.getIdUser1())){
                all_friends_and_pending.add(service.findUser(f.getIdUser2()));
            }else if (Objects.equals(user.getId(), f.getIdUser2())){
                all_friends_and_pending.add(service.findUser(f.getIdUser1()));
            }
        }
        for (User u : all_users){
            if (!Objects.equals(u.getId(), user.getId()) && !all_friends_and_pending.contains(u)){
                nonFriends.add(u);
            }
        }

        nonFriends.forEach(System.out::println);

        model.setAll(nonFriends);
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);

    }

    @FXML
    public void handleSendRequest(ActionEvent actionEvent) {
        if(!tableView.getSelectionModel().isEmpty()){
            User sender = tableView.getSelectionModel().getSelectedItem();
            service.createFriendRequest(user.getId(), sender.getId());
        }
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void handleGoBack(ActionEvent actionEvent) {
        currentStage.close();
    }
}
