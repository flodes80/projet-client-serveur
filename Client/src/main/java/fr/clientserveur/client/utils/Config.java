package fr.clientserveur.client.utils;

import fr.clientserveur.common.utils.AbstractConfig;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config extends AbstractConfig {

    /**
     * Instance du Singleton
     */
    private static Config INSTANCE;

    /**
     * Adresse IP du magasin
     */
    public static String SERVER_IP;

    /**
     * Port RMI utilisé par le serveur
     */
    public static int SERVER_PORT;

    /**
     * Constructeur privé pour Singleton
     * Définition du nom du fichier dans le constructeur
     */
    private Config(){
        super("client.properties");
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
            fileConfig.setProperty("SERVER_IP", "localhost");
            fileConfig.setProperty("SERVER_PORT", 10000);

            // Sauvegarde du fichier
            builder.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Chargement des variables depuis le fichier de configuration
     * @param fileConfig Fichier de configuration
     */
    public void loadConfig(FileBasedConfiguration fileConfig) {
        SERVER_IP = fileConfig.getString("SERVER_IP");
        SERVER_PORT = fileConfig.getInt("SERVER_PORT");
    }

}
