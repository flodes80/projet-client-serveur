package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Stock;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class MagasinPane extends SplitPane {

    private final InterfaceController controller;
    private final ArticlePane articlesPane;
    private final PanierPane panierPane;

    public MagasinPane(InterfaceController controller) {
        this.controller = controller;

        // Properties
        this.setDividerPositions(0.8);
        AnchorPane.setBottomAnchor(this,0.);
        AnchorPane.setLeftAnchor(this,0.);
        AnchorPane.setRightAnchor(this,0.);
        AnchorPane.setTopAnchor(this,0.);

        // Left Anchor
        articlesPane = new ArticlePane(controller);
        this.getItems().add(articlesPane);
        panierPane = new PanierPane(controller);
        this.getItems().add(panierPane);
    }

    public ArticlePane getArticlesPane() {
        return articlesPane;
    }

    public void setStocks(List<Stock> stocks) {
        articlesPane.setStocks(stocks);
    }

    public void setAchat(Stock stock, Achat achat) {
        panierPane.setAchat(stock, achat);
    }

    public void removeAchat(Achat achat) {
        panierPane.removeAchat(achat);
    }

    public void clearPanier() {
        panierPane.clearPanier();
    }

}
