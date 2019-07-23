package Client.Controller;

import Server.Domain.Auction;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class ControllerServices {

    private static ControllerServices instance;

    public static ControllerServices getInstance() {
        if(instance==null)
            instance = new ControllerServices();
        return instance;
    }

    void setImagetoTheAuction(Auction auction, ImageView auctionImage){

        if(auction.getImage() == null) {
            loadLocalImage(auctionImage);
        }
        else {
            try {
                Image img = new Image(new FileInputStream(auction.getImage()),268,226,false,false);
                auctionImage.setImage(img);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void loadLocalImage(ImageView auctionImage){
        try {
                File file;
                URL res = getClass().getClassLoader().getResource("Images/Destrauction.png");
                assert res != null;
                file = Paths.get(res.toURI()).toFile();
                String absolutePath = file.getAbsolutePath();
                Image img = new Image(new FileInputStream(absolutePath),268,226,false,false);
                auctionImage.setImage(img);
            }
        catch (URISyntaxException|FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void setCenterofPage(Stage children,Stage parent){
        double centerXPosition =parent.getX() + parent.getWidth()/2d;
        double centerYPosition = parent.getY() + parent.getHeight()/2d;

        // Hide the pop-up stage before it is shown and becomes relocated
        children.setOnShowing(ev ->children.hide());

        // Relocate the pop-up Stage
        children.setOnShown(ev -> {
            children.setX(centerXPosition - children.getWidth()/2d);
            children.setY(centerYPosition - children.getHeight()/2d);
            children.show();
        });

        children.show();
    }

    public void showAlert(String title, String header,String message, Window owner, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();

    }

    Optional<String> getSelectionFromDialog(String title,String headerText, String message, Dialog dialog){
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }
    Optional<ButtonType> getSelectionFromAlert(String title,String headerText, String message, Alert alert){
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
