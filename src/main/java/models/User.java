package models;

import java.util.TimeZone;

/**
 * User model
 */
public class User {

    private final int id;
    private String username;
    private String password;
    private TimeZone currentTimeZone;

    /**
     *
     * @param id
     * @param username
     * @param password
     * @param currentTimeZone
     * User constructor
     */
    public User(int id, String username, String password, TimeZone currentTimeZone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.currentTimeZone = currentTimeZone;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TimeZone getCurrentTimeZone() {
        return currentTimeZone;
    }

    public void setCurrentTimeZone(TimeZone currentTimeZone) {
        this.currentTimeZone = currentTimeZone;
    }
}


