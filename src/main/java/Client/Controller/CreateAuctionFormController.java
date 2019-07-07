package Client.Controller;

import Client.Domain.ClientManager;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateAuctionFormController {
    private ClientManager client;
    private Stage popUpStage;
    private Stage primaryStage;
    private  File selectedFile;

    @FXML
    private JFXTextField itemName;
    private String name;

    @FXML
    private JFXTextField description;
    private String desc;

    @FXML
    private JFXTextField basePrice;
    private int price;

    @FXML
    private JFXDatePicker closeDate;

    @FXML
    private JFXTimePicker closeTime;

    private LocalDateTime close;

    @FXML
    private JFXButton loadImage;


    @FXML
    private JFXListView listview;

    @FXML
    private JFXButton createAuction;

    @FXML
    public void loadImageAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);


        if (selectedFile != null) {
            String extension = selectedFile.getAbsolutePath().replaceAll("^[^.]*.", "");  //Regex per ricavare l'estensione
            if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
                listview.getItems().add(selectedFile.getAbsolutePath());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error File");
                alert.setHeaderText("File is not valid! Only jpg and png are allowed");
                alert.initOwner(popUpStage);

                alert.showAndWait();
            }
        }
    }

    @FXML
    public void createAuctionAction(ActionEvent event) throws RemoteException {
        if(validateInput()) {
            if(client.createAuctionGUI(name,desc,price,close) == 1) {
                if(selectedFile != null) {
                    client.sendFile(selectedFile);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ottimo");
                alert.setHeaderText("Asta Creata con successo");
                alert.initOwner(popUpStage);

                alert.showAndWait();

                backToHome();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Date");
                alert.setHeaderText("La data inserita non e' valida, inserire una data successiva a quella attuale");
                alert.initOwner(popUpStage);

                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Input");
            alert.setHeaderText("I campi non posso essere nulli, eccetto descrizione ed immagine");
            alert.initOwner(popUpStage);

            alert.showAndWait();
        }
    }

    private boolean validateInput() {
        if(itemName.getText().equals(""))
            return false;
        name = itemName.getText();
        desc = description.getText();
        if(basePrice.getText().equals(""))
            return false;
        price = Integer.parseInt(basePrice.getText());
        if(closeDate.getValue() == null)
            return false;
        if(closeTime.getValue() == null)
            return false;
        LocalDate date = closeDate.getValue();
        LocalTime time = closeTime.getValue();
        close = LocalDateTime.of(date,time);

        return true;
    }

    @FXML
    private void backToHome() {
        AnchorPane pane = (AnchorPane) primaryStage.getScene().lookup("#windowsPane");
        pane.setEffect(null);
        popUpStage.close();
    }

    public void setClient(ClientManager client) {
        this.client = client;
    }

    public void setPopUpStage(Stage popUpStage) {
        this.popUpStage = popUpStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
