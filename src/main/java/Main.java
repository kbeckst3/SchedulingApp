import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main function for program
 */
public class Main extends Application {


    /**
     * Start of program
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/SignIn.fxml")));
            primaryStage.setTitle("Scheduling Application");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException error){
            System.err.print(error.getMessage());
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
