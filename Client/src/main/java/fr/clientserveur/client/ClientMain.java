package fr.clientserveur.client;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.utils.Config;
import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurCentral;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import fr.clientserveur.common.utils.Utils;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe principale du client
 */
public class ClientMain extends Application {

    private InterfaceServeurMagasin server;
    private Magasin magasin;
    private InterfaceController controller;
    private Stage primaryStage;
    private Scene scene;

    private Label centerLabel;
    private Label connectMenuLabel;
    private AnchorPane mainPane;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Chargement de la configuration
        Config.getInstance().loadOrCreateConfig();

        // Création et affichage de la fenêtre
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("Client");
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Bind interface
        this.centerLabel = (Label) scene.lookup("#centerLabel");
        this.connectMenuLabel = (Label) scene.lookup("#connectMenuLabel");
        this.mainPane = (AnchorPane) scene.lookup("#mainPane");

        // Affichage chargement
        centerLabel.setText("Connexion en cours ...");

        // Connexion au serveur magasin
        Task<InterfaceServeurMagasin> task = new Task<InterfaceServeurMagasin>() {
            @Override
            public InterfaceServeurMagasin call() throws RemoteException, NotBoundException {
                Registry registry = LocateRegistry.getRegistry(Config.SERVER_IP, Config.SERVER_PORT);
                if(Utils.isPublicIp(Config.SERVER_IP))
                    return (InterfaceServeurMagasin) registry.lookup("ServeurMagasinRemote");
                else
                    return (InterfaceServeurMagasin) registry.lookup("ServeurMagasinLocal");
            }
        };
        task.setOnSucceeded(event -> {
            this.server = task.getValue();
            loadMagasin();
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            this.centerLabel.setText("Erreur de connexion");
        });
        new Thread(task).start();
    }

    private void loadMagasin()
    {
        this.centerLabel.setText("Chargement du magasin...");
        Task<Magasin> task = new Task<Magasin>() {
            @Override
            public Magasin call() throws RemoteException {
                return server.getCurrentMagasin();
            }
        };
        task.setOnSucceeded(event -> {
            this.magasin = task.getValue();
            this.connectMenuLabel.setText("Connecté à " + this.magasin.getNom());
            controller = new InterfaceController(this.server, scene);
            controller.showMagasin();
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            this.centerLabel.setText("Erreur de chargement magasin");
        });
        new Thread(task).start();
    }

}
