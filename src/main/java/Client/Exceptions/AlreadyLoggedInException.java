package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AlreadyLoggedInException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Error SignIn";
        String header="It seems like you're already signed in!";
        String message ="Someone is alredy logged in your account";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage,Alert.AlertType.ERROR);
    }
}
