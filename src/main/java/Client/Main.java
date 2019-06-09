package Client;

import Client.Controller.LoginDataController;
import Client.Controller.SignUpController;
import Client.Domain.ClientManager;
import Client.Domain.ConnectionLayer;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.rmi.RemoteException;

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ConnectionLayer connection;
    private ClientManager c;

    private LoginDataController loginDataController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AuctionHouse");

        initRootLayout();

        //Event Handler per la chiusura del client dopo aver premuto sulla classica X
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if(c.getLoggedUser() != null) {
                    try {
                        c.logoutGUI();
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                connection.setUserWantDisconnect(true);
            }
        });

    }


    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            connection = new ConnectionLayer("hii");
            c = new ClientManager(connection, connection.getServer());

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/main/java/Client/Controller/Login.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

            primaryStage.setResizable(false);

            //Animate the stage
            new FadeIn(rootLayout).play();

            //Passo riferimento a stage e connessione
            loginDataController = (LoginDataController) loader.getController();
            loginDataController.setPrimaryStage(primaryStage);
            loginDataController.setClient(c);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
