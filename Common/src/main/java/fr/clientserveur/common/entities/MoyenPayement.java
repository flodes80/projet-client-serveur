package fr.clientserveur.common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "moyenpayement")
public class MoyenPayement implements Serializable {
    private int id;
    private String nom;

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

    @Override
    public String toString() {
        return nom;
    }
}
