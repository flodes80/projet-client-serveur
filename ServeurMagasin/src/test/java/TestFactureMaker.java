import com.itextpdf.text.DocumentException;
import fr.clientserveur.common.entities.*;
import fr.clientserveur.serveurmagasin.utils.FactureMakerGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

public class TestFactureMaker {

    public static String FACTURE_FILE_PATH;

    @Before
    @Test
    public void testGeneratePdf() throws FileNotFoundException, DocumentException {

        // Magasin
        Magasin magasin = new Magasin();
        magasin.setNom("Leroy Merlin");
        magasin.setAdresse1("8 Rue le Gréco");
        magasin.setAdresse2("80085 Amiens");

        // Client
        Client client = new Client();
        client.setNom("Cocherel");
        client.setPrenom("Valentin");
        client.setNaissance(LocalDate.of(1998, 8, 3));
        client.setEmail("valentin.cocherel@gmail.com");
        client.setAdresse1("14 Rue Octave Tierce");
        client.setAdresse2("80080 Amiens");

        // Famille
        Famille famille = new Famille();
        famille.setNom("Outils");

        // Articles
        Article[] articles = new Article[4];
        articles[0] = new Article();
        articles[0].setReference("RXTTYR675");
        articles[0].setNom("Scie FACOM");
        articles[0].setPrix(BigDecimal.valueOf(12.50));
        articles[0].setFamille(famille);
        articles[1] = new Article();
        articles[1].setReference("RXTR432RT");
        articles[1].setNom("Marteau");
        articles[1].setPrix(BigDecimal.valueOf(10));
        articles[1].setFamille(famille);
        articles[2] = new Article();
        articles[2].setReference("RETZGGF5Y");
        articles[2].setNom("Tourne vis");
        articles[2].setPrix(BigDecimal.valueOf(2.55));
        articles[2].setFamille(famille);

        // Moyen de payement
        MoyenPayement moyenPayement = new MoyenPayement();
        moyenPayement.setNom("Carte bleue");

        // Facture
        Facture facture = new Facture();
        facture.setId(1234);
        facture.setMagasin(magasin);
        facture.setClient(client);
        facture.setDate(new Date());
        facture.setMoyenPaye(moyenPayement);
        facture.setDatePaye(new Date());

        // Achats
        Achat[] achats = new Achat[3];
        achats[0] = new Achat();
        achats[0].setFacture(facture);
        achats[0].setArticle(articles[0]);
        achats[0].setPrixUnit(achats[0].getArticle().getPrix());
        achats[0].setQuantite(2);
        achats[1] = new Achat();
        achats[1].setFacture(facture);
        achats[1].setArticle(articles[1]);
        achats[1].setPrixUnit(achats[1].getArticle().getPrix());
        achats[1].setQuantite(5);
        achats[2] = new Achat();
        achats[2].setFacture(facture);
        achats[2].setArticle(articles[2]);
        achats[2].setPrixUnit(achats[2].getArticle().getPrix());
        achats[2].setQuantite(1);

        // Génération de la facture
        FACTURE_FILE_PATH = FactureMakerGenerator.generatePdf(facture, Arrays.asList(achats));

    }

}
