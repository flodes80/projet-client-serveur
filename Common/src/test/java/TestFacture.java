import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.*;

@Ignore public class TestFacture {

    private static Session SESSION;
    private static Client CLIENT;
    private static Magasin MAGASIN;
    private static MoyenPayement MOYENPAYEMENT;
    private static Article[] ARTICLES;

    @BeforeClass
    public static void start() {
        SESSION = HibernateUtils.getHibernateDevSession();
        MAGASIN = new Magasin();
        MAGASIN.setNom("TEST_ACHAT");
        MAGASIN.setAdresse1("14 Rue Octave Tierce");
        MAGASIN.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, MAGASIN);
        CLIENT = new Client();
        CLIENT.setNom("Cocherel");
        CLIENT.setPrenom("Valentin");
        CLIENT.setEmail("valentin.cocherel@gmail.com");
        CLIENT.setAdresse1("14 Rue Octave Tierce");
        CLIENT.setAdresse2("80080 Amiens");
        CLIENT.setNaissance(LocalDate.of(1998, 8, 3));
        ClientUtils.add(SESSION, CLIENT);
        MOYENPAYEMENT = new MoyenPayement();
        MOYENPAYEMENT.setNom("TestFacture");
        MoyenPayementUtils.add(SESSION, MOYENPAYEMENT);
        Famille famille = new Famille();
        famille.setNom("TEST_ACHAT");
        FamilleUtils.add(SESSION, famille);
        final int nb = 4;
        ARTICLES = new Article[nb];
        for (int i = 0; i < nb; i++) {
            ARTICLES[i] = new Article();
            ARTICLES[i].setNom("Test Achat " + i);
            ARTICLES[i].setReference("TEST_ACHAT_" + i);
            ARTICLES[i].setFamille(famille);
            ARTICLES[i].setPrix(BigDecimal.valueOf((i + 1) * 10));
            ArticleUtils.add(SESSION, ARTICLES[i]);
        }
    }

    @AfterClass
    public static void end() {
        SESSION.close();
    }

    @Test
    public void testAdd() {

        // Création instance
        Facture facture = new Facture();
        facture.setMagasin(MAGASIN);
        facture.setClient(CLIENT);
        facture.setDate(new Date());

        // Ajout instance
        FactureUtils.add(SESSION, facture);

        // Get instance
        Facture get = FactureUtils.get(SESSION, facture.getId());

        // Vérification instance
        assertEquals(facture, get);
        assertNull(facture.getDatePaye());
    }

    @Test
    public void testUpdate() {

        // Création instance
        Facture facture = new Facture();
        facture.setMagasin(MAGASIN);
        facture.setClient(CLIENT);
        facture.setDate(new Date());

        // Ajout instance
        FactureUtils.add(SESSION, facture);

        // Modification instance
        facture.setDatePaye(new Date());
        facture.setMoyenPaye(MOYENPAYEMENT);

        // Update
        FactureUtils.update(SESSION, facture);

        // Get instance
        Facture get = FactureUtils.get(SESSION, facture.getId());

        // Vérifications
        assertEquals(facture, get);
        assertNotNull(get.getDatePaye());
    }

    @Test
    public void testRemove() {

        // Création instance
        Facture facture = new Facture();
        facture.setMagasin(MAGASIN);
        facture.setClient(CLIENT);
        facture.setDate(new Date());

        // Ajout instance
        FactureUtils.add(SESSION, facture);

        // Suppression instance
        final int id = facture.getId();
        FactureUtils.delete(SESSION, facture);
        try {
            FactureUtils.get(SESSION, facture.getId());
            fail("La facture n'a pas été supprimée.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testGetTotal() {

        // Création instance
        Facture facture = new Facture();
        facture.setMagasin(MAGASIN);
        facture.setClient(CLIENT);
        facture.setDate(new Date());
        FactureUtils.add(SESSION, facture);

        // Vérification si pas d'achat
        BigDecimal empty = FactureUtils.getTotal(SESSION, facture);
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.CEILING), empty);

        // Création achats
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < 3; i++) {
            Achat achat = new Achat();
            achat.setFacture(facture);
            achat.setArticle(ARTICLES[i]);
            achat.setPrixUnit(achat.getArticle().getPrix());
            achat.setQuantite(i * 3);
            AchatUtils.add(SESSION, achat);
            total = total.add(achat.getPrixUnit().multiply(BigDecimal.valueOf(achat.getQuantite())));
        }

        // getTotal
        BigDecimal result = FactureUtils.getTotal(SESSION, facture);

        // Vérifications
        assertEquals(total.setScale(2, RoundingMode.CEILING), result);
    }

    @Test
    public void testCascade() {

        // Création facture
        Facture facture = new Facture();
        facture.setClient(CLIENT);
        facture.setMagasin(MAGASIN);
        facture.setDate(new Date());
        FactureUtils.add(SESSION, facture);

        // Ajout achat
        Achat achat = new Achat();
        achat.setArticle(ARTICLES[0]);
        achat.setQuantite(1);
        achat.setPrixUnit(achat.getArticle().getPrix());
        achat.setFacture(facture);
        AchatUtils.add(SESSION, achat);
        achat = new Achat();
        achat.setArticle(ARTICLES[1]);
        achat.setQuantite(1);
        achat.setPrixUnit(achat.getArticle().getPrix());
        achat.setFacture(facture);
        AchatUtils.add(SESSION, achat);

        // Récupération nombre achats
        String sql = "SELECT d FROM " + Achat.class.getName() + " d";
        Query<Achat> query = SESSION.createQuery(sql);
        final int achatCount = query.getResultList().size();

        // Suppression facture
        FactureUtils.delete(SESSION, facture);

        // Vérification cascade
        sql = "SELECT d FROM " + Achat.class.getName() + " d";
        query = SESSION.createQuery(sql);
        final int achatCountResult = query.getResultList().size();
        assertEquals(achatCount - 2, achatCountResult);
    }
}
