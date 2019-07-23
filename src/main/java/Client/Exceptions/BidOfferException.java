package Client.Exceptions;

import Client.Controller.ControllerServices;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class BidOfferException extends RuntimeException {
    public void show(Stage popUpStage){
        String title="Error Offer";
        String header="Offer rejected";
        String message ="Offer must be greater than the actual one";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }

    public void show2(Stage popUpStage){
        String title="Error Offer";
        String header="Offer rejected";
        String message="You can't raise your own bid";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage, Alert.AlertType.ERROR);
    }


}
