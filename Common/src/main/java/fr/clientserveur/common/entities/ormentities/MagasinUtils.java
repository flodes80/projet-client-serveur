package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.Magasin;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MagasinUtils {

    /**
     * @param session Session hibernate
     * @param id ID de l'instance
     * @return Instance associée
     */
    public static Magasin get(Session session, int id) {
        String sql = "SELECT d FROM " + Magasin.class.getName() + " d WHERE d.id = :id";
        Query<Magasin> query = session.createQuery(sql);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public static Magasin getByIp(Session session, String ip) {
        String sql = "SELECT m FROM Magasin m WHERE m.ip = :ip";
        Query<Magasin> query = session.createQuery(sql);
        query.setParameter("ip", ip);
        return query.uniqueResult();
    }

    /**
     * @param session Session hibernate
     * @return Toutes les instances
     */
    public static List<Magasin> getAll(Session session) {
        String sql = "SELECT d FROM " + Magasin.class.getName() + " d";
        Query<Magasin> query = session.createQuery(sql);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param magasin Instance à insérer
     * @return Instance crée
     */
    public static Magasin add(Session session, Magasin magasin) {
        session.beginTransaction();
        try {
            session.save(magasin);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return magasin;
    }

    /**
     * @param session Session hibernate
     * @param magasin Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Magasin update(Session session, Magasin magasin) {
        session.beginTransaction();
        try {
            session.update(magasin);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return magasin;
    }

    /**
     * @param session Session hibernate
     * @param magasin Instance à supprimer
     * @return Instance supprimée
     */
    public static Magasin delete(Session session, Magasin magasin) {
        session.beginTransaction();
        try {
            session.delete(magasin);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return magasin;
    }

    /**
     * Fonction permettant d'ajouter ou de mettre à jour l'enregistrement magasin
     * @param session Session hibernate
     * @param magasin Instance à ajouter ou mettre à jour
     */
    public static void addOrUpdate(Session session, Magasin magasin){
        // Tentative de récupération d'un magasin existant avec l'id du magasin
        Magasin magasinToUpdate = get(session, magasin.getId());

        // Si aucune donnée on ajoute
        if(magasinToUpdate == null){
            addWithId(session, magasin);
        }
        // Sinon mise à jour
        else{
            magasinToUpdate.setId(magasin.getId());
            magasinToUpdate.setIp(magasin.getIp());
            magasinToUpdate.setNom(magasin.getNom());
            magasinToUpdate.setAdresse1(magasin.getAdresse1());
            magasinToUpdate.setAdresse2(magasin.getAdresse2());
            update(session, magasinToUpdate);
        }
    }

    public static void addWithId(Session session, Magasin magasin){
        session.beginTransaction();
        try {
            Query query = session.createSQLQuery("INSERT INTO magasin (id, adresse1, adresse2, ip, nom, rmiPort) VALUES (:id, :adresse1, :adresse2, :ip, :nom, :rmiPort)");
            query.setParameter("id", magasin.getId());
            query.setParameter("adresse1", magasin.getAdresse1());
            query.setParameter("adresse2", magasin.getAdresse2());
            query.setParameter("ip", magasin.getIp());
            query.setParameter("nom", magasin.getNom());
            query.setParameter("rmiPort", magasin.getRmiPort());
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

}
