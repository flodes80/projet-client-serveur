package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.Client;
import fr.clientserveur.common.entities.Magasin;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ClientUtils {

    /**
     * @param session Session hibernate
     * @param id ID de l'instance
     * @return Instance associée
     */
    public static Client get(Session session, int id) {
        String sql = "SELECT d FROM " + Client.class.getName() + " d WHERE d.id = :id";
        Query<Client> query = session.createQuery(sql);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    /**
     * @param session Session hibernate
     * @param nom Nom du client
     * @param prenom Prénom du client
     * @return Liste correspondante
     */
    public static List<Client> searchByNomPrenom(Session session, String nom, String prenom) {
        String sql = "SELECT d FROM " + Client.class.getName() + " d WHERE lower(d.nom) LIKE lower(:nom)"
                + " OR lower(d.prenom) LIKE lower(:prenom)";
        Query<Client> query = session.createQuery(sql);
        query.setParameter("nom", "%" + nom + "%");
        query.setParameter("prenom", "%" + prenom + "%");
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param client Instance à insérer
     * @return Instance crée
     */
    public static Client add(Session session, Client client) {
        session.beginTransaction();
        try {
            session.save(client);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return client;
    }

    /**
     * @param session Session hibernate
     * @param client Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Client update(Session session, Client client) {
        session.beginTransaction();
        try {
            session.update(client);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return client;
    }

    /**
     * @param session Session hibernate
     * @param client Instance à supprimer
     * @return Instance supprimée
     */
    public static Client delete(Session session, Client client) {
        session.beginTransaction();
        try {
            session.delete(client);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return client;
    }

    /**
     * Méthode supprimant toutes les données dans la table
     * @param session Session hibernate
     */
    public static void removeAll(Session session){
        session.beginTransaction();
        session.createSQLQuery("delete from client").executeUpdate();
        session.getTransaction().commit();
    }

    /**
     * Fonction permettant d'ajouter ou de mettre à jour l'enregistrement magasin
     * @param session Session hibernate
     * @param client Instance à ajouter ou mettre à jour
     * @return Magasin ajouté ou mis à jour
     */
    public static Client addOrUpdate(Session session, Client client){
        // Tentative de récupération d'un magasin existant avec l'id du magasin
        Client clientToUpdate = get(session, client.getId());

        // Si aucune donnée on ajoute
        if(clientToUpdate == null){
            return add(session, client);
        }
        // Sinon mise à jour
        else{
            clientToUpdate.setNom(client.getNom());
            clientToUpdate.setPrenom(client.getPrenom());
            clientToUpdate.setAdresse1(client.getAdresse1());
            clientToUpdate.setAdresse2(client.getAdresse2());
            clientToUpdate.setEmail(client.getEmail());
            clientToUpdate.setNaissance(client.getNaissance());
            update(session, clientToUpdate);
            return clientToUpdate;
        }
    }

}
