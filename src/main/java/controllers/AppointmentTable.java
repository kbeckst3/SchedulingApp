package controllers;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import daos.AppointmentDAO;
import daos.ContactDAO;
import daos.CustomerDAO;
import models.Appointment;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for the appointment table page
 */
public class AppointmentTable {
    //Alerts can be used throughout the appointment table
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    public ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    public ToggleGroup appointmentTableView;

    @FXML
    TableView<Appointment> appointmentTable;

    @FXML
    TableColumn<Appointment, String> appointmentTitle, appointmentDescription, appointmentType, appointmentStart, appointmentEnd, appointmentCreatedBy, customerName, coachName;

    @FXML
    TableColumn<Appointment, Number> appointmentID;

    @FXML
    RadioButton tableMonthView, tableWeekView;

    @FXML
    TextField searchTextBox;

    /**
     * Lambda used inside of a initialize method to provide brevity in setting celldata to a property of an appointment
     */
    public void initialize() throws SQLException, ClassNotFoundException {
        //clears all appointments to stop program from doubling up.
        allAppointments.clear();
        allAppointments.addAll(AppointmentDAO.getAllAppointments());
        //Setting table cell values via setCellValueFactory
        appointmentID.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getId()));
        appointmentTitle.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
        appointmentDescription.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDescription()));
        appointmentType.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getType()));
        appointmentStart.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStartDateTimeString()));
        appointmentEnd.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEndDateTimeString()));
        appointmentCreatedBy.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCreatedBy()));
        customerName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerName()));
        coachName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getContactName()));
        appointmentTable.setItems(allAppointments);

        //Adding functionality to onMouseClicked to for implementing appointment edit
        appointmentTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && appointmentTable.getSelectionModel().getSelectedItem() != null) {
                    Appointment appointment = appointmentTable.getSelectionModel().getSelectedItem();
                    try {
                        editAppointment(appointment);
                    } catch (IOException | SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    /**
     *
     * @param appointment
     * @throws IOException Exception being used to catch exception on the FXML loader
     * appointment is passed in and loaded into the appointment window for editing
     */
    public void editAppointment(Appointment appointment) throws IOException, SQLException, ClassNotFoundException {
        //Appointment Edit Window
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/fxml/Appointment.fxml"));
        Parent root = loader.load();

        //Date time formatter used to format time for appointment time field
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        //Get appointment controller
        AppointmentController appointmentController = loader.getController();

        //Load values into appointment form
        appointmentController.appointmentID.setText(String.valueOf(appointment.getId()));
        appointmentController.appointmentTitle.setText(appointment.getTitle());
        appointmentController.appointmentType.setValue(appointment.getType());
        appointmentController.appointmentLocation.setText(appointment.getLocation());
        appointmentController.customerName.setValue(CustomerDAO.getCustomer(appointment.getCustomerID()));
        appointmentController.coachName.setValue(ContactDAO.getContact(appointment.getContactID()));
        appointmentController.appointmentStartDate.setValue(appointment.getStartDateTime().toLocalDate());
        appointmentController.appointmentStartTime.setText(timeFormatter.format(appointment.getStartDateTime()));
        appointmentController.appointmentEndDate.setValue(appointment.getEndDateTime().toLocalDate());
        appointmentController.appointmentEndTime.setText(timeFormatter.format(appointment.getEndDateTime()));
        appointmentController.appointmentDescription.setText(appointment.getDescription());

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        initialize();


    }

    /**
     *  Shows appointments happening within the week
     */
    public void changeToWeekView() throws SQLException, ClassNotFoundException {
        searchTextBox.clear();
        allAppointments.clear();
        allAppointments.addAll(AppointmentDAO.getNextWeekAppointments());
        appointmentTable.setItems(allAppointments);
    }
    /**
     *  Shows appointments happening within the month
     */
    public void changeToMonthView() throws SQLException, ClassNotFoundException {
        searchTextBox.clear();
        allAppointments.clear();
        allAppointments.addAll(AppointmentDAO.getAllThisMonthAppointments());
        appointmentTable.setItems(allAppointments);
    }
    /**
     *  Shows all appointments
     */
    public void changeToAllView() throws SQLException, ClassNotFoundException {
        searchTextBox.clear();
        allAppointments.clear();
        allAppointments.addAll(AppointmentDAO.getAllAppointments());
        appointmentTable.setItems(allAppointments);
    }

    /**
     *  Opens the appointment form window
     */
    public void addAppointment() throws SQLException, ClassNotFoundException {

        try {
            Stage stage = new Stage();
            //Open appointment window
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Appointment.fxml")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            initialize();
        } catch (IOException error) {
            System.err.println(error.getMessage());
        }
    }

    /**
     * Deletes appointment also confirms deletion to user
     */
    public void deleteAppointment() throws SQLException, ClassNotFoundException {

        Appointment appointment = appointmentTable.getSelectionModel().getSelectedItem();

        //Checks to make sure appointment has been selected
        if(appointment == null){
            errorAlert.setContentText("No appointment selected for delete");
            errorAlert.showAndWait();
            return;
        }
        //Confirms delete with user
        confirmationAlert.setContentText("Are you sure you want to delete this appointment");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        //check users reply perform action or return
        if(result.get() == ButtonType.OK){
            AppointmentDAO.deleteAppointment(appointment.getId());
            confirmationAlert.setContentText(String.format("Appointment ID: %d Type: %s was deleted", appointment.getId(), appointment.getType()));
            confirmationAlert.showAndWait();
            initialize();
        }else {
            return;
        }

    }

    /**
     * filters the table based off text entered into the search box
     * @param event
     * function called every key press in the search box
     */
    public void searchTable(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        ObservableList<Appointment> sortedAppointmentList = FXCollections.observableArrayList();
        //Get Correct Text Field and perform search
        String[] appointments = field.getText().toUpperCase().split(" ");
        for (Appointment entry : allAppointments) {
            boolean match = true;
            String entryText = entry.toString();
            for (String appointment : appointments) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                if (!entryText.toUpperCase().contains(appointment)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                sortedAppointmentList.add(entry);
            }
        }
        appointmentTable.setItems(sortedAppointmentList);
    }


    public void backToMainMenu(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MainMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
