import fr.clientserveur.common.entities.Client;
import fr.clientserveur.common.entities.Facture;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.MoyenPayement;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Ignore public class TestMoyenPayement {

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
        MoyenPayement moyenPayement = new MoyenPayement();
        moyenPayement.setNom("TestAdd");

        // Ajout instance en base
        MoyenPayementUtils.add(SESSION, moyenPayement);

        // Get stock
        MoyenPayement moyenPayementGet = MoyenPayementUtils.get(SESSION, moyenPayement.getId());
        assertEquals(moyenPayement, moyenPayementGet);
    }

    @Test
    public void testGetAll() {

        // Création des instances
        MoyenPayement moyenPayement1 = new MoyenPayement();
        MoyenPayement moyenPayement2 = new MoyenPayement();
        moyenPayement1.setNom("TestGetAll1");
        moyenPayement2.setNom("TestGetAll2");
        MoyenPayementUtils.add(SESSION, moyenPayement1);
        MoyenPayementUtils.add(SESSION, moyenPayement2);

        // Get all des instances
        List<MoyenPayement> getAll = MoyenPayementUtils.getAll(SESSION);

        // Vérification données
        assertTrue(getAll.size() >= 2);
        assertTrue(getAll.contains(moyenPayement1));
        assertTrue(getAll.contains(moyenPayement2));
    }

    @Test
    public void testUpdate() {

        // Création instance
        MoyenPayement moyenPayement = new MoyenPayement();
        moyenPayement.setNom("TestUpdate");
        MoyenPayementUtils.add(SESSION, moyenPayement);

        // Get instance
        moyenPayement = MoyenPayementUtils.get(SESSION, moyenPayement.getId());

        // Modification stock
        moyenPayement.setNom("TestUpdateModified");
        MoyenPayementUtils.update(SESSION, moyenPayement);

        // Vérification résultat
        moyenPayement = MoyenPayementUtils.get(SESSION, moyenPayement.getId());
        assertEquals("TestUpdateModified", moyenPayement.getNom());
    }

    @Test
    public void testDelete() {

        // Création instance
        MoyenPayement moyenPayement = new MoyenPayement();
        moyenPayement.setNom("TestDelete");
        MoyenPayementUtils.add(SESSION, moyenPayement);

        // Get instance
        moyenPayement = MoyenPayementUtils.get(SESSION, moyenPayement.getId());

        // Suppression instance
        int id = moyenPayement.getId();
        MoyenPayementUtils.delete(SESSION, moyenPayement);

        // Vérification suppression
        try {
            MoyenPayementUtils.get(SESSION, id);
            fail("Stock non supprimé.");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testCascade() {

        // Création instance
        MoyenPayement moyenPayement = new MoyenPayement();
        moyenPayement.setNom("TestCascade");
        MoyenPayementUtils.add(SESSION, moyenPayement);

        // Crétion magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Test cascade");
        magasin.setAdresse1("475 Route de la mer");
        magasin.setAdresse2("76590 Torcy le grand");
        MagasinUtils.add(SESSION, magasin);

        // Création client
        Client client = new Client();
        client.setPrenom("Valentin");
        client.setNom("Cocherel");
        client.setNaissance(LocalDate.of(1998, 8, 3));
        client.setEmail("valentin.cocherel@gmail.com");
        ClientUtils.add(SESSION, client);

        // Création facture
        Facture facture = new Facture();
        facture.setMagasin(magasin);
        facture.setDate(new Date());
        facture.setClient(client);
        facture.setDatePaye(new Date());
        facture.setMoyenPaye(moyenPayement);
        FactureUtils.add(SESSION, facture);

        // Récupération nombre factures
        String sql = "SELECT d FROM " + Facture.class.getName() + " d";
        Query<Facture> query = SESSION.createQuery(sql);
        final int factureCount = query.getResultList().size();

        // Suppression instance
        MoyenPayementUtils.delete(SESSION, moyenPayement);

        // Vérifications
        sql = "SELECT d FROM " + Facture.class.getName() + " d";
        query = SESSION.createQuery(sql);
        final int factureCountResult = query.getResultList().size();
        assertEquals(factureCount - 1, factureCountResult);
    }
}
