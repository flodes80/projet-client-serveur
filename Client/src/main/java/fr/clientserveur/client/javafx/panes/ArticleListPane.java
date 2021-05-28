package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.Stock;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.text.DecimalFormat;

public class ArticleListPane extends AnchorPane {

    private static final double HEIGTH = 60;
    private static final String COLOR = "#ebebeb";
    private static final double MARGIN = 1;

    private final InterfaceController controller;
    private final Stock stock;
    private final TextField qteTextField;

    public ArticleListPane(InterfaceController controller, Stock stock) {
        this.controller = controller;
        this.stock = stock;

        // Properties
        this.maxHeight(HEIGTH);
        this.minHeight(HEIGTH);
        this.prefHeight(HEIGTH);
        this.setStyle("-fx-background-color: " + COLOR + ";");
        VBox.setMargin(this, new Insets(MARGIN, 0, MARGIN, 0));

        // Ajout éléments
        this.getChildren().add(nameLabel(stock.getArticle()));
        this.getChildren().add(familleLabel(stock.getArticle().getFamille()));
        this.getChildren().add(referenceLabel(stock.getArticle()));
        this.getChildren().add(stockLabel(stock));
        this.getChildren().add(prixLabel(stock.getArticle()));
        this.getChildren().add(acheterButton());
        this.qteTextField = quantiteTextField();
        this.getChildren().add(qteTextField);
    }

    private Label nameLabel(Article article) {
        Label label = new Label();
        label.setLayoutX(5);
        label.setLayoutY(3);
        label.setText(article.getNom());
        label.setFont(Font.font("System Bold", 15));
        return label;
    }

    private Label familleLabel(Famille famille) {
        Label label = new Label();
        label.setLayoutX(5);
        label.setLayoutY(22);
        label.setText(famille.getNom());
        return label;
    }

    private Label referenceLabel(Article article) {
        Label label = new Label();
        label.setLayoutX(5);
        label.setLayoutY(39);
        label.setText("Ref #" + article.getReference());
        label.setFont(Font.font("System Italic", 12));
        return label;
    }

    private Label stockLabel(Stock stock) {
        Label label = new Label();
        AnchorPane.setRightAnchor(label, 5.);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setTextAlignment(TextAlignment.RIGHT);
        label.setLayoutY(4);
        label.setText("Stock : " + stock.getStock());
        return label;
    }

    private Label prixLabel(Article article) {
        Label label = new Label();
        AnchorPane.setRightAnchor(label, 5.);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setTextAlignment(TextAlignment.RIGHT);
        label.setLayoutY(18);
        label.setText("Prix/u : " + new DecimalFormat("#0.##").format(article.getPrix()) + "€");
        return label;
    }

    private Button acheterButton() {
        Button button = new Button();
        AnchorPane.setRightAnchor(button, 5.);
        button.setPrefHeight(22);
        button.setMinHeight(22);
        button.setMaxHeight(22);
        button.setText("Ajouter");
        button.setLayoutY(35);
        button.setFont(Font.font(11));
        button.setOnMouseClicked(e -> ajouter());
        return button;
    }

    private TextField quantiteTextField() {
        TextField textField = new TextField();
        AnchorPane.setRightAnchor(textField, 55.);
        textField.setAlignment(Pos.CENTER);
        textField.setLayoutY(35);
        textField.setMaxHeight(22);
        textField.setMinHeight(22);
        textField.setPrefHeight(22);
        textField.setPrefWidth(30);
        textField.setText("1");
        return textField;
    }

    private void ajouter() {
        try {
            int qte = Integer.parseInt(qteTextField.getText());
            controller.addAchat(stock, qte);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veuillez entrer une quantité correcte.");
            alert.show();
        }
    }
}
