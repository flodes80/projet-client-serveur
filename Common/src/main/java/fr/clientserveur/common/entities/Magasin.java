package fr.clientserveur.common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "magasin")
public class Magasin implements Serializable {
    private int id;
    private String nom;
    private String ip;
    private int rmiPort;
    private String adresse1;
    private String adresse2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Column(unique = true)
    public String getIp(){
        return ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public int getRmiPort(){
        return rmiPort;
    }

    public void setRmiPort(int rmiPort){
        this.rmiPort = rmiPort;
    }

    @Column(nullable = false)
    public String getAdresse1() {
        return adresse1;
    }

    public void setAdresse1(String adresse1) {
        this.adresse1 = adresse1;
    }

    @Column(nullable = false)
    public String getAdresse2() {
        return adresse2;
    }

    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }

    @Override
    public String toString() {
        return nom;
    }
}
