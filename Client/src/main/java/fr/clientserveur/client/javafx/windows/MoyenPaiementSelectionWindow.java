package fr.clientserveur.client.javafx.windows;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractSelectionWindow;
import fr.clientserveur.common.entities.Facture;
import fr.clientserveur.common.entities.MoyenPayement;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

public class MoyenPaiementSelectionWindow extends AbstractSelectionWindow<MoyenPayement> {

    private final Facture facture;

    public MoyenPaiementSelectionWindow(InterfaceController controller, List<MoyenPayement> selection, Facture facture) throws IOException {
        super(controller, "Paiement de la facture", "Veuillez sélectionner votre moyen de paiement :", selection);
        this.facture = facture;
    }

    @Override
    protected void select(MoyenPayement selection) {
        if (selection == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veuillez sélectionner un moyen de paiement.");
            alert.show();
            return;
        }
        this.close();
        controller.payFacture(facture, selection);
    }

    @Override
    protected void cancel() {
        this.close();
        controller.openFacture(facture);
    }
}
