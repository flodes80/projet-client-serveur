package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Stock;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AchatPane extends AnchorPane {

    private final InterfaceController controller;

    private final Label prixQuantiteLabel;
    private final Button moinsButton;
    private final Button plusButton;

    private Stock stock;
    private final Achat achat;

    public AchatPane(InterfaceController controller, Stock stock, Achat achat) {
        this.controller = controller;
        this.stock = stock;
        this.achat = achat;
        this.setPrefHeight(40);
        this.setStyle("-fx-background-color: #ebebeb;");
        VBox.setMargin(this,new Insets(3,0,3,0));

        // Nom
        this.getChildren().add(labelNom(achat.getArticle()));

        // Prix quantité
        prixQuantiteLabel = labelPrixQuantite(achat);
        this.getChildren().add(prixQuantiteLabel);

        // Buttons
        moinsButton = moinsButton();
        moinsButton.setOnMouseClicked(e -> controller.addAchat(stock, -1));
        plusButton = plusButton();
        plusButton.setOnMouseClicked(e -> controller.addAchat(stock, 1));
        this.getChildren().addAll(plusButton, moinsButton);

        updateButton();
    }

    public void update(Stock stock) {
        this.stock = stock;
        prixQuantiteLabel.setText(achat.getQuantite() + "x" + new DecimalFormat("#0.##")
                .format(achat.getArticle().getPrix()) + " = " + new DecimalFormat("#0.##")
                .format(achat.getArticle().getPrix().multiply(BigDecimal.valueOf(achat.getQuantite()))) + "€");
        updateButton();
    }

    private void updateButton() {
        plusButton.setDisable(achat.getQuantite() >= stock.getStock());
        moinsButton.setDisable(achat.getQuantite() <= 0);
    }

    private Label labelNom(Article article) {
        Label label = new Label();
        label.setText(article.getNom());
        label.setLayoutX(2.);
        label.setLayoutY(2.);
        return label;
    }

    private Label labelPrixQuantite(Achat achat) {
        Label label = new Label();
        label.setText(achat.getQuantite() + "x" + new DecimalFormat("#0.##")
                .format(achat.getArticle().getPrix()) + " = " + new DecimalFormat("#0.##")
                .format(achat.getArticle().getPrix().multiply(BigDecimal.valueOf(achat.getQuantite()))) + "€");
        label.setLayoutX(2.);
        label.setLayoutY(15.);
        return label;
    }

    private Button moinsButton() {
        Button button = new Button();
        button.setText("-");
        button.setLayoutY(30);
        AnchorPane.setLeftAnchor(button, 0.);
        return button;
    }

    private Button plusButton() {
        Button button = new Button();
        button.setText("+");
        button.setLayoutY(30);
        AnchorPane.setRightAnchor(button, 0.);
        return button;
    }
}
