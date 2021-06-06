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
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import daos.AppointmentDAO;
import daos.ContactDAO;
import models.Appointment;
import models.Contact;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MyReports {

    //Alerts
    Alert alert = new Alert(Alert.AlertType.ERROR);

    ObservableList<Contact> contactNames = ContactDAO.getAllContacts();

    @FXML
    StackedBarChart<String, Double> appointmentByTypeChart;

    //These are for the contact schedule page.

    @FXML
    ChoiceBox<Contact> coachDropDown;

    @FXML
    TableColumn<Appointment, String> appointmentTitle, appointmentDescription, appointmentType, appointmentStart, appointmentEnd, customerName;

    @FXML
    TableColumn<Appointment, Number> appointmentID;

    @FXML
    TableView<Appointment> coachSchedule;

    //These are for the number of appointments scheduled by customer

    @FXML
    TableView<Pair<String, Number>> customerAppointmentCountTable;

    @FXML
    TableColumn<Pair<String, Number>, String> customerNameCount;

    @FXML
    TableColumn<Pair<String, Number>, Number> numberOfAppointments;

    public MyReports() throws SQLException, ClassNotFoundException {
    }


    public void initialize() throws SQLException, ClassNotFoundException {

        appointmentByTypeChart.getData().addAll(appointmentSeriesListByTypeAndMonth());

        /**
         initialize contacts schedule table
         */
        appointmentID.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getId()));
        appointmentTitle.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
        appointmentDescription.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDescription()));
        appointmentType.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getType()));
        appointmentStart.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStartDateTimeString()));
        appointmentEnd.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEndDateTimeString()));
        customerName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerName()));


        coachDropDown.setItems(contactNames);

        /**
        Convert contact from object name reference to contact name
         */
        StringConverter<Contact> contactStringConverter = new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                return contact.getContactName();
            }

            @Override
            public Contact fromString(String name) {
                return contactNames.stream()
                        .filter(contact -> contact.getContactName().equals(name))
                        .collect(Collectors.toList()).get(0);
            }
        };
        coachDropDown.setConverter(contactStringConverter);

        //Initialization for the # of appointments by Customer page
        customerNameCount.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getKey()));
        numberOfAppointments.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper((Integer) cellData.getValue().getValue()));

    }
    /**
        This function returns an Observable list of appointments by type and month
    */
    public ObservableList<XYChart.Series<String, Double>> appointmentSeriesListByTypeAndMonth() throws SQLException, ClassNotFoundException {

        //A series for each a month
        final XYChart.Series<String, Double> January =
                new XYChart.Series<String, Double>();
        January.setName("January");
        final XYChart.Series<String, Double> February =
                new XYChart.Series<String, Double>();
        February.setName("February");
        final XYChart.Series<String, Double> March =
                new XYChart.Series<String, Double>();
        March.setName("March");
        final XYChart.Series<String, Double> April =
                new XYChart.Series<String, Double>();
        April.setName("April");
        final XYChart.Series<String, Double> May =
                new XYChart.Series<String, Double>();
        May.setName("May");
        final XYChart.Series<String, Double> June =
                new XYChart.Series<String, Double>();
        June.setName("June");
        final XYChart.Series<String, Double> July =
                new XYChart.Series<String, Double>();
        July.setName("July");
        final XYChart.Series<String, Double> August =
                new XYChart.Series<String, Double>();
        August.setName("August");
        final XYChart.Series<String, Double> September =
                new XYChart.Series<String, Double>();
        September.setName("September");
        final XYChart.Series<String, Double> October =
                new XYChart.Series<String, Double>();
        October.setName("October");
        final XYChart.Series<String, Double> November =
                new XYChart.Series<String, Double>();
        November.setName("November");
        final XYChart.Series<String, Double> December =
                new XYChart.Series<String, Double>();
        December.setName("December");

        final ObservableList<Appointment> allAppointments = AppointmentDAO.getAllAppointments();
        //Get the add for each month
        January.getData().addAll(getCountByMonthAndTypeDataSet("January", allAppointments));
        February.getData().addAll(getCountByMonthAndTypeDataSet("February", allAppointments));
        March.getData().addAll(getCountByMonthAndTypeDataSet("March", allAppointments));
        April.getData().addAll(getCountByMonthAndTypeDataSet("April", allAppointments));
        May.getData().addAll(getCountByMonthAndTypeDataSet("May", allAppointments));
        June.getData().addAll(getCountByMonthAndTypeDataSet("June", allAppointments));
        July.getData().addAll(getCountByMonthAndTypeDataSet("July", allAppointments));
        August.getData().addAll(getCountByMonthAndTypeDataSet("August", allAppointments));
        September.getData().addAll(getCountByMonthAndTypeDataSet("September", allAppointments));
        October.getData().addAll(getCountByMonthAndTypeDataSet("October", allAppointments));
        November.getData().addAll(getCountByMonthAndTypeDataSet("November", allAppointments));
        December.getData().addAll(getCountByMonthAndTypeDataSet("December", allAppointments));

        //add months to series list
        final ObservableList<XYChart.Series<String, Double>> series =
                FXCollections.observableArrayList();
        series.add(January);
        series.add(February);
        series.add(March);
        series.add(April);
        series.add(May);
        series.add(June);
        series.add(July);
        series.add(August);
        series.add(September);
        series.add(October);
        series.add(November);
        series.add(December);

        return series;
    }

    /**
    Returns a data list base on a month and a list of appointments used in Main bar chart
     */
    public ObservableList<XYChart.Data<String, Double>> getCountByMonthAndTypeDataSet(String month, ObservableList<Appointment> allAppointments) {
        ObservableList<Appointment> appointmentInMonth = appointmentsForMonth(month, allAppointments);
        ObservableList<XYChart.Data<String, Double>> result = FXCollections.observableArrayList();
        //Loop through list of types
        for (String type : getListOfTypes(appointmentInMonth)) {
            //get the number of occurrences of that type
            double typeCount = getTypeAmount(type, appointmentInMonth);
            //add data to observablelist
            result.add(new XYChart.Data<String, Double>(type, typeCount));
        }
        return result;
    }

    /**
    Returns all appointments for a particular month
     */
    public ObservableList<Appointment> appointmentsForMonth(String month, ObservableList<Appointment> allAppointments) {
        ObservableList<Appointment> appointmentForMonth = FXCollections.observableArrayList();
        for (Appointment appointment : allAppointments) {
            if (appointment.getStartDateTime().getMonth().name().equals(month.toUpperCase())) {
                appointmentForMonth.add(appointment);
            }
        }
        return appointmentForMonth;
    }

    /**
    Returns a list of types that are found in a particular month
     */
    public ObservableList<String> getListOfTypes(ObservableList<Appointment> allAppointments) {
        ObservableList<String> listOfTypes = FXCollections.observableArrayList();
        for (Appointment appointment : allAppointments) {
            if (!listOfTypes.contains(appointment.getType()))
                listOfTypes.add(appointment.getType());
        }
        return listOfTypes;
    }

    /**
    Returns a double for the amount of occurrences of a certain type.
     */
    public double getTypeAmount(String type, ObservableList<Appointment> allAppointments) {
        AtomicInteger numberOfOccurrences = new AtomicInteger();
        for (Appointment appointment : allAppointments) {
            if (appointment.getType().equals(type))
                numberOfOccurrences.getAndIncrement();
        }
        return numberOfOccurrences.get();
    }

    /**
    Used to generate appointments into a the contacts schedule table
     */
    public void generateSchedule() throws SQLException, ClassNotFoundException {

        //Grabs the selected contact and creates their schedule
        if (coachDropDown.getSelectionModel().getSelectedItem() == null) {
            alert.setContentText("A contact must be selected to generate the schedule");
            alert.showAndWait();
            return;
        }
        Contact contact = coachDropDown.getSelectionModel().getSelectedItem();
        coachSchedule.setItems(ContactDAO.getMyAppointments(contact.getId()));
        initialize();

    }

    public void loadAppointments() throws SQLException, ClassNotFoundException {
        customerAppointmentCountTable.setItems(AppointmentDAO.getAppointmentCustomerCount());
    }


    public void backToMainMenu(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MainMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
