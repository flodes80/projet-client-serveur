package fr.clientserveur.common.rmi.interfaces;

import com.itextpdf.text.DocumentException;
import fr.clientserveur.common.entities.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InterfaceServeurMagasin extends Remote {

    /**
     * Fonction permettant de générer une facture à partir d'une liste d'achats et d'une facture (entités)
     * @param facture Entité facture
     * @param achats Liste d'achats effectués
     * @return Le chemin vers le fichier généré
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     * @throws FileNotFoundException Erreur lors de la génération du fichier
     * @throws DocumentException Erreur lors de la génération du fichier
     */
    String generateFacture(Facture facture, List<Achat> achats) throws RemoteException, FileNotFoundException, DocumentException;

    /**
     * Fonction retournant l'entité du magasin actuel
     * @return Entité du magasin du serveur magasin
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Magasin getCurrentMagasin() throws RemoteException;

    /**
     * Fonction permettant d'obtenir la liste des magasins disponibles depuis le serveur central
     * @return La liste des magasins dont le serveur central a connaissance
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Magasin> getAllMagasins() throws RemoteException;

    /**
     * Fonction permettant d'obtenir la les stocks par pagination
     * @param pageSize Taille de la page demandée
     * @param pagination Numéro de page
     * @return Liste de stock selon la pagination
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Stock> getStocks(int pageSize, int pagination) throws RemoteException;

    /**
     * Fonction retournant le nombre de page de stocks
     * @param pageSize Taille des pages
     * @return Nombre de pages maximales
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int getStocksNumberPages(int pageSize) throws RemoteException;

    /**
     * Fonction permettant d'obtenir les factures par pagination
     * @param pageSize Taille des pages
     * @param pagination Numéro de la page
     * @return Liste de stock selon la pagination
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Facture> getFacturesByPages(int pageSize, int pagination) throws RemoteException;

    /**
     * Fonction retournant le nombre de page de factures
     * @param pageSize Taille des pages
     * @return Nombre de pages maximales
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int getFactureNumberPages(int pageSize) throws RemoteException;

    /**
     * Fonction permettant d'obtenir toutes les factures existantes
     * @return Liste de toutes les factures
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Facture> getAllFactures() throws RemoteException;

    /**
     * Fonction permettant d'obtenir le stock d'un article auprès d'un autre magasin
     * @param article Article recherché
     * @return Une hashmap associant le stock de chaque magasin
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    HashMap<Magasin, Stock> getStockOtherMagasins(Article article) throws RemoteException;

    /**
     * Fonction permettant d'obtenir le stock d'un article dans le magasin actuel
     * @param article Article recherché
     * @return Stock de l'article
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Stock getStock(Article article) throws RemoteException;

    /**
     * Fonction permettant d'obtenir le stock d'un article dans un autre magasin
     * @param article Article recherché
     * @param magasin Magasin de recherche
     * @return Stock de l'article
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Stock getStockFromOtherMagasin(Article article, Magasin magasin) throws RemoteException;

    /**
     * Fonction permettant la création d'un nouveau client
     * @param client Entité client à créer
     * @return Client créé
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Client createOrUpdateClient(Client client) throws RemoteException;

    /**
     * Fonction permettant de rechercher le client à partir de son nom / prénom
     * @param nom Nom du client recherché
     * @param prenom Prénom du client recherché
     * @return Une liste de client correspondant aux critères de recherches
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Client> searchClient(String nom, String prenom) throws RemoteException;

    /**
     * Fonction permettant de placer une commande pour un client
     * @param client Client plaçant la commande
     * @param achats Liste des achats faits
     * @return La facture générée pour cette commande
     * @throws Exception Erreur lors de la transmission de la requête rmi
     */
    Facture clientOrder(Client client, Map<Article, Integer> achats) throws Exception;

    /**
     * Fonction retournant le montant total d'une facture
     * @param facture Facture recherchée
     * @return Montant total en decimales
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    BigDecimal totalFacture(Facture facture) throws RemoteException;

    /**
     * Fonction permettant d'obtenir la liste des achats faits à partir d'une facture
     * @param facture La facture recherchée
     * @return Liste d'achats
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Achat> achatsFacture(Facture facture) throws RemoteException;

    /**
     * Fonction permettant de payer une facture
     * @param facture Facture à payer
     * @param moyenPayement Moyen de paiement
     * @return La facture payée
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Facture payeFacture(Facture facture, MoyenPayement moyenPayement) throws RemoteException;

    /**
     * Fonction retournant la liste des moyens de paiement disponibles
     * @return Liste des moyens de paiement disponibles dans le magasin
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<MoyenPayement> getMoyensPayement() throws RemoteException;

    /**
     * Fonction retournant la liste des factures d'un client
     * @param client Client recherché
     * @return Liste des factures d'un client
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Facture> getClientFactures(Client client) throws RemoteException;

    /**
     * Fonction permettant de répondre à une commande interne d'un autre magasin
     * @param article Article demandé
     * @param quantite Quantité demandée
     * @return La quantité émise
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int respondToInternalOrder(Article article, int quantite) throws Exception;

    /**
     * Fonction permettant d'émettre une commande interne à un magasin
     * @param article Article demandé
     * @param quantite Quantité demandée
     * @param magasin Magasin cible
     * @return Quantité reçue
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int requestInternalOrder(Article article, int quantite, Magasin magasin) throws Exception;

    /**
     * Fonction permettant de retourner tous les articles d'une même famille
     * @param famille Famille désirée
     * @return Liste d'articles
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Article> getArticleByFamille(Famille famille) throws RemoteException;

    /**
     * Fonction permettant d'obtenir le chiffre d'affaire du jour
     * @param date Date de calcul du chiffre d'affaire
     * @return Chiffre d'affaire en BigDecimal
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    BigDecimal getChiffreAffaire(LocalDate date) throws RemoteException;

    /**
     * Méthode permettant d'ajouter du stock à un article du magasin
     * @param article Article concerné
     * @param quantite Quantité à ajouter
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    void addStockArticle(Article article, int quantite) throws RemoteException;

    /**
     * Méthode permettant de définir le stock d'un article manuellement
     * @param article Article désiré
     * @param quantite Quantité désirée
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    void setStockArticle(Article article, int quantite) throws RemoteException;

    /**
     * Fonction de téléchargement d'un fichier depuis le serveur
     * @param fileName Nom du fichier recherché
     * @return un tableau de byte représentant le fichier
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    byte[] downloadFactureFromServer(String fileName) throws RemoteException, FileNotFoundException;

    /**
     * Fonction permettant de mettre à jour les références du magasin tous les matins par le serveur central
     * @param articles Liste des articles
     * @param familles Liste des familles
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    void updateReferences(List<Article> articles, List<Famille> familles) throws RemoteException;

    /**
     * Fonction permettant de faire une recherche de facture dans le magasin
     * @param query String recherché
     * @return Liste de factures
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Facture> searchFacture(String query) throws RemoteException;

    /**
     * Fonction permettant d'obtenir la les stocks par pagination et par recherche
     * @param search Critère de recherche
     * @param pageSize Taille de la page demandée
     * @param pagination Numéro de page
     * @return Liste de stock selon la pagination
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Stock> searchStock(String search, int pageSize, int pagination) throws RemoteException;

    /**
     * Fonction retournant le nombre de page de stocks
     * @param search Critère de recherche
     * @param pageSize Taille des pages
     * @return Nombre de pages maximales
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int getSearchStockNumberPages(String search, int pageSize) throws RemoteException;

    /**
     * Fonction permettant d'obtenir les factures par pagination et par recherche
     * @param search Critère de recherche
     * @param pageSize Taille de la page demandée
     * @param pagination Numéro de page
     * @return Liste de stock selon la pagination
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    List<Facture> searchFacture(String search, int pageSize, int pagination) throws RemoteException;

    /**
     * Fonction retournant le nombre de page de factures
     * @param search Critère de recherche
     * @param pageSize Taille des pages
     * @return Nombre de pages maximales
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int getSearchFactureNumberPages(String search, int pageSize) throws RemoteException;
}
