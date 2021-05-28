package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.MoyenPayement;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class PaiementPane extends Pane {

    private final InterfaceController controller;

    private final CheckBox laterCheckBox;
    private final ComboBox<MoyenPayement> moyenComboBox;
    private final Label totalLabel;

    public PaiementPane(
            InterfaceController controller,
            List<MoyenPayement> moyenPayements,
            BigDecimal total
    ) {
        this.controller = controller;

        // Label
        Label label = new Label();
        this.getChildren().add(label);
        label.setLayoutX(8);
        label.setText("Paiement");

        // Checkbox
        laterCheckBox = new CheckBox();
        this.getChildren().add(laterCheckBox);
        laterCheckBox.setText("Payer plus tard");
        laterCheckBox.setLayoutX(8);
        laterCheckBox.setLayoutY(20);

        // Moyen de paiement
        moyenComboBox = new ComboBox<>();
        moyenComboBox.setItems(FXCollections.observableArrayList(moyenPayements));
        this.getChildren().add(moyenComboBox);
        moyenComboBox.setLayoutX(199);
        moyenComboBox.setLayoutY(6);
        moyenComboBox.setPrefWidth(188);
        moyenComboBox.setPromptText("Moyen de paiement");

        // Label
        totalLabel = new Label();
        this.getChildren().add(totalLabel);
        totalLabel.setLayoutX(390);
        totalLabel.setLayoutY(12);
        totalLabel.setText("Montant total : " + new DecimalFormat("#0.##").format(total) + "€");
    }

    public boolean getLater() {
        return laterCheckBox.isSelected();
    }

    public MoyenPayement getMoyenPaiement() {
        return moyenComboBox.getValue();
    }

}
