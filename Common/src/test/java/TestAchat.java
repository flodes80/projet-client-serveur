import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Ignore public class TestAchat {

    private static Session SESSION;
    private static Facture FACTURE;
    private static Article[] ARTICLES;

    @BeforeClass
    public static void start() {
        SESSION = HibernateUtils.getHibernateDevSession();
        Magasin magasin = new Magasin();
        magasin.setNom("TEST_ACHAT");
        magasin.setAdresse1("14 Rue Octave Tierce");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);
        Client client = new Client();
        client.setNom("Cocherel");
        client.setPrenom("Valentin");
        client.setEmail("valentin.cocherel@gmail.com");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setNaissance(LocalDate.of(1998, 8, 3));
        ClientUtils.add(SESSION, client);
        Famille famille = new Famille();
        famille.setNom("TEST_ACHAT");
        FamilleUtils.add(SESSION, famille);
        FACTURE = new Facture();
        FACTURE.setClient(client);
        FACTURE.setDate(new Date());
        FactureUtils.add(SESSION, FACTURE);
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
        Achat achat = new Achat();
        achat.setFacture(FACTURE);
        achat.setArticle(ARTICLES[0]);
        achat.setPrixUnit(achat.getArticle().getPrix());

        // Ajout instance
        AchatUtils.add(SESSION, achat);

        // Get instance
        Achat get = AchatUtils.get(SESSION, achat.getFacture(), achat.getArticle());

        // Vérification instance
        assertEquals(achat, get);
    }

    @Test
    public void testUpdate() {

        // Création instance
        Achat achat = new Achat();
        achat.setFacture(FACTURE);
        achat.setArticle(ARTICLES[1]);
        achat.setPrixUnit(achat.getArticle().getPrix());

        // Ajout instance
        AchatUtils.add(SESSION, achat);

        // Get instance
        Achat get = AchatUtils.get(SESSION, achat.getFacture(), achat.getArticle());

        // Modification instance
        BigDecimal update = BigDecimal.valueOf(achat.getPrixUnit().doubleValue() + 1.);
        get.setPrixUnit(update);

        // Update
        AchatUtils.update(SESSION, achat);

        // Get instance
        get = AchatUtils.get(SESSION, achat.getFacture(), achat.getArticle());

        // Vérifications
        assertEquals(update, get.getPrixUnit());
    }

    @Test
    public void testRemove() {

        // Création instance
        Achat achat = new Achat();
        achat.setFacture(FACTURE);
        achat.setArticle(ARTICLES[2]);
        achat.setPrixUnit(achat.getArticle().getPrix());

        // Ajout instance
        AchatUtils.add(SESSION, achat);

        // Suppression instance
        AchatUtils.delete(SESSION, achat);
        try {
            AchatUtils.get(SESSION, FACTURE, ARTICLES[2]);
            fail("L'achat n'a pas été supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testGetByFacture() {

        // Création instance
        Achat achat = new Achat();
        achat.setFacture(FACTURE);
        achat.setArticle(ARTICLES[3]);
        achat.setPrixUnit(achat.getArticle().getPrix());

        // Ajout instance
        AchatUtils.add(SESSION, achat);

        // GetByFacture
        List<Achat> list = AchatUtils.getByFacture(SESSION, FACTURE);

        // Vérifications
        assertFalse(list.isEmpty());
        assertTrue(list.contains(achat));
    }
}
