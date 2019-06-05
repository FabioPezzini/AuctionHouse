package Client.Controller;

import Client.Domain.ClientManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.rmi.RemoteException;


public class LoginDataController {
    private ClientManager client;
    private Stage primaryStage;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton signIn;

    @FXML
    private JFXButton signUp;

    private HomeController homeController;


    @FXML
    private void handleSignIn() throws RemoteException, IOException {

        String us = username.getText();
        String pass = password.getText();

        int esito = client.loginGUI(us,pass);

        if(esito == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ottimo");
            alert.setHeaderText("Login Eseguito");

            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/Home.fxml"));
            Parent root = (Parent) loader.load();

            homeController = (HomeController)loader.getController();
            homeController.setPrimaryStage(primaryStage);
            homeController.setClient(client);
            homeController.setHomeController(homeController);

            primaryStage.setScene(new Scene(root));



        }
        if(esito == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Login");
            alert.setHeaderText("Username and Password doesn't exist!");

            alert.showAndWait();
        }
        if(esito == -1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error SignUp");
            alert.setHeaderText("Invalid Password");
            alert.setContentText("Someone is alredy logged in your account,if you believe it is an unauthorized access contact the system's manager ");

            alert.showAndWait();
        }

    }

    @FXML
    private void changeSceneSignUp() throws RemoteException, IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/SignUp.fxml"));
        Parent root = (Parent) loader.load();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(primaryStage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.setScene(new Scene(root));
        popUpStage.show();

        ((SignUpController)loader.getController()).setClient(client);
        ((SignUpController)loader.getController()).setPopUpStage(popUpStage);

    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public ClientManager getClient() { return client; }

    public void setClient(ClientManager client) { this.client = client; }
}
