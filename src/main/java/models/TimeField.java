package models;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * Time field used for appointment times to stop user from entering in an invalid time.
 */
public class TimeField extends TextField {
    //This is used the prevent the user from entering an incorrect time format.
    public TimeField() {
        super();

        focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                //Checks to see if time input matches regex if not it throws an error and clears the Timefield
                if (!getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Not a valid time");
                    alert.showAndWait();
                    setText("");
                }
            }
        });
    }
}
