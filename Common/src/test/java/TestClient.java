import fr.clientserveur.common.entities.Client;
import fr.clientserveur.common.entities.Facture;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.ormentities.ClientUtils;
import fr.clientserveur.common.entities.ormentities.FactureUtils;
import fr.clientserveur.common.entities.ormentities.HibernateUtils;
import fr.clientserveur.common.entities.ormentities.MagasinUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

import static org.junit.Assert.*;

@Ignore public class TestClient {

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

        // Création
        Client client = new Client();
        client.setNom("Cocherel");
        client.setPrenom("Valentin");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("valentin.cocherel.add@gmail.com");
        client.setNaissance(LocalDate.of(1998, Month.AUGUST,3));

        // Ajout de l'article en base
        ClientUtils.add(SESSION, client);
        assertNotNull(client.getId());

        // Création nouveau client
        client = new Client();
        client.setNom("Cocherel");
        client.setPrenom("Valentin");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("valentin.cocherel.add@gmail.com");
        client.setNaissance(LocalDate.of(1998, Month.AUGUST,3));

        // Test unique
        try {
            ClientUtils.add(SESSION, client);
            fail("Contrainte unique non respectée.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testUpdate() {

        // Création
        Client client = new Client();
        client.setNom("Leclère");
        client.setPrenom("Florian");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("valentin.cocherel.update@gmail.com");
        client.setNaissance(LocalDate.of(1998, Month.AUGUST,3));
        ClientUtils.add(SESSION, client);

        // Modification de l'instance
        client.setNom("Garcia");
        ClientUtils.update(SESSION, client);

        // Vérification
        assertEquals("Garcia", client.getNom());
    }

    @Test
    public void testDelete() {

        // Création
        Client client = new Client();
        client.setNom("Le berre");
        client.setPrenom("Brendan");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("valentin.cocherel.delete@gmail.com");
        client.setNaissance(LocalDate.of(1998, Month.AUGUST,3));
        ClientUtils.add(SESSION, client);

        // Suppression instance
        int id = client.getId();
        ClientUtils.delete(SESSION, client);

        // Vérification suppression
        try {
            ClientUtils.get(SESSION, id);
            fail("Client non supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testCascade() {

        // Création
        Client client = new Client();
        client.setNom("Garcia");
        client.setPrenom("Florian");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setEmail("valentin.cocherel.cascade@gmail.com");
        client.setNaissance(LocalDate.of(1998, Month.AUGUST,3));
        ClientUtils.add(SESSION, client);

        // Ajout magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Test Article Cascade");
        magasin.setAdresse1("475 Route de la mer");
        magasin.setAdresse2("80080 Amiens");
        MagasinUtils.add(SESSION, magasin);

        // Ajout facture
        Facture facture = new Facture();
        facture.setMagasin(magasin);
        facture.setDate(new Date());
        facture.setClient(client);
        FactureUtils.add(SESSION, facture);

        // Récupération nombre factures
        String sql = "SELECT d FROM " + Facture.class.getName() + " d";
        Query<Facture> query = SESSION.createQuery(sql);
        final int factureCount = query.getResultList().size();

        // Suppression instance
        ClientUtils.delete(SESSION, client);

        // Vérification cascade
        sql = "SELECT d FROM " + Facture.class.getName() + " d";
        query = SESSION.createQuery(sql);
        final int factureCountResult = query.getResultList().size();
        assertEquals(factureCount - 1, factureCountResult);
    }
}
