import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

import static org.junit.Assert.*;

@Ignore public class TestMagasin {

    private static Session SESSION;

    @BeforeClass
    public static void start() {
        SESSION = HibernateUtils.getHibernateDevSession();;
    }

    @AfterClass
    public static void end() {
        SESSION.close();
    }

    @Test
    public void testAdd() {

        // Ajout magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Le magasin d'Amiens");
        magasin.setAdresse1("14 Rue Octave Tierce");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);
        assertNotNull(magasin.getId());

        // Get du magasin
        magasin = MagasinUtils.get(SESSION, magasin.getId());
        assertEquals("Le magasin d'Amiens", magasin.getNom());
    }

    @Test
    public void testUpdate() {

        // Ajout magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Le magasin d'Amiens");
        magasin.setAdresse1("14 Rue Octave Tierce");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);

        // Maj du magasin
        magasin.setNom("Amiens Nord");
        MagasinUtils.update(SESSION, magasin);
        magasin = MagasinUtils.get(SESSION, magasin.getId());
        assertEquals("Amiens Nord", magasin.getNom());
    }

    @Test
    public void testRemove() {

        // Ajout magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Le magasin d'Amiens");
        magasin.setAdresse1("14 Rue Octave Tierce");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);

        // Suppression magasin
        int id = magasin.getId();
        MagasinUtils.delete(SESSION, magasin);
        try {
            MagasinUtils.get(SESSION, id);
            fail("Le magasin n'a pas été supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testCascade() {

        // Ajout magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Test Cascade");
        magasin.setAdresse1("14 Rue Octave Tierce");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);

        Famille famille = new Famille();
        famille.setNom("Test cascade");
        FamilleUtils.add(SESSION, famille);

        // Création instance
        Article article = new Article();
        article.setReference("TEST_CASCADE");
        article.setNom("Test cascade");
        article.setPrix(BigDecimal.TEN);
        article.setFamille(famille);
        ArticleUtils.add(SESSION, article);

        // Ajout stock
        Stock stock = new Stock();
        stock.setArticle(article);
        stock.setMagasin(magasin);
        stock.setStock(1);
        StockUtils.add(SESSION, stock);

        // Ajout client
        Client client = new Client();
        client.setNom("Garcia");
        client.setPrenom("Florian");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("tutozmodzhackz@gmail.com");
        client.setNaissance(LocalDate.of(1998, Month.AUGUST,3));
        ClientUtils.add(SESSION, client);

        // Ajout facture
        Facture facture = new Facture();
        facture.setMagasin(magasin);
        facture.setDate(new Date());
        facture.setClient(client);
        FactureUtils.add(SESSION, facture);

        // Récupération count
        final int countStock = StockUtils.getByArticle(SESSION, article).getStock();
        String sql = "SELECT d FROM " + Facture.class.getName() + " d";
        Query<Facture> query = SESSION.createQuery(sql);
        final int countFacture = query.getResultList().size();

        // Suppression magasin
        MagasinUtils.delete(SESSION, magasin);

        // Vérification résultats
        final int countStockResult = StockUtils.getByArticle(SESSION, article).getStock();
        sql = "SELECT d FROM " + Facture.class.getName() + " d";
        query = SESSION.createQuery(sql);
        final int countFactureResult = query.getResultList().size();
        assertEquals(countStock - 1, countStockResult);
        assertEquals(countFacture - 1, countFactureResult);

    }
}
