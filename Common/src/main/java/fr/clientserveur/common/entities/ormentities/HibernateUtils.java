package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtils {

    /**
     * Fichier de configuration hibernate de tests unitaires
     */
    private static final String DEVENV = "hibernate_test.cfg.xml";

    /**
     * Création d'une session hibernate de dev pour tests unitaires
     * @return Session
     */
    public static Session getHibernateDevSession(){
        try {
            final SessionFactory sf = new Configuration().configure(DEVENV).buildSessionFactory();
            return sf.openSession();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Création d'une session à partir d'un fichier de config du type customProperties (.properties)
     * @param customProperties Chemin vers le fichier de configuration (.properties)
     * @param serveurMagasin Booléen indiquant si il s'agit d'un serveur magasin ou non
     * @return Session hibernate
     */
    public static Session createHibernateSession(Properties customProperties, boolean serveurMagasin) {
        Session hibernateSession = null;
        try {
            Configuration configuration = new Configuration();
            // Configuration du serveur distant pouvant êtres modifiées
            configuration.setProperties(customProperties);

            // Ajout de la configuration par défaut
            Properties defaultProperties = new Properties();
            defaultProperties.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
            defaultProperties.setProperty("hibernate.connection.pool_size", "5");
            defaultProperties.setProperty("hibernate.connection.autoReconnect", "true");
            defaultProperties.setProperty("hibernate.connection.autoReconnectForPools", "true");
            defaultProperties.setProperty("hibernate.connection.is-connection-validation-required", "true");
            defaultProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
            defaultProperties.setProperty("hibernate.current_session_context_class", "thread");
            defaultProperties.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.addProperties(defaultProperties);

            // Mapping des classes
            if(serveurMagasin){
                configuration.addAnnotatedClass(Achat.class);
                configuration.addAnnotatedClass(Client.class);
                configuration.addAnnotatedClass(Facture.class);
                configuration.addAnnotatedClass(MoyenPayement.class);
                configuration.addAnnotatedClass(Stock.class);
            }
            configuration.addAnnotatedClass(Magasin.class);
            configuration.addAnnotatedClass(Article.class);
            configuration.addAnnotatedClass(Famille.class);

            hibernateSession = configuration.buildSessionFactory().openSession();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return hibernateSession;
    }

}
