package fr.clientserveur.client.javafx.panes.abstractpanes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public abstract class AbstractSelectionWindow<T> extends Stage {

    protected final InterfaceController controller;
    private final ComboBox selectComboBox;

    public AbstractSelectionWindow(
            InterfaceController controller,
            String title,
            String label,
            List<T> selection
    ) throws IOException {
        this.controller = controller;

        // Création et affichage de la fenêtre
        Parent root = FXMLLoader.load(getClass().getResource("/selection.fxml"));
        this.setTitle(title);
        Scene scene = new Scene(root);
        this.setScene(scene);

        // Elements
        ((Label)scene.lookup("#label")).setText(label);

        this.show();

        // Sélection
        selectComboBox = (ComboBox) scene.lookup("#select");
        selectComboBox.setItems(FXCollections.observableArrayList(selection));

        // Buttons
        scene.lookup("#confirm").setOnMouseClicked(event -> select((T) selectComboBox.getValue()));
        scene.lookup("#cancel").setOnMouseClicked(event -> cancel());

    }

    protected abstract void select(T selection);
    protected abstract void cancel();


}
