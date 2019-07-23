package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class IncorrectPasswordException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Invalid Password";
        String header="Follow the rules!";
        String message ="Password requires at least 8 characters. Password must contain lowercase and uppercase letter,numbers and at least one special character";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }

}
