/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import base.DisplayMethods;
import bl.HoehenlinienManagement;
import bo.GuiEnum;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import tools.StandaloneLogger;

/**
 * Controller-Klasse für das StatusGUI
 *
 * @author u203011
 */
public class StatusController implements Initializable {
    @FXML
    private Button btnFinish;
    @FXML
    private Label mainTitle;
    @FXML
    private Label subTitle;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblStatus;
    @FXML
    private ProgressBar pbAppProgress;
    @FXML
    private Label lblStatusMessage;
    @FXML
    private Label lblAppProgress;
    @FXML
    private ScrollPane spStatus;
    @FXML
    private AnchorPane apStatusContent;
    @FXML
    private TextArea txtStatus;
    @FXML
    private Button btnOrder;
    @FXML
    private Label lblOrder;

    private MainController mainController;
    private HoehenlinienManagement hm;
    private DisplayMethods logger;
    @FXML
    private Button btnBack;
    
    /**
     * Initialisierungsmethode des Controllers
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mainController = MainController.getMainController(null);
        
        logger = mainController.getStandaloneLogger();
        ((StandaloneLogger) logger).setStatusGui(this);
        
        hm = new HoehenlinienManagement(mainController.getHoehenlinienConfig(), this, logger);
        
        mainTitle.setText("Höhenlinien bestellen");
        subTitle.setText("Höhenlinien erstellen");
        lblStatus.setVisible(false);
        btnFinish.setDisable(true);
    }    

    /**
     * Methode, welche das Programm nach erfolgreicher Ausführung beendet.
     * @param event Click-Event
     */
    @FXML
    private void onFinish(MouseEvent event) {
        System.exit(0);
    }

    /**
     * Methode, welche das Programm abbricht.
     * @param event Click-Event
     */
    @FXML
    private void onCancel(MouseEvent event) {
        System.exit(0);
    }

    /**
     * Methode, welche den Bestellungsprozess startet
     * @param event Click-Event
     */
    @FXML
    private void onOrder(MouseEvent event) {
        //this.setProgress(-1);
        Thread th = new Thread(hm);
        th.start();
    }

    /**
     * Getter-Methode für den Beenden-Knopf
     * @return Beenden-Knopf
     */
    public Button getBtnFinish() {
        return btnFinish;
    }

    /**
     * Getter-Methode für das Status-Label
     * @return Status-Label
     */
    public Label getLblStatus() {
        return lblStatus;
    }

    /**
     * Getter-Methode für den Fortschrittsbalken
     * @return Fortschrittsbalken
     */
    public ProgressBar getPbAppProgress() {
        return pbAppProgress;
    }

    /**
     * Getter-Methode für das Statusnachrichten-Fenster
     * @return Statusnachrichten-Fenster
     */
    public TextArea getTxtStatus() {
        return txtStatus;
    }

    /**
     * Getter-Methode für den Bestell-Knopf
     * @return Bestell-Knopf
     */
    public Button getBtnOrder() {
        return btnOrder;
    }

    public void setProgress(final double progress) {
        //this.pbAppProgress.setProgress(progress);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pbAppProgress.setProgress(progress);
            }
        });
    }
    
    public void addStatusMessages(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtStatus.appendText(message);
            }
        });
    }
    
    /**
     * Anzeige des Pfad-Konfiguration-Eingabefensters 
     * @param event Click-Event
     */
    @FXML
    private void onBack(MouseEvent event) {
        mainController.showWindow(GuiEnum.PATH);
    }
}
