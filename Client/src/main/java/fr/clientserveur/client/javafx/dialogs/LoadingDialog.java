package fr.clientserveur.client.javafx.dialogs;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;

public class LoadingDialog extends Alert {

    public LoadingDialog() {
        super(AlertType.NONE);
        this.setTitle("Chargement ");
        this.setHeaderText("Veuillez patienter... ");
        ProgressBar progressBar = new ProgressBar();
        this.setGraphic(progressBar);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.show();
    }

    public void done() {
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        this.close();
    }
}
