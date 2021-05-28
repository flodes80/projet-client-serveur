package fr.clientserveur.common.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "stock")
public class Stock implements Serializable {
    private Magasin magasin;
    private Article article;
    private int stock;

    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    public Magasin getMagasin() {
        return magasin;
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @Column(nullable = false)
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
