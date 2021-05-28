package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.Stock;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class StockListPane extends AnchorPane {

    private static final double HEIGTH = 60;
    private static final String COLOR = "#ebebeb";
    private static final double MARGIN = 1;

    private final InterfaceController controller;
    private final Stock stock;

    public StockListPane(InterfaceController controller, Stock stock) {
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
        this.getChildren().add(stockMagasinButton());
        this.getChildren().add(modifierButton());
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

    private Button stockMagasinButton() {
        Button button = new Button("Consulter stock autres magasins");
        AnchorPane.setRightAnchor(button, 5.);
        button.setPrefHeight(22);
        button.setMinHeight(22);
        button.setMaxHeight(22);
        button.setLayoutY(21);
        button.setFont(Font.font(11));
        button.setOnMouseClicked(event -> stockMagasin());
        return button;
    }

    private Button modifierButton() {
        Button button = new Button("Modifier");
        AnchorPane.setRightAnchor(button, 5.);
        button.setPrefHeight(22);
        button.setMinHeight(22);
        button.setMaxHeight(22);
        button.setLayoutY(43);
        button.setFont(Font.font(11));
        button.setOnMouseClicked(event -> modifier());
        return button;
    }

    private void modifier() {
        controller.openStockDefinition(stock);
    }

    private void stockMagasin() {
        controller.openStockMagasin(stock.getArticle());
    }
}
