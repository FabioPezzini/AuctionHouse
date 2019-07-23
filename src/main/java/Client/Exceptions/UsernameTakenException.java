package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UsernameTakenException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Error SignUp";
        String header="Username already taken";
        String message ="Username already exists";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.WARNING);
    }
}
