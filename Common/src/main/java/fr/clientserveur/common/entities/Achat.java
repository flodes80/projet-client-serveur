package fr.clientserveur.common.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "achat")
public class Achat implements Serializable {
    private Facture facture;
    private Article article;
    private int quantite;
    private BigDecimal prixUnit;

    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @Column(nullable = false)
    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Column(scale=2, nullable = false)
    public BigDecimal getPrixUnit() {
        return prixUnit;
    }

    public void setPrixUnit(BigDecimal prixUnit) {
        this.prixUnit = prixUnit;
    }
}
