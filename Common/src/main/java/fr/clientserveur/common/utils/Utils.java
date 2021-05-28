package fr.clientserveur.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class Utils {

    /**
     * Méthode permettant d'obtenir sa propre ip publique
     * @return Notre ip publique
     */
    public static String getPublicIP(){
        BufferedReader in;
        String myIP = "";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            myIP = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myIP;
    }

    /**
     * Fonction permettant de savoir si une ip est publique ou non
     * @return vrai si ip publique ou faux si ip non publique
     */
    public static boolean isPublicIp(String ip){
        if(ip.startsWith("192.168"))
            return false;
        else if(ip.startsWith("172.50"))
            return false;
        else
            return true;
    }

    /**
     * Méthode permettant d'obtenir sa propre ip locale
     * @return Notre ip locale
     */
    public static String getLocalIP(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
