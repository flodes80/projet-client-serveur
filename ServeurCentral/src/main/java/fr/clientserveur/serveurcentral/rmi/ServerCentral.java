package fr.clientserveur.serveurcentral.rmi;

import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.ormentities.ArticleUtils;
import fr.clientserveur.common.entities.ormentities.FamilleUtils;
import fr.clientserveur.common.entities.ormentities.MagasinUtils;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import fr.clientserveur.common.utils.Utils;
import fr.clientserveur.serveurcentral.rmi.implementations.ImplementServeurCentral;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurCentral;
import fr.clientserveur.serveurcentral.utils.Config;
import fr.clientserveur.serveurcentral.utils.Fixtures;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerCentral {

    private final Session session;
    private final Logger logger;
    private static InterfaceServeurCentral localInterface;
    private static InterfaceServeurCentral remoteInterface;
    private static Registry localRegistry;
    private static Registry remoteRegistry;

    public ServerCentral(Session session, Logger logger) {
        this.session = session;
        this.logger = logger;
        scheduleTasks();
    }

    public void start() throws RemoteException {
        // Création registre pour utilisation locale
        localRegistry = LocateRegistry.createRegistry(Config.RMI_LOCAL_PORT);
        localInterface = new ImplementServeurCentral(0, session, logger);
        localRegistry.rebind("ServeurCentralLocal", localInterface);

        // Création registre pour utilisation distante
        System.setProperty("java.rmi.server.hostname", Utils.getPublicIP());

        remoteRegistry = LocateRegistry.createRegistry(Config.RMI_REMOTE_PORT);
        remoteInterface = new ImplementServeurCentral(Config.RMI_REMOTE_PORT, session, logger);
        remoteRegistry.rebind("ServeurCentralRemote", remoteInterface);
    }

    /**
     * Méthode qui va permettre de programmer certaines tâches
     * pour une exécution plus tard
     */
    private void scheduleTasks() {
        // Création d'une tâche pour envoyer tous les soirs les factures au serveur central
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                updateAllMagasins();
            }
        };
        Timer timer = new Timer("updateAllMagasins");

        // Définition du délai
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Config.HEURE_MAJ_MAGASINS);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // On vérifie que le délai n'est pas dépassé sinon on met au prochain jour
        if(Calendar.getInstance().after(calendar)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Date firstTime = calendar.getTime();

        long period = 86400000L; // 24h en ms

        timer.scheduleAtFixedRate(repeatedTask, firstTime, period);
    }

    /**
     * Méthode de mise à jour de tous les magasins chaque matins
     */
    private void updateAllMagasins(){
        logger.info("Envoi des références du jour aux magasins ...");

        for(Magasin magasin : MagasinUtils.getAll(session)){
            try {
                remoteInterface.updateMagasinReference(magasin);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        logger.info("Fin de l'envoi des références du jour aux magasins ...");
    }

    /**
     * Méthode permettant de lancer le chargement des fixtures pour le magasin
     */
    public void loadFixtures(){
        logger.info("Chargement des fixtures ...");
        Fixtures.loadFixtures(session);
        logger.info("Chargement des fixtures terminé !");
    }
}
