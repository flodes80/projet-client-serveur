package fr.clientserveur.serveurmagasin.rmi.implementations;

import com.itextpdf.text.DocumentException;
import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.entities.ormentities.*;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurCentral;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import fr.clientserveur.serveurmagasin.utils.Config;
import fr.clientserveur.serveurmagasin.utils.FactureMakerGenerator;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImplementServeurMagasin extends UnicastRemoteObject implements InterfaceServeurMagasin {

    private final Session session;
    private final Logger logger;
    private final Magasin magasin;
    private final InterfaceServeurCentral serveurCentral;

    /**
     * Constructeur pour le côté serveur
     * @param session Session hibernate
     * @param logger Logger général de l'app
     * @param serverCentral Interface du serveur central
     */
    public ImplementServeurMagasin(int port, Session session, Logger logger, Magasin magasin, InterfaceServeurCentral serverCentral) throws RemoteException {
        super(port);
        this.session = session;
        this.logger = logger;
        this.magasin = magasin;
        this.serveurCentral = serverCentral;
    }

    /**
     * Méthode permettant la génération du fichier PDF de facture.
     * @param facture Facture du client
     * @param achats Achats du client
     * @return Chemin du fichier généré ou null si erreur
     */
    @Override
    public String generateFacture(Facture facture, List<Achat> achats) throws FileNotFoundException, DocumentException {
        logger.info("Génération d'un fichier pdf pour la facture: " + facture.getId());
        return FactureMakerGenerator.generatePdf(facture,  achats);
    }

    @Override
    public Magasin getCurrentMagasin() {
        return this.magasin;
    }

    @Override
    public List<Magasin> getAllMagasins() throws RemoteException {
        return serveurCentral.getAllMagasins();
    }

    @Override
    public List<Stock> getStocks(int pageSize, int pagination) {
        return StockUtils.getByMagasin(session, getCurrentMagasin(), pageSize, pagination);
    }

    @Override
    public Stock getStock(Article article) {
        return StockUtils.getByArticle(session, article);
    }

    @Override
    public Stock getStockFromOtherMagasin(Article article, Magasin magasin) throws RemoteException {
        return serveurCentral.getStockFromMagasin(article, magasin);
    }

    @Override
    public int getStocksNumberPages(int pageSize) {
        return StockUtils.getByMagasinPageNumbers(session, getCurrentMagasin(), pageSize);
    }

    @Override
    public List<Facture> getFacturesByPages(int pageSize, int pagination){
        return FactureUtils.getByPage(session, pageSize, pagination);
    }

    @Override
    public int getFactureNumberPages(int pageSize) {
        return FactureUtils.getByPageNumbers(session, pageSize);
    }

    @Override
    public List<Facture> getAllFactures() {
        return FactureUtils.getAll(session);
    }

    @Override
    public HashMap<Magasin, Stock> getStockOtherMagasins(Article article) {
        try {
            return serveurCentral.getStockFromEveryMagasin(article);
        } catch (RemoteException e) {
            logger.error("Erreur lors de la tentative de récupération des stocks magasins.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Client createOrUpdateClient(Client client) {
        return ClientUtils.addOrUpdate(session, client);
    }

    @Override
    public List<Client> searchClient(String nom, String prenom) {
        return ClientUtils.searchByNomPrenom(session, nom, prenom);
    }

    @Override
    public Facture clientOrder(Client client, Map<Article, Integer> achats) throws Exception {
        Facture facture = FactureUtils.command(session, getCurrentMagasin(), client, achats);
        List<Achat> achatsListe = AchatUtils.getByFacture(session, facture);

        // Création du fichier de facture et mise à jour en bdd
        FactureMakerGenerator.generatePdf(facture, achatsListe);
        FactureUtils.update(session, facture);
        return facture;
    }

    @Override
    public BigDecimal totalFacture(Facture facture){
        return FactureUtils.getTotal(session, facture);
    }

    @Override
    public List<Achat> achatsFacture(Facture facture) {
        return AchatUtils.getByFacture(session, facture);
    }

    @Override
    public Facture payeFacture(Facture facture, MoyenPayement moyenPayement) {
        facture = FactureUtils.get(session, facture.getId());
        moyenPayement = MoyenPayementUtils.get(session, moyenPayement.getId());
        List<Achat> listeAchats = AchatUtils.getByFacture(session, facture);

        session.beginTransaction();
        try {
            facture.setMoyenPaye(moyenPayement);
            facture.setDatePaye(new Date());
            // Génération fichier de facture
            FactureMakerGenerator.generatePdf(facture, listeAchats);
            session.update(facture);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            try {
                throw e;
            } catch (FileNotFoundException | DocumentException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        return facture;
    }

    @Override
    public List<MoyenPayement> getMoyensPayement() {
        return MoyenPayementUtils.getAll(session);
    }

    @Override
    public List<Facture> getClientFactures(Client client) {
        return FactureUtils.getByClient(session, client);
    }

    @Override
    public int respondToInternalOrder(Article article, int quantite) throws Exception {
        logger.info("Demande de " + quantite + " " + article.getNom() + " reçue");
        Stock stock = StockUtils.getByArticle(session, article);
        int stockSent;
        // On vérifie que l'on peut envoyer la quantité demandée
        if(stock.getStock() - quantite >= 0){
            // Mise à jour de l'entité
            stock.setStock(stock.getStock() - quantite);
            stockSent = quantite;
            // Mise à jour en BDD
            StockUtils.update(session, stock);
            logger.info("Envoi de " + quantite + " " + article.getNom() + "...");
        }
        else
            throw new Exception("Quantité insuffisante pour envoi de : " + quantite + " " + article.getReference() +
                    " Présence de " + stock.getStock() + " en stock.");
        return stockSent;
    }

    @Override
    public int requestInternalOrder(Article article, int quantite, Magasin targetMagasin) throws Exception {
        logger.info("Emission d'une requête de stock au magasin " + magasin + " de " + quantite + " " + article.getNom());
        if(targetMagasin.getId() != magasin.getId()){
            int stock = serveurCentral.internalOrderToMagasin(article, quantite, targetMagasin);
            addStockArticle(article, stock);
            logger.info("Commande réussie !");
            return stock;
        }
        else throw new Exception("Impossible de commander un article dans son propre magasin.");
    }

    @Override
    public List<Article> getArticleByFamille(Famille famille) {
        return ArticleUtils.getByFamille(session, famille);
    }

    @Override
    public BigDecimal getChiffreAffaire(LocalDate date) {
        BigDecimal total = new BigDecimal(0);
        for(Facture facture : FactureUtils.getFacturesByDate(session, date)){
            BigDecimal value = FactureUtils.getTotal(session, facture);
            total = total.add(value);
        }
        return total;
    }

    @Override
    public void addStockArticle(Article article, int quantite) {
        Stock stock = StockUtils.getByArticle(session, article);
        stock.setStock(stock.getStock() + quantite);
        StockUtils.update(session, stock);
        logger.info("Ajout de " + quantite + " " + article.getNom());
    }

    @Override
    public void setStockArticle(Article article, int quantite) {
        Stock stock = StockUtils.getByArticle(session, article);
        stock.setStock(quantite);
        StockUtils.update(session, stock);
        logger.info("Stock de " + article.getNom() + " mis à jour: " + quantite + " en stock.");
    }

    @Override
    public byte[] downloadFactureFromServer(String fileName) throws FileNotFoundException {
        logger.info("Transmission de la facture (fichier) " + fileName);
        byte [] mydata;
        File file = new File(Config.REPERTOIRE_FACTURE + File.separator + fileName);
        if(!file.exists()){
            throw new FileNotFoundException("Impossible de trouver la facture:" + Config.REPERTOIRE_FACTURE + File.separator + fileName);
        }
        mydata=new byte[(int) file.length()];
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            in.read(mydata, 0, mydata.length);
            in.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return mydata;
    }

    @Override
    public void updateReferences(List<Article> articles, List<Famille> familles) {
        logger.info("Début de mise à jour des références du serveur ...");
        for(Famille famille : familles){
            FamilleUtils.addOrUpdate(session, famille);
        }

        for(Article article : articles){
            ArticleUtils.addOrUpdate(session, article);
        }
        logger.info("Fin de mise à jour des références du serveur.");
    }

    @Override
    public List<Facture> searchFacture(String query) {
        return FactureUtils.findBySearch(session, query);
    }

    @Override
    public List<Stock> searchStock(String search, int pageSize, int pagination) throws RemoteException {
        return StockUtils.findBySearchPagination(session, search, pageSize, pagination);
    }

    @Override
    public int getSearchStockNumberPages(String search, int pageSize) throws RemoteException {
        return StockUtils.findBySearchNbPage(session, search, pageSize);
    }

    @Override
    public List<Facture> searchFacture(String search, int pageSize, int pagination) throws RemoteException {
        return FactureUtils.findBySearchPagination(session, search, pageSize, pagination);
    }

    @Override
    public int getSearchFactureNumberPages(String search, int pageSize) throws RemoteException {
        return FactureUtils.findBySearchNbPage(session, search, pageSize);
    }
}
