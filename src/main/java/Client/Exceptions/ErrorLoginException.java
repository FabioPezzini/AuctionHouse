package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ErrorLoginException extends RuntimeException{
    public void show(Stage popUpStage){
        String title="Error Login";
        String header="Check your credentials";
        String message ="Username and Password doesn't exist!";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }

}
