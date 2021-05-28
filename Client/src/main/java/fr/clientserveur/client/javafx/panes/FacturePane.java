package fr.clientserveur.client.javafx.panes;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractSearchPane;
import fr.clientserveur.common.entities.Client;
import fr.clientserveur.common.entities.Facture;
import fr.clientserveur.common.entities.MoyenPayement;
import fr.clientserveur.common.entities.Stock;

import java.util.Date;
import java.util.List;

public class FacturePane extends AbstractSearchPane {

    public FacturePane(InterfaceController controller) {
        super(controller);
    }

    public void setFactures(List<Facture> factures) {
        getList().clear();
        for (Facture facture : factures) {
            getList().add(new FactureListPane(controller, facture));
        }
    }

    @Override
    protected void search(String value) {
        controller.searchFacture();
    }

    @Override
    protected void openPage(int page) {
        controller.setFacturePage(page);
    }
}
