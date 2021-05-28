package fr.clientserveur.serveurcentral.rmi.implementations;

import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.Stock;
import fr.clientserveur.common.entities.ormentities.ArticleUtils;
import fr.clientserveur.common.entities.ormentities.FamilleUtils;
import fr.clientserveur.common.entities.ormentities.MagasinUtils;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurCentral;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import fr.clientserveur.serveurcentral.utils.Config;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static fr.clientserveur.serveurcentral.utils.Utils.getServeurMagasinInterface;

public class ImplementServeurCentral extends UnicastRemoteObject implements InterfaceServeurCentral {

    private final Session session;
    private final Logger logger;

    public ImplementServeurCentral(int port, Session session, Logger logger) throws RemoteException{
        super(port);
        this.session = session;
        this.logger = logger;
    }

    @Override
    public void uploadFileToServer(String fileName, byte[] fileBytes, int length) {
        logger.info("Réception d'un fichier en cours ...");
        try {
            // Récupération du répertoire courrant
            String filePath = Config.REPERTOIRE_FACTURE + File.separator + fileName;
            File serverFile = new File(filePath);
            FileOutputStream out = new FileOutputStream(serverFile);
            byte[] data = fileBytes;

            // Ecriture dans le flux
            out.write(data);
            // "Flush" du flux
            out.flush();
            // Fermeture du flux
            out.close();

        } catch (IOException e) {
            logger.error("Erreur lors de la réception du fichier: ");
            e.printStackTrace();
        }

        logger.info("Fichier receptionné dans le répertoire: " + Config.REPERTOIRE_FACTURE + "/" + fileName);
    }

    @Override
    public int connectToServeurCentral(int rmiRemotePort, String nom, String adresse1, String adresse2) {
        String clientIp = "";
        try {
            clientIp = RemoteServer.getClientHost(); // Récupération de l'ip du client
            logger.info("Tentative de connexion d'un serveur magasin avec l'ip: " + clientIp);

            // On vérifie si l'ip est dans la liste des ips autorisés de la config
            if(!Config.IP_FILTER || Arrays.stream(Config.MAGASIN_SERVERS_IPS).anyMatch(clientIp::equalsIgnoreCase)){
                Magasin magasin = MagasinUtils.getByIp(session, clientIp);

                // Si Magasin non trouvé dans bdd on créer un enregistrement
                if(magasin == null){
                    magasin = new Magasin();
                    magasin.setNom(nom);
                    magasin.setIp(clientIp);
                    magasin.setRmiPort(rmiRemotePort);
                    magasin.setAdresse1(adresse1);
                    magasin.setAdresse2(adresse2);
                    MagasinUtils.add(session, magasin);
                    logger.info("Nouveau magasin connecté : " + nom + ". Attribution de l'id :" + magasin.getId());
                }
                else{
                    logger.info("Magasin reconnu, autorisation de connexion.");
                    // Mise à jour des informations du magasin si changement côté magasin dans la config
                    magasin.setRmiPort(rmiRemotePort);
                    magasin.setNom(nom);
                    magasin.setAdresse1(adresse1);
                    magasin.setAdresse2(adresse2);
                    MagasinUtils.update(session, magasin);
                }

                // Programmation task pour envoi des références au serveur magasin*
                scheduleUpdate(magasin);

                // Dans tous les cas on renvoit l'id du magasin
                return magasin.getId();
            }
            else
                logger.error("Magasin non autorisé à se connecter.");
        } catch (ServerNotActiveException e) {
            logger.error("Erreur lors de la connexion d'un serveur magasin au serveur central.");
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public HashMap<Magasin, Stock> getStockFromEveryMagasin(Article article) throws RemoteException {
        HashMap<Magasin, Stock> stocksMagasins = new HashMap<>();

        // Parcours tous les magasins
        for(Magasin magasin : MagasinUtils.getAll(session)){
            // Tentative de connexion au magasin
            InterfaceServeurMagasin serveurMagasin = getServeurMagasinInterface(magasin, logger);

            // Obtention du stock du magasin
            Stock stock = serveurMagasin.getStock(article);

            // Stock trouvé ajout à la hashmap
            if(stock != null)
                stocksMagasins.put(magasin, stock);

        }

        return stocksMagasins;
    }

    @Override
    public Stock getStockFromMagasin(Article article, Magasin magasin) throws RemoteException {
        InterfaceServeurMagasin serveurMagasin = getServeurMagasinInterface(magasin, logger);
        return serveurMagasin.getStock(article);
    }

    @Override
    public int internalOrderToMagasin(Article article, int quantite, Magasin magasin) throws Exception {
        InterfaceServeurMagasin serveurMagasin = getServeurMagasinInterface(magasin, logger);
        return serveurMagasin.respondToInternalOrder(article, quantite);
    }

    @Override
    public List<Magasin> getAllMagasins() {
        return MagasinUtils.getAll(session);
    }

    @Override
    public Integer add(Integer nb1, Integer nb2) {
        return nb1 + nb2;
    }

    /**
     * Méthode permettant de mettre à jour les références d'un magasin lors de sa connexion
     * @param magasin Magasin tentant de se connecter
     */
    @Override
    public void updateMagasinReference(Magasin magasin){
        logger.info("Envoi des références du jour au magasin " + magasin.getNom() );
        List<Article> articles = ArticleUtils.getAll(session);
        List<Famille> famille = FamilleUtils.getAll(session);

        InterfaceServeurMagasin interfaceServeurMagasin = fr.clientserveur.serveurcentral.utils.Utils.getServeurMagasinInterface(magasin, logger);

        if(interfaceServeurMagasin != null){
            try {
                interfaceServeurMagasin.updateReferences(articles, famille);
                logger.info("Mise à jour du magasin: " + magasin.getNom() + " réussie !");
            } catch (RemoteException e) {
                logger.error("Erreur lors de la tentative de mise à jour du magasin.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Programmation d'une tache de mise à jour du magasin quelques temps après sa connexion (10sec)
     * @param magasin Magasin nécessitant une mise à jour
     */
    private void scheduleUpdate(Magasin magasin){
        // Programmation d'une tache dans X secondes
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);

        // Création de la tâche
        TimerTask futureTask = new TimerTask() {
            public void run() {
                updateMagasinReference(magasin);
            }
        };
        Timer timer = new Timer("updateMagasin"+magasin.getNom());
        timer.schedule(futureTask, calendar.getTime());
    }
}
