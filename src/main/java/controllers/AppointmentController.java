package controllers;

import daos.AppointmentDAO;
import daos.ContactDAO;
import daos.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.Appointment;
import models.Contact;
import models.Customer;
import models.MainOffice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 Controller controls the functionality of the appointment form
 */

public class AppointmentController {

    ObservableList<String> appointmentTypes =  FXCollections.observableArrayList(Arrays.asList("Career Change",
            "Getting a Promotion",
            "Finding a job",
            "Finding Balance",
            "Skill Finding/Growing",
            "Interest Assessment"));
    ObservableList<String> appointmentMediums = FXCollections.observableArrayList(Arrays.asList("In Person",
            "Phone Call",
            "Video Call"));
    //Alerts for the scope of this controller
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    //Used to format date and time fields for database
    DateTimeFormatter datepickerFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");

    //Contacts are retrieved to here so the don't need to be retrieved everytime dropdown is called
    ObservableList<Customer> customerNames = CustomerDAO.getAllCustomers();
    ObservableList<Contact> contactNames = ContactDAO.getAllContacts();

    @FXML
    public TextField appointmentID, appointmentTitle, appointmentLocation, appointmentStartTime, appointmentEndTime, userID;

    @FXML
    public ComboBox<String> appointmentType, appointmentMedium;

    @FXML
    public ComboBox<Contact> coachName;

    @FXML
    public ComboBox<Customer> customerName;

    @FXML
    public DatePicker appointmentStartDate, appointmentEndDate;

    //Not being implemented in this version
//    @FXML
//    public ToggleButton appointmentStartAM, appointmentStartPM, appointmentEndAM, appointmentEndPM;

    @FXML
    public TextArea appointmentDescription;

    public AppointmentController() throws SQLException, ClassNotFoundException {
    }

    public void initialize() {

        appointmentType.setItems(appointmentTypes);

        coachName.setItems(contactNames);


        StringConverter<Contact> contactStringConverter = new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                return contact.getContactName();
            }

