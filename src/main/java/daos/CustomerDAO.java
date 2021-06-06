package daos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import models.Customer;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Used to work with the customer table in the database
 */
public class CustomerDAO {
    /**
     *
     * @param customerID
     * @return Gets a single customer
     */
    public static Customer getCustomer(int customerID) throws SQLException, ClassNotFoundException {

        Connection conn = DBConnection.getConn();
        Customer customerResult = null;
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select customers.*, first_level_divisions.Division," +
                    " countries.Country_ID, countries.country From customers\n" +
                    "Inner Join first_level_divisions On customers.Division_ID = first_level_divisions.Division_ID\n" +
                    "Inner Join countries On first_level_divisions.Country_ID = countries.Country_ID WHERE customers.Customer_ID = " + customerID);
            while (rs.next()) {
                int id = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phoneNumber= rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                int countryID = rs.getInt("Country_ID");
                String country = rs.getString("Country");

                customerResult = new Customer(id, customerName, address, postalCode, phoneNumber, divisionID, division, countryID, country);
            }
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }
        return customerResult;
    }

    /**
     *
     * @return Gets a list of all the customer
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select customers.*, first_level_divisions.Division," +
                    " countries.Country_ID, countries.country From customers\n" +
                    "Inner Join first_level_divisions On customers.Division_ID = first_level_divisions.Division_ID\n" +
                    "Inner Join countries On first_level_divisions.Country_ID = countries.Country_ID;");
            while (rs.next()) {
                int id = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phoneNumber= rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                int countryID = rs.getInt("Country_ID");
                String country = rs.getString("Country");

                allCustomers.add(new Customer(id, customerName, address, postalCode, phoneNumber, divisionID, division, countryID, country));
            }
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }

        return allCustomers;
    }

    /**
     *
     * @param customer
     * @return add/edits a customer
     */
    public static boolean addEditCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        Connection conn = DBConnection.getConn();
        boolean wasSaved = false;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO customers" +
                    "(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Updated_By, Division_ID)" +
                    " VALUES ('"
                    + customer.getId() + "','"
                    + customer.getCustomerName() + "','"
                    + customer.getAddress() + "','"
                    + customer.getPostalCode() + "','"
                    + customer.getPhoneNumber() + "',"
                    + " NOW(),'"
                    + "Keller" + "','" //TODO Change this to whoever created the appointment
                    + "Keller" + "'," //TODO Change this to whoever last edited the appointment.
                    + customer.getDivisionID() + ")" +
                    "ON DUPLICATE KEY UPDATE " +
                    "Customer_Name = '" + customer.getCustomerName() + "', " +
                    "Address = '" + customer.getAddress() + "', " +
                    "Postal_Code = '" + customer.getPostalCode() + "', " +
                    "Phone = '" + customer.getPhoneNumber() + "', " +
                    "Last_Updated_By = '" + "Keller" + "', " +
                    "Division_ID = " + customer.getDivisionID() + "; ");
            wasSaved = true;
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }
        return wasSaved;
    }

    /**
     *
     * @param customerID
     * @return deletes a customer and returns a 1 if it was deleted
     */
    public static int deleteCustomer(int customerID) throws SQLException, ClassNotFoundException {

        int customerDeleted = 0;
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {

            int rs = stmt.executeUpdate("DELETE FROM customers WHERE Customer_ID =" + customerID);
            customerDeleted += rs;
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
            //Confirms that user want so delete customer and all the customers appointments
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, customerID + " This customer ID has appointments associated with it are you sure you want to delete?");
            Optional<ButtonType> option = alert.showAndWait();
            if(option.get() == ButtonType.OK) {
                AppointmentDAO.deleteCustomerAppointments(customerID);
            }
        }
        return customerDeleted;
    }
}
