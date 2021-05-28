package fr.clientserveur.client.javafx.windows;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Facture;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class FactureWindow {

    private final Stage stage;
    private final InterfaceController controller;
    private final Facture facture;

    public FactureWindow(
            InterfaceController controller,
            Facture facture,
            List<Achat> achats,
            BigDecimal total
    ) throws IOException {
        this.stage = new Stage();
        this.controller = controller;
        this.facture = facture;

        // Création et affichage de la fenêtre
        Parent root = FXMLLoader.load(getClass().getResource("/facture.fxml"));
        stage.setTitle("Facture");
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Définition champs
        ((Label)scene.lookup("#numero")).setText("Facture N°" + facture.getId());
        ((Label)scene.lookup("#date")).setText("Daté du "
                + new SimpleDateFormat("dd/MM/yyyy à HH:mm").format(facture.getDate()));
        ((Label)scene.lookup("#client")).setText(facture.getClient().getPrenom() + " "
                + facture.getClient().getNom());
        ((Label)scene.lookup("#email")).setText(facture.getClient().getEmail());
        ((Label)scene.lookup("#adresse1")).setText(facture.getClient().getAdresse1());
        ((Label)scene.lookup("#adresse2")).setText(facture.getClient().getAdresse2());
        ((Label)scene.lookup("#magasin")).setText("Magasin : " + facture.getMagasin().getNom());
        ((Label)scene.lookup("#total")).setText("Total : " + new DecimalFormat("#0.##").format(total) + "€");
        if (facture.getMoyenPaye() != null) {
            scene.lookup("#pay").setDisable(true);
            ((Label)scene.lookup("#payed")).setText("Payé : " + new SimpleDateFormat("dd/MM/yyyy HH:mm")
                    .format(facture.getDatePaye()) + "(" + facture.getMoyenPaye().getNom() + ")");
        } else {
            scene.lookup("#pay").setOnMouseClicked(event -> pay());
            ((Label)scene.lookup("#payed")).setText("Non payé");
        }
        scene.lookup("#download").setOnMouseClicked(event -> download());
        scene.lookup("#back").setOnMouseClicked(event -> back());

        stage.show();

        // Définition achats
        VBox content = (VBox) scene.lookup("#content");
        for (Achat achat : achats) {
            AnchorPane pane = new AnchorPane();
            VBox.setMargin(pane, new Insets(0.,0.,2.,0.));
            pane.setStyle("-fx-background-color: #efefef;");
            Label label = new Label(achat.getArticle().getNom() + " (" + achat.getArticle().getReference() + ")");
            pane.getChildren().add(label);
            AnchorPane.setLeftAnchor(label, 2.);
            label = new Label(achat.getQuantite() + " x " + new DecimalFormat("#0.##")
                    .format(achat.getPrixUnit()) + " = " + new DecimalFormat("#0.##")
                    .format(achat.getPrixUnit().multiply(BigDecimal.valueOf(achat.getQuantite()))) + "€");
            pane.getChildren().add(label);
            AnchorPane.setRightAnchor(label, 2.);
            content.getChildren().add(pane);
        }

    }

    private void pay() {
        stage.close();
        controller.openPayFacture(facture);
    }

    private void download() {
        controller.downloadAndOpenFacture(facture);
    }

    private void back() {
        stage.close();
    }
}
