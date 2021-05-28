package fr.clientserveur.client.javafx.windows;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractDatePickerWindow;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.time.LocalDate;

public class ChiffreAffaireWindow extends AbstractDatePickerWindow {

    public ChiffreAffaireWindow(InterfaceController controller) throws IOException {
        super(controller, "Chiffre d'affaire", "Jour à calculer :");
    }

    @Override
    protected void confirm(LocalDate value) {
        if (value == null || value.isAfter(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veuillez entrer une date correcte.");
            alert.show();
            return;
        }
        controller.getChiffeAffaire(value);
        this.close();
    }

    @Override
    protected void cancel() {
        this.close();
    }
}
