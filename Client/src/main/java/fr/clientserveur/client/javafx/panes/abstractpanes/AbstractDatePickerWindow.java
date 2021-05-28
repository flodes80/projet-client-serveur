package fr.clientserveur.client.javafx.panes.abstractpanes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public abstract class AbstractDatePickerWindow extends Stage {

    protected final InterfaceController controller;
    private final DatePicker datePicker;

    public AbstractDatePickerWindow(
            InterfaceController controller,
            String title,
            String label
    ) throws IOException {
        this.controller = controller;

        // Création et affichage de la fenêtre
        Parent root = FXMLLoader.load(getClass().getResource("/datepicker.fxml"));
        this.setTitle(title);
        Scene scene = new Scene(root);
        this.setScene(scene);

        // Elements
        ((Label)scene.lookup("#label")).setText(label);

        this.show();

        // DatePicker
        datePicker = (DatePicker) scene.lookup("#date");
        datePicker.setValue(LocalDate.now());

        // Buttons
        scene.lookup("#confirm").setOnMouseClicked(event -> confirm(datePicker.getValue()));
        scene.lookup("#cancel").setOnMouseClicked(event -> cancel());

    }

    protected void setDate(LocalDate value) {
        datePicker.setValue(value);
    }

    protected abstract void confirm(LocalDate value);
    protected abstract void cancel();

}
