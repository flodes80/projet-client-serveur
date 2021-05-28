import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.Stock;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@Ignore  public class TestStock {

    private static Magasin[] MAGASINS;
    private static Article[] ARTICLES;
    private static Session SESSION;

    @BeforeClass
    public static void start() {
        final int nb = 4;
        SESSION = HibernateUtils.getHibernateDevSession();
        MAGASINS = new Magasin[nb];
        ARTICLES = new Article[nb];
        Famille famille = new Famille();
        famille.setNom("Test stock");
        FamilleUtils.add(SESSION, famille);
        for (int i = 0; i < nb; i++) {
            MAGASINS[i] = new Magasin();
            MAGASINS[i].setNom("Magasin " + i);
            MAGASINS[i].setAdresse1("14 Rue Octave Tierce");
            MAGASINS[i].setAdresse2("80080 Amiens");
            MagasinUtils.add(SESSION, MAGASINS[i]);
            ARTICLES[i] = new Article();
            ARTICLES[i].setReference("TEST_STOCK_" + i);
            ARTICLES[i].setNom("Article " + i);
            ARTICLES[i].setPrix(BigDecimal.valueOf((i + 1) * 10));
            ARTICLES[i].setFamille(famille);
            ArticleUtils.add(SESSION, ARTICLES[i]);
        }
    }

    @AfterClass
    public static void end() {
        SESSION.close();
    }

    @Test
    public void testAdd() {

        // Création stock
        Stock stock = new Stock();
        stock.setMagasin(MAGASINS[0]);
        stock.setArticle(ARTICLES[0]);
        stock.setStock(10);

        // Ajout du stock en base
        StockUtils.add(SESSION, stock);

        // Get stock
        stock = StockUtils.get(SESSION, ARTICLES[0], MAGASINS[0]);
        assertEquals(10, stock.getStock());

        // Vérification contrainte unique
        stock = new Stock();
        stock.setMagasin(MAGASINS[0]);
        stock.setArticle(ARTICLES[0]);
        stock.setStock(0);
        try {
            StockUtils.add(SESSION, stock);
            fail("Contrainte unique non respectée");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testUpdate() {

        // Création stock
        Stock stock = new Stock();
        stock.setMagasin(MAGASINS[1]);
        stock.setArticle(ARTICLES[1]);
        stock.setStock(10);
        StockUtils.add(SESSION, stock);

        // Get stock
        stock = StockUtils.get(SESSION, ARTICLES[1], MAGASINS[1]);

        // Modification stock
        stock.setStock(stock.getStock() - 2);
        StockUtils.update(SESSION, stock);
        assertEquals(8, stock.getStock());

        // Vérification résultat
        stock = StockUtils.get(SESSION, ARTICLES[1], MAGASINS[1]);
        assertEquals(8, stock.getStock());
    }

    @Test
    public void testDelete() {

        // Création stock
        Stock stock = new Stock();
        stock.setMagasin(MAGASINS[2]);
        stock.setArticle(ARTICLES[2]);
        stock.setStock(10);
        StockUtils.add(SESSION, stock);

        // Get stock
        stock = StockUtils.get(SESSION, ARTICLES[2], MAGASINS[2]);

        // Suppression instance
        StockUtils.delete(SESSION, stock);

        // Vérification suppression
        try {
            StockUtils.get(SESSION, ARTICLES[2], MAGASINS[2]);
            fail("Stock non supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testGetByMagasin() {

        // Création stock
        Stock stock1 = new Stock();
        Stock stock2 = new Stock();
        stock1.setMagasin(MAGASINS[3]);
        stock2.setMagasin(MAGASINS[3]);
        stock1.setArticle(ARTICLES[0]);
        stock2.setArticle(ARTICLES[1]);
        StockUtils.add(SESSION, stock1);
        StockUtils.add(SESSION, stock2);

        // Récupération getByMagasin
        List<Stock> stockList = StockUtils.getByMagasin(SESSION, MAGASINS[3]);

        // Vérification
        assertEquals(2, stockList.size());
        assertTrue(stockList.contains(stock1));
        assertTrue(stockList.contains(stock2));
    }

    @Test
    public void testGetByArticle() {

        // Création stock
        Stock stock1 = new Stock();
        Stock stock2 = new Stock();
        stock1.setMagasin(MAGASINS[0]);
        stock2.setMagasin(MAGASINS[1]);
        stock1.setArticle(ARTICLES[3]);
        stock2.setArticle(ARTICLES[3]);
        StockUtils.add(SESSION, stock1);
        StockUtils.add(SESSION, stock2);

        // Récupération getByArticle
        Stock stock = StockUtils.getByArticle(SESSION, ARTICLES[3]);

        // Vérification
        assertEquals(2, stock.getStock());
        //assertTrue(stockList.contains(stock1));
        //assertTrue(stockList.contains(stock2));
    }
}
