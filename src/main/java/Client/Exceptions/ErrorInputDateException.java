package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ErrorInputDateException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Error Date";
        String header="Invalid input";
        String message="Inserted date is not valid! Select only dates after the current one";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }
}
