package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Stock;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PanierPane extends AnchorPane {

    private final InterfaceController controller;

    private final VBox articlesVBox;
    private final Label totalLabel;
    private final Button confirmerButton;

    private Map<Achat, AchatPane> achats;

    public PanierPane(InterfaceController controller) {
        this.controller = controller;
        this.achats = new HashMap<>();

        // VBox main
        VBox vbox = new VBox();
        this.getChildren().add(vbox);
        AnchorPane.setLeftAnchor(vbox, 0.);
        AnchorPane.setRightAnchor(vbox, 0.);
        AnchorPane.setTopAnchor(vbox, 0.);
        AnchorPane.setBottomAnchor(vbox, 0.);

        // Label panier
        Label label = new Label();
        vbox.getChildren().add(label);
        label.setText("Panier");

        // Scrollpane
        ScrollPane scrollPane = new ScrollPane();
        vbox.getChildren().add(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        articlesVBox = new VBox();
        scrollPane.setContent(articlesVBox);

        // Label total
        totalLabel = new Label();
        vbox.getChildren().add(totalLabel);
        totalLabel.setText("Total : 0,00€");

        // Confirmer
        AnchorPane anchorPane = new AnchorPane();
        vbox.getChildren().add(anchorPane);
        anchorPane.setPrefHeight(27);
        confirmerButton = new Button();
        anchorPane.getChildren().add(confirmerButton);
        confirmerButton.setText("Confirmer");
        AnchorPane.setLeftAnchor(confirmerButton, 0.);
        AnchorPane.setRightAnchor(confirmerButton, 0.);
        confirmerButton.setOnMouseClicked(e -> controller.confirmPanier(new ArrayList<>(achats.keySet())));
    }

    public void setAchat(Stock stock, Achat achat) {
        if (achats.containsKey(achat)) {
            achats.get(achat).update(stock);
        } else {
            AchatPane pane = new AchatPane(controller, stock, achat);
            articlesVBox.getChildren().add(pane);
            achats.put(achat, pane);
        }
        refreshTotal();
    }

    public void removeAchat(Achat achat) {
        articlesVBox.getChildren().remove(achats.get(achat));
        achats.remove(achat);
        refreshTotal();
    }

    private void refreshTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Achat achat : new ArrayList<>(achats.keySet())) {
            total = total.add(achat.getPrixUnit().multiply(BigDecimal.valueOf(achat.getQuantite())));
        }
        totalLabel.setText("Total : " + new DecimalFormat("#0.##").format(total) + "€");
    }

    public void clearPanier() {
        articlesVBox.getChildren().clear();
        this.achats.clear();
        refreshTotal();
    }
}
