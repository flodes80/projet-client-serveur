package fr.clientserveur.client.utils;

import fr.clientserveur.common.entities.Facture;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;

public class Utils {

    /**
     * Méthode lançant une tâche de téléchargement d'une facture
     * @param facture Facture à télécharger
     * @param serveurMagasin interface du serveur magasin
     */
    public static void downloadFactureTask(Facture facture, InterfaceServeurMagasin serveurMagasin){
        // Connexion au serveur magasin
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws IOException, NotBoundException {
                downloadFacture(facture, serveurMagasin);
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Méthode lançant le téléchargement d'une facture
     * @param facture Facture à télécharger
     * @param serveurMagasin interface du serveur magasin
     */
    public static void downloadFacture(Facture facture, InterfaceServeurMagasin serveurMagasin) throws IOException {
        if(facture == null || facture.getNomFichier() == null){
            throw new NullPointerException("La facture est nulle ou le nom du fichier est nul");
        }
        String fileName = facture.getNomFichier();
        byte[] data = serveurMagasin.downloadFactureFromServer(fileName);
        File file = new File(fileName);
        FileOutputStream out = new FileOutputStream(file);

        // Ecriture dans le flux
        out.write(data);
        // "Flush" du flux
        out.flush();
        // Fermeture du flux
        out.close();
    }
}
