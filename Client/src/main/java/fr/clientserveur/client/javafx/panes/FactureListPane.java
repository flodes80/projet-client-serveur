package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Facture;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;


public class FactureListPane extends AnchorPane {

    private final InterfaceController controller;
    private final Facture facture;

    private final Button afficheButton;

    public FactureListPane(InterfaceController controller, Facture facture) {
        this.controller = controller;
        this.facture = facture;

        VBox.setVgrow(this, Priority.ALWAYS);
        VBox.setMargin(this, new Insets(3,3,3,3));
        this.setStyle("-fx-background-color: #ebebeb;");

        Label label = new Label("Facture N°" + facture.getId());
        AnchorPane.setLeftAnchor(label, 8.);
        label.setFont(Font.font(14));
        label.setLayoutY(6);
        this.getChildren().add(label);

        label = new Label("Date d'achat : "
                + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(facture.getDate()));
        AnchorPane.setLeftAnchor(label, 8.);
        label.setLayoutY(24);
        this.getChildren().add(label);

        label = new Label(facture.getClient().getPrenom() + " " + facture.getClient().getNom());
        AnchorPane.setLeftAnchor(label, 8.);
        label.setLayoutY(41);
        this.getChildren().add(label);

        label = new Label();
        AnchorPane.setLeftAnchor(label, 8.);
        label.setLayoutY(58);
        this.getChildren().add(label);
        if (facture.getMoyenPaye() == null) {
            label.setText("Non payé");
        } else {
            label.setText("Payé le " + new SimpleDateFormat("dd/MM/yyyy à HH:mm").format(facture.getDate())
            + " par " + facture.getMoyenPaye().getNom());
        }

        afficheButton = new Button("Afficher");
        afficheButton.setOnMouseClicked(e -> open());
        AnchorPane.setRightAnchor(afficheButton, 8.);
        afficheButton.setLayoutY(45);
        this.getChildren().add(afficheButton);
    }

    private void open() {
        controller.openFacture(facture);
    }
}
