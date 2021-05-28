package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class FamilleUtils {

    /**
     * @param session Session hibernate
     * @param id ID de l'instance
     * @return Instance associée
     */
    public static Famille get(Session session, int id) {
        String sql = "SELECT d FROM " + Famille.class.getName() + " d WHERE d.id = :id";
        Query<Famille> query = session.createQuery(sql);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public static Famille getByNom(Session session, String nom) {
        String sql = "SELECT d FROM " + Famille.class.getName() + " d WHERE d.nom = :nom";
        Query<Famille> query = session.createQuery(sql);
        query.setParameter("nom", nom);
        return query.getSingleResult();
    }

    /**
     * @param session Session hibernate
     * @return List de toutes les instances
     */
    public static List<Famille> getAll(Session session) {
        String sql = "SELECT d FROM " + Famille.class.getName() + " d";
        Query<Famille> query = session.createQuery(sql);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param famille Instance à insérer
     * @return Instance crée
     */
    public static Famille add(Session session, Famille famille) {
        session.beginTransaction();
        try {
            session.save(famille);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return famille;
    }

    /**
     * @param session Session hibernate
     * @param famille Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Famille update(Session session, Famille famille) {
        session.beginTransaction();
        try {
            session.update(famille);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return famille;
    }

    /**
     * @param session Session hibernate
     * @param famille Instance à supprimer
     * @return Instance supprimée
     */
    public static Famille delete(Session session, Famille famille) {
        session.beginTransaction();
        try {
            session.delete(famille);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return famille;
    }

    /**
     * Méthode supprimant toutes les données dans la table
     * @param session Session hibernate
     */
    public static void removeAll(Session session){
        session.beginTransaction();
        session.createSQLQuery("delete from famille").executeUpdate();
        session.getTransaction().commit();
    }

    /**
     * Fonction permettant d'ajouter ou de mettre à jour un article dans le magasin
     * @param session Session hibernate
     * @param famille Instance à ajouter ou mettre à jour
     * @return Magasin ajouté ou mis à jour
     */
    public static Famille addOrUpdate(Session session, Famille famille){
        // Tentative de récupération d'un article existant avec l'id de l'article
        Famille familleToUpdate = get(session, famille.getId());

        // Si aucune donnée on ajoute
        if(familleToUpdate == null){
            session.beginTransaction();
            try {
                session.replicate(famille, ReplicationMode.EXCEPTION);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
            return famille;
        }
        // Sinon mise à jour
        else{
            familleToUpdate.setId(famille.getId());
            familleToUpdate.setNom(famille.getNom());
            update(session, familleToUpdate);
            return familleToUpdate;
        }
    }

}
