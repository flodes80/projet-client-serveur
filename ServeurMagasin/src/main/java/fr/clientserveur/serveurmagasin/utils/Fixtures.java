package fr.clientserveur.serveurmagasin.utils;

import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Fixtures {

    /**
     * Classe utilitaire de chargement de fixtures pour serveur magasin
     * @param session Session hibernate
     */
    public static void loadFixtures(Session session, Magasin magasin){
        // Clear de la BDD avant ajout des fixtures
        clearDatabase(session);

        // Création de stocks
        loadStockFixtures(session, magasin);

        // Création de clients
        loadClientFixtures(session);

        // Création des moyens de paiement
        loadMoyenPayementFixtures(session);

        // Création de factures
        loadFactureFixtures(session, magasin);
    }

    private static void loadClientFixtures(Session session){
        Client client1 = new Client();
        client1.setNom("Guilmette");
        client1.setPrenom("Françoise");
        client1.setNaissance(LocalDate.of(1960, 8, 19));
        client1.setAdresse1("59 rue du Palais");
        client1.setAdresse2("91150 ETAMPES");
        client1.setEmail("FrancoiseGuilmette@teleworm.com");
        ClientUtils.add(session, client1);

        Client client2 = new Client();
        client2.setNom("Sciverit");
        client2.setPrenom("Clémence");
        client2.setNaissance(LocalDate.of(1968, 5, 21));
        client2.setAdresse1("6 rue Jean Vilar");
        client2.setAdresse2("60000 BEAUVAIS");
        client2.setEmail("ClemenceSciverit@jourrapide.com");
        ClientUtils.add(session, client2);

        Client client3 = new Client();
        client3.setNom("Corbeil");
        client3.setPrenom("Georges");
        client3.setNaissance(LocalDate.of(1957, 1, 16));
        client3.setAdresse1("70 rue Cazade");
        client3.setAdresse2("28100 DREUX");
        client3.setEmail("GeorgesCorbeil@jourrapide.com");
        ClientUtils.add(session, client3);

        Client client4 = new Client();
        client4.setNom("Picard");
        client4.setPrenom("Christophe");
        client4.setNaissance(LocalDate.of(1960, 4, 27));
        client4.setAdresse1("48 Avenue des Près");
        client4.setAdresse2("34000 MONTPELLIER");
        client4.setEmail("ChristophePicard@rhyta.com");
        ClientUtils.add(session, client4);
    }

    private static void loadFactureFixtures(Session session, Magasin magasin){
        Client client1 = ClientUtils.searchByNomPrenom(session, "Guilmette", "Françoise").get(0);
        Client client2 = ClientUtils.searchByNomPrenom(session, "Sciverit", "Clémence").get(0);
        Client client3 = ClientUtils.searchByNomPrenom(session, "Corbeil", "Georges").get(0);
        Client client4 = ClientUtils.searchByNomPrenom(session, "Picard", "Christophe").get(0);

        Article article1 = ArticleUtils.get(session, "#F01");
        Article article2 = ArticleUtils.get(session, "#T01");
        Article article3 = ArticleUtils.get(session, "#T02");
        Article article4 = ArticleUtils.get(session, "#C02");
        Article article5 = ArticleUtils.get(session, "#B02");

        Map<Article, Integer> achatsClient1 = new HashMap<>();
        achatsClient1.put(article1, 1);
        achatsClient1.put(article3, 2);

        Map<Article, Integer> achatsClient2 = new HashMap<>();
        achatsClient2.put(article2, 2);
        achatsClient2.put(article5, 3);
        achatsClient2.put(article4, 1);

        Map<Article, Integer> achatsClient3 = new HashMap<>();
        achatsClient3.put(article4, 1);
        achatsClient3.put(article1, 2);

        Map<Article, Integer> achatsClient4 = new HashMap<>();
        achatsClient4.put(article1, 1);
        achatsClient4.put(article2, 2);
        achatsClient4.put(article3, 2);
        achatsClient4.put(article4, 1);
        achatsClient4.put(article5, 3);

        try {
            Facture facture1 = FactureUtils.command(session, magasin, client1, achatsClient1);

            FactureMakerGenerator.generatePdf(facture1, AchatUtils.getByFacture(session, facture1));

            Facture facture2 = FactureUtils.command(session, magasin, client2, achatsClient2);
            FactureMakerGenerator.generatePdf(facture2, AchatUtils.getByFacture(session, facture2));

            Facture facture3 = FactureUtils.command(session, magasin, client3, achatsClient3);
            FactureMakerGenerator.generatePdf(facture3, AchatUtils.getByFacture(session, facture3));

            Facture facture4 = FactureUtils.command(session, magasin, client4, achatsClient4);
            FactureMakerGenerator.generatePdf(facture4, AchatUtils.getByFacture(session, facture4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadMoyenPayementFixtures(Session session){
        MoyenPayement moyenPayement1 = new MoyenPayement();
        moyenPayement1.setNom("Carte bleu");
        MoyenPayementUtils.add(session, moyenPayement1);

        MoyenPayement moyenPayement2 = new MoyenPayement();
        moyenPayement2.setNom("Espèce");
        MoyenPayementUtils.add(session, moyenPayement2);

        MoyenPayement moyenPayement3 = new MoyenPayement();
        moyenPayement3.setNom("Chèque");
        MoyenPayementUtils.add(session, moyenPayement3);
    }

    private static void loadStockFixtures(Session session, Magasin magasin){
        Stock stock1 = new Stock();
        stock1.setArticle(ArticleUtils.get(session, "#F01"));
        stock1.setStock(10);
        stock1.setMagasin(magasin);
        StockUtils.add(session, stock1);

        Stock stock2 = new Stock();
        stock2.setArticle(ArticleUtils.get(session, "#F02"));
        stock2.setStock(30);
        stock2.setMagasin(magasin);
        StockUtils.add(session, stock2);

        Stock stock3 = new Stock();
        stock3.setArticle(ArticleUtils.get(session, "#F03"));
        stock3.setStock(20);
        stock3.setMagasin(magasin);
        StockUtils.add(session, stock3);

        Stock stock4 = new Stock();
        stock4.setArticle(ArticleUtils.get(session, "#B01"));
        stock4.setStock(5);
        stock4.setMagasin(magasin);
        StockUtils.add(session, stock4);

        Stock stock5 = new Stock();
        stock5.setArticle(ArticleUtils.get(session, "#B02"));
        stock5.setStock(18);
        stock5.setMagasin(magasin);
        StockUtils.add(session, stock5);

        Stock stock6 = new Stock();
        stock6.setArticle(ArticleUtils.get(session, "#B03"));
        stock6.setStock(13);
        stock6.setMagasin(magasin);
        StockUtils.add(session, stock6);

        Stock stock7 = new Stock();
        stock7.setArticle(ArticleUtils.get(session, "#T01"));
        stock7.setStock(9);
        stock7.setMagasin(magasin);
        StockUtils.add(session, stock7);

        Stock stock8 = new Stock();
        stock8.setArticle(ArticleUtils.get(session, "#T02"));
        stock8.setStock(20);
        stock8.setMagasin(magasin);
        StockUtils.add(session, stock8);

        Stock stock9 = new Stock();
        stock9.setArticle(ArticleUtils.get(session, "#T03"));
        stock9.setStock(35);
        stock9.setMagasin(magasin);
        StockUtils.add(session, stock9);

        Stock stock10 = new Stock();
        stock10.setArticle(ArticleUtils.get(session, "#C01"));
        stock10.setStock(5);
        stock10.setMagasin(magasin);
        StockUtils.add(session, stock10);

        Stock stock11 = new Stock();
        stock11.setArticle(ArticleUtils.get(session, "#C02"));
        stock11.setStock(10);
        stock11.setMagasin(magasin);
        StockUtils.add(session, stock11);

        Stock stock12 = new Stock();
        stock12.setArticle(ArticleUtils.get(session, "#C03"));
        stock12.setStock(21);
        stock12.setMagasin(magasin);
        StockUtils.add(session, stock12);
    }

    private static void clearDatabase(Session session){
        StockUtils.removeAll(session);
        ClientUtils.removeAll(session);
        MoyenPayementUtils.removeAll(session);
    }


}
