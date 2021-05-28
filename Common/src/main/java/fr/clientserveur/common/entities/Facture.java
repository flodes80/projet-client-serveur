package fr.clientserveur.common.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "facture")
public class Facture implements Serializable {
    private int id;
    private Magasin magasin;
    private Client client;
    private Date date;
    private Date datePaye;
    private MoyenPayement moyenPaye;
    private String nomFichier;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Magasin getMagasin() {
        return magasin;
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDatePaye() {
        return datePaye;
    }

    public void setDatePaye(Date datePaye) {
        this.datePaye = datePaye;
    }

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    public MoyenPayement getMoyenPaye() {
        return moyenPaye;
    }

    public void setMoyenPaye(MoyenPayement moyenPaye) {
        this.moyenPaye = moyenPaye;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }
}
