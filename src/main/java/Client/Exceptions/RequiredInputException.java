package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class RequiredInputException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Error Input";
        String header="Invalid input";
        String message="All the fields are required except for the image";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }
}
