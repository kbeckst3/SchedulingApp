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
import daos.CustomerDAO;
import daos.UtilityDAO;
import models.Country;
import models.Customer;
import models.Division;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 *  Controller for customer table gives functionality to table.
 */
public class CustomerTable {

    Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

    ObservableList<Customer> allCustomer = FXCollections.observableArrayList();

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Number> customerID;

    @FXML
    private TableColumn<Customer, String> customerName;

    @FXML
    private TableColumn<Customer, String> customerPhone;

    @FXML
    private TableColumn<Customer, String> customerAddress;

    @FXML
    private TableColumn<Customer, String> customerPostalCode;

    @FXML
    private TableColumn<Customer, String> customerCountry;

    @FXML
    private TableColumn<Customer, String> customerDivision;

    /**
     *  Initialize contains Lambas for binding cell factories
     *  double click edit functionality included here aswell.
     */
    public void initialize() throws SQLException, ClassNotFoundException {
        allCustomer.clear();
        allCustomer.addAll(CustomerDAO.getAllCustomers());
        //set cell factory for table columns
        customerID.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getId()));
        customerName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerName()));
        customerPhone.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPhoneNumber()));
        customerAddress.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAddress()));
        customerPostalCode.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPostalCode()));
        customerCountry.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCountry()));
        customerDivision.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDivision()));
        customerTable.setItems(allCustomer);

        //set double click edit for customer table
        customerTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && customerTable.getSelectionModel().getSelectedItem() != null) {
                    Customer customer = customerTable.getSelectionModel().getSelectedItem();
                    try {
                        editCustomer(customer);
                    } catch (IOException | SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    /**
     *  opens Customer form view
     */
    public void addCustomer(ActionEvent event) throws SQLException, ClassNotFoundException {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/fxml/Customer.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            initialize();
        } catch (IOException error) {
            System.err.println(error.getMessage());
        }

    }

    /**
     *  Deletes and confirms the delete with user
     */
    public void deleteCustomer(ActionEvent event) throws SQLException, ClassNotFoundException {

        int customerDelete = 0;
        Customer customer = customerTable.getSelectionModel().getSelectedItem();

        //Error alert thrown if no customer is selected
        if(customer == null){
            errorAlert.setContentText("No customer selected for a delete");
            errorAlert.showAndWait();
            return;
        }

        //Confirm they want to delete selected customer return if they don't
        confirmationAlert.setContentText(String.format("Are you sure you want to Delete Customer: ID %d Name %s",
                customer.getId(),
                customer.getCustomerName()));
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        //Confirms delete with user and awaits response
        if(result.isEmpty())
            return;
        else if(result.get() == ButtonType.OK)
            customerDelete = CustomerDAO.deleteCustomer(customer.getId());
        else
            return;

        //Provides a confirmation that the customer was deleted
        if(customerDelete > 0){
            informationAlert.setContentText(String.format("Customer Id: %d Name: %s has been deleted", customer.getId(), customer.getCustomerName()));
            informationAlert.showAndWait();
        }
        //Done to reload the table data
        initialize();

    }

    /**
     * Grabs customer if and loads it into customer form via the controller
     */
    void editCustomer(Customer customer) throws IOException, SQLException, ClassNotFoundException {

        //Appointment Edit Window
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/fxml/Customer.fxml"));
        Parent root = loader.load();
        Country country = UtilityDAO.getCountry(customer.getCountryID());
        Division division = UtilityDAO.getDivision(customer.getDivisionID());

        //Get customer form controller
        CustomerController customerController = loader.getController();
        //Load values into customer form
        customerController.customerID.setText(String.valueOf(customer.getId()));
        customerController.customerName.setText(customer.getCustomerName());
        customerController.customerPhone.setText(customer.getPhoneNumber());
        customerController.customerAddress.setText(customer.getAddress());
        customerController.customerPostalCode.setText(customer.getPostalCode());
        customerController.customerCountry.setValue(country);
        customerController.customerDivision.setValue(division);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        initialize();

    }

    //takes user back to Main Menu
    public void backToMainMenu(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MainMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }
    /**
     * Used to search table
     */
    public void searchTable(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        ObservableList<Customer> sortedCustomersList = FXCollections.observableArrayList();

        //Get Correct Text Field and perform search
        String[] customers = field.getText().toUpperCase().split(" ");
        for (Customer entry : allCustomer) {
            boolean match = true;
            String entryText = entry.toString();
            for (String customer : customers) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                if (!entryText.toUpperCase().contains(customer)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                sortedCustomersList.add(entry);
            }
        }
        customerTable.setItems(sortedCustomersList);
    }
}
