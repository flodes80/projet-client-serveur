package fr.clientserveur.client.javafx.controllers;

import fr.clientserveur.client.javafx.dialogs.LoadingDialog;
import fr.clientserveur.client.javafx.panes.ConfirmPanierPane;
import fr.clientserveur.client.javafx.panes.FacturePane;
import fr.clientserveur.client.javafx.panes.MagasinPane;
import fr.clientserveur.client.javafx.panes.StockPane;
import fr.clientserveur.client.javafx.windows.*;
import fr.clientserveur.common.entities.*;
import fr.clientserveur.common.rmi.interfaces.InterfaceServeurMagasin;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class InterfaceController {

    public static class ThreadDataPanier{
        List<MoyenPayement> moyenPayements;
        List<Client> clients;
    }

    public static class ThreadDataStock{
        List<Stock> stocks;
        int pages;
    }

    public static class ThreadDataFacture{
        List<Facture> factures;
        int pages;
    }

    public static class ThreadDataOpenFacture{
        Facture facture;
        List<Achat> achats;
        BigDecimal total;
    }

    private final InterfaceServeurMagasin server;
    private final AnchorPane mainPane;
    private final Map<String, Achat> panier;

    public InterfaceController(InterfaceServeurMagasin server, Scene scene) {
        this.server = server;
        this.mainPane = (AnchorPane) scene.lookup("#mainPane");
        this.panier = new HashMap<>();

        // Menu
        scene.lookup("#menu_magasin").setOnMouseClicked(e -> showMagasin());
        scene.lookup("#menu_stocks").setOnMouseClicked(e -> showStock());
        scene.lookup("#menu_factures").setOnMouseClicked(e -> showFactures());
        scene.lookup("#menu_affaire").setOnMouseClicked(e -> showChiffreAffaireSelection());
    }

    public InterfaceServeurMagasin getServer() {
        return server;
    }

    private MagasinPane magasinPane;
    private StockPane stockPane;
    private FacturePane facturePane;

    public void showMagasin() {
        if (magasinPane == null) {
            magasinPane = new MagasinPane(this);
        }

        mainPane.getChildren().clear();
        mainPane.getChildren().add(magasinPane);

        LoadingDialog loading = new LoadingDialog();

        Task<ThreadDataStock> task = new Task<ThreadDataStock>() {
            @Override
            public ThreadDataStock call() throws RemoteException {
                ThreadDataStock data = new ThreadDataStock();
                if (!magasinPane.getArticlesPane().isFiltered()) {
                    data.stocks = server.getStocks(10, 0);
                    data.pages = server.getStocksNumberPages(10);
                } else {
                    data.stocks = server.searchStock(
                            magasinPane.getArticlesPane().getSearch(),
                            10,
                            0
                    );
                    data.pages = server.getSearchStockNumberPages(
                            magasinPane.getArticlesPane().getSearch(),
                            10
                    );
                }
                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataStock data = task.getValue();
            magasinPane.getArticlesPane().setStocks(data.stocks);
            magasinPane.getArticlesPane().setPageCount(data.pages);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void setMagasinPage(int page) {
        LoadingDialog loading = new LoadingDialog();

        Task<List<Stock>> task = new Task<List<Stock>>() {
            @Override
            public List<Stock> call() throws RemoteException {
                if (!magasinPane.getArticlesPane().isFiltered()) {
                    return server.getStocks(10, page - 1);
                } else {
                    return server.searchStock(
                            magasinPane.getArticlesPane().getSearch(),
                            10,
                            page - 1
                    );
                }
            }
        };
        task.setOnSucceeded(event -> {
            magasinPane.getArticlesPane().setStocks(task.getValue());
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void searchMagasin() {
        LoadingDialog loading = new LoadingDialog();

        Task<ThreadDataStock> task = new Task<ThreadDataStock>() {
            @Override
            public ThreadDataStock call() throws RemoteException {
                ThreadDataStock data = new ThreadDataStock();
                data.stocks = server.getStocks(10, 0);
                data.pages = server.getStocksNumberPages(10);

                if (!magasinPane.getArticlesPane().isFiltered()) {
                    data.stocks = server.getStocks(10, 0);
                    data.pages = server.getStocksNumberPages(10);
                } else {
                    data.stocks = server.searchStock(
                            magasinPane.getArticlesPane().getSearch(),
                            10,
                            0
                    );
                    data.pages = server.getSearchStockNumberPages(
                            magasinPane.getArticlesPane().getSearch(),
                            10
                    );
                }

                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataStock data = task.getValue();
            magasinPane.getArticlesPane().setStocks(data.stocks);
            magasinPane.getArticlesPane().setPageCount(data.pages);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void showStock() {

        // Création & affichage pane
        LoadingDialog loading = new LoadingDialog();
        if (stockPane == null) {
            stockPane = new StockPane(this);
        }
        mainPane.getChildren().clear();
        mainPane.getChildren().add(stockPane);

        Task<ThreadDataStock> task = new Task<ThreadDataStock>() {
            @Override
            public ThreadDataStock call() throws RemoteException {
                ThreadDataStock data = new ThreadDataStock();
                if (!stockPane.isFiltered()) {
                    data.stocks = server.getStocks(10, 0);
                    data.pages = server.getStocksNumberPages(10);
                } else {
                    data.stocks = server.searchStock(stockPane.getSearch(),10,0);
                    data.pages = server.getSearchStockNumberPages(stockPane.getSearch(),10);
                }
                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataStock data = task.getValue();
            stockPane.setStocks(data.stocks);
            stockPane.setPageCount(data.pages);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void setStockPage(int page) {
        LoadingDialog loading = new LoadingDialog();

        Task<List<Stock>> task = new Task<List<Stock>>() {
            @Override
            public List<Stock> call() throws RemoteException {
                if (!stockPane.isFiltered()) {
                    return server.getStocks(10, page - 1);
                } else {
                    return server.searchStock(stockPane.getSearch(), 10, page - 1);
                }
            }
        };
        task.setOnSucceeded(event -> {
            stockPane.setStocks(task.getValue());
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void searchStock() {
        LoadingDialog loading = new LoadingDialog();

        Task<ThreadDataStock> task = new Task<ThreadDataStock>() {
            @Override
            public ThreadDataStock call() throws RemoteException {
                ThreadDataStock data = new ThreadDataStock();
                if (!stockPane.isFiltered()) {
                    data.stocks = server.getStocks(10, 0);
                    data.pages = server.getStocksNumberPages(10);
                } else {
                    data.stocks = server.searchStock(stockPane.getSearch(), 10, 0);
                    data.pages = server.getSearchStockNumberPages(stockPane.getSearch(), 10);
                }
                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataStock data = task.getValue();
            stockPane.setStocks(data.stocks);
            stockPane.setPageCount(data.pages);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void addAchat(Stock stock, int qte) {
        // Vérification qte
        int total = qte;
        if (panier.containsKey(stock.getArticle().getReference())) {
            total += panier.get(stock.getArticle().getReference()).getQuantite();
        }
        if (total > stock.getStock()) {
            new Alert(Alert.AlertType.INFORMATION, "Pas assez de stock").show();
            return;
        }

        if (total <= 0) {
            if (panier.containsKey(stock.getArticle().getReference())) {
                magasinPane.removeAchat(panier.get(stock.getArticle().getReference()));
                panier.remove(stock.getArticle().getReference());
            }
            return;
        }

        // Définition / maj propriétés
        Achat achat = panier.getOrDefault(stock.getArticle().getReference(), new Achat());
        achat.setArticle(stock.getArticle());
        achat.setPrixUnit(achat.getArticle().getPrix());
        achat.setQuantite(total);
        panier.put(stock.getArticle().getReference(), achat);
        magasinPane.setAchat(stock, achat);
    }

    public void confirmPanier(List<Achat> achats) {
        // Vérification si panier non vide
        if (achats.size() == 0) {
            new Alert(Alert.AlertType.INFORMATION, "Veuillez ajouter des articles à votre panier.").show();
            return;
        }

        LoadingDialog loading = new LoadingDialog();

        Task<ThreadDataPanier> task = new Task<ThreadDataPanier>() {
            @Override
            public ThreadDataPanier call() throws RemoteException {
                ThreadDataPanier result = new ThreadDataPanier();
                result.clients = server.searchClient("", "");
                result.moyenPayements = server.getMoyensPayement();
                return result;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataPanier result = task.getValue();
            ConfirmPanierPane confirmPane = new ConfirmPanierPane(this, achats, result.moyenPayements, result.clients);
            mainPane.getChildren().clear();
            mainPane.getChildren().add(confirmPane);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void confirmFacture(Client client, List<Achat> achats, MoyenPayement moyenPayement) {

        LoadingDialog loading = new LoadingDialog();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                // Create / Maj client
                Client clientPersist = server.createOrUpdateClient(client);

                // Création facture
                Map<Article, Integer> list = new HashMap<>();
                for (Achat achat : achats) {
                    list.put(achat.getArticle(),achat.getQuantite());
                }
                Facture facture = server.clientOrder(clientPersist, list);

                // Paiement
                if (moyenPayement != null) {
                    server.payeFacture(facture, moyenPayement);
                }

                return null;
            }
        };
        task.setOnSucceeded(event -> {
            loading.done();
            new Alert(Alert.AlertType.INFORMATION, "Commande effectuée avec succès.").show();
            this.panier.clear();
            magasinPane.clearPanier();
            showMagasin();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue, veuillez vérifier le stock.").show();
        });

        new Thread(task).start();
    }

    public void openStockMagasin(Article article) {
        LoadingDialog loading = new LoadingDialog();
        Task<List<Magasin>> task = new Task<List<Magasin>>() {
            @Override
            public List<Magasin> call() throws RemoteException {
                return server.getAllMagasins();
            }
        };
        task.setOnSucceeded(event -> {
            try {
                new StockMagasinSelectionWindow(this, task.getValue(), article).show();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void getStockMagasin(Article article, Magasin magasin) {
        LoadingDialog loading = new LoadingDialog();
        Task<Stock> task = new Task<Stock>() {
            @Override
            public Stock call() throws RemoteException {
                return server.getStockFromOtherMagasin(article, magasin);
            }
        };
        task.setOnSucceeded(event -> {
            // Affichage fenêtre de commande interne
            Stock stock = task.getValue();
            loading.done();
            showOrderDialog(article, stock, magasin);
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void openStockDefinition(Stock stock) {
        try {
            new StockDefinitionWindow(this, stock).show();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setStock(Stock stock, int qte) {
        LoadingDialog loading = new LoadingDialog();
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws RemoteException {
                server.setStockArticle(stock.getArticle(), qte);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            loading.done();
            showStock();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });
        new Thread(task).start();
    }

    public void showFactures() {

        // Création & affichage pane
        LoadingDialog loading = new LoadingDialog();
        if (facturePane == null) {
            facturePane = new FacturePane(this);
        }
        mainPane.getChildren().clear();
        mainPane.getChildren().add(facturePane);

        Task<ThreadDataFacture> task = new Task<ThreadDataFacture>() {
            @Override
            public ThreadDataFacture call() throws RemoteException {
                ThreadDataFacture data = new ThreadDataFacture();
                if (!facturePane.isFiltered()) {
                    data.factures = server.getFacturesByPages(10, 0);
                    data.pages = server.getFactureNumberPages(10);
                } else {
                    data.factures = server.searchFacture(facturePane.getSearch(),10,0);
                    data.pages = server.getSearchFactureNumberPages(facturePane.getSearch(),10);
                }
                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataFacture data = task.getValue();
            facturePane.setFactures(data.factures);
            facturePane.setPageCount(data.pages);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void setFacturePage(int page) {
        LoadingDialog loading = new LoadingDialog();

        Task<List<Facture>> task = new Task<List<Facture>>() {
            @Override
            public List<Facture> call() throws RemoteException {
                if (!facturePane.isFiltered()) {
                    return server.getFacturesByPages(10, page - 1);
                } else {
                    return server.searchFacture(facturePane.getSearch(),10,page - 1);
                }
            }
        };
        task.setOnSucceeded(event -> {
            facturePane.setFactures(task.getValue());
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void searchFacture() {
        LoadingDialog loading = new LoadingDialog();

        Task<ThreadDataFacture> task = new Task<ThreadDataFacture>() {
            @Override
            public ThreadDataFacture call() throws RemoteException {
                ThreadDataFacture data = new ThreadDataFacture();
                if (!facturePane.isFiltered()) {
                    data.factures = server.getFacturesByPages(10, 0);
                    data.pages = server.getFactureNumberPages(10);
                } else {
                    data.factures = server.searchFacture(facturePane.getSearch(), 10, 0);
                    data.pages = server.getSearchFactureNumberPages(facturePane.getSearch(), 10);
                }
                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataFacture data = task.getValue();
            facturePane.setFactures(data.factures);
            facturePane.setPageCount(data.pages);
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void openFacture(Facture facture) {
        LoadingDialog loading = new LoadingDialog();

        Task<ThreadDataOpenFacture> task = new Task<ThreadDataOpenFacture>() {
            @Override
            public ThreadDataOpenFacture call() throws RemoteException {
                ThreadDataOpenFacture data = new ThreadDataOpenFacture();
                data.facture = facture;
                data.achats = server.achatsFacture(facture);
                data.total = server.totalFacture(facture);
                return data;
            }
        };
        task.setOnSucceeded(event -> {
            ThreadDataOpenFacture data = task.getValue();
            try {
                new FactureWindow(this, data.facture, data.achats, data.total);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();

    }

    public void openPayFacture(Facture facture) {
        LoadingDialog loading = new LoadingDialog();
        Task<List<MoyenPayement>> task = new Task<List<MoyenPayement>>() {
            @Override
            public List<MoyenPayement> call() throws RemoteException {
                return server.getMoyensPayement();
            }
        };
        task.setOnSucceeded(event -> {
            try {
                new MoyenPaiementSelectionWindow(this, task.getValue(), facture).show();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            loading.done();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

    public void payFacture(Facture facture, MoyenPayement moyen) {
        LoadingDialog loading = new LoadingDialog();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                server.payeFacture(facture, moyen);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            loading.done();
            new Alert(Alert.AlertType.INFORMATION, "Facture payée avec succès.").show();
            this.panier.clear();
            magasinPane.clearPanier();
            showFactures();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
            showFactures();
        });

        new Thread(task).start();

    }

    public void downloadAndOpenFacture(Facture facture){
        if(facture != null && facture.getNomFichier() != null){
            File file = null;
            // Téléchargement de la facture
            try {
                // Téléchargement ...
                byte[] data = server.downloadFactureFromServer(facture.getNomFichier());

                // Ecriture sur disque dur
                file = new File(System.getProperty("user.dir") + File.separator  + facture.getNomFichier());
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Erreur lors du téléchargement du fichier.").show();
            }


            // Tentative ouverture fichier
            if(!Desktop.isDesktopSupported()){
                new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le fichier sur votre ordinateur").show();
                return;
            }


            Desktop desktop = Desktop.getDesktop();
            if(file != null && file.exists()){
                try {
                    desktop.open(file); // Ouverture du fichier
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du fichier.");
                }
            }
        }
        else{
            new Alert(Alert.AlertType.ERROR, "Facture erronnée.");
        }

    }

    public List<Client> searchClient(String searchQuery){
        List<Client> listeClient = null;
        try{
            listeClient = server.searchClient(searchQuery, searchQuery);
        } catch (RemoteException e) {

        }
        return listeClient;
    }

    private void showOrderDialog(Article article, Stock stock, Magasin magasin){
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle("Commande à un autre magasin");
        tid.setHeaderText("Le stock du magasin " + magasin.getNom() + " pour l'article " + article.getNom()
                + " est de " + stock.getStock());
        tid.setContentText("Combien souhaitez-vous en commander ?");
        Optional<String> result = tid.showAndWait();

        // Analyse du résultat de la dialog
        if(result.isPresent()){
            try{
                int quantite = Integer.parseInt(result.get());
                if(quantite <= 0)
                    throw new NumberFormatException();
                else if(quantite > stock.getStock()){
                    new Alert(Alert.AlertType.ERROR, "Impossible de commander plus que la quantité disponible !");
                }
                // Si tout est ok envoi d'une requête de commande interne au magasin
                else{
                    LoadingDialog loading = new LoadingDialog();
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            server.requestInternalOrder(article, quantite, magasin);
                            return null;
                        }
                    };
                    task.setOnSucceeded(event -> {
                        new Alert(Alert.AlertType.INFORMATION, "La commande a été effectuée avec succès !").show();
                    });

                    task.setOnFailed(event -> {
                        new Alert(Alert.AlertType.ERROR, "Une erreur est survenue !").show();
                    });
                    loading.done();
                    new Thread(task).start();
                }

            } catch(NumberFormatException e){
                new Alert(Alert.AlertType.ERROR, "Quantité incorrect").show();
            }
        }
    }

    public void showChiffreAffaireSelection() {
        try {
            new ChiffreAffaireWindow(this);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void getChiffeAffaire(LocalDate date) {
        LoadingDialog loading = new LoadingDialog();
        Task<BigDecimal> task = new Task<BigDecimal>() {
            @Override
            public BigDecimal call() throws Exception {
                return server.getChiffreAffaire(date);
            }
        };
        task.setOnSucceeded(event -> {
            loading.done();
            new Alert(Alert.AlertType.INFORMATION, "Le chiffre d'affaire du "
                    + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " est de "
                    + new DecimalFormat("#0.##").format(task.getValue()) + "€.").show();
        });
        task.setOnFailed(event -> {
            loading.done();
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue.").show();
        });

        new Thread(task).start();
    }

}
