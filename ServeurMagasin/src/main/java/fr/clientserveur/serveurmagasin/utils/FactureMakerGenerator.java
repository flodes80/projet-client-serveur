package fr.clientserveur.serveurmagasin.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import fr.clientserveur.common.entities.Achat;
import fr.clientserveur.common.entities.Client;
import fr.clientserveur.common.entities.Facture;
import fr.clientserveur.common.entities.Magasin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

public class FactureMakerGenerator {

    private final static Font NORMAL = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
    private final static Font BOLD = FontFactory.getFont(FontFactory.COURIER_BOLD, 13, BaseColor.BLACK);
    private final static Font SMALL = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK);
    private final static Font TITLE = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK);
    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
    private final static DecimalFormat PRICE_FORMATTER = new DecimalFormat("#0.00");

    /**
     * Nom du fichier généré
     */
    private static String fileName;

    /**
     * Génération d'une facture
     * @param facture Instance
     * @param achats Liste des achats de la facture
     * @return Chemin absolu vers le fichier
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static String generatePdf(Facture facture, List<Achat> achats) throws FileNotFoundException, DocumentException {
        FactureMakerGenerator fmg = new FactureMakerGenerator(facture, achats);
        facture.setNomFichier(fileName);
        return fmg.getAbsolutePath();
    }

    /**
     * Génération de la facture
     * @param facture
     * @param achats
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    private FactureMakerGenerator(Facture facture, List<Achat> achats) throws FileNotFoundException, DocumentException {
        fileName = generateFileName(facture);

        // Ouverture du document
        Document document = new Document();
        PdfWriter.getInstance(
                document,
                new FileOutputStream(Config.REPERTOIRE_FACTURE + File.separator + fileName)
        );
        document.open();

        // Ecriture des données
        writeTitle(document, facture);
        writeSpace(document);
        writeSpace(document);
        writeHead(document, facture.getMagasin(), facture.getClient());
        writeInfos(document, facture);
        writeSpace(document);
        writeSpace(document);
        writeAchats(document, achats);
        writeSpace(document);
        writeTotal(document, achats);
        writeSpace(document);
        writeMoyenPayement(document, facture);

        // Fermeture du document
        document.close();
    }

    /**
     * @param facture Instance
     * @return Nom du fichier
     */
    private String generateFileName(Facture facture) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return "FACTURE_N" + facture.getId() + "_"
                + formatter.format(facture.getDate()) + ".pdf";
    }

    /**
     * Obtient le chemin absolu vers le fichier généré
     * @return le chemin absolu vers le fichier généré
     */
    private String getAbsolutePath(){
        return Paths.get("").toAbsolutePath().toString() + "/" + fileName;
    }

    /**
     * Ecriture tu titre
     * @param document
     * @param facture
     * @throws DocumentException
     */
    private void writeTitle(Document document, Facture facture) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);
        Chunk chunk = new Chunk("Facture N°" + facture.getId(), TITLE);
        paragraph.add(chunk);
        document.add(paragraph);
    }

    /**
     * Ecriture du magasin / client
     * @param document
     * @param magasin
     * @param client
     * @throws DocumentException
     */
    private void writeHead(Document document, Magasin magasin, Client client) throws DocumentException {
        document.add(getTwoColomns("Magasin", "Client", BOLD));
        document.add(getTwoColomns(magasin.getNom(), client.getNom() + " " + client.getPrenom(), NORMAL));
        document.add(getTwoColomns(magasin.getAdresse1(), client.getAdresse1(), NORMAL));
        document.add(getTwoColomns(magasin.getAdresse2(), client.getAdresse2(), NORMAL));
        document.add(getTwoColomns("", client.getEmail(), SMALL));
    }

    /**
     * Ecriture date de génération
     * @param document
     * @param facture
     * @throws DocumentException
     */
    private void writeInfos(Document document, Facture facture) throws DocumentException {
        document.add(new Paragraph("Facture N°" + facture.getId(), BOLD));
        document.add(new Paragraph("Commande enregistrée le " + DATE_FORMATTER.format(facture.getDate()), SMALL));
        document.add(new Paragraph("Facture générée le " + DATE_FORMATTER.format(facture.getDate()), SMALL));
    }

    /**
     * Ecriture de la liste des articles
     * @param document
     * @param achats
     * @throws DocumentException
     */
    private void writeAchats(Document document, List<Achat> achats) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3.5f,1,1,1});
        Stream.of("Article", "Quantité", "Prix/u TTC", "Montant TTC")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
        for (Achat achat: achats) {
            writeAchat(table, achat);
        }
        document.add(table);
    }

    /**
     * Ecriture d'un achat dans le tableau
     * @param table
     * @param achat
     */
    private void writeAchat(PdfPTable table, Achat achat) {
        table.addCell(achat.getArticle().getReference() + " - " + achat.getArticle().getNom());
        table.addCell(getRigthCell(String.valueOf(achat.getQuantite())));
        table.addCell(getRigthCell(PRICE_FORMATTER.format(achat.getPrixUnit()) + "€"));
        BigDecimal montant = achat.getPrixUnit().multiply(BigDecimal.valueOf(achat.getQuantite()));
        table.addCell(getRigthCell(PRICE_FORMATTER.format(montant) + "€"));
    }

    /**
     * Ecriture du total
     * @param document
     * @param achats
     * @throws DocumentException
     */
    private void writeTotal(Document document, List<Achat> achats) throws DocumentException {
        BigDecimal total = BigDecimal.ZERO;
        for (Achat achat : achats) {
            total = total.add(achat.getPrixUnit().multiply(BigDecimal.valueOf(achat.getQuantite())));
        }
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{5.5f,1});
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase("Total TTC"));
        table.addCell(header);
        table.addCell(getRigthCell(PRICE_FORMATTER.format(total) + "€"));
        document.add(table);
    }

    /**
     * Ecriture du moyen de payement
     * @param document
     * @param facture
     * @throws DocumentException
     */
    private void writeMoyenPayement(Document document, Facture facture) throws DocumentException {
        document.add(getTwoColomns("", "Payement", BOLD));
        if (facture.getDatePaye() != null && facture.getMoyenPaye() != null) {
            document.add(
                    getTwoColomns("", "Payé le " + DATE_FORMATTER.format(facture.getDatePaye()), NORMAL)
            );
            document.add(getTwoColomns("", "avec \"" + facture.getMoyenPaye().getNom() + "\"", NORMAL));
        } else {
            document.add(getTwoColomns("", "A PAYER", NORMAL));
        }
    }

    /**
     * @param document
     * @throws DocumentException
     */
    private void writeSpace(Document document) throws DocumentException {
        document.add(new Paragraph(" ", NORMAL));
    }

    /**
     * Ecriture en 2 columns
     * @param left
     * @param right
     * @param font
     * @return
     */
    private Paragraph getTwoColomns(String left, String right, Font font) {
        Paragraph paragraph = new Paragraph(left, font);
        paragraph.add(new Chunk(new VerticalPositionMark()));
        paragraph.add(right);
        return paragraph;
    }

    /**
     * Get un cell aligné sur la droite
     * @param string
     * @return
     */
    private PdfPCell getRigthCell(String string) {
        PdfPCell horizontalAlignCell = new PdfPCell(new Phrase(string));
        horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return horizontalAlignCell;
    }

    public static String getLastFileNameGenerated() {
        return fileName;
    }

}
