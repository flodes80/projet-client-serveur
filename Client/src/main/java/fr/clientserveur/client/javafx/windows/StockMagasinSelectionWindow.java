package fr.clientserveur.client.javafx.windows;

import fr.clientserveur.client.javafx.controllers.InterfaceController;
import fr.clientserveur.client.javafx.panes.abstractpanes.AbstractSelectionWindow;
import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Magasin;

import java.io.IOException;
import java.util.List;

public class StockMagasinSelectionWindow extends AbstractSelectionWindow<Magasin> {

    private final Article article;

    public StockMagasinSelectionWindow(InterfaceController controller, List<Magasin> selection, Article article) throws IOException {
        super(controller, "Sélection du magasin", "Veuillez sélectionner le magasin :", selection);
        this.article = article;
    }

    @Override
    protected void select(Magasin selection) {
        this.close();
        controller.getStockMagasin(article, selection);
    }

    @Override
    protected void cancel() {
        this.close();
    }
}
