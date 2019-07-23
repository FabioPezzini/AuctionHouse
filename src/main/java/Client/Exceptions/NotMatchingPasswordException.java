package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class NotMatchingPasswordException extends RuntimeException {

    public void show(Stage popUpStage){
        String title="Password doesn't match";
        String header="Passwords must match";
        String message = "New passwords are different";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.WARNING);
    }
}
