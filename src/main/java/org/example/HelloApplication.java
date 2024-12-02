package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.validator.FriendshipValidator;
import org.example.domain.validator.UserValidator;
import org.example.repository.FriendshipDBRepository;
import org.example.repository.Repository;
import org.example.repository.UserDBRepository;
import org.example.service.SocialNetwork;

import java.io.IOException;

public class HelloApplication extends Application {
    SocialNetwork service;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Reading data from database");
        String username="postgres";
        String pasword="postgres";
        String url="jdbc:postgresql://localhost:5432/socialnetwork";
        UserDBRepository repoUser = new UserDBRepository(new UserValidator());
        FriendshipDBRepository repoFriendship = new FriendshipDBRepository(repoUser, new  FriendshipValidator(repoUser));
        service = new SocialNetwork(repoUser, repoFriendship);
        initView(stage);
        stage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));

        primaryStage.setScene(new Scene(fxmlLoader.load()));

        LoginController userController = fxmlLoader.getController();
        userController.setUserService(service);
    }
}