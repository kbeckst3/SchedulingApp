package controllers;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import daos.UserDAO;
import models.Appointment;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Main menu for the whole program
 */
public class MainMenu {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    ObservableList<Appointment> myAppointmentsToday = FXCollections.observableArrayList();

    @FXML
    private TableView<Appointment> todayTable;

    @FXML
    private TableColumn<Appointment, String> appointmentTitle, appointmentStart;

    @FXML
    private TableColumn<Appointment, Number> appointmentID;

    @FXML
    private Button editAccount;

    @FXML
    private Button customerMenu;

    @FXML
    private Button appointmentMenu;

    @FXML
    private Button myReportsMenu;

    @FXML
    private Text alertHeader;

    @FXML
    public ListView<String> alertWindowList;
    /**
     * Intialize calls function for creating alerts and causes listview strings to wrap.
     *  Explanation for lambda found inside of function
     */
    public void initialize() throws SQLException, ClassNotFoundException {
        myAppointmentsToday = UserDAO.getMyAppointmentsToday(SignIn.signedInUser.getId());

        appointmentID.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getId()));
        appointmentTitle.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
        appointmentStart.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStartDateTimeString()));

        todayTable.setItems(myAppointmentsToday);
        //Use some sort of sort of observable list to list out alerts for Users(use appointmentAlert object in list)
        alertWindowList.setItems(userAppointmentAlerts());

        //Used to wrap text in the list view
        //Lambda was used here to quickly set the cell factory and allow listview to wrap string after it reached the edge of the screen
        alertWindowList.setCellFactory(param -> new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setGraphic(null);
                    setText(null);
                    // other stuff to do...

                }else{

                    // set the width's
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());

                    // allow wrapping
                    setWrapText(true);

                    setText(item.toString());


                }
            }
        });

    }

    /**
     * Opens appointment window
     */
    public void openAppointmentMenu(ActionEvent event) throws IOException {
        //Grabs the stage and set a appointment menu
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/AppointmentTable.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Opens account window
     */
    public void openCustomerMenu(ActionEvent event) throws IOException {
        //Grabs the stage and set a customer menu
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/CustomerTable.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Opens account window
     */
    public void openEditAccount(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MyAccount.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Opens Report menu
     */
   public void openMyReportsMenu(ActionEvent event) throws IOException {
        //Grabs the stage and set a My reports menu
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MyReports.fxml")));
        stage.setScene(new Scene(root));
        stage.show();

    }

    /**
     * Used to get user appointments that start in 15 mins or less.
     */
    ObservableList<String> userAppointmentAlerts() {
        ObservableList<String> appointmentsInFifteen = FXCollections.observableArrayList();
        //Get a list of users appointments.
        myAppointmentsToday.forEach(appointment -> {
            //convert times to local date times
            LocalDateTime timestamp1 = appointment.getStartDateTime().toLocalDateTime();
            LocalDateTime timestamp2 = LocalDateTime.now();
            //check how many minutes are between appointment and users current time
            long difference_In_Time = timestamp2.until(timestamp1, ChronoUnit.MINUTES);
            //add appointments to list view if they happen within 15 mins
            if(difference_In_Time <= 15 && difference_In_Time > 0){
                appointmentsInFifteen.add(String.format("Appointment Id: %d Start Date and Time: %s", appointment.getId(), appointment.getStartDateTimeString()));
            }
        });
        if(appointmentsInFifteen.isEmpty())
            appointmentsInFifteen.add("No upcoming appointments");
        return appointmentsInFifteen;
    }

}
