package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class EmailTakenException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Error SignUp";
        String header="Change your email";
        String message ="Email already taken";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }
}
