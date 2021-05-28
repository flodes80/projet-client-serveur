package fr.clientserveur.serveurcentral.utils;

import fr.clientserveur.common.utils.AbstractConfig;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class Config extends AbstractConfig {

    /**
     * Instance du Singleton
     */
    private static Config INSTANCE;

    /**
     * Port RMI utilisé par le serveur en local pour les magasins locaux
     */
    public static int RMI_LOCAL_PORT;

    /**
     * Port RMI utilisé par le serveur à distance
     */
    public static int RMI_REMOTE_PORT;

    /**
     * Liste des IPs des serveurs magasin
     */
    public static String[] MAGASIN_SERVERS_IPS;

    /**
     * Répertoire utilisé pour stocker les factures
     */
    public static String REPERTOIRE_FACTURE;

    /**
     * Heure d'envoi des mises à jour des références magasins
     */
    public static int HEURE_MAJ_MAGASINS;

    /**
     * Booléen permettant de définir si l'on souhaite filtrer les ips se connectant au serveur central
     * Si oui il faut définir les ips avec MAGASIN_SERVERS_IPS
     */
    public static boolean IP_FILTER;

    /**
     * Constructeur privé pour Singleton
     * Définition du nom du fichier dans le constructeur
     */
    private Config(){
        super("serveur-central.properties");
    }

    /**
     * Singleton de la classe config
     * @return config
     */
    public static Config getInstance(){
        if(INSTANCE == null)
            INSTANCE = new Config();
        return INSTANCE;
    }

    /**
     * Création du fichier de configuration
     * @param builder FileBasedConfigurationBuilder pour création du fichier de configuration
     */
    public void createConfig(FileBasedConfigurationBuilder<FileBasedConfiguration> builder){
        try {
            // Création du fichier de configuration
            FileBasedConfiguration fileConfig = builder.getConfiguration();

            // Ajout des différentes propriétés
            fileConfig.setProperty("RMI_LOCAL_PORT", 20000);
            fileConfig.setProperty("RMI_REMOTE_PORT", 25000);
            fileConfig.setProperty("MAGASIN_SERVERS_IPS", new String[] {"127.0.0.1", "192.168.1.2", "192.168.1.3"});
            fileConfig.setProperty("IP_FILTER", false);
            fileConfig.setProperty("REPERTOIRE_FACTURE", System.getProperty("user.dir") + File.separator + "factures");
            fileConfig.setProperty("HEURE_MAJ_MAGASINS", 7);

            // Config hibernate
            fileConfig.setProperty("hibernate.connection.url", "jdbc:mysql://dedi.valentincocherel.fr:3306/clientserveur_centr?serverTimezone=UTC");
            fileConfig.setProperty("hibernate.connection.username", "dev");
            fileConfig.setProperty("hibernate.connection.password", "QVpghb%2h6ktx9dX");

            // Sauvegarde du fichier
            builder.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Chargement des variables depuis le fichier de configuration
     * Le load des infos de hibernate est géré par HibernateUtils
     * @param fileConfig Fichier de configuration
     */
    public void loadConfig(FileBasedConfiguration fileConfig) {
        RMI_LOCAL_PORT = fileConfig.getInt("RMI_LOCAL_PORT");
        RMI_REMOTE_PORT = fileConfig.getInt("RMI_REMOTE_PORT");
        MAGASIN_SERVERS_IPS = fileConfig.getStringArray("MAGASIN_SERVERS_IPS");
        IP_FILTER = fileConfig.getBoolean("IP_FILTER");
        REPERTOIRE_FACTURE = fileConfig.getString("REPERTOIRE_FACTURE");
        HEURE_MAJ_MAGASINS = fileConfig.getInt("HEURE_MAJ_MAGASINS");
    }

}
