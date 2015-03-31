/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import bo.GuiEnum;
import bo.InputModel;
import bo.LogPrefix;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import tools.FormatTools;
import tools.NumberTools;

/**
 * Controller-Klasse für das InputGUI
 *
 * @author u203011
 */
public class InputController implements Initializable {
    @FXML
    private Label lblOrdercode;
    @FXML
    private TextField txtOrdercode;
    @FXML
    private Label lblOrderCodeStatus;
    @FXML
    private Label lblKoordRectangle;
    @FXML
    private Label lblInputStatus;
    @FXML
    private Label lblOutput;
    @FXML
    private Label lblOutputStatus;
    @FXML
    private Label lblAequi;
    @FXML
    private Label lblAequiStatus;
    @FXML
    private Label lblSmoothOptions;
    @FXML
    private Label lblThinning;
    @FXML
    private Label lbl3D;
    @FXML
    private Label lblLidar;
    @FXML
    private Label lblLog;
    @FXML
    private Label lblSmoothStatus;
    @FXML
    private Label lblThinningStatus;
    @FXML
    private Label lbl3DStatus;
    @FXML
    private Label lblLidarStatus;
    @FXML
    private Label lblLogStatus;
    @FXML
    private Button btnNext;
    @FXML
    private Label mainTitle;
    @FXML
    private Label subTitle;
    @FXML
    private TextField txtInput;
    @FXML
    private TextField txtOutput;
    @FXML
    private TextField txtAequi;
    @FXML
    private TextField txtThinning;
    @FXML
    private Button btnOutput;
    @FXML
    private ComboBox<String> cbSmooth;
    @FXML
    private CheckBox ck3D;
    @FXML
    private TextField txtLidar;
    @FXML
    private TextField txtLog;
    @FXML
    private Button btnLidar;
    @FXML
    private Button btnLog;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblStatus;
    
    private MainController mainController;
    private NumberTools numTool;
    private FormatTools formatTool;
    private InputModel inputModel;

