package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import daos.CustomerDAO;
import daos.UtilityDAO;
import models.Country;
import models.Customer;
import models.Division;

import java.sql.SQLException;
import java.util.stream.Collectors;

/**
    Controller for the customer form
 */
public class CustomerController {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    ObservableList<Country> countries = FXCollections.observableArrayList();
    ObservableList<Division> divisions = FXCollections.observableArrayList();

    @FXML
    public TextField customerName;

    @FXML
    public TextField customerPhone;

    @FXML
    public TextField customerAddress;

    @FXML
    public TextField customerPostalCode;

    @FXML
    public ComboBox<Country> customerCountry;

    @FXML
    public ComboBox<Division> customerDivision;

    @FXML
    public TextField customerID, divisionID, countryID;

    /**
     * Initialize contains the two string converters for the country and division combo boxes
     * Lmabda used her in the filter portion of the string converter again to make the code more efficient by making more brief and easier to read.
     */
    public void initialize() {
        customerCountry.setItems(countries);
        customerDivision.setItems(divisions);

        // Displays name of country in combobox
        StringConverter<Country> countryStringConverter = new StringConverter<Country>() {
            @Override
            public String toString(Country country) {
                return country.getCountryName();
            }

            @Override
            public Country fromString(String name) {
                return countries.stream()
                        .filter(country -> country.getCountryName().equals(name))
                        .collect(Collectors.toList()).get(0);
            }
        };
        customerCountry.setConverter(countryStringConverter);
        //Displays name of divisions in Combobox
        StringConverter<Division> divisionStringConverter = new StringConverter<Division>() {
            @Override
            public String toString(Division division) {
                return division.getDivisionName();
            }

            @Override
            public Division fromString(String name) {
                return divisions.stream()
                        .filter(division -> division.getDivisionName().equals(name))
                        .collect(Collectors.toList()).get(0);
            }
        };
        customerDivision.setConverter(divisionStringConverter);


    }

    /**
     *
     * @param event closes customer form
     * used to add or update a customer
     */
    public void saveCustomer(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (fieldsChecked())
            return;

        //Creates customer to be saved or edited
        Customer customer = new Customer(
                Integer.parseInt(customerID.getText()),
                customerName.getText(),
                customerAddress.getText(),
                customerPhone.getText(),
                customerPostalCode.getText(),
                customerDivision.getSelectionModel().getSelectedItem().getId());
        //Sends customer object to DAO static method
        CustomerDAO.addEditCustomer(customer);
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * Closes customer form window
    */
    public void cancelCustomerWindow(ActionEvent event) {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * Loads countries into combobox on select of combobox
     */
    public void loadCountries() throws SQLException, ClassNotFoundException {
        if (countries.isEmpty()) {
            countries.addAll(UtilityDAO.getAllCountries());
            customerCountry.setItems(countries);
        }
    }

    /**
     * loads divisions in based on country
     */
    public void loadDivisions() throws ClassNotFoundException {
        //Stops divisions from being selected if country isn't selected.
        if (customerCountry.getSelectionModel().getSelectedItem() == null) {
            errorAlert.setContentText("Must select a country first");
            errorAlert.showAndWait();
            return;
        }
        //Help speed up loading divisions(divisions only load if country changes or divisions are empty)
        if (customerDivision.getSelectionModel().isEmpty()) {
            divisions.addAll(UtilityDAO.getAllDivisions());
            customerDivision.setItems(divisions.filtered(divs -> divs.getAssociatedID() == customerCountry.getSelectionModel().getSelectedItem().getId()));
        }

    }

    /**
     * clears division box if country has been changed and country is no longer associated with selected division
     */
    public void checkDivisions() {
        if (!customerDivision.getSelectionModel().isEmpty()) {
            if (customerCountry.getSelectionModel().getSelectedItem().getId() != customerDivision.getSelectionModel().getSelectedItem().getAssociatedID()) {
                customerDivision.getSelectionModel().clearSelection();
            }
        }
    }
    /**
     * Checks form fields before allowing a save.
     */
    public boolean fieldsChecked() {

        String mcustomerName = customerName.getText();
        String mcustomerAddress = customerAddress.getText();
        String mcustomerPhone = customerPhone.getText();
        String mcustomerPostalCode = customerPostalCode.getText();
        if (mcustomerName.equals("")) {
            errorAlert.setContentText("Please enter a customer name");
            errorAlert.showAndWait();
            return true;
        }
        if (mcustomerAddress.equals("")) {
            errorAlert.setContentText("Please enter in customer address");
            errorAlert.showAndWait();
            return true;
        }
        if (mcustomerPhone.equals("")) {
            errorAlert.setContentText("Please enter customer phone number");
            errorAlert.showAndWait();
            return true;
        }
        if (mcustomerPostalCode.equals("")) {
            errorAlert.setContentText("Please enter in customer postal code");
            errorAlert.showAndWait();
            return true;
        }
        if (customerDivision.getSelectionModel().getSelectedItem() == null) {
            errorAlert.setContentText("Please select a customer division");
            errorAlert.showAndWait();
            return true;
        }
        return false;
    }
}
