import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import fr.clientserveur.serveurmagasin.rmi.implementations.ImplementServeurMagasin;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import fr.clientserveur.serveurmagasin.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import fr.clientserveur.serveurmagasin.rmi.ServerMagasin;
import org.junit.Test;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class TestRMIClientMagasin {

    private static InterfaceServeurMagasin CLIENT;
    private static ServerMagasin SERVER;
    private static Session SESSION;
    private static Magasin MAGASIN;
    private static final Logger logger = LogManager.getRootLogger();

    @BeforeClass
    public static void start() throws Exception {

        // Création magasin test
        MAGASIN = new Magasin();
        MAGASIN.setNom("Magasin test");
        MAGASIN.setIp("127.0.0.1");
        MAGASIN.setAdresse1("Adresse 1");
        MAGASIN.setAdresse2("Adresse 2");

        // Persistence magasin
        SESSION = HibernateUtils.getHibernateDevSession();
        MagasinUtils.add(SESSION, MAGASIN);

        // Démarrage serveur magasin
        SERVER = new ServerMagasin(SESSION, logger, MAGASIN);
        InterfaceServeurMagasin skeleton = (InterfaceServeurMagasin) UnicastRemoteObject.exportObject(
                new ImplementServeurMagasin(0, SESSION, logger, MAGASIN, null),
                Config.RMI_LOCAL_PORT
        );
        Registry registry = LocateRegistry.createRegistry(10010);
        registry.rebind("ServeurMagasin", skeleton);

        // Initialisation des fixtures
        serverFixtures();

        // Démarrage du client
        registry = LocateRegistry.getRegistry(10010);
        CLIENT = (InterfaceServeurMagasin) registry.lookup("ServeurMagasin");
    }

    @AfterClass
    public static void stop() {
        System.exit(0);
    }

    private static void serverFixtures() {

        // Famille
        Famille famille = new Famille();
        famille.setNom("Outils");
        FamilleUtils.add(SESSION, famille);

        // Articles
        Article[] articles = new Article[3];
        articles[0] = new Article();
        articles[0].setReference("RXTTYR675");
        articles[0].setNom("Scie FACOM");
        articles[0].setPrix(BigDecimal.valueOf(12.50));
        articles[0].setFamille(famille);
        articles[1] = new Article();
        articles[1].setReference("RXTR432RT");
        articles[1].setNom("Marteau");
        articles[1].setPrix(BigDecimal.valueOf(10));
        articles[1].setFamille(famille);
        articles[2] = new Article();
        articles[2].setReference("RETZGGF5Y");
        articles[2].setNom("Tourne vis");
        articles[2].setPrix(BigDecimal.valueOf(2.55));
        articles[2].setFamille(famille);
        for (Article article: articles) {
            ArticleUtils.add(SESSION, article);
        }

        // Moyen de payement
        MoyenPayement moyenPayement = new MoyenPayement();
        moyenPayement.setNom("Carte bleue");
        MoyenPayementUtils.add(SESSION, moyenPayement);

        // Stocks
        for (int i = 0; i < articles.length; i++) {
            Stock stock = new Stock();
            stock.setMagasin(MAGASIN);
            stock.setArticle(articles[i]);
            stock.setStock((i + 1) * 10);
            StockUtils.add(SESSION, stock);
        }
    }

    @Test
    public void testAddAndSearchClient() throws RemoteException {

        // Création de l'instance
        Client client = new Client();
        client.setEmail("valentin.cocherel@gmail.com");
        client.setNom("Cocherel");
        client.setPrenom("Valentin");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setNaissance(LocalDate.of(1998,8,3));

        // Envoi de l'instance
        CLIENT.createOrUpdateClient(client);

        // Recherche du client
        List<Client> clients = CLIENT.searchClient("CocheRel", "alent");
        assertEquals(1, clients.size());
        assertEquals("Valentin", clients.get(0).getPrenom());
        assertEquals("Cocherel", clients.get(0).getNom());
    }

    @Test
    public void testStocksPagination() throws RemoteException {
        assertEquals(3, CLIENT.getStocks(10, 0).size());
        assertEquals(1, CLIENT.getStocksNumberPages(10));
        assertEquals(2, CLIENT.getStocks(2, 0).size());
        assertEquals(1, CLIENT.getStocks(2, 1).size());
        assertEquals(0, CLIENT.getStocks(2, 2).size());
        assertEquals(2, CLIENT.getStocksNumberPages(2));
    }

    @Test
    public void testStocksAndCommand() throws Exception {

        // Création du client
        Client client = new Client();
        client.setEmail("test.command@gmail.com");
        client.setNom("Command");
        client.setPrenom("Test");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");
        client.setNaissance(LocalDate.of(1998,8,3));
        client = CLIENT.createOrUpdateClient(client);

        // Récupération du stock
        List<Stock> stocks = CLIENT.getStocks(10, 0);
        assertEquals(3, stocks.size());

        // Création achats
        HashMap<Article, Integer> achatsList = new HashMap<>();
        achatsList.put(stocks.get(0).getArticle(), 2);
        achatsList.put(stocks.get(1).getArticle(), 4);

        // Définition résultat attendu
        String refExp1 = stocks.get(0).getArticle().getReference();
        String refExp2 = stocks.get(1).getArticle().getReference();
        int stockExp1 = stocks.get(0).getStock() - 2;
        int stockExp2 = stocks.get(1).getStock() - 4;
        BigDecimal prixExp1 = stocks.get(0).getArticle().getPrix();
        BigDecimal prixExp2 = stocks.get(1).getArticle().getPrix();
        BigDecimal totalExp = prixExp1.multiply(BigDecimal.valueOf(2)).add(prixExp2.multiply(BigDecimal.valueOf(4)));

        // Création de la commande
        Facture facture = CLIENT.clientOrder(client, achatsList);
        assertNotNull(facture);
        assertEquals(client.getId(), facture.getClient().getId());
        assertNotNull(facture.getDate());
        assertNull(facture.getDatePaye());
        assertNull(facture.getMoyenPaye());


        // Vérification total
        assertEquals(totalExp, CLIENT.totalFacture(facture));

        // Vérification achats
        List<Achat> achats = CLIENT.achatsFacture(facture);
        assertEquals(2, achats.size());
        assertEquals(refExp1, achats.get(0).getArticle().getReference());
        assertEquals(refExp2, achats.get(1).getArticle().getReference());
        assertEquals(prixExp1, achats.get(0).getPrixUnit());
        assertEquals(prixExp2, achats.get(1).getPrixUnit());
        assertEquals(2, achats.get(0).getQuantite());
        assertEquals(4, achats.get(1).getQuantite());

        // Vérification stock restant
        stocks = CLIENT.getStocks(10, 0);
        assertEquals(3, stocks.size());
        assertEquals(stockExp1, stocks.get(0).getStock());
        assertEquals(stockExp2, stocks.get(1).getStock());

        // Génération de la facture
        try {
            CLIENT.generateFacture(facture, achats);
        } catch (Exception e) {
            fail("Erreur lors de la génération de la facture.");
        }

        // Récupération des moyens de payement
        List<MoyenPayement> moyensPayement = CLIENT.getMoyensPayement();
        assertEquals(1, moyensPayement.size());
        assertEquals("Carte bleue", moyensPayement.get(0).getNom());

        // Définition du payement
        facture = CLIENT.payeFacture(facture, moyensPayement.get(0));
        assertEquals("Carte bleue", facture.getMoyenPaye().getNom());
        assertNotNull(facture.getDatePaye());
        assertEquals(1, CLIENT.getClientFactures(client).size());

        // Vérification contrainte stock
        final int stockErrExp = stocks.get(0).getStock();
        achatsList = new HashMap<>();
        achatsList.put(stocks.get(0).getArticle(), stockErrExp + 1);
        try {
            CLIENT.clientOrder(client, achatsList);
            fail("Commande créée stock négatif.");
        } catch (Exception e) {
            // OK
        }
        stocks = CLIENT.getStocks(10, 0);
        assertEquals(stockErrExp, stocks.get(0).getStock());
    }

}
