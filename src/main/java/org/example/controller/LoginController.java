package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.HelloApplication;
import org.example.domain.User;
import org.example.service.SocialNetwork;
import org.example.utils.events.UserEntityChangeEvent;
import org.example.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController implements Observer<UserEntityChangeEvent> {

    SocialNetwork service;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    ComboBox<User> comboUser;


    public void setUserService(SocialNetwork service){
        this.service = service;
        initModel();
        this.service.addObserver(this);
    }

    private void initModel() {
        List<User> all_users= StreamSupport.stream(service.getUsers().spliterator(), false).collect(Collectors.toList());
        model.setAll(all_users);
    }

    @FXML
    public void initialize(){
        comboUser.setItems(model);
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }


    public void showUserEditDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
//            loader.setLocation(getClass().getResource("../views/edit-user-view.fxml"));


            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friends");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            UserController userController = loader.getController();
            userController.setUserService(service, user);
            System.out.println(user.getFirstName());

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onUserSelected(ActionEvent actionEvent) {
        showUserEditDialog(comboUser.getValue());
    }
}
