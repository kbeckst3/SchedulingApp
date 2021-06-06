package daos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Country;
import models.Division;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UtilityDAO {

    /**
     * @return gets a list of countries
     */
    public static ObservableList<Country> getAllCountries() throws SQLException, ClassNotFoundException {
        ObservableList<Country> countries = FXCollections.observableArrayList();
        Connection connection = DBConnection.getConn();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM countries");
        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            countries.add(new Country(countryID, countryName));
        }
        return countries;
    }

    /**
     * @param id
     * @return Gets a country from an id
     */
    public static Country getCountry(int id) throws ClassNotFoundException, SQLException {

        Connection connection = DBConnection.getConn();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM countries WHERE Country_ID = " + id);
        if (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            return new Country(countryID, countryName);
        }

        return null;
    }

    /**
     * @return gets a list of all first level divisions
     */
    public static ObservableList<Division> getAllDivisions() throws ClassNotFoundException {
        ObservableList<Division> divisions = FXCollections.observableArrayList();
        try (Connection connection = DBConnection.getConn()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM first_level_divisions");
            while (rs.next()) {
                int divisionID = rs.getInt("Division_ID");
                String divisionName = rs.getString("Division");
                int associatedCountryID = rs.getInt("Country_ID");
                divisions.add(new Division(divisionID, divisionName, associatedCountryID));
            }
        } catch (SQLException error) {
            error.getErrorCode();
        }
        return divisions;
    }

    /**
     * @param id
     * @return Gets a division from a division id
     */
    public static Division getDivision(int id) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConn();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM first_level_divisions WHERE Division_ID = " + id);
            if (rs.next()) {
                int divisionID = rs.getInt("Division_ID");
                String divisionName = rs.getString("Division");
                int associatedCountryID = rs.getInt("Country_ID");
                return new Division(divisionID, divisionName, associatedCountryID);
            }

        return null;
    }

    //Gets a list of divisions by country id
    public static ObservableList<Division> getAllDivisionsByCountry(int countryID) throws SQLException, ClassNotFoundException {
        ObservableList<Division> divisionsByCountry = FXCollections.observableArrayList();
        Connection connection = DBConnection.getConn();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM first_level_divisions WHERE Country_ID = " + countryID);
            if (rs.next()) {
                int divisionID = rs.getInt("Division_ID");
                String divisionName = rs.getString("Division");
                int associatedCountryID = rs.getInt("Country_ID");
                divisionsByCountry.add(new Division(divisionID, divisionName, associatedCountryID));
            }

        return divisionsByCountry;
    }
}
