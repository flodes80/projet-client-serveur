package fr.clientserveur.common.entities.ormentities;

import fr.clientserveur.common.entities.Article;
import fr.clientserveur.common.entities.Famille;
import fr.clientserveur.common.entities.Magasin;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ArticleUtils {

    /**
     * @param session Session hibernate
     * @param reference Reference de l'instance
     * @return Instance associée
     */
    public static Article get(Session session, String reference) {
        String sql = "SELECT d FROM " + Article.class.getName() + " d WHERE d.reference = :reference";
        Query<Article> query = session.createQuery(sql);
        query.setParameter("reference", reference);
        return query.uniqueResult();
    }

    /**
     * @param session Session hibernate
     * @return List des instances
     */
    public static List<Article> getAll(Session session) {
        String sql = "SELECT d FROM " + Article.class.getName() + " d";
        Query<Article> query = session.createQuery(sql);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param famille Famille associée
     * @return List des instances
     */
    public static List<Article> getByFamille(Session session, Famille famille) {
        String sql = "SELECT d FROM " + Article.class.getName() + " d WHERE d.famille = :famille";
        Query<Article> query = session.createQuery(sql);
        query.setParameter("famille", famille);
        return query.getResultList();
    }

    /**
     * @param session Session hibernate
     * @param article Instance à insérer
     * @return Instance crée
     */
    public static Article add(Session session, Article article) {
        session.beginTransaction();
        try {
            session.save(article);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return article;
    }

    /**
     * @param session Session hibernate
     * @param article Instance à mettre à jour
     * @return Instance mise à jour
     */
    public static Article update(Session session, Article article) {
        session.beginTransaction();
        try {
            session.update(article);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return article;
    }

    /**
     * @param session Session hibernate
     * @param article Instance à supprimer
     * @return Instance supprimée
     */
    public static Article delete(Session session, Article article) {
        session.beginTransaction();
        try {
            session.delete(article);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        return article;
    }

    /**
     * Méthode supprimant toutes les données dans la table
     * @param session Session hibernate
     */
    public static void removeAll(Session session){
        session.beginTransaction();
        session.createSQLQuery("delete from article").executeUpdate();
        session.getTransaction().commit();
    }

    /**
     * Fonction permettant d'ajouter ou de mettre à jour un article dans le magasin
     * @param session Session hibernate
     * @param article Instance à ajouter ou mettre à jour
     * @return Magasin ajouté ou mis à jour
     */
    public static Article addOrUpdate(Session session, Article article){
        // Tentative de récupération d'un article existant avec l'id de l'article
        Article articleToUpdate = get(session, article.getReference());

        // Si aucune donnée on ajoute
        if(articleToUpdate == null){
            session.beginTransaction();
            try {
                session.replicate(article, ReplicationMode.EXCEPTION);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
            return article;
        }
        // Sinon mise à jour
        else{
            articleToUpdate.setReference(article.getReference());
            articleToUpdate.setNom(article.getNom());
            articleToUpdate.setDescription(article.getDescription());
            articleToUpdate.setPrix(article.getPrix());
            articleToUpdate.setFamille(article.getFamille());
            update(session, articleToUpdate);
            return articleToUpdate;
        }
    }
    
}
