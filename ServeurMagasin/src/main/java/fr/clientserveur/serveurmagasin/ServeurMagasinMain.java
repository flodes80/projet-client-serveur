package fr.clientserveur.serveurmagasin;

import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.entities.ormentities.HibernateUtils;
import fr.clientserveur.serveurmagasin.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.clientserveur.serveurmagasin.rmi.ServerMagasin;
import org.hibernate.Session;

import java.io.File;
import java.io.PrintStream;

/**
 * Classe principale de lancement du serveur magasin
 */
public class ServeurMagasinMain {

    private static final Logger logger = LogManager.getRootLogger();
    private static final Config config = Config.getInstance();
    private static Session session;
    private static ServerMagasin server;
    private static final Magasin magasin = new Magasin();

    public static void main(String[] args) {
        // Redirection des flux d'erreurs
        redirectErrorsToLogger();

        logger.info("Démarrage du serveur...");

        // Chargement du fichier de configuration
        config.loadOrCreateConfig();

        // Création du serveur
        server = null;
        try {
            session = HibernateUtils.createHibernateSession(config.getPropertiesFile(), true);    // Création d'une session hibernate pour le serveur
            server = new ServerMagasin(session, logger, magasin);
            server.start();

            // Si le premier argument demande chargement des fixtures
            if(args.length > 0 && args[0].equalsIgnoreCase("fixture")){
                server.loadFixtures();
            }
            createFactureDirIfNotExist();
            hookShutdown();
        } catch (Exception e) {
            logger.error("Erreur lors du démarrage du serveur: " + e.getMessage());
            System.exit(1);
        }

        logger.info("Serveur démarré avec succès.");
    }

    /**
     * Méthode permettant la création du répertoire des factures
     * @throws Exception Erreur lors de la création du répertoire
     */
    private static void createFactureDirIfNotExist() throws Exception {
        // Récupération du répertoire depuis la config
        String path = Config.REPERTOIRE_FACTURE;

        // Création de l'objet
        File file = new File(path);

        // Vérification que le répertoire n'existe pas et sinon création
        // Lance une exception si impossibilité de  créer le répertoire
        if(!file.exists() && !file.mkdir())
            throw new Exception("Impossible de créer le répertoire des factures");
    }

    /**
     * Redirection de la sortie d'erreur vers le fichier de log
     */
    private static void redirectErrorsToLogger(){
        // Sortie par défaut des erreurs
        PrintStream sysErr = System.err;

        // Modification de son comportement
        PrintStream newSysErr = new PrintStream(sysErr){
            public void print(final String string) {
                sysErr.print(string);
                logger.error(string);
            }
        };

        System.setErr(newSysErr);
    }

    /**
     * Actions à faire à l'arrêt du serveur
     */
    private static void hookShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                session.close();
                logger.info("Arrêt du serveur ...");
            }
        });
    }
}
