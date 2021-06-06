package models;

/**
 * First level division model
 */
public class Division {

    private final int id;
    private final String divisionName;
    private final int associatedID;

    /**
     *
     * @param id
     * @param divisionName
     * @param associatedID
     * constructor for first level divisions
     */
    public Division(int id, String divisionName, int associatedID) {
        this.id = id;
        this.divisionName = divisionName;
        this.associatedID = associatedID;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public int getAssociatedID() {
        return associatedID;
    }
}