            @Override
            public Contact fromString(String name) {
                List<Contact> list = new ArrayList<>();
                for (Contact contact : contactNames) {
                    if (contact.getContactName().equals(name)) {
                        list.add(contact);
                    }
                }
                return list.get(0);
            }
        };
        coachName.setConverter(contactStringConverter);


        customerName.setItems(customerNames);
        //String converter used to have customer name displayed instead of object string
        StringConverter<Customer> customerStringConverter = new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer.getCustomerName();
            }

            @Override
            public Customer fromString(String name) {
                return customerNames.stream()
                        .filter(contact -> contact.getCustomerName().equals(name))
                        .collect(Collectors.toList()).get(0);
            }
        };
        customerName.setConverter(customerStringConverter);

        //Sets user ID field to currently logged in users ID
        userID.setText(String.valueOf(SignIn.signedInUser.getId()));
    }

    /**
     *
     * @param event is only passed in the close the appointment form window once it is save
     *  Save appointment grabs all form fields and creates a new appointment object to pass to the database to save
     *
     */
    public void saveAppointment(ActionEvent event) throws SQLException, ClassNotFoundException {
        AtomicBoolean timeConflict = new AtomicBoolean(false);
        //checking to make sure necessary fields are filled out.
        if(fieldsChecked())
            return;
        //Description is not necessary adding "none" if users enters nothing
        if(appointmentDescription.getText().isBlank())
            appointmentDescription.setText("none");

        LocalDateTime startDateTime = LocalDateTime.parse((datepickerFormatter.format(appointmentStartDate.getValue()) + " " + appointmentStartTime.getText()), sdf);
        LocalDateTime endDateTime = LocalDateTime.parse((datepickerFormatter.format(appointmentEndDate.getValue()) + " " + appointmentEndTime.getText()), sdf);
        ZonedDateTime zonedStartDateTime = startDateTime.atZone(SignIn.signedInUser.getCurrentTimeZone().toZoneId());
        ZonedDateTime zonedEndDateTime = endDateTime.atZone(SignIn.signedInUser.getCurrentTimeZone().toZoneId());
        System.out.println(zonedStartDateTime);
        System.out.println(zonedEndDateTime);
        if(endDateTime.isBefore(startDateTime)){
            //Appointment end time is before appointment start time
            errorAlert.setContentText("Appointment end time takes place before appointment start time.");
            errorAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            errorAlert.showAndWait();
            return;
        }
        if(!endDateTime.toLocalDate().isEqual(startDateTime.toLocalDate())){
            //Appointment start and end dates need to be on the same date
            errorAlert.setContentText("Appointment start and end date must be on the same day.");
            errorAlert.showAndWait();
            return;
        }
        if(!MainOffice.isAppointmentInOfficeHours(zonedStartDateTime, zonedEndDateTime)){
            //Appointment is not in office hours
            errorAlert.setContentText("Appointment must take place during office hours which are 8 AM 10 PM EST please adjust appointment accordingly");
            errorAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            errorAlert.showAndWait();
            return;
        }
        /**
         *  For loop used to check if there is a conflict in the customers or contacts schedule
         */
        for (Appointment appointment1 : AppointmentDAO.getAllAppointments()) {
            if (appointment1.getId() != Integer.parseInt(appointmentID.getText())) {

                if (appointment1.getCustomerID() == customerName.getSelectionModel().getSelectedItem().getId()) {
                    if (!((zonedStartDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedStartDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getEndDateTime())) && zonedEndDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedEndDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getEndDateTime())))
                            || (zonedStartDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedStartDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getEndDateTime())) && zonedEndDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedEndDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getEndDateTime()))))) {
                        //Appointment is conflicting with another appointment for this customer.
                        errorAlert.setContentText("Customer already has appointment during this time.");
                        errorAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        errorAlert.showAndWait();
                        timeConflict.set(true);
                        break;

                    }
                } else if (appointment1.getContactID() == coachName.getSelectionModel().getSelectedItem().getId()) {
                    if (!((zonedStartDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedStartDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getEndDateTime())) && zonedEndDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedEndDateTime.isBefore(ChronoZonedDateTime.from(appointment1.getEndDateTime())))
                            || (zonedStartDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedStartDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getEndDateTime())) && zonedEndDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getStartDateTime())) && zonedEndDateTime.isAfter(ChronoZonedDateTime.from(appointment1.getEndDateTime()))))) {
                        //Appointment is conflicting with another appointment for this contact.
                        errorAlert.setContentText("Contact already has an appointment associated with this time.");
                        errorAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
                        errorAlert.showAndWait();
                        timeConflict.set(true);
                        break;

                    }
                }
            }
        }
        if(timeConflict.get())
            return;
        //Create a new appointment
        Appointment appointment = new Appointment(
                Integer.parseInt(appointmentID.getText()),
                appointmentTitle.getText(),
                appointmentDescription.getText(),
                appointmentLocation.getText(),
                appointmentType.getSelectionModel().toString(),
                zonedStartDateTime,
                zonedEndDateTime,
                ZonedDateTime.now(),
                //Grabs username of user that is currently logged in
                SignIn.signedInUser.getUsername(),
                customerName.getSelectionModel().getSelectedItem().getId(),
                customerName.getSelectionModel().getSelectedItem().getCustomerName(),
                Integer.parseInt(userID.getText()),
                coachName.getSelectionModel().getSelectedItem().getId(),
                coachName.getSelectionModel().getSelectedItem().getContactName()
        );
        //send appointment to database to either add or edit an appointment
        AppointmentDAO.addEditAppointment(appointment);
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     *
     * @param event used to cancel appointment form window
     */
    public void cancelAppointmentWindow(ActionEvent event) {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * Checks all form fields to ensure fields have valid inputs.
     * @return boolean value
     */
    public boolean fieldsChecked(){
        if(appointmentTitle.getText().equals("")){
            errorAlert.setContentText("Please fill in an appointment title");
            errorAlert.showAndWait();
            return true;
        }
        if(appointmentLocation.getText().equals("")){
            errorAlert.setContentText("Please fill in an appointment location");
            errorAlert.showAndWait();
            return true;
        }
        if(appointmentType.getSelectionModel().getSelectedItem() == null){
            errorAlert.setContentText("Please fill in an appointment type");
            errorAlert.showAndWait();
            return true;
        }
        if(appointmentStartDate.getValue() == null) {
            errorAlert.setContentText("Please fill in an appointment start date");
            errorAlert.showAndWait();
            return true;
        }
        if(appointmentStartTime.getText().equals("")){
            errorAlert.setContentText("Please fill in an appointment start time");
            errorAlert.showAndWait();
            return true;
        }
        if(appointmentEndDate.getValue() == null) {
            errorAlert.setContentText("Please fill in an appointment end date");
            errorAlert.showAndWait();
            return true;
        }
        if(appointmentEndTime.getText().equals("")){
            errorAlert.setContentText("Please fill in an appointment end time");
            errorAlert.showAndWait();
            return true;
        }
        if(customerName.getSelectionModel().getSelectedItem() == null){
            errorAlert.setContentText("Please choose a customer name");
            errorAlert.showAndWait();
            return true;
        }
        if(coachName.getSelectionModel().getSelectedItem() == null){
            errorAlert.setContentText("Please choose a contact name");
            errorAlert.showAndWait();
            return true;
        }
        return false;
    }

}
