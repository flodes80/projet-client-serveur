package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Client;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.List;

public class ClientPane extends HBox {

    private final InterfaceController controller;

    private final CheckBox newCheckbox;
    private final TextField searchTextField;
    private final ComboBox<Client> clientComboBox;
    private final ClientFormPane clientFormPane;
    private Client select = null;

    public ClientPane(
            InterfaceController controller,
            List<Client> clients
    ) {
        this.controller = controller;

        // Chargement client
        Pane chargementClient = new Pane();
        this.getChildren().add(chargementClient);
        Label label = new Label();
        chargementClient.getChildren().add(label);
        label.setLayoutX(8);
        label.setLayoutY(6);
        label.setText("Client associé à l'achat");

        // Checkbox
        newCheckbox = new CheckBox();
        newCheckbox.setOnMouseClicked(e -> selectNew());
        chargementClient.getChildren().add(newCheckbox);
        newCheckbox.setLayoutX(8);
        newCheckbox.setLayoutY(27);
        newCheckbox.setText("Nouveau client");

        // Client
        clientComboBox = new ComboBox<>();
        clientComboBox.setItems(FXCollections.observableArrayList(clients));
        chargementClient.getChildren().add(clientComboBox);
        clientComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectClient(newValue);
        });
        clientComboBox.setLayoutX(8);
        clientComboBox.setLayoutY(78);
        clientComboBox.setPrefWidth(172);
        clientComboBox.setPromptText("Client");

        // Search
        searchTextField = new TextField();
        chargementClient.getChildren().add(searchTextField);
        searchTextField.setLayoutX(8);
        searchTextField.setLayoutY(48);
        searchTextField.setPrefWidth(172);
        searchTextField.setPromptText("Recherche");
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            clientComboBox.setItems(FXCollections.observableArrayList(controller.searchClient(newValue)));
        });

        // Client form
        clientFormPane = new ClientFormPane(controller);
        this.getChildren().add(clientFormPane);
    }

    private void selectClient(Client value) {
        select = value;
        clientFormPane.setClient(select);
    }

    private void selectNew() {
        if (newCheckbox.isSelected()) {
            clientComboBox.setValue(null);
            clientComboBox.setDisable(true);
            searchTextField.setDisable(true);
            select = null;
            clientFormPane.setClient(null);
        } else {
            clientComboBox.setDisable(false);
            searchTextField.setDisable(false);
        }
    }

    public boolean isValid() {
        return clientFormPane.isValid();
    }

    public Client getClient() {
        return clientFormPane.getClient();
    }
}
