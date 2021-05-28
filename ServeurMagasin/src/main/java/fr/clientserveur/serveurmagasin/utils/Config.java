package fr.clientserveur.serveurmagasin.utils;

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
     * Nom du magasin utilisé pour l'entité statique du magasin
     */
    public static String NOM_MAGASIN;

    /**
     * Adresse du magasin utilisée pour l'entité statique du magasin
     */
    public static String ADRESSE1;

    /**
     * Adresse du magasin utilisée pour l'entité statique du magasin
     */
    public static String ADRESSE2;

    /**
     * Adresse IP du serveur central
     */
    public static String SERVER_CENTRAL_IP;

    /**
     * Port utilisé par le serveur central
     */
    public static int SERVER_CENTRAL_PORT;

    /**
     * Port RMI utilisé par le serveur en local pour les magasins
     */
    public static int RMI_LOCAL_PORT;

    /**
     * Port RMI utilisé par le serveur central pour accés à distance
     */
    public static int RMI_REMOTE_PORT;

    /**
     * Répertoire utilisé pour stocker les factures
     */
    public static String REPERTOIRE_FACTURE;

    /**
     * Heure d'envoi des factures
     */
    public static int HEURE_ENVOI_FACTURE;

    /**
     * Constructeur privé pour Singleton
     * Définition du nom du fichier dans le constructeur
     */
    private Config(){
        super("serveur-magasin.properties");
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

            // Ajout des différentes propriétés par défaut
            fileConfig.setProperty("NOM_MAGASIN", "Magasin de Test");
            fileConfig.setProperty("ADRESSE1", "Adresse de Test 1");
            fileConfig.setProperty("ADRESSE2", "Adresse de Test 2");
            fileConfig.setProperty("SERVER_CENTRAL_IP", "dedi.valentincocherel.fr");
            fileConfig.setProperty("SERVER_CENTRAL_PORT", 25000);
            fileConfig.setProperty("RMI_LOCAL_PORT", 10000);
            fileConfig.setProperty("RMI_REMOTE_PORT", 15000);
            fileConfig.setProperty("REPERTOIRE_FACTURE", System.getProperty("user.dir") + File.separator + "factures");
            fileConfig.setProperty("HEURE_ENVOI_FACTURE", 21);

            // Config hibernate
            fileConfig.setProperty("hibernate.connection.url", "jdbc:mysql://dedi.valentincocherel.fr:3306/clientserveur_mag1?serverTimezone=UTC");
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
        NOM_MAGASIN = fileConfig.getString("NOM_MAGASIN");
        ADRESSE1 = fileConfig.getString("ADRESSE1");
        ADRESSE2 = fileConfig.getString("ADRESSE2");
        RMI_LOCAL_PORT = fileConfig.getInt("RMI_LOCAL_PORT");
        RMI_REMOTE_PORT = fileConfig.getInt("RMI_REMOTE_PORT");
        SERVER_CENTRAL_IP = fileConfig.getString("SERVER_CENTRAL_IP");
        SERVER_CENTRAL_PORT = fileConfig.getInt("SERVER_CENTRAL_PORT");
        REPERTOIRE_FACTURE = fileConfig.getString("REPERTOIRE_FACTURE");
        HEURE_ENVOI_FACTURE = fileConfig.getInt("HEURE_ENVOI_FACTURE");
    }

}
