package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractSearchPane;
import fr.clientserveur.common.entities.Stock;

import java.util.List;

public class ArticlePane extends AbstractSearchPane {

    public ArticlePane(InterfaceController controller) {
        super(controller);
    }

    public void setStocks(List<Stock> stocks) {
        getList().clear();
        for (Stock stock : stocks) {
            getList().add(new ArticleListPane(controller, stock));
        }
    }

    @Override
    protected void search(String value) {
        controller.searchMagasin();
    }

    @Override
    protected void openPage(int page) {
        controller.setMagasinPage(page);
    }
}
