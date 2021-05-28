package fr.clientserveur.common.rmi.interfaces;

import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.Stock;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface InterfaceServeurCentral extends Remote {

    /**
     * Envoi de fichier au serveur
     * @param fileName Nom du fichier
     * @param fileBytes Bytes du fichier
     * @param length Longueur du fichier
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    void uploadFileToServer(String fileName, byte[] fileBytes, int length) throws RemoteException;

    /**
     * Méthode permettant la connexion d'un serveur magasin au serveur central
     * @param rmiRemotePort Port RMI distant utilisé par ce serveur magasin
     * @param nom Nom du serveur Magasin
     * @param adresse1 Adresse 1 du magasin
     * @param adresse2 Adresse 2 du magasin
     * @return L'id attribué au serveur magasin par le serveur central. -1 si connexion refusée.
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int connectToServeurCentral(int rmiRemotePort, String nom, String adresse1, String adresse2) throws RemoteException;

    /**
     * Fonction permettant d'obtenir les stocks d'un article dans tous les magasins connectés au serveur
     * @param article Article recherché
     * @return Une hashmap de stocks disponibles
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    HashMap<Magasin, Stock> getStockFromEveryMagasin(Article article) throws RemoteException;

    /**
     * Fonction permettant d'obtenir le stock d'un article d'un magasin
     * @param article Article recherché
     * @param magasin Magasin recherché
     * @return Stock du magasin
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Stock getStockFromMagasin(Article article, Magasin magasin) throws RemoteException;

    /**
     * Fonction permettant de passer une commande interne par le serveur central à un autre magasin
     * @param article L'article demandé
     * @param quantite La quantité demandée
     * @return La quantité émise par le magasin
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    int internalOrderToMagasin(Article article, int quantite, Magasin magasin) throws Exception;

    /**
     * Fonction retournant la liste des magasins disponibles
     * @return Une lite de tous les magasins connus par le serveur central
     */
    List<Magasin> getAllMagasins() throws RemoteException;

    /**
     * Fonction utilisée pour les tests (unitaires)
     * @param nb1 Premier nombre de l'opérande
     * @param nb2 Deuxième nombre de l'opérande
     * @return nb1 + nb2
     * @throws RemoteException Erreur lors de la transmission de la requête rmi
     */
    Integer add(Integer nb1, Integer nb2) throws RemoteException;

    /**
     * Fonction utilisée en interne pour mettre à jour les références d'un magasin
     * Non mise à disposition en RMI car utilisation par serveur central uniquement
     * @param magasin Magasin magaisn
     */
    void updateMagasinReference(Magasin magasin) throws RemoteException;
}
