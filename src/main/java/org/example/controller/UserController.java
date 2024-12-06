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
import org.example.domain.FriendRequest;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.UserDate;
import org.example.service.ChatMessages;
import org.example.service.SocialNetwork;
import org.example.utils.events.UserEntityChangeEvent;
import org.example.utils.observer.Observer;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserEntityChangeEvent> {
    public TextField fieldNume;
    public TextField fieldPrenume;
    public Button nextButton;
    public Button prevButton;

    SocialNetwork service;

    ChatMessages chatService;
    User user;
    ObservableList<UserDate> model = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 2;


    public void setUserService(SocialNetwork service,ChatMessages chatService, User user){
        this.service = service;
        this.chatService = chatService;
        this.user = user;
        this.service.addObserver(this);
        initModel();
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
        Page<Friendship> page = service.getFriendshipsOnPage(new Pageable(currentPage, pageSize), user);
        List<Friendship> friendships = StreamSupport.stream(page.getElementsOnPage().spliterator(), false).toList();
        List<UserDate> all_friends = new ArrayList<>();
        for (Friendship f : friendships){
            if ((!Objects.equals(f.getIdUser1(), user.getId()))){
                all_friends.add(new UserDate(service.findUser(f.getIdUser1()), f.getDateTime()));
            }else if ((!Objects.equals(f.getIdUser2(), user.getId()))){
                all_friends.add(new UserDate(service.findUser(f.getIdUser2()), f.getDateTime()));
            }
        }
        model.setAll(all_friends);
        prevButton.setDisable(currentPage == 0);
        int noOfPages = (int)(Math.ceil((double) page.getTotalNumberElements() / pageSize));
        nextButton.setDisable(currentPage + 1 == noOfPages);
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


            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    private void showChatDialog(User from, User to) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("chat-view.fxml"));


            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            ChatController chatController = loader.getController();
            chatController.setUserService(chatService, from, to, dialogStage);

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

    public void handleChat(ActionEvent actionEvent) {
        if (!tableView.getSelectionModel().isEmpty()){
            UserDate friend = tableView.getSelectionModel().getSelectedItem();
            showChatDialog(user, friend.getUser());
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void handleBackPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }
}
