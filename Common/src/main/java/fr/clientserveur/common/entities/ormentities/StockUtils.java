package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class StockUtils {

    /**
     * @param session Session hibernate
     * @param article Article associé
     * @param magasin Magasin associé
     * @return Instance associée
     */
    public static Stock get(Session session, Article article, Magasin magasin) {
        String sql = "SELECT d FROM " + Stock.class.getName()
                + " d WHERE d.article = :article AND d.magasin = :magasin";
        Query<Stock> query = session.createQuery(sql);
        query.setParameter("article", article);
        query.setParameter("magasin", magasin);
        return query.getSingleResult();
    }

    /**
     * @param session Session hibernate
     * @param magasin Magasin associé
     * @return Liste des stocks
     */
    public static List<Stock> getByMagasin(Session session, Magasin magasin) {
        String sql = "SELECT d FROM " + Stock.class.getName()
                + " d WHERE d.magasin = :magasin";
        Query<Stock> query = session.createQuery(sql);
        query.setParameter("magasin", magasin);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param magasin Magasin associé
     * @param pageSize Taille de la page
     * @param page Numéro de page
     * @return Liste des stocks
     */
    public static List<Stock> getByMagasin(Session session, Magasin magasin, int pageSize, int page) {
        String sql = "SELECT d FROM " + Stock.class.getName() + " d WHERE d.magasin = :magasin";
        Query<Stock> query = session.createQuery(sql);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        query.setParameter("magasin", magasin);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param magasin Instance du magasin associée
     * @param pageSize Tailles d'une page
     * @return Nombre de pages
     */
    public static int getByMagasinPageNumbers(Session session, Magasin magasin, int pageSize) {
        String sql = "SELECT COUNT(d) FROM " + Stock.class.getName() + " d WHERE d.magasin = :magasin";
        Query<Long> countQuery = session.createQuery(sql);
        countQuery.setParameter("magasin", magasin);
        Long count = countQuery.uniqueResult();
        return (int) Math.ceil(count/ (double) pageSize);
    }

    /**
     * @param session Session hibernate
     * @param article Article associé
     * @return Le stock pour l'article demandé ou null si rien
     */
    public static Stock getByArticle(Session session, Article article) {
        String sql = "SELECT d FROM " + Stock.class.getName()
                + " d WHERE d.article = :article";
        Query<Stock> query = session.createQuery(sql);
        query.setParameter("article", article);
        return query.uniqueResult();
    }

    /**
     * @param session Session hibernate
     * @param stock Instance à insérer
     * @return Instance crée
     */
    public static Stock add(Session session, Stock stock) {
        session.beginTransaction();
        try {
            session.save(stock);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return stock;
    }

    /**
     * @param session Session hibernate
     * @param stock Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Stock update(Session session, Stock stock) {
        session.beginTransaction();
        try {
            session.update(stock);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return stock;
    }

    /**
     * @param session Session hibernate
     * @param stock Instance à supprimer
     * @return Instance supprimée
     */
    public static Stock delete(Session session, Stock stock) {
        session.beginTransaction();
        try {
            session.delete(stock);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return stock;
    }

    /**
     * Méthode supprimant toutes les données dans la table
     * @param session Session hibernate
     */
    public static void removeAll(Session session){
        session.beginTransaction();
        session.createSQLQuery("delete from stock").executeUpdate();
        session.getTransaction().commit();
    }

    public static List<Stock> findBySearchPagination(Session session, String search, int pageSize, int page) {
        String sql = "SELECT s FROM " + Stock.class.getName() + " s " +
                "WHERE s.article.reference IN " +
                "(SELECT a.reference FROM "+ Article.class.getName()  + " a " +
                "WHERE a.reference like :querySearch " +
                "OR a.nom like :querySearch "+
                "OR a.description like :querySearch " +
                "OR a.famille.id IN (SELECT f.id FROM " + Famille.class.getName() + " f WHERE f.nom like :querySearch ))";
        Query<Stock> query = session.createQuery(sql);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        query.setParameter("querySearch", "%"+search+"%");
        return query.getResultList();
    }

    public static int findBySearchNbPage(Session session, String search, int pageSize) {
        String sql = "SELECT COUNT(s) FROM " + Stock.class.getName() + " s " +
                "WHERE s.article.reference IN " +
                "(SELECT a.reference FROM "+ Article.class.getName()  + " a " +
                "WHERE a.reference like :querySearch " +
                "OR a.nom like :querySearch "+
                "OR a.description like :querySearch " +
                "OR a.famille.id IN (SELECT f.id FROM " + Famille.class.getName() + " f WHERE f.nom like :querySearch ))";
        Query<Long> countQuery = session.createQuery(sql);
        countQuery.setParameter("querySearch", "%"+search+"%");
        Long count = countQuery.uniqueResult();
        return (int) Math.ceil(count/ (double) pageSize);
    }
}
