package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FactureUtils {

    /**
     * @param session Session hibernate
     * @param id ID de l'instance
     * @return Instance associée
     */
    public static Facture get(Session session, int id) {
        String sql = "SELECT d FROM " + Facture.class.getName() + " d WHERE d.id = :id";
        Query<Facture> query = session.createQuery(sql);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    /**
     * @param session Session hibernate
     * @return List des instances
     */
    public static List<Facture> getAll(Session session) {
        String sql = "SELECT d FROM " + Facture.class.getName() + " d";
        Query<Facture> query = session.createQuery(sql);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param client Client associé
     * @return Liste des factures du client
     */
    public static List<Facture> getByClient(Session session, Client client) {
        String sql = "SELECT d FROM " + Facture.class.getName() + " d WHERE d.client = :client";
        Query<Facture> query = session.createQuery(sql);
        query.setParameter("client", client);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param facture Instance associée
     * @return Total de la facture
     */
    public static BigDecimal getTotal(Session session, Facture facture) {
        String sql = "SELECT COALESCE(SUM(d.prixUnit * d.quantite), 0) FROM " + Achat.class.getName()
                + " d WHERE d.facture = :facture";
        Query<BigDecimal> query = session.createQuery(sql);
        query.setParameter("facture", facture);
        return query.getSingleResult();
    }

    /**
     * @param session Session hibernate
     * @param facture Instance à insérer
     * @return Instance crée
     */
    public static Facture add(Session session, Facture facture) {
        session.beginTransaction();
        try {
            session.save(facture);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return facture;
    }

    /**
     * @param session Session hibernate
     * @param facture Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Facture update(Session session, Facture facture) {
        session.beginTransaction();
        try {
            session.update(facture);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return facture;
    }

    /**
     * @param session Session hibernate
     * @param facture Instance à supprimer
     * @return Instance supprimée
     */
    public static Facture delete(Session session, Facture facture) {
        session.beginTransaction();
        try {
            session.delete(facture);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return facture;
    }

    /**
     * Fonction retournant la liste des factures par Date
     * @param session Session hibernate
     * @param datePaye Date des factures
     * @return Liste de factures
     */
    public static List<Facture> getFacturesByDate(Session session, LocalDate datePaye){
        Date min = Date.from(datePaye.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date max = Date.from(datePaye.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        String sql = "SELECT d FROM " + Facture.class.getName() + " d WHERE d.datePaye >= :dmin AND d.datePaye <= :dmax";
        Query<Facture> query = session.createQuery(sql);
        query.setParameter("dmin", min);
        query.setParameter("dmax", max);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param client Client acheteur
     * @param achats Liste des articles associés à leurs quantités
     * @return Nouvelle facture
     * @throws Exception En cas d'erreur
     */
    public static Facture command(
            Session session,
            Magasin magasin,
            Client client,
            Map<Article, Integer> achats
    ) throws Exception {
        session.beginTransaction();
        try {
            // Sauvegarde de la facture
            Facture facture = new Facture();
            facture.setMagasin(magasin);
            facture.setClient(client);
            facture.setDate(new Date());
            session.save(facture);

            // Ajout des achats
            for (Map.Entry<Article, Integer> achat : achats.entrySet()) {
                // vérification disponibilité stock
                String sql = "SELECT d FROM " + Stock.class.getName()
                        + " d WHERE d.article = :article AND d.magasin = :magasin";
                Query<Stock> query = session.createQuery(sql);
                query.setParameter("article", achat.getKey());
                query.setParameter("magasin", facture.getMagasin());
                Stock stock = query.getSingleResult();
                if (stock.getStock() < achat.getValue()) {
                    throw new Exception("No enought stock.");
                }
                // Soustraction stock
                stock.setStock(stock.getStock() - achat.getValue());
                session.update(stock);
                // Ajout achat facture
                Achat iAchat = new Achat();
                iAchat.setFacture(facture);
                iAchat.setArticle(achat.getKey());
                iAchat.setQuantite(achat.getValue());
                iAchat.setPrixUnit(iAchat.getArticle().getPrix());
                session.save(iAchat);
            }

            // Application de la transaction et retour de l'instance
            session.getTransaction().commit();
            return facture;
        } catch (Exception e) {
            // En cas d'erreur
            session.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * @param session Session hibernate
     * @param pageSize Tailles d'une page
     * @return Nombre de pages
     */
    public static int getByPageNumbers(Session session, int pageSize) {
        String sql = "SELECT COUNT(d) FROM " + Facture.class.getName() + " d ";
        Query<Long> countQuery = session.createQuery(sql);
        Long count = countQuery.uniqueResult();
        return (int) Math.ceil(count/ (double) pageSize);
    }

    /**
     * @param session Session hibernate
     * @param pageSize Taille de la page
     * @param page Numéro de page
     * @return Liste des stocks
     */
    public static List<Facture> getByPage(Session session, int pageSize, int page) {
        String sql = "SELECT d FROM " + Facture.class.getName() + " d ";
        Query<Facture> query = session.createQuery(sql);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    /**
     * Fonction permettant la recherche de facture à partir d'une chaine de caractére
     * @param session Session hibernate
     * @param querySearch Chaîne de caractères recherchée
     * @return Liste de facture correspondant aux critéres de recherche
     */
    public static List<Facture> findBySearch(Session session, String querySearch){
        String sql = "SELECT f FROM " + Facture.class.getName() + " f " +
                "WHERE f.client.id IN " +
                "(SELECT id FROM "+ Client.class.getName()  + " " +
                "WHERE prenom like :querySearch " +
                "OR nom like :querySearch ) ";
        Query<Facture> query = session.createQuery(sql);
        query.setParameter("querySearch", "%"+querySearch+"%");
        return query.getResultList();
    }

    public static List<Facture> findBySearchPagination(Session session, String search, int pageSize, int page) {
        String sql = "SELECT f FROM " + Facture.class.getName() + " f " +
                "WHERE f.client.id IN " +
                "(SELECT id FROM "+ Client.class.getName()  + " " +
                "WHERE prenom like :querySearch " +
                "OR nom like :querySearch ) ";
        Query<Facture> query = session.createQuery(sql);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        query.setParameter("querySearch", "%"+search+"%");
        return query.getResultList();
    }

    public static int findBySearchNbPage(Session session, String search, int pageSize) {
        String sql = "SELECT COUNT(f) FROM " + Facture.class.getName() + " f " +
                "WHERE f.client.id IN " +
                "(SELECT id FROM "+ Client.class.getName()  + " " +
                "WHERE prenom like :querySearch " +
                "OR nom like :querySearch ) ";
        Query<Long> countQuery = session.createQuery(sql);
        countQuery.setParameter("querySearch", "%"+search+"%");
        Long count = countQuery.uniqueResult();
        return (int) Math.ceil(count/ (double) pageSize);
    }
}
