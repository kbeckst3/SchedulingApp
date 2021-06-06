package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Class is not fully implemented disregard functions here
 */
public class MyAccount {

    @FXML
    TextField userID, username;

    @FXML
    PasswordField password;

    public void initialize(){

        //Add users data to the form
        userID.setText(String.valueOf(SignIn.signedInUser.getId()));
        username.setText(SignIn.signedInUser.getUsername());
        password.setText(SignIn.signedInUser.getPassword());

    }
    //Allow user to leave form
    public void cancel(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MainMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }
    //Functionality not yet created.
    public void setUserPassword(){

    }
}
