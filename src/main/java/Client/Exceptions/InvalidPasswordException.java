package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class InvalidPasswordException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Wrong Password";
        String header="Invalid input";
        String message ="Password entered incorrectly";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }
}
