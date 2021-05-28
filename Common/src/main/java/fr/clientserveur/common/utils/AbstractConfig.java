package fr.clientserveur.common.utils;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Classe abstraite d'une config
 */
public abstract class AbstractConfig {

    /**
     * Nom du fichier de configuration
     */
    protected String fileName;

    /**
     * Constructeur d'une config
     * @param fileName nom du fichier
     */
    protected AbstractConfig(String fileName){
        this.fileName = fileName;
    }

    /**
     * Procédure de création / chargement du fichier de configuration
     */
    public void loadOrCreateConfig() {
        // Initialisation fichier config
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(fileName));

        if(!configExist())
            createConfig(builder);

        try {
            // Tentative de chargement du fichier
            FileBasedConfiguration fileConfig = builder.getConfiguration();

            // Chargement des variables
            loadConfig(fileConfig);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si le fichier de configuration existe et le créer sinon
     * @return Existe ou pas
     */
    private boolean configExist() {
        // Création du fichier
        File file = new File(fileName);
        try {
            return !file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Fonction permettant d'obtenir l'objet Properties à partir du fichier de config
     * Utilisé pour la config d'hibernate
     * @return objet Properties
     */
    public Properties getPropertiesFile(){
        Properties propertiesFile = new Properties();
        try {
            InputStream is = Files.newInputStream(Paths.get(fileName));
            propertiesFile.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propertiesFile;
    }

    /**
     * Méthode de création de la configuration à définir
     * @param builder FileBasedConfigurationBuilder pour création du fichier de configuration
     */
    public abstract void createConfig(FileBasedConfigurationBuilder<FileBasedConfiguration> builder);

    /**
     * Méthode de chargement de la configuraiton à définir
     * @param fileConfig Fichier de configuration
     */
    public abstract void loadConfig(FileBasedConfiguration fileConfig);
}
