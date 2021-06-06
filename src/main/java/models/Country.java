package models;

/**
 * Model for country
 */
public class Country {

    private final int id;
    private final String countryName;

    /**
     *
     * @param id
     * @param countryName
     * constructor for country
     */
    public Country(int id, String countryName) {
        this.id = id;
        this.countryName = countryName;
    }
    //Getters and setters
    public int getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", countryName='" + countryName + '\'' +
                '}';
    }
}
