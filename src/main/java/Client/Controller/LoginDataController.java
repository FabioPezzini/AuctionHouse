package Client.Controller;

import Client.Domain.ClientManager;
import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.rmi.RemoteException;


public class LoginDataController {
    private ClientManager client;
    private Stage primaryStage;

    private HomeController homeController;
    private SignUpController signupController;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton signIn;

    @FXML
    private JFXButton signUp;


    @FXML
    private void handleSignIn() throws IOException {

        String us = username.getText();
        String pass = password.getText();

        int esito = client.loginGUI(us,pass);

        if(esito == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ottimo");
            alert.setHeaderText("Login Eseguito");

            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/Home.fxml"));
            Parent home = (Parent) loader.load();

            homeController = (HomeController)loader.getController();
            homeController.setPrimaryStage(primaryStage);
            homeController.setClient(client);
            homeController.setHomeController(homeController);

            primaryStage.setScene(new Scene(home));
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
    private void changeSceneSignUp() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/Client/Controller/SignUp.fxml"));
        Parent signup = (Parent) loader.load();

        Stage popUpStageSignUp = new Stage(StageStyle.TRANSPARENT);
        popUpStageSignUp.initOwner(primaryStage);
        popUpStageSignUp.initModality(Modality.APPLICATION_MODAL);
        Scene sigUpScene = new Scene(signup);
        popUpStageSignUp.setScene(sigUpScene);

        // Calculate the center position of the parent Stage
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth()/2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight()/2d;

        // Hide the pop-up stage before it is shown and becomes relocated
        popUpStageSignUp.setOnShowing(ev -> popUpStageSignUp.hide());

        // Relocate the pop-up Stage
        popUpStageSignUp.setOnShown(ev -> {
            popUpStageSignUp.setX(centerXPosition - popUpStageSignUp.getWidth()/2d);
            popUpStageSignUp.setY(centerYPosition - popUpStageSignUp.getHeight()/2d);
            popUpStageSignUp.show();
        });

        popUpStageSignUp.show();

        new FadeIn(signup).play();


        signupController = (SignUpController) loader.getController();
        signupController.setPopUpStage(popUpStageSignUp);
        signupController.setClient(client);

    }

    public ClientManager getClient() { return client; }

    public void setClient(ClientManager client) { this.client = client; }

    public Stage getPrimaryStage() { return primaryStage; }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public HomeController getHomeController() { return homeController; }

    public void setHomeController(HomeController homeController) { this.homeController = homeController; }

    public SignUpController getSignupController() { return signupController; }

    public void setSignupController(SignUpController signupController) { this.signupController = signupController; }

}
