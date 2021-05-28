package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Facture;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AchatUtils {

    /**
     * @param session Session hibernate
     * @param facture Instance associée
     * @param article Instance associée
     * @return Instance associée
     */
    public static Achat get(Session session, Facture facture, Article article) {
        String sql = "SELECT d FROM " + Achat.class.getName()
                + " d WHERE d.facture = :facture AND d.article = :article";
        Query<Achat> query = session.createQuery(sql);
        query.setParameter("facture", facture);
        query.setParameter("article", article);
        return query.getSingleResult();
    }

    /**
     * @param session Session hibernate
     * @param facture Instance associée
     * @return List des instances
     */
    public static List<Achat> getByFacture(Session session, Facture facture) {
        String sql = "SELECT d FROM " + Achat.class.getName() + " d WHERE d.facture = :facture";
        Query<Achat> query = session.createQuery(sql);
        query.setParameter("facture", facture);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param achat Instance à insérer
     * @return Instance crée
     */
    public static Achat add(Session session, Achat achat) {
        session.beginTransaction();
        try {
            session.save(achat);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return achat;
    }

    /**
     * @param session Session hibernate
     * @param achat Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Achat update(Session session, Achat achat) {
        session.beginTransaction();
        try {
            session.update(achat);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return achat;
    }

    /**
     * @param session Session hibernate
     * @param achat Instance à supprimer
     * @return Instance supprimée
     */
    public static Achat delete(Session session, Achat achat) {
        session.beginTransaction();
        try {
            session.delete(achat);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return achat;
    }

}
