package fr.clientserveur.serveurcentral.utils;

import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.ormentities.ArticleUtils;
import fr.clientserveur.common.entities.ormentities.FamilleUtils;
import org.hibernate.Session;

import java.math.BigDecimal;

public class Fixtures {

    /**
     * Classe utilitaire de chargement de fixtures pour le serveur central
     * @param session Session hibernate
     */
    public static void loadFixtures(Session session){
        clearDatabase(session);

        // Création de familles
        loadFamilleFixtures(session);

        // Création d'articles
        loadArticleFixtures(session);
    }
    private static void loadArticleFixtures(Session session){
        Article article1 = new Article();
        article1.setReference("#F01");
        article1.setNom("But de foot");
        article1.setDescription("Cage en fer pour jouer au foot");
        article1.setFamille(FamilleUtils.getByNom(session, "Football"));
        article1.setPrix(new BigDecimal("99.9"));
        ArticleUtils.add(session, article1);

        Article article2 = new Article();
        article2.setReference("#F02");
        article2.setNom("Ballon de foot");
        article2.setDescription("Une balle de foot robuste pour de grands matchs");
        article2.setFamille(FamilleUtils.getByNom(session, "Football"));
        article2.setPrix(new BigDecimal("7.99"));
        ArticleUtils.add(session, article2);

        Article article3 = new Article();
        article3.setReference("#F03");
        article3.setNom("Chasuble");
        article3.setDescription("Un chasuble pour les entraînements");
        article3.setFamille(FamilleUtils.getByNom(session, "Football"));
        article3.setPrix(new BigDecimal("3.00"));
        ArticleUtils.add(session, article3);

        Article article4 = new Article();
        article4.setReference("#B01");
        article4.setNom("Panier de basket");
        article4.setDescription("Un beau panier pour les grands joueurs");
        article4.setFamille(FamilleUtils.getByNom(session, "Basketball"));
        article4.setPrix(new BigDecimal("149.90"));
        ArticleUtils.add(session, article4);

        Article article5 = new Article();
        article5.setReference("#B02");
        article5.setNom("Tenue de basket");
        article5.setDescription("Tenue complète rouge");
        article5.setFamille(FamilleUtils.getByNom(session, "Basketball"));
        article5.setPrix(new BigDecimal("24.50"));
        ArticleUtils.add(session, article5);

        Article article6 = new Article();
        article6.setReference("#B03");
        article6.setNom("Ballon de basket");
        article6.setDescription("Ballon de basket d'intérieur");
        article6.setFamille(FamilleUtils.getByNom(session, "Basketball"));
        article6.setPrix(new BigDecimal("9.20"));
        ArticleUtils.add(session, article6);

        Article article7 = new Article();
        article7.setReference("#T01");
        article7.setNom("Raquette de tennis");
        article7.setDescription("Raquette en nylon");
        article7.setFamille(FamilleUtils.getByNom(session, "Tennis"));
        article7.setPrix(new BigDecimal("45.00"));
        ArticleUtils.add(session, article7);

        Article article8 = new Article();
        article8.setReference("#T02");
        article8.setNom("Filet de tennis");
        article8.setDescription("Filet de tennis de 20m");
        article8.setFamille(FamilleUtils.getByNom(session, "Tennis"));
        article8.setPrix(new BigDecimal("60.00"));
        ArticleUtils.add(session, article8);

        Article article9 = new Article();
        article9.setReference("#T03");
        article9.setNom("3 Balles de tennis");
        article9.setDescription("Un lot de  3 balles de tennis");
        article9.setFamille(FamilleUtils.getByNom(session, "Tennis"));
        article9.setPrix(new BigDecimal("6.00"));
        ArticleUtils.add(session, article9);

        Article article10 = new Article();
        article10.setReference("#C01");
        article10.setNom("Tente étoile");
        article10.setDescription("Une tente en forme d'étoile pour de petits moments conviviaux");
        article10.setFamille(FamilleUtils.getByNom(session, "Camping"));
        article10.setPrix(new BigDecimal("189.99"));
        ArticleUtils.add(session, article10);

        Article article11 = new Article();
        article11.setReference("#C02");
        article11.setNom("Barbecue");
        article11.setDescription("Un barbecue en acier inoxydable");
        article11.setFamille(FamilleUtils.getByNom(session, "Camping"));
        article11.setPrix(new BigDecimal("49.99"));
        ArticleUtils.add(session, article11);

        Article article12 = new Article();
        article12.setReference("#C03");
        article12.setNom("Glacière");
        article12.setDescription("Une glacière idéale pour un pique-nique en famille ou entres amis !");
        article12.setFamille(FamilleUtils.getByNom(session, "Camping"));
        article12.setPrix(new BigDecimal("24.90"));
        ArticleUtils.add(session, article12);
    }

    private static void loadFamilleFixtures(Session session){
        Famille famille1 = new Famille();
        famille1.setNom("Football");
        FamilleUtils.add(session, famille1);

        Famille famille2 = new Famille();
        famille2.setNom("Basketball");
        FamilleUtils.add(session, famille2);

        Famille famille3 = new Famille();
        famille3.setNom("Tennis");
        FamilleUtils.add(session, famille3);

        Famille famille4 = new Famille();
        famille4.setNom("Camping");
        FamilleUtils.add(session, famille4);
    }

    private static void clearDatabase(Session session){
        FamilleUtils.removeAll(session);
    }
}
