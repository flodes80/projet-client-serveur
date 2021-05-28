import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;
import org.junit.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Ignore public class TestArticle {

    private static Famille[] FAMILLES;
    private static Session SESSION;

    @BeforeClass
    public static void start() {
        final int nb = 4;
        SESSION = HibernateUtils.getHibernateDevSession();
        FAMILLES = new Famille[nb];
        for (int i = 0; i < nb; i++) {
            FAMILLES[i] = new Famille();
            FAMILLES[i].setNom("Test " + i);
            FamilleUtils.add(SESSION, FAMILLES[i]);
        }
    }

    @AfterClass
    public static void end() {
        SESSION.close();
    }

    @Test
    public void testAdd() {

        // Création article
        Article article = new Article();
        article.setReference("TEST_ADD");
        article.setNom("Scie");
        article.setFamille(FAMILLES[0]);
        article.setPrix(BigDecimal.valueOf(24.10));

        // Ajout de l'article en base
        ArticleUtils.add(SESSION, article);
    }

    @Test
    public void testGetAll() {

        // Création article
        Article article = new Article();
        article.setReference("TEST_GETALL");
        article.setNom("GetAll");
        article.setFamille(FAMILLES[0]);
        article.setPrix(BigDecimal.valueOf(24.10));

        // Ajout de l'article en base
        ArticleUtils.add(SESSION, article);

        // Récupération des instances
        List<Article> all = ArticleUtils.getAll(SESSION);

        // Vérification des données
        assertFalse(all.isEmpty());
        assertTrue(all.contains(article));
    }

    @Test
    public void testUpdate() {

        // Création article
        Article article = new Article();
        article.setReference("TEST_UPDATE");
        article.setNom("Scie");
        article.setFamille(FAMILLES[0]);
        article.setPrix(BigDecimal.valueOf(24.10));
        ArticleUtils.add(SESSION, article);

        // Modification de l'instance
        article.setDescription("Ceci est une description.");
        article.setNom("Scie FACOM");
        article.setFamille(FAMILLES[1]);

        // Mise a jour de l'instance
        ArticleUtils.update(SESSION, article);

        // Vérification
        assertEquals("Scie FACOM", article.getNom());
        assertEquals("Ceci est une description.", article.getDescription());
        assertEquals(FAMILLES[1], article.getFamille());
    }

    @Test
    public void testDelete() {

        // Création article
        Article article = new Article();
        article.setReference("TEST_DELETE");
        article.setNom("Scie");
        article.setFamille(FAMILLES[0]);
        article.setPrix(BigDecimal.valueOf(24.10));
        ArticleUtils.add(SESSION, article);

        // Suppression instance
        String reference = article.getReference();
        ArticleUtils.delete(SESSION, article);

        // Vérification suppression
        try {
            ArticleUtils.get(SESSION, reference);
            fail("Article non supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testGetByFamille() {

        // Création articles
        Article article1 = new Article();
        article1.setReference("TEST_GETBYFAMILLE_1");
        article1.setNom("GetByFamille 1");
        article1.setFamille(FAMILLES[2]);
        article1.setPrix(BigDecimal.valueOf(24.10));
        ArticleUtils.add(SESSION, article1);
        Article article2 = new Article();
        article2.setReference("TEST_GETBYFAMILLE_2");
        article2.setNom("GetByFamille 2");
        article2.setFamille(FAMILLES[2]);
        article2.setPrix(BigDecimal.valueOf(24.10));
        ArticleUtils.add(SESSION, article2);

        // GetByFamille
        List<Article> articleList = ArticleUtils.getByFamille(SESSION, FAMILLES[2]);

        // Vérification
        assertEquals(2, articleList.size());
        assertTrue(articleList.contains(article1));
        assertTrue(articleList.contains(article2));
    }

    @Test
    public void testCascade() {

        // Création instance
        Article article = new Article();
        article.setReference("TEST_CASCADE");
        article.setNom("Test cascade");
        article.setPrix(BigDecimal.TEN);
        article.setFamille(FAMILLES[3]);
        ArticleUtils.add(SESSION, article);

        // Ajout magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Test Article Cascade");
        magasin.setAdresse1("475 Route de la mer");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);

        // Ajout stock
        Stock stock = new Stock();
        stock.setArticle(article);
        stock.setMagasin(magasin);
        stock.setStock(1);
        StockUtils.add(SESSION, stock);

        // Ajout client
        Client client = new Client();
        client.setNom("Cocherel");
        client.setPrenom("Valentin");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("valentin.cocherel@gmail.com");
        client.setNaissance(LocalDate.of(1998, 8, 3));
        ClientUtils.add(SESSION, client);

        // Ajout facture
        Facture facture = new Facture();
        facture.setMagasin(magasin);
        facture.setDate(new Date());
        facture.setClient(client);
        FactureUtils.add(SESSION, facture);

        // Ajout achat
        Achat achat = new Achat();
        achat.setFacture(facture);
        achat.setArticle(article);
        achat.setQuantite(1);
        achat.setPrixUnit(article.getPrix());
        AchatUtils.add(SESSION, achat);

        // Vérification données
        final int familleCount = ArticleUtils.getByFamille(SESSION, FAMILLES[3]).size();
        final int stockCount = StockUtils.getByMagasin(SESSION, magasin).size();
        final int achatCount = AchatUtils.getByFacture(SESSION, facture).size();

        // Suppression instance
        ArticleUtils.delete(SESSION, article);

        // Vérification cascade
        assertEquals(
                familleCount - 1,
                ArticleUtils.getByFamille(SESSION, FAMILLES[3]).size()
        );
        assertEquals(
                stockCount - 1,
                StockUtils.getByMagasin(SESSION, magasin).size()
        );
        assertEquals(
                achatCount - 1,
                AchatUtils.getByFacture(SESSION, facture).size()
        );

    }
}