    /**
     * Initialisierungsmethode des InputControllers.
     * Initialisiert Eingabefelder und setzt die Tooltip-Hilfestellungen.
     * Falls Konfigurationen schon gemacht wurden und der Zurück-Button im vorherigen 
     * Fenster geklickt wurde, so werden die Eingaben direkt in die Eingabefelder 
     * gesetzt.
     * @param url JavaFX-URL
     * @param rb JavaFX Resource Bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainController = MainController.getMainController(null);
        
        cbSmooth.getItems().clear();
        cbSmooth.getItems().add("Chaiken");
        cbSmooth.getItems().add("Hermite");
        cbSmooth.getItems().add("Boyle");
        
        mainTitle.setText("Höhenlinien bestellen");
        subTitle.setText("Steuerungsparameter eingeben");
        lblStatus.setVisible(false);
        
        if(mainController.getHoehenlinienConfig().getInputModel().isForce3D() != null) {
            ck3D.setSelected(mainController.getHoehenlinienConfig().getInputModel().isForce3D());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getAequidistance() != null) {
            txtAequi.setText(mainController.getHoehenlinienConfig().getInputModel().getAequidistance().toString());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getCoordRectangle() != null) {
            txtInput.setText(mainController.getHoehenlinienConfig().getInputModel().getCoordRectangle());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getLidardatapath() != null) {
            txtLidar.setText(mainController.getHoehenlinienConfig().getInputModel().getLidardatapath().getAbsolutePath());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getLogdir() != null) {
            txtLog.setText(mainController.getHoehenlinienConfig().getInputModel().getLogdir().getAbsolutePath());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getOutput() != null) {
            txtOutput.setText(mainController.getHoehenlinienConfig().getInputModel().getOutput().getAbsolutePath());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getSmooth() != null) {
            cbSmooth.setValue(mainController.getHoehenlinienConfig().getInputModel().getSmooth());
        }
        
        if(mainController.getHoehenlinienConfig().getInputModel().getThinning() != null) {
            txtThinning.setText(mainController.getHoehenlinienConfig().getInputModel().getThinning().toString());
        }
        
        lbl3DStatus.setText("");
        lblAequiStatus.setText("");
        lblInputStatus.setText("");
        lblLidarStatus.setText("");
        lblLogStatus.setText("");
        lblOrderCodeStatus.setText("");
        lblOutputStatus.setText("");
        lblSmoothStatus.setText("");
        lblThinningStatus.setText("");
        
        mainController.addInterrogationPic(lbl3DStatus);
        mainController.addInterrogationPic(lblAequiStatus);
        mainController.addInterrogationPic(lblInputStatus);
        mainController.addInterrogationPic(lblLidarStatus);
        mainController.addInterrogationPic(lblLogStatus);
        mainController.addInterrogationPic(lblOrderCodeStatus);
        mainController.addInterrogationPic(lblOutputStatus);
        mainController.addInterrogationPic(lblSmoothStatus);
        mainController.addInterrogationPic(lblThinningStatus);
        
        //setTooltip(lbl3DStatus, "Bei gewünschter 3D-Visualisierung, bitte ankreuzen.");
        
        mainController.setTooltip(lbl3DStatus, "Bei gewünschter 3D-Visualisierung, "
                + "bitte ankreuzen.");
        mainController.setTooltip(lblAequiStatus, "Zahl von 0 bis 100 (in Metern zu sehen)");
        mainController.setTooltip(lblInputStatus, "Eingabe im Format: "
                + "xmin, ymin, xmax, ymax (maximal 3 Nachkommastellen)");
        mainController.setTooltip(lblLidarStatus, "Gültiger Pfad zu LiDAR DTM Daten");
        mainController.setTooltip(lblLogStatus, "Gültiger Pfad zur Logdatei");
        mainController.setTooltip(lblOrderCodeStatus, "Zahl eingeben");
        mainController.setTooltip(lblOutputStatus, "Gültiger Pfad zum Ausgabe-Verzeichnis");
        mainController.setTooltip(lblSmoothStatus, "Bei Bedarf Smooth-Algorithmus auswählen");
        mainController.setTooltip(lblThinningStatus, "Zahl eingeben (in Metern zu sehen)"
                + "; 0 heisst keine Ausdünnung");
        
        numTool = new NumberTools();
        formatTool = new FormatTools();
        inputModel = new InputModel();
    }

    /**
     * Methode zum Anzeigen des nächsten Fensters.
     * Alle Werte werden kontrolliert und anschliessend in das zentrale 
     * HohenlinienConfig-Objekt gespeichert. Anschliessend werden alle Eingaben 
     * in das Logfile geschrieben und das Pfad-Konfigurations-Fenster wird angezeigt.
     * @param event Click-Event
     */
    @FXML
    private void onNextWindow(MouseEvent event) {
        if(this.checkValues()) {
            inputModel.setAequidistance(numTool.convertToInteger(txtAequi.getText()));
            inputModel.setCoordRectangle(txtInput.getText());
            inputModel.setForce3D(ck3D.isSelected());
            inputModel.setLidardatapath(new File(txtLidar.getText()));
            inputModel.setLogdir(new File(txtLog.getText()));
            inputModel.setOrderNumber(numTool.convertToInteger(txtOrdercode.getText()));
            inputModel.setOutput(new File(txtOutput.getText()));
            inputModel.setSmooth(cbSmooth.getValue());
            inputModel.setThinning(numTool.convertToInteger(txtThinning.getText()));
            
            mainController.addHoehenlinienConfig(GuiEnum.INPUT, inputModel);
            
            //Log Block darf erst Nach dem Methodenaufruf geschehen: mainController.addHoehenlinienConfig(GuiEnum.INPUT, inputModel);
            //mainController.getStandaloneLogger().writeLog("Beginn Logfile");
            //mainController.getStandaloneLogger().writeLog("-------------------------");
            //mainController.getStandaloneLogger().writeLog("");
            //mainController.getStandaloneLogger().writeLog("Steuerungsparameter: ");
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUTTITLE + "Steuerungsparameter");
            //mainController.getStandaloneLogger().writeLog("-------------------------");
            //mainController.getStandaloneLogger().writeLog("");
            //mainController.getStandaloneLogger().writeLog("Äquidistanz: " + txtAequi.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Äquidistanz;" + txtAequi.getText());
//            mainController.getStandaloneLogger().writeLog("Koordinatneausschnitt: " 
//                    + txtInput.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Koordinatneausschnitt;" 
                    + txtInput.getText());
//            mainController.getStandaloneLogger().writeLog("Bestellnummer: " 
//                    + txtOrdercode.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Bestellnummer;" 
                    + txtOrdercode.getText());
            //mainController.getStandaloneLogger().writeLog("Output-Pfad: " + txtOutput.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Output-Pfad;" + txtOutput.getText());
