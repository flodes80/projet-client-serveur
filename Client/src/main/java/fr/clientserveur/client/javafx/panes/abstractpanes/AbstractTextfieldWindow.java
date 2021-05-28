package fr.clientserveur.client.javafx.panes.abstractpanes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractTextfieldWindow extends Stage {

    protected final InterfaceController controller;
    private final TextField textfield;

    public AbstractTextfieldWindow(
            InterfaceController controller,
            String title,
            String label,
            String placeholder
    ) throws IOException {
        this.controller = controller;

        // Création et affichage de la fenêtre
        Parent root = FXMLLoader.load(getClass().getResource("/textfield.fxml"));
        this.setTitle(title);
        Scene scene = new Scene(root);
        this.setScene(scene);

        // Elements
        ((Label)scene.lookup("#label")).setText(label);

        this.show();

        // Sélection
        textfield = (TextField) scene.lookup("#textfield");
        textfield.setPromptText(placeholder);

        // Buttons
        scene.lookup("#confirm").setOnMouseClicked(event -> confirm(textfield.getText()));
        scene.lookup("#cancel").setOnMouseClicked(event -> cancel());

    }

    protected void setText(String value) {
        textfield.setText(value);
    }

    protected abstract void confirm(String value);
    protected abstract void cancel();

}
