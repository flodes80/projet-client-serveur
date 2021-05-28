package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractSearchPane;
import fr.clientserveur.common.entities.*;

import java.math.BigDecimal;
import java.util.List;

public class StockPane extends AbstractSearchPane {

    public StockPane(InterfaceController controller) {
        super(controller);
    }

    public void setStocks(List<Stock> stocks) {
        getList().clear();
        for (Stock stock : stocks) {
            getList().add(new StockListPane(controller, stock));
        }
    }

    @Override
    protected void search(String value) {
        controller.searchStock();
    }

    @Override
    protected void openPage(int page) {
        controller.setStockPage(page);
    }
}
