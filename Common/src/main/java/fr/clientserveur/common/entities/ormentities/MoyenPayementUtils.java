package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.MoyenPayement;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MoyenPayementUtils {

    /**
     * @param session Session hibernate
     * @param id Reference de l'instance
     * @return Instance associée
     */
    public static MoyenPayement get(Session session, int id) {
        String sql = "SELECT d FROM " + MoyenPayement.class.getName() + " d WHERE d.id = :id";
        Query<MoyenPayement> query = session.createQuery(sql);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    /**
     * @param session Session hibernate
     * @return Liste de tous les moyens de payement
     */
    public static List<MoyenPayement> getAll(Session session) {
        String sql = "SELECT d FROM " + MoyenPayement.class.getName() + " d";
        Query<MoyenPayement> query = session.createQuery(sql);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param moyenPayement Instance à insérer
     * @return Instance crée
     */
    public static MoyenPayement add(Session session, MoyenPayement moyenPayement) {
        session.beginTransaction();
        try {
            session.save(moyenPayement);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return moyenPayement;
    }

    /**
     * @param session Session hibernate
     * @param moyenPayement Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static MoyenPayement update(Session session, MoyenPayement moyenPayement) {
        session.beginTransaction();
        try {
            session.update(moyenPayement);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return moyenPayement;
    }

    /**
     * @param session Session hibernate
     * @param moyenPayement Instance à supprimer
     * @return Instance supprimée
     */
    public static MoyenPayement delete(Session session, MoyenPayement moyenPayement) {
        session.beginTransaction();
        try {
            session.delete(moyenPayement);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return moyenPayement;
    }

    /**
     * Méthode supprimant toutes les données dans la table
     * @param session Session hibernate
     */
    public static void removeAll(Session session){
        session.beginTransaction();
        session.createSQLQuery("delete from moyenpayement").executeUpdate();
        session.getTransaction().commit();
    }
    
}
