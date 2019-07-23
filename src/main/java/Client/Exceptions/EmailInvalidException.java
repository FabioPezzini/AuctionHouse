package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class EmailInvalidException extends RuntimeException {

    public void show(Stage primaryStage){
        String title="Error e-mail";
        String header="Invalid input";
        String message ="E-mail is not valid";
        ControllerServices.getInstance().showAlert(title,header,message,primaryStage, Alert.AlertType.WARNING);

    }
}
