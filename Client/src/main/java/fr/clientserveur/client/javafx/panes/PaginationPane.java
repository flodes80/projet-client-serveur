package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class PaginationPane extends AnchorPane {

    private final InterfaceController controller;

    private final Label pageLabel;
    private final Button leftButton;
    private final Button rightButton;

    private int currentPage;
    private int nbPages;

    public PaginationPane(InterfaceController controller) {
        this.controller = controller;

        // Définition propriétés
        this.setPrefHeight(25);
        this.setMinHeight(25);
        this.setMaxHeight(25);

        // Ajout des éléments
        pageLabel = pageLabel();
        leftButton = leftButton();
        rightButton = rightButton();
        this.getChildren().addAll(pageLabel, leftButton, rightButton);

        leftButton.setOnMouseClicked(event -> changePage(currentPage - 1));
        rightButton.setOnMouseClicked(event -> changePage(currentPage + 1));

        // Test label
        this.pageLabel.setText("1/5");
    }

    private void changePage(int page) {
        if (page > nbPages || page < 1) {
            return;
        }
        controller.setMagasinPage(page);
    }

    private Button leftButton() {
        Button button = new Button();
        button.setText("<-");
        AnchorPane.setLeftAnchor(button, 0.);
        return button;
    }

    private Button rightButton() {
        Button button = new Button();
        button.setText("->");
        AnchorPane.setRightAnchor(button, 0.);
        return button;
    }

    private Label pageLabel() {
        Label label = new Label();
        label.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 5.);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    public void refresh() {
        pageLabel.setText(currentPage + "/" + nbPages);
        leftButton.setDisable(currentPage <= 1);
        rightButton.setDisable(currentPage >= nbPages);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }
}
