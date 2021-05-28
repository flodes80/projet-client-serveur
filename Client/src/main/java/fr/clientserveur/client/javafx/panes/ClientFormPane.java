package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Client;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.time.LocalDate;

public class ClientFormPane extends AnchorPane {

    private final InterfaceController controller;

    private final TextField nomField;
    private final TextField prenomField;
    private final TextField emailField;
    private final TextField adresse1Field;
    private final TextField adresse2Field;
    private final DatePicker naissanceDate;
    private final Label idLabel;
    private Client client;

    public ClientFormPane(InterfaceController controller) {
        this.controller = controller;
        this.client = new Client();

        // Properties
        HBox.setHgrow(this, Priority.ALWAYS);

        // Label
        Label label = new Label();
        this.getChildren().add(label);
        label.setText("Informations sur le client");
        label.setLayoutX(14);
        label.setLayoutY(4);

        // region Textfields
        nomField = new TextField();
        this.getChildren().add(nomField);
        nomField.setPromptText("Nom");
        nomField.setLayoutX(14);
        nomField.setLayoutY(23);
        nomField.setPrefWidth(211);

        prenomField = new TextField();
        this.getChildren().add(prenomField);
        prenomField.setPromptText("Prénom");
        prenomField.setLayoutX(225);
        prenomField.setLayoutY(23);
        prenomField.setPrefWidth(211);

        emailField = new TextField();
        this.getChildren().add(emailField);
        emailField.setPromptText("Email");
        emailField.setLayoutX(14);
        emailField.setLayoutY(53);
        emailField.setPrefWidth(422);

        adresse1Field = new TextField();
        this.getChildren().add(adresse1Field);
        adresse1Field.setPromptText("Adresse 1");
        adresse1Field.setLayoutX(14);
        adresse1Field.setLayoutY(83);
        adresse1Field.setPrefWidth(422);

        adresse2Field = new TextField();
        this.getChildren().add(adresse2Field);
        adresse2Field.setPromptText("Adresse 2");
        adresse2Field.setLayoutX(14);
        adresse2Field.setLayoutY(113);
        adresse2Field.setPrefWidth(422);
        // endregion

        // Naissance
        naissanceDate = new DatePicker();
        this.getChildren().add(naissanceDate);
        naissanceDate.setPromptText("Date de naissance");
        naissanceDate.setLayoutX(14);
        naissanceDate.setLayoutY(143);

        // ID
        idLabel = new Label();
        this.getChildren().add(idLabel);
        idLabel.setText("ID utilisateur : NA");
        idLabel.setLayoutX(210);
        idLabel.setLayoutY(148);
    }

    public void setClient(Client client) {
        if (client == null) {
            idLabel.setText("ID utilisateur : NA");
            nomField.clear();
            prenomField.clear();
            adresse1Field.clear();
            adresse2Field.clear();
            emailField.clear();
            naissanceDate.setValue(LocalDate.now());
            this.client = new Client();
            return;
        }

        this.client = client;
        idLabel.setText("ID utilisateur : " + client.getId());
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        adresse1Field.setText(client.getAdresse1());
        adresse2Field.setText(client.getAdresse2());
        emailField.setText(client.getEmail());
        naissanceDate.setValue(client.getNaissance());
    }

    public boolean isValid() {
        return !(nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty()
            || emailField.getText().trim().isEmpty() || adresse1Field.getText().trim().isEmpty()
            || adresse2Field.getText().trim().isEmpty() || naissanceDate.getValue() == null
            || !naissanceDate.getValue().isBefore(LocalDate.now()));
    }

    public Client getClient() {
        client.setNom(nomField.getText().trim());
        client.setPrenom(prenomField.getText().trim());
        client.setAdresse1(adresse1Field.getText().trim());
        client.setAdresse2(adresse2Field.getText().trim());
        client.setEmail(emailField.getText().trim());
        client.setNaissance(naissanceDate.getValue());
        return client;
    }

}