//            mainController.getStandaloneLogger().writeLog("LiDAR-Datenpfad: " 
//                    + txtLidar.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "LiDAR-Datenpfad;" 
                    + txtLidar.getText());
//            mainController.getStandaloneLogger().writeLog("Smooth-Alogrithmus: " 
//                    + cbSmooth.getValue());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Smooth-Alogrithmus;" 
                    + cbSmooth.getValue());
//            mainController.getStandaloneLogger().writeLog("Ausdünnung: " + txtThinning.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Ausdünnung;" + txtThinning.getText());
//            mainController.getStandaloneLogger().writeLog("3D-Visualisierung: " 
//                    + ck3D.isSelected());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "3D-Visualisierung;" 
                    + ck3D.isSelected());
//            mainController.getStandaloneLogger().writeLog("Logfile-Pfad: " 
//                    + txtLog.getText());
            mainController.getStandaloneLogger().writeLog(LogPrefix.LOGUSERINPUT + "Logfile-Pfad;" 
                    + txtLog.getText());
            //mainController.getStandaloneLogger().writeLog("");
            
            mainController.showWindow(GuiEnum.PATH);
        }
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des Ausgabe-Pfades aufruft.
     * @param event Click-Event
     */
    @FXML
    private void onOutputChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Ausgabe-Pfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtOutput.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void onSmoothChange(InputMethodEvent event) {
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des LiDAR-Datenpfades aufruft.
     * @param event Click-Event
     */ 
    @FXML
    private void onLidarChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("LiDAR-Datenpfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtLidar.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des Logdatei-Pfades aufruft.
     * @param event Click-Event
     */
    @FXML
    private void onLogChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Logdatei-Pfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtLog.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Methode, welche das Programm abbricht
     * @param event Click-Event
     */
    @FXML
    private void onCancel(MouseEvent event) {
        System.exit(0);
    }
    
    /**
     * Methode, welche überprüft, ob alle Eingabeparameter korrekt sind.
     * Alle Eingabeparameter werden überprüft. Falls ein Fehler bemerkt wurde, 
     * wird das Fragezeichen-Icon im Fenster in ein Ausrufezeichen-Icon umgewandelt, 
     * um dem Benutzer mitzuteilen, dass etwas schief gelaufen ist.
     * @return Validierungsstatus
     */
    private boolean checkValues(){
        boolean error = false;
        this.resetStatusColors();
        
        if(txtOrdercode == null || !numTool.isNumeric(txtOrdercode.getText())) {
            error = true;
            //this.changeStatus(lblOrderCodeStatus);
            mainController.addExclamationPic(lblOrderCodeStatus);
        } else {
            mainController.addInterrogationPic(lblOrderCodeStatus);
        }
        
        if(txtInput == null || !formatTool.isInputRectangle(txtInput.getText())) {
            error = true;
            //this.changeStatus(lblInputStatus);
            mainController.addExclamationPic(lblInputStatus);
        } else {
            mainController.addInterrogationPic(lblInputStatus);
        }
        
        File output = new File(txtOutput.getText());
        
        if(txtOutput == null || !output.isDirectory()) {
            error = true;
            //this.changeStatus(lblOutputStatus);
            mainController.addExclamationPic(lblOutputStatus);
        } else {
            mainController.addInterrogationPic(lblOutputStatus);
        }
        
        if(txtAequi == null || !numTool.isNumeric(txtAequi.getText())) {
            error = true;
            //this.changeStatus(lblAequiStatus);
            mainController.addExclamationPic(lblAequiStatus);
        } else {
            int aequi = numTool.convertToInteger(txtAequi.getText());
            
            if(aequi <= 0 || aequi > 100) {
                error = true;
                //this.changeStatus(lblAequiStatus);
                mainController.addExclamationPic(lblAequiStatus);
            } else {
                mainController.addInterrogationPic(lblAequiStatus);
            }
        }
        
        if(txtThinning == null || !numTool.isNumeric(txtThinning.getText())) {
            error = true;
            //this.changeStatus(lblThinningStatus);
            mainController.addExclamationPic(lblThinningStatus);
        } else {
            mainController.addInterrogationPic(lblThinningStatus);
        }
        
        File lidar = new File(txtLidar.getText());
        
        if(txtLidar == null || !lidar.isDirectory()) {
            error = true;
            //this.changeStatus(lblLidarStatus);
            mainController.addExclamationPic(lblLidarStatus);
        } else {
            mainController.addInterrogationPic(lblLidarStatus);
        }
        
        File log = new File(txtLog.getText());
        
        if(txtLog == null || !log.isDirectory()) {
            error = true;
            //this.changeStatus(lblLogStatus);
            mainController.addExclamationPic(lblLogStatus);
        } else {
            mainController.addInterrogationPic(lblLogStatus);
        }
        
        if (error) {
            lblStatus.setText("Bitte korrigieren Sie die rot markierten Felder!");
            lblStatus.setVisible(true);
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Methode, welches die Farbe eines Labels in Rot ändert.
     * @param lbl Label
     */
    private void changeStatus(Label lbl) {
        lbl.setTextFill(Color.web("#FF0101"));
    }
    
    /**
     * Methode zum die Fehlermeldung im Kopfbereich verschwinden zu lassen.
     */
    private void resetStatusColors() {
        lblStatus.setVisible(false);
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Bestellnummer-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onOrderCodeHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblOrderCodeStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Bestellnummer-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onOrderCodeExit(MouseEvent event) {
        lblOrderCodeStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Eingabe-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onInputHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblInputStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Eingabe-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onInputExit(MouseEvent event) {
        lblInputStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Ausgabe-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onOutputHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblOutputStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Ausgabe-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onOutputExit(MouseEvent event) {
        lblOutputStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Äquidistanz-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onAequiHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblAequiStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Äquidistanz-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onAequiExit(MouseEvent event) {
        lblAequiStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Smooth-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onSmoothHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblSmoothStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Smooth-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onSmoothExit(MouseEvent event) {
        lblSmoothStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Ausdünnen-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onThinningHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblThinningStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Ausdünnen-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onThinningExit(MouseEvent event) {
        lblThinningStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den 3D-Visualisierungs-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void on3DHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lbl3DStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den 3D-Visualisierungs-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void on3DExit(MouseEvent event) {
        lbl3DStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den LiDAR-Daten-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onLidarHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblLidarStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den LiDAR-Daten-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onLidarExit(MouseEvent event) {
        lblLidarStatus.getTooltip().hide();
    }

    /**
     * Anzeige des Hilfs-Tooltips für den Logfile-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onLogHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblLogStatus);
    }

    /**
     * Verstecken des Hilfs-Tooltips für den Logfile-Infoknopf
     * @param event Mouseover-Event
     */
    @FXML
    private void onLogExit(MouseEvent event) {
        lblLogStatus.getTooltip().hide();
    }
}
