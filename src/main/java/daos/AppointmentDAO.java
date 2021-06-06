package daos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import controllers.SignIn;
import models.Appointment;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * DAO for appointments
 */
public class AppointmentDAO {


    static final ZoneId utcZoneID = ZoneId.of("UTC");

    /**
     * Gets all appointments
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select appointments.*, customers.Customer_Name, contacts.Contact_Name From appointments\n" +
                    "Inner Join customers On appointments.Customer_ID = customers.Customer_ID\n" +
                    "Inner Join contacts On appointments.Contact_ID = contacts.Contact_ID;");
            allAppointments(allAppointments, rs, utcZoneID);
        } catch (SQLException error) {

            System.out.println(error.getErrorCode());

        }

        return allAppointments;
    }

    static void allAppointments(ObservableList<Appointment> allAppointments, ResultSet rs, ZoneId utcZoneID) throws SQLException {
        while (rs.next()) {
            int id = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Medium");
            String type = rs.getString("Type");
            ZonedDateTime startDateTime = rs.getTimestamp("startdatetime").toLocalDateTime().atZone(utcZoneID);
            ZonedDateTime endDateTime = rs.getTimestamp("enddatetime").toLocalDateTime().atZone(utcZoneID);
            ZonedDateTime createdDateTime = rs.getTimestamp("Create_Date").toLocalDateTime().atZone(utcZoneID);
            String createdBy = rs.getString("Created_By");
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            int consultantID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");

            //Adjust displayed times and dates to whatever the users timezone is.

            ZonedDateTime adjustedStartDateTime = startDateTime.withZoneSameInstant(SignIn.signedInUser.getCurrentTimeZone().toZoneId());
            ZonedDateTime adjustedEndDateTime = endDateTime.withZoneSameInstant(SignIn.signedInUser.getCurrentTimeZone().toZoneId());

            allAppointments.add(new Appointment(id, title, description, location, type, adjustedStartDateTime, adjustedEndDateTime, createdDateTime, createdBy, customerID, customerName, consultantID, contactID, contactName));
        }
    }

    /**
     * Used to get view for this months appointments
     */
    public static ObservableList<Appointment> getAllThisMonthAppointments() throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select appointments.*, customers.Customer_Name, contacts.Contact_Name From appointments\n" +
                    "Inner Join customers On appointments.Customer_ID = customers.Customer_ID\n" +
                    "Inner Join contacts On appointments.Contact_ID = contacts.Contact_ID WHERE MONTH(appointments.Start) = MONTH(CURRENT_DATE())\n" +
                    "AND YEAR(appointments.Start) = YEAR(CURRENT_DATE());");
            allAppointments(allAppointments, rs, utcZoneID);
        } catch (SQLException error) {

            System.out.println(error.getErrorCode());

        }

        return allAppointments;
    }

    /**
     * Used to get next weeks appointments
     */
    public static ObservableList<Appointment> getNextWeekAppointments() throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select appointments.*, customers.Customer_Name, contacts.Contact_Name From appointments\n" +
                    "Inner Join customers On appointments.Customer_ID = customers.Customer_ID\n" +
                    "Inner Join contacts On appointments.Contact_ID = contacts.Contact_ID WHERE appointments.Start < DATE_ADD(CURDATE(), INTERVAL 7 DAY);");
            allAppointments(allAppointments, rs, utcZoneID);
        } catch (SQLException error) {

            System.out.println(error.getErrorCode());

        }

        return allAppointments;
    }

//    /**
//     * Get appointment count by type
//     */
//    public static Series<String, Number> getAppointmentTypeCount() {
//        Series<String, Number> series = new Series<String, Number>();
//        Connection conn = DBConnection.getConn();
//        try (Statement stmt = conn.createStatement()) {
//            ResultSet rs = stmt.executeQuery("SELECT appointments.Type, COUNT(*) FROM appointments GROUP BY Type");
//            while (rs.next()) {
//                String type = rs.getString("Type");
//                int count = rs.getInt(2);
//
//                series.getData().add(new XYChart.Data<String, Number>(type, count));
//            }
//        } catch (SQLException error) {
//
//            System.out.println(error.getErrorCode());
//
//        }
//        return series;
//    }

    /**
     * Get appointment count by customer
     */
    public static ObservableList<Pair<String, Number>> getAppointmentCustomerCount() throws SQLException, ClassNotFoundException {
        ObservableList<Pair<String, Number>> customerAppointmentCount = FXCollections.observableArrayList();
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT customers.*,  COUNT(appointments.Appointment_ID) as appointments_scheduled FROM customers " +
                    " Left Join  appointments ON (appointments.Customer_ID = customers.Customer_ID) Group By Customer_ID;");
            while (rs.next()) {
                String customerName = rs.getString("Customer_Name");
                int count = rs.getInt("appointments_scheduled");

                customerAppointmentCount.add(new Pair<>(customerName, count));
            }
        } catch (SQLException error) {

            System.out.println(error.getErrorCode());

        }
        return customerAppointmentCount;
    }

    /**
     * Used to add/edit appointments depending on appointment that is passed in
     *
     * @param appointment appointment
     */
    public static void addEditAppointment(Appointment appointment) throws SQLException, ClassNotFoundException {
        Connection conn = DBConnection.getConn();
        System.out.println(appointment.getStartDateTime());

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO appointments" +
                    "(Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                    " VALUES ('"
                    + appointment.getId() + "','"
                    + appointment.getTitle() + "','"
                    + appointment.getDescription() + "','"
                    + appointment.getLocation() + "','"
                    + appointment.getType() + "','"
                    + appointment.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() + "','"
                    + appointment.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() + "',"
                    + " NOW(),'"
                    + SignIn.signedInUser.getUsername() + "','"
                    + SignIn.signedInUser.getUsername() + "',"
                    + appointment.getCustomerID() + ","
                    + appointment.getUserID() + ","
                    + appointment.getContactID() + ")" +
                    "ON DUPLICATE KEY UPDATE " +
                    "Title = '" + appointment.getTitle() + "', " +
                    "Description = '" + appointment.getDescription() + "', " +
                    "Location = '" + appointment.getLocation() + "', " +
                    "Type = '" + appointment.getType() + "', " +
                    "Start = '" + appointment.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() + "', " +
                    "End = '" + appointment.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() + "', " +
                    "Last_Updated_By = '" + SignIn.signedInUser.getUsername() + "', " +
                    "Customer_ID =" + appointment.getCustomerID() + ", " +
                    "User_ID =" + appointment.getUserID() + ", " +
                    "Contact_ID =" + appointment.getContactID() + "; ");
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }
    }

    /**
     * Deletes appointment
     */
    public static void deleteAppointment(int appointmentID) throws SQLException, ClassNotFoundException {
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM appointments WHERE Appointment_ID =" + appointmentID);
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }

    }

    public static void deleteCustomerAppointments(int customerID) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConn();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("Delete From appointments WHERE Customer_ID =" + customerID);
        } catch (SQLException error) {
            System.out.println(error.getErrorCode());
        }
        CustomerDAO.deleteCustomer(customerID);
    }
}

