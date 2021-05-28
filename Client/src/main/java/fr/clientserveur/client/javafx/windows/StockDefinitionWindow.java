package fr.clientserveur.client.javafx.windows;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractSelectionWindow;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractTextfieldWindow;
import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.Stock;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

public class StockDefinitionWindow extends AbstractTextfieldWindow {

    private final Stock stock;

    public StockDefinitionWindow(InterfaceController controller, Stock stock) throws IOException {
        super(controller, "Définition du stock", "Stock de l'article " + stock.getArticle().getNom() + " :", "Quantité");
        this.stock = stock;
        setText(Integer.toString(stock.getStock()));
    }

    @Override
    protected void confirm(String value) {
        try {
            int qte = Integer.parseInt(value);
            if (qte < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veuillez entrer une quantité correcte.");
                alert.show();
            } else {
                this.close();
                controller.setStock(stock, qte);
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veuillez entrer une quantité correcte.");
            alert.show();
        }
    }

    @Override
    protected void cancel() {
        this.close();
    }
}
