package fr.clientserveur.serveurcentral.utils;

import fr.clientserveur.common.entities.Magasin;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import org.apache.logging.log4j.Logger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Utils {

    /**
     * Fonction permettant d'obtenir l'interface à un magasin spécifique
     * afin de pouvoir intéragir avec celui-ci
     * @param magasin Magasin avec lequel on souhaite communiquer
     * @param logger Logger de l'application en cas de problème
     * @return L'interface du magasin ou null si problème
     */
    public static InterfaceServeurMagasin getServeurMagasinInterface(Magasin magasin, Logger logger){
        InterfaceServeurMagasin serveurMagasin = null;
        try {
            if(fr.clientserveur.common.utils.Utils.isPublicIp(magasin.getIp())){
                Registry serverMagasinRegistry = LocateRegistry.getRegistry(magasin.getIp(), magasin.getRmiPort());
                serveurMagasin = (InterfaceServeurMagasin) serverMagasinRegistry.lookup("ServeurMagasinRemote");
            }
            else{
                Registry serverMagasinRegistry = LocateRegistry.getRegistry(magasin.getIp(), magasin.getRmiPort());
                serveurMagasin = (InterfaceServeurMagasin) serverMagasinRegistry.lookup("ServeurMagasinLocal");
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            logger.warn("Impossible de joindre le magasin: " + magasin.getNom() + " " + magasin.getIp());
        }
        return serveurMagasin;
    }

}
