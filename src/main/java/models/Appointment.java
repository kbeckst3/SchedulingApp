package models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model for appointments
 */
public class Appointment {

    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private String type;
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final ZonedDateTime createdDateTime;
    private final String createdBy;
    private final int customerID;
    private String customerName;
    private final int userID;
    private final int contactID;
    private String contactName;


    public Appointment(int id, String title, String description, String location, String type, ZonedDateTime startDateTime, ZonedDateTime endDateTime, ZonedDateTime createdDateTime, String createdBy, int customerID, String customerName, int userID, int contactID, String contactName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.createdDateTime = createdDateTime;
        this.createdBy = createdBy;
        this.customerID = customerID;
        this.customerName = customerName;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Appointment(int id, String title, String description, String location, String type, ZonedDateTime startDateTime, ZonedDateTime endDateTime, ZonedDateTime createdDateTime, String createdBy, int customerID, int userID, int contactID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.createdDateTime = createdDateTime;
        this.createdBy = createdBy;
        this.customerID = customerID;
        this.contactID = contactID;
        this.userID = userID;
    }

    //Getters and setters for object
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public String getStartDateTimeString() {
        return startDateTime.format(formatter);
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getEndDateTimeString() {
        return endDateTime.format(formatter);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getUserID() {
        return userID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getContactID() {
        return contactID;
    }

    public String getContactName() {
        return contactName;
    }

    /**
     *
     * @return to string class for appointment
     */
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", createdDateTime=" + createdDateTime +
                ", createdBy='" + createdBy + '\'' +
                ", customerID=" + customerID +
                ", customerName='" + customerName + '\'' +
                ", consultantID=" + userID +
                ", contactID=" + contactID +
                ", contactName='" + contactName + '\'' +
                '}';
    }
}
