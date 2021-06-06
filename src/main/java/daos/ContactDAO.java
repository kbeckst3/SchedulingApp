package daos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import controllers.SignIn;
import models.Appointment;
import models.Contact;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Used to work with the contact table in the database
 */
public class ContactDAO {

    static final ZoneId utcZoneID = ZoneId.of("UTC");

    /**
     *
     * @return Gets a list of contacts
     */
    public static ObservableList<Contact> getAllContacts() throws SQLException, ClassNotFoundException {
        ObservableList<Contact> allCustomers = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select * From contacts");
            while (rs.next()) {
                int id = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                allCustomers.add(new Contact(id, contactName, email));
            }
        } catch (SQLException error) {
            System.out.println(error);
        }

        return allCustomers;
    }
    /**
     * Gets a list of appointments tied to a contact
     **/
    public static ObservableList<Appointment> getMyAppointments(int myID) throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select appointments.*, customers.Customer_Name, contacts.Contact_Name From appointments\n" +
                    "Inner Join customers On appointments.Customer_ID = customers.Customer_ID\n" +
                    "Inner Join contacts On appointments.Contact_ID = contacts.Contact_ID Where appointments.Contact_ID = "+ myID);
            AppointmentDAO.allAppointments(allAppointments, rs, utcZoneID);
        } catch (SQLException error) {

            System.out.println(error);

        }

        return allAppointments;
    }

    /**
     *
     * @param contactID
     * @return Gets a single contact
     */
    public static Contact getContact(int contactID) throws SQLException, ClassNotFoundException {

        Connection conn = DBConnection.getConn();
        Contact contactResult = null;
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select * From contacts WHERE Contact_ID =" + contactID);
            while (rs.next()) {
                int id = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                contactResult = new Contact(id, contactName, email);
            }
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }
        return contactResult;
    }


}
