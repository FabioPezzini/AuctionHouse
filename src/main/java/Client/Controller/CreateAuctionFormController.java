package Client.Controller;

import Client.Exceptions.ErrorInputDateException;
import Client.Exceptions.RequiredInputException;
import Server.Domain.Auction;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class CreateAuctionFormController extends TemplateController {
    private  File selectedFile;
    private Auction auction;

    @FXML
    private AnchorPane windowsPane;

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
    private JFXListView<String> listview;

    @FXML
    private JFXButton createAuction;

    @FXML
    public void loadImageAction() {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            String extension = selectedFile.getAbsolutePath().replaceAll("^[^.]*.", "");  //Regex per ricavare l'estensione
            if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
                listview.getItems().add(selectedFile.getAbsolutePath());
            } else {
                String title="Error File extension";
                String header="Operation failed";
                String message="File is not valid! Only jpg and png are allowed";
                ControllerServices.getInstance().showAlert(title,header,message,popUpStage,Alert.AlertType.ERROR);
                selectedFile=null;
            }
        }
    }

    @FXML
    public void createAuctionAction() throws RemoteException {
        try{
            validateInput();
            try{
                client.createAuctionGUI(name,price,close);

                if(selectedFile != null) {
                    client.sendFile(selectedFile);
                }
                String title="Success";
                String header="Auction added";
                String message="Auction created successfully";
                ControllerServices.getInstance().showAlert(title,header,message,popUpStage,Alert.AlertType.INFORMATION);
                backToHome();
            }
           catch(ErrorInputDateException e) {
                e.show(popUpStage);
            }
        }
        catch(RequiredInputException e){
            e.show(popUpStage);
        }
    }

    @FXML
    public void modifyAuction() throws RemoteException {
        name = null;
        price = -1;

        if( !itemName.getText().equals("") && !itemName.getText().equals(auction.getDescriptionLot())) {//protected variation
            name = itemName.getText();
        }
        if(!basePrice.getText().equals("") && (Integer.parseInt(basePrice.getText())>0) && auction.getBidsList().size()==0) {
            price = Integer.parseInt(basePrice.getText());
        }

        client.modifyAuctio(name,price,auction.getId());

        String title="Success";
        String header="Done";
        String message="Auction modified successfully. Refresh the page";
        ControllerServices.getInstance().showAlert(title,header,message,popUpStage,Alert.AlertType.INFORMATION);

    }

    @FXML
    public void closeAuction() throws RemoteException {
        String title="Delete Auction";
        String headerText="Are you sure want to delete this auction";
        Optional<ButtonType> option =ControllerServices.getInstance().getSelectionFromAlert(title,headerText,"",new Alert(Alert.AlertType.CONFIRMATION));
        if(option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                client.closeAuction(auction.getId());
                popUpStage.close();
                primaryStage.close();
            } else if (option.get() == ButtonType.CANCEL) {
                popUpStage.close();
                primaryStage.close();
            }
        }

    }


    @FXML
    public void handleCursorHand() {
        popUpStage.getScene().setCursor(Cursor.HAND);
    }

    @FXML
    public void handleCursor() {
        popUpStage.getScene().setCursor(Cursor.DEFAULT);
    }



    void setParameter() {
        ControllerServices.getInstance().setImagetoTheAuction(auction,imageView);
        itemName.setText(auction.getDescriptionLot());//protected var
        basePrice.setText(Integer.toString(auction.getHigherOffer()));
        closeDate.setVisible(false);
        closeTime.setVisible(false);
        if(auction.getBidsList().size()!=0)
            basePrice.setDisable(true);
    }

    private void validateInput() {
        try {
            if (itemName.getText().equals(""))
                throw new RequiredInputException();
            name = itemName.getText();
            if (basePrice.getText().equals(""))
                throw new RequiredInputException();
            price = Integer.parseInt(basePrice.getText());
            if (closeDate.getValue() == null)
                throw new RequiredInputException();
            if (closeTime.getValue() == null)
                throw new RequiredInputException();
            LocalDate date = closeDate.getValue();
            LocalTime time = closeTime.getValue();
            close = LocalDateTime.of(date, time);
        }catch (NumberFormatException e) {
            throw new RequiredInputException();
        }
    }

    void disableModifyDeleteAuction() {
        modifyAuction.setDisable(true);
        modifyAuction.setVisible(false);
        deleteAuction.setDisable(true);
        deleteAuction.setVisible(false);
    }

    void disableCreateAuction() {
        createAuction.setDisable(true);
        createAuction.setVisible(false);

    }

    void initializeWindow() {
        popUpStage.getScene().setFill(Color.TRANSPARENT);
        windowsPane.setStyle(

                "-fx-background-insets: 5; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);"
        );
    }

    @FXML
    private void backToHome() {
        AnchorPane pane = (AnchorPane) primaryStage.getScene().lookup("#windowsPane");
        pane.setEffect(null);
        popUpStage.close();
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }


}
