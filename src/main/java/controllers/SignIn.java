package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import utils.DBConnection;
import utils.PasswordAuthenticator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Controller for sign in form
 */
public class SignIn {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    //TODO remove this is temporary
    public static User signedInUser;

    //Used to set user timezone and for display timezone.
    public Calendar myZone = Calendar.getInstance();

    @FXML
    TextField username, password;

    @FXML
    Text signInHeader;

    @FXML
    Button signInButton;

    @FXML
    Text currentTimeZone;

    @FXML
    Text createAccount;

    @FXML
    Text usernameLabel, passwordLabel, timezoneLabel;

    public void initialize() {

        //get current TimeZone using getTimeZone method of Calendar class
        TimeZone timeZone = myZone.getTimeZone();

        currentTimeZone.setText(timeZone.getDisplayName());

    }

    public void unsuccessfulLogin(String uname) throws IOException{
        //Write successful login attempt to login_attempt text file
        writeToLoginAttempt(false, uname);
        errorAlert.setContentText("Username or Password is incorrect.. Try Again.");
        errorAlert.showAndWait();
    }
    /**
     * Checks and signs user into software. Throw validation errors if user enters non valid user. Can throw errors in French.
     */
    public void signInUser(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException, SQLException, IOException {
        String uname = username.getText();
        String pword = password.getText();


        ResultSet rs;
                //Create Database connection and check if any of any users with username and password exist.
                Connection conn = DBConnection.getConn();
                PreparedStatement login = conn.prepareStatement("SELECT * FROM users WHERE User_Name=?");

                login.setString(1, uname);
                rs = login.executeQuery();

                if (rs.next()) {
                    String password = rs.getString("Password");
                    System.out.println(password);
                    boolean validPassword = PasswordAuthenticator.validatePassword(pword, password);
                    if (validPassword) {

                        //Write successful login attempt to login_attempt text file
                        writeToLoginAttempt(true, uname);
                        //add user to static variable for use in other application features.
                        int userID = rs.getInt("User_ID");
                        String username = rs.getString("User_Name");
                        signedInUser = new User(userID, username, password, myZone.getTimeZone());

                        //if they exist load the Main Menu
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MainMenu.fxml")));
                        stage.setScene(new Scene(root));
                        stage.show();
                    } else
                        unsuccessfulLogin(uname);
                }
                 else
                    unsuccessfulLogin(uname);

    }

    /**
     * function for appending login tries to login attempt text file.
     */
    public void writeToLoginAttempt(boolean loginAttempt, String username) throws IOException {
        String textToAppend;

        if (loginAttempt)
            textToAppend = String.format("\r\n Successful Login: Username %s login at Timestamp: %s", username, Timestamp.from(Instant.now()));
        else
            textToAppend = String.format("\r\n Unsuccessful Login: Username %s login attempt at Timestamp: %s", username, Timestamp.from(Instant.now()));

        //writes to login activity text file
        Path path = Paths.get("login_activity.txt");
        Files.write(path, textToAppend.getBytes(), StandardOpenOption.APPEND);

    }

}