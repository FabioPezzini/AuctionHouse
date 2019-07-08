package Client.Controller;

import Client.Domain.ClientManager;
import Server.Domain.Auction;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class CreateAuctionFormController {
    private ClientManager client;
    private Stage popUpStage;
    private Stage primaryStage;
    private  File selectedFile;
    private Auction auction;

    @FXML
    private JFXTextField itemName;
    private String name;

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
    private  JFXButton modifyAuction;

    @FXML
    private  JFXButton deleteAuction;

    @FXML
    private ImageView imageView;


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
            if(client.createAuctionGUI(name,price,close) == 1) {
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

    @FXML
    public void modifyAuction() throws RemoteException {
        name = null;
        price = -1;

        if( !itemName.getText().equals("") && !itemName.getText().equals(auction.getLot().getDescription())) {
            name = itemName.getText();
        }
        if(!basePrice.getText().equals("") && (Integer.parseInt(basePrice.getText())>0) && auction.getBidsList().size()==0) {
            price = Integer.parseInt(basePrice.getText());
        }

        client.modifyAuctio(name,price,auction.getId());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ottimo");
        alert.setHeaderText("Modifiche effettuate con successo, RICARICA LA PAGINA PER VEDERE GLI AGGIORNAMENTI");
        alert.initOwner(popUpStage);

        alert.showAndWait();
    }

    @FXML
    public void closeAuction() throws RemoteException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Auction");
        alert.setHeaderText("Are you sure want to delete this auction");

        Optional<ButtonType> option = alert.showAndWait();


        if (option.get() == null) {
        }
        else if (option.get() == ButtonType.OK) {
            client.closeAuction(auction.getId());
            popUpStage.close();
            primaryStage.close();
        } else if (option.get() == ButtonType.CANCEL) {
            popUpStage.close();
            primaryStage.close();
        }

    }



    public void setParameter() {
        Image img;

        if (auction.getImage() != null) {
            try {
                img = new Image(new FileInputStream(auction.getImage()), 100, 100, false, false);

                imageView.setImage(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                File file = null;
                try {
                    URL res = getClass().getClassLoader().getResource("Images/i_have_no_idea.png");
                    file = Paths.get(res.toURI()).toFile();
                }catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                String absolutePath = file.getAbsolutePath();
                img = new Image(new FileInputStream(absolutePath), 100, 100, false, false);

                imageView.setImage(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        itemName.setText(auction.getLot().getDescription());
        basePrice.setText(Integer.toString(auction.getHigherOffer()));
        closeDate.setVisible(false);
        closeTime.setVisible(false);
    }

    private boolean validateInput() {
        if(itemName.getText().equals(""))
            return false;
        name = itemName.getText();
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

    public void disableModifyDeleteAuction() {
        modifyAuction.setDisable(true);
        modifyAuction.setVisible(false);
        deleteAuction.setDisable(true);
        deleteAuction.setVisible(false);
    }

    public void disableCreateAuction() {
        createAuction.setDisable(true);
        createAuction.setVisible(false);

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

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
