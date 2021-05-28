package fr.clientserveur.client.javafx.panes.abstractpanes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class AbstractSearchPane extends AnchorPane {

    protected final InterfaceController controller;

    private final TextField searchTextField;
    private final VBox contentVBox;
    private final Button moinsButton;
    private final Button plusButton;
    private final Label pageLabel;

    private int currentPage;
    private int countPage;
    private String search = null;

    public AbstractSearchPane(InterfaceController controller) {
        this.controller = controller;

        // Properties
        VBox.setVgrow(this, Priority.ALWAYS);
        AnchorPane.setTopAnchor(this, 0.);
        AnchorPane.setBottomAnchor(this, 0.);
        AnchorPane.setRightAnchor(this, 0.);
        AnchorPane.setLeftAnchor(this, 0.);

        VBox vBox = new VBox();
        this.getChildren().add(vBox);
        AnchorPane.setRightAnchor(vBox, 0.);
        AnchorPane.setLeftAnchor(vBox, 0.);
        AnchorPane.setTopAnchor(vBox, 0.);
        AnchorPane.setBottomAnchor(vBox, 0.);

        // Search anchor
        AnchorPane anchorSearch = new AnchorPane();
        vBox.getChildren().add(anchorSearch);
        anchorSearch.setPrefHeight(30);

        // Search field
        searchTextField = new TextField();
        anchorSearch.getChildren().add(searchTextField);
        searchTextField.setLayoutY(2);
        searchTextField.setPrefWidth(170);
        searchTextField.setPromptText("Recherche");
        AnchorPane.setLeftAnchor(searchTextField, 5.);

        // Search Button
        Button buttonSearch = new Button("Rechercher");
        anchorSearch.getChildren().add(buttonSearch);
        AnchorPane.setLeftAnchor(buttonSearch, 175.);
        buttonSearch.setLayoutY(2);
        buttonSearch.setOnMouseClicked(event -> searchEventHandler(this.searchTextField.getText()));

        // Scrollpane VBox
        ScrollPane scrollPane = new ScrollPane();
        vBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);

        contentVBox = new VBox();
        scrollPane.setContent(contentVBox);

        // Pagination anchor
        AnchorPane pageAnchor = new AnchorPane();
        AnchorPane.setBottomAnchor(pageAnchor, 0.);
        vBox.getChildren().add(pageAnchor);

        // Page
        pageLabel = new Label("1/1");
        pageAnchor.getChildren().add(pageLabel);
        AnchorPane.setLeftAnchor(pageLabel, 0.);
        AnchorPane.setRightAnchor(pageLabel, 0.);
        AnchorPane.setTopAnchor(pageLabel, 5.);
        pageLabel.setMaxWidth(Double.MAX_VALUE);
        pageLabel.setAlignment(Pos.CENTER);
        currentPage = 1;
        countPage = 1;

        // Buttons
        moinsButton = new Button("<-");
        plusButton = new Button("->");
        AnchorPane.setLeftAnchor(moinsButton, 0.);
        AnchorPane.setRightAnchor(plusButton, 0.);
        moinsButton.setOnMouseClicked(event -> setPage(currentPage - 1));
        plusButton.setOnMouseClicked(event -> setPage(currentPage + 1));
        pageAnchor.getChildren().addAll(moinsButton, plusButton);
        moinsButton.setDisable(true);
        plusButton.setDisable(true);
    }

    protected ObservableList<Node> getList() {
        return contentVBox.getChildren();
    }

    protected void setSearchText(String text) {
        this.searchTextField.setText(text);
    }

    private void setPage(int page) {
        if (page <= 0 || page > countPage) {
            return;
        }
        currentPage = page;
        moinsButton.setDisable(currentPage == 1);
        plusButton.setDisable(currentPage == countPage);
        pageLabel.setText(currentPage + "/" + countPage);
        openPage(currentPage);
    }

    public void setPageCount(int page) {
        currentPage = 1;
        countPage = page;
        pageLabel.setText(currentPage + "/" + countPage);
        moinsButton.setDisable(true);
        plusButton.setDisable(1 == countPage);
    }

    public String getSearch() {
        return search;
    }

    private void searchEventHandler(String search) {
        if (search == null || search.trim().isEmpty()) {
            this.search = null;
            this.searchTextField.setText("");
        } else {
            this.search = search.trim();
            this.searchTextField.setText(this.search);
        }
        search(this.search);
    }

    public boolean isFiltered() {
        return !(search == null || search.trim().isEmpty());
    }

    protected abstract void search(String value);

    protected abstract void openPage(int page);

}
