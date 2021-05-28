import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.ormentities.ArticleUtils;
import fr.clientserveur.common.entities.ormentities.FamilleUtils;
import fr.clientserveur.common.entities.ormentities.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@Ignore public class TestFamille {

    private static Session SESSION;

    @BeforeClass
    public static void start() {
        SESSION = HibernateUtils.getHibernateDevSession();
    }

    @AfterClass
    public static void end() {
        SESSION.close();
    }

    @Test
    public void testAdd() {

        // Création instance
        Famille famille = new Famille();
        famille.setNom("TestAdd");

        // Ajout instance en base
        FamilleUtils.add(SESSION, famille);

        // Get stock
        Famille get = FamilleUtils.get(SESSION, famille.getId());
        assertEquals(famille, get);
    }

    @Test
    public void testGetAll() {

        // Création des instances
        Famille famille1 = new Famille();
        Famille famille2 = new Famille();
        famille1.setNom("TestGetAll1");
        famille2.setNom("TestGetAll2");
        FamilleUtils.add(SESSION, famille1);
        FamilleUtils.add(SESSION, famille2);

        // Get all des instances
        List<Famille> getAll = FamilleUtils.getAll(SESSION);

        // Vérification données
        assertTrue(getAll.size() >= 2);
        assertTrue(getAll.contains(famille1));
        assertTrue(getAll.contains(famille2));
    }

    @Test
    public void testUpdate() {

        // Création instance
        Famille famille = new Famille();
        famille.setNom("TestUpdate");
        FamilleUtils.add(SESSION, famille);

        // Get instance
        famille = FamilleUtils.get(SESSION, famille.getId());

        // Modification stock
        famille.setNom("TestUpdateModified");
        FamilleUtils.update(SESSION, famille);

        // Vérification résultat
        famille = FamilleUtils.get(SESSION, famille.getId());
        assertEquals("TestUpdateModified", famille.getNom());
    }

    @Test
    public void testDelete() {

        // Création instance
        Famille famille = new Famille();
        famille.setNom("TestDelete");
        FamilleUtils.add(SESSION, famille);

        // Get instance
        famille = FamilleUtils.get(SESSION, famille.getId());

        // Suppression instance
        int id = famille.getId();
        FamilleUtils.delete(SESSION, famille);

        // Vérification suppression
        try {
            FamilleUtils.get(SESSION, id);
            fail("Stock non supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testCascade() {

        // Création instance
        Famille famille = new Famille();
        famille.setNom("TestCascade");
        FamilleUtils.add(SESSION, famille);

        // Ajout articles
        Article article = new Article();
        article.setNom("Test Cascade 1");
        article.setFamille(famille);
        article.setPrix(BigDecimal.ONE);
        article.setReference("TEST_ARTICLE_1");
        ArticleUtils.add(SESSION, article);
        article = new Article();
        article.setNom("Test Cascade 2");
        article.setFamille(famille);
        article.setPrix(BigDecimal.ONE);
        article.setReference("TEST_ARTICLE_2");
        ArticleUtils.add(SESSION, article);

        // Récupération nombre article
        String sql = "SELECT d FROM " + Article.class.getName() + " d";
        Query<Article> query = SESSION.createQuery(sql);
        final int articleCount = query.getResultList().size();

        // Suppression instance
        FamilleUtils.delete(SESSION, famille);

        // Vérification résultats
        sql = "SELECT d FROM " + Article.class.getName() + " d";
        query = SESSION.createQuery(sql);
        final int articleCountResult = query.getResultList().size();
        assertEquals(articleCount - 2, articleCountResult);
    }
}
