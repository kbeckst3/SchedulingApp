package daos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import controllers.SignIn;
import models.Appointment;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Used to work with the user table in the database
 */
public class UserDAO {
    //Used to set logged in users time zone
    static final ZoneId utcZoneID = ZoneId.of("UTC");

    /**
     * @return gets a list of users appointmetns
     */
    public static ObservableList<Appointment> getMyAppointmentsToday(int userID) throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        System.out.println(userID);
        Connection conn = DBConnection.getConn();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("Select * From appointments WHERE user_id = " + userID + "AND startdatetime > now() - interval '1 year'");
            while (rs.next()) {
                int id = rs.getInt("appointment_id");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Medium");
                String type = rs.getString("Type");
                ZonedDateTime startDateTime = rs.getTimestamp("startdatetime").toLocalDateTime().atZone(utcZoneID);
                ZonedDateTime endDateTime = rs.getTimestamp("enddatetime").toLocalDateTime().atZone(utcZoneID);
                ZonedDateTime createdDateTime = rs.getTimestamp("Create_Date").toLocalDateTime().atZone(utcZoneID);
                String createdBy = rs.getString("Created_By");
                int customerID = rs.getInt("Customer_ID");
                int consultantID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");

                //Adjust displayed times and dates to whatever the users timezone is.

                ZonedDateTime adjustedStartDateTime = startDateTime.withZoneSameInstant(SignIn.signedInUser.getCurrentTimeZone().toZoneId());
                ZonedDateTime adjustedEndDateTime = endDateTime.withZoneSameInstant(SignIn.signedInUser.getCurrentTimeZone().toZoneId());

                allAppointments.add(new Appointment(id, title, description, location, type, adjustedStartDateTime, adjustedEndDateTime, createdDateTime, createdBy, customerID, consultantID, contactID));
            }
        } catch (SQLException error) {

            System.out.println(error.getErrorCode());

        }

        return allAppointments;
    }
}
