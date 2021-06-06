package models;

/**
 * Model for contact
 */
public class Contact {

    private final int id;
    private String contactName;
    private String email;

    /**
     *
     * @param id
     * @param contactName
     * @param email
     * constructor for the contact
     */
    public Contact(int id, String contactName, String email) {
        this.id = id;
        this.contactName = contactName;
        this.email = email;
    }
    //Getters and setters
    public int getId() {
        return id;
    }
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
/** To string for contact */
    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", contactName='" + contactName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
