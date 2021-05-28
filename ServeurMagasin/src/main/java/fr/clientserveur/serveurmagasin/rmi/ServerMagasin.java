package fr.clientserveur.serveurmagasin.rmi;

import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.ormentities.MagasinUtils;
import fr.clientserveur.common.utils.Utils;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurCentral;
import fr.clientserveur.serveurmagasin.rmi.implementations.ImplementServeurMagasin;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import fr.clientserveur.serveurmagasin.utils.Config;
import fr.clientserveur.serveurmagasin.utils.Fixtures;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ServerMagasin {

    private static InterfaceServeurCentral serverCentral;

    private final Session session;
    private final Logger logger;
    private final Magasin magasin;
    private static InterfaceServeurMagasin localInterface;
    private static InterfaceServeurMagasin remoteInterface;
    private static Registry localRegistry;
    private static Registry remoteRegistry;
    private static Registry serverCentralRegistry;

    public ServerMagasin(Session session, Logger logger, Magasin magasin){
        this.session = session;
        this.logger = logger;
        this.magasin = magasin;
        scheduleTasks();
    }

    public void start() throws RemoteException {
        // Initialisation entité statique du magasin
        magasin.setNom(Config.NOM_MAGASIN);
        magasin.setAdresse1(Config.ADRESSE1);
        magasin.setAdresse2(Config.ADRESSE2);
        // Si serveur central distant on met l'ip publique + remote port
        if(Utils.isPublicIp(Config.SERVER_CENTRAL_IP)){
            magasin.setIp(Utils.getPublicIP());
            magasin.setRmiPort(Config.RMI_REMOTE_PORT);
        }
        // Sinon ip locale + port local
        else{
            magasin.setIp(Utils.getLocalIP());
            magasin.setRmiPort(Config.RMI_LOCAL_PORT);
        }


        createStubToCentralServer();

        // Création registre pour utilisation locale
        localRegistry = LocateRegistry.createRegistry(Config.RMI_LOCAL_PORT);
        localInterface = new ImplementServeurMagasin(0, session, logger, magasin, serverCentral);
        localRegistry.rebind("ServeurMagasinLocal", localInterface);

        // Création registre pour utilisation distante
        System.setProperty("java.rmi.server.hostname", Utils.getPublicIP());

        remoteRegistry = LocateRegistry.createRegistry(Config.RMI_REMOTE_PORT);
        remoteInterface = new ImplementServeurMagasin(Config.RMI_REMOTE_PORT, session, logger, magasin, serverCentral);
        remoteRegistry.rebind("ServeurMagasinRemote", remoteInterface);

        // Enregistrement des informations du magasin en bdd si aucun problème
        if(magasin.getId() > 0)
            MagasinUtils.addOrUpdate(session, magasin);
    }

    /**
     * Procédure permettant la création des stubs vers le RMI du serveur central
     */
    public void createStubToCentralServer(){
        logger.info("Tentative de connexion au serveur central");
        try {
            serverCentralRegistry = LocateRegistry.getRegistry(Config.SERVER_CENTRAL_IP, Config.SERVER_CENTRAL_PORT);

            // Check si serveur central est dans le réseau local ou non via test même ip publique ou non
            if(!Utils.isPublicIp(Config.SERVER_CENTRAL_IP)){
                logger.info("Connexion mode local au serveur central");
                serverCentral = (InterfaceServeurCentral) serverCentralRegistry.lookup("ServeurCentralLocal");
            }
            else{
                logger.info("Connexion mode distant au serveur central");
                serverCentral = (InterfaceServeurCentral) serverCentralRegistry.lookup("ServeurCentralRemote");
            }


            logger.info("Obtention d'un id...");

            // Tentative d'autorisation de connexion et récupération de l'id attribué par le serveur central
            magasin.setId(serverCentral.connectToServeurCentral(magasin.getRmiPort(), magasin.getNom(), magasin.getAdresse1(), magasin.getAdresse2()));

            // Connexion refusée
            if(magasin.getId() == -1){
                logger.error("Connexion rejetée par le serveur central.");
                System.exit(1);
            }

            logger.info("ID attribué: " + magasin.getId());
            logger.info("Connexion au serveur central réussie !");
        } catch (RemoteException | NotBoundException e) {
            logger.error("Connexion au serveur central impossible:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Procédure permettant l'envoi de toutes les factures au serveur central
     */
    public void sendAllFacturesToCentralServer(){
        logger.info("Envoi des factures du magasin au serveur central");
        File folder = new File(Config.REPERTOIRE_FACTURE);
        File[] listOfFiles = folder.listFiles();

        // Parcours de tous les fichiers présents dans le répertoire de factures
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                // Vérification que c'est un fichier et qu'il a une extension en ".pdf"
                if (file.isFile() && getFileExtension(file.getName()).get().equalsIgnoreCase("pdf")) {
                    byte[] fileBytes = new byte[(int) file.length()];
                    FileInputStream in = null;

                    try {
                        // Génération d'un flux en bytes
                        in = new FileInputStream(file);
                        in.read(fileBytes, 0, fileBytes.length);

                        // Envoi du fichier / flux
                        serverCentral.uploadFileToServer(file.getName(), fileBytes, (int) file.length());

                        // Fermeture du flux
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            logger.info(listOfFiles.length + " fichier(s) envoyé(s) au serveur central !");
        }
    }

    /**
     * Fonction permettant de récupérer l'extension d'un fichier
     * @param filename Nom du fichier
     * @return l'extension du fichier ou chaine de caractère vide si aucune extension
     */
    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     * Méthode qui va permettre de programmer certaines tâches
     * pour une exécution plus tard
     */
    private void scheduleTasks() {
        // Création d'une tâche pour envoyer tous les soirs les factures au serveur central
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                sendAllFacturesToCentralServer();
            }
        };
        Timer timer = new Timer("sendAllFacturesToCentralServer");

        // Définition du délai
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Config.HEURE_ENVOI_FACTURE);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // On vérifie que le délai n'est pas dépassé sinon on met au prochain jour
        if(Calendar.getInstance().after(calendar)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Date firstTime = calendar.getTime();

        long period = 86400000L; // 24h en ms

        timer.scheduleAtFixedRate(repeatedTask, firstTime.getTime(), period);
    }

    /**
     * Méthode permettant de lancer le chargement des fixtures pour le magasin
     */
    public void loadFixtures(){
        logger.info("Chargement des fixtures ...");
        Fixtures.loadFixtures(session, magasin);
        logger.info("Chargement des fixtures terminé !");
    }

}
