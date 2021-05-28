package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Client;
import fr.clientserveur.common.entities.MoyenPayement;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class ConfirmPanierPane extends AnchorPane {

    private final InterfaceController controller;

    private final ClientPane clientPane;
    private final PaiementPane paiementPane;
    private final Button confirmButton;
    private final Button cancelButton;
    private final List<Achat> achats;

    public ConfirmPanierPane(
            InterfaceController controller,
            List<Achat> achats,
            List<MoyenPayement> moyenPayements,
            List<Client> clients
    ) {
        this.controller = controller;
        this.achats = achats;

        // Properties
        VBox.setVgrow(this, Priority.ALWAYS);
        VBox vBox = new VBox();
        this.getChildren().add(vBox);

        // Client
        clientPane = new ClientPane(controller, clients);
        vBox.getChildren().add(clientPane);

        Separator separator = new Separator();
        vBox.getChildren().add(separator);
        VBox.setMargin(separator, new Insets(5,0,5,0));

        // Paiement
        paiementPane = new PaiementPane(controller, moyenPayements, getTotal(achats));
        vBox.getChildren().add(paiementPane);

        // Confirmer
        AnchorPane anchorPane = new AnchorPane();
        vBox.getChildren().add(anchorPane);
        confirmButton = new Button();
        confirmButton.setOnMouseClicked(e -> confirmButtonHandler());
        anchorPane.getChildren().add(confirmButton);
        confirmButton.setText("Confirmer l'achat");
        AnchorPane.setRightAnchor(confirmButton, 0.);

        // Cancel
        cancelButton = new Button();
        anchorPane.getChildren().add(cancelButton);
        cancelButton.setText("Annuler");
        cancelButton.setOnMouseClicked(e -> {
            cancelButtonHandler();
        });
        AnchorPane.setRightAnchor(cancelButton, 120.);
    }

    private BigDecimal getTotal(List<Achat> achats) {
        BigDecimal total = BigDecimal.ZERO;
        for (Achat achat : achats) {
            total = total.add(achat.getPrixUnit().multiply(BigDecimal.valueOf(achat.getQuantite())));
        }
        return total;
    }

    private void cancelButtonHandler(){
        controller.showMagasin();
    }

    private void confirmButtonHandler() {
        // Vérification données
        if (!clientPane.isValid()) {
            new Alert(Alert.AlertType.INFORMATION, "Veuillez définir un client valide.").show();
            return;
        }
        if (!paiementPane.getLater() && paiementPane.getMoyenPaiement() == null) {
            new Alert(Alert.AlertType.INFORMATION, "Veuillez définir un moyen de paiement valide.").show();
            return;
        }

        // Méthode de paiement
        MoyenPayement moyenPayement = paiementPane.getMoyenPaiement();
        if (moyenPayement != null && paiementPane.getLater()) {
            moyenPayement = null;
        }

        // Création facture
        controller.confirmFacture(clientPane.getClient(), achats, moyenPayement);
    }
}
