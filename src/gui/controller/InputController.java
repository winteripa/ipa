/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import bo.GuiEnum;
import bo.InputModel;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
     * Initialisierungsmethode für den InputController
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mainController = MainController.getMainController(null);
        
        cbSmooth.getItems().clear();
        cbSmooth.getItems().add("Chaiken");
        cbSmooth.getItems().add("Hermite");
        cbSmooth.getItems().add("Boyle");
        
        mainTitle.setText("Höhenlinien bestellen");
        subTitle.setText("Steuerungsparameter eingeben");
        lblStatus.setVisible(false);
        
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
     * Methode, welche das nächste Fenster anzeigen soll und die eingegebenen 
     * Werte kontrolliert und anschliessend der Höhenlinienkonfiguration hinzufügt.
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
     * Methode, welches die Farbe eines Labels in Rot ändert
     * @param lbl Label
     */
    private void changeStatus(Label lbl) {
        lbl.setTextFill(Color.web("#FF0101"));
    }
    
    /**
     * Methode zum Zurücksetzen der Label-Farben
     */
    private void resetStatusColors() {
        /*lbl3DStatus.setTextFill(Color.web("#000000"));
        lblAequiStatus.setTextFill(Color.web("#000000"));
        lblInputStatus.setTextFill(Color.web("#000000"));
        lblLidarStatus.setTextFill(Color.web("#000000"));
        lblLogStatus.setTextFill(Color.web("#000000"));
        lblOrderCodeStatus.setTextFill(Color.web("#000000"));
        lblOutputStatus.setTextFill(Color.web("#000000"));
        lblSmoothStatus.setTextFill(Color.web("#000000"));
        lblThinningStatus.setTextFill(Color.web("#000000"));*/
        lblStatus.setVisible(false);
    }
    
    private void addPic(Label lbl) {
        lbl.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/interrogation.png"))));
    }
    
    private void setTooltip(Label lbl, String text) {
        Tooltip t = new Tooltip();
        t.setText(text);
        lbl.setTooltip(t);
    }
    
    private void showTooltipOnPosition(Label lbl) {
        Point2D p = lbl.localToScene(0.0, 0.0);
        lbl.getTooltip().show(lbl,
        p.getX() + lbl.getScene().getX() + lbl.getScene().getWindow().getX(),
        p.getY() + lbl.getScene().getY() + lbl.getScene().getWindow().getY() + 10);
    }

    @FXML
    private void onOrderCodeHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblOrderCodeStatus);
    }

    @FXML
    private void onOrderCodeExit(MouseEvent event) {
        lblOrderCodeStatus.getTooltip().hide();
    }

    @FXML
    private void onInputHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblInputStatus);
    }

    @FXML
    private void onInputExit(MouseEvent event) {
        lblInputStatus.getTooltip().hide();
    }

    @FXML
    private void onOutputHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblOutputStatus);
    }

    @FXML
    private void onOutputExit(MouseEvent event) {
        lblOutputStatus.getTooltip().hide();
    }

    @FXML
    private void onAequiHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblAequiStatus);
    }

    @FXML
    private void onAequiExit(MouseEvent event) {
        lblAequiStatus.getTooltip().hide();
    }

    @FXML
    private void onSmoothHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblSmoothStatus);
    }

    @FXML
    private void onSmoothExit(MouseEvent event) {
        lblSmoothStatus.getTooltip().hide();
    }

    @FXML
    private void onThinningHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblThinningStatus);
    }

    @FXML
    private void onThinningExit(MouseEvent event) {
        lblThinningStatus.getTooltip().hide();
    }

    @FXML
    private void on3DHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lbl3DStatus);
    }

    @FXML
    private void on3DExit(MouseEvent event) {
        lbl3DStatus.getTooltip().hide();
    }

    @FXML
    private void onLidarHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblLidarStatus);
    }

    @FXML
    private void onLidarExit(MouseEvent event) {
        lblLidarStatus.getTooltip().hide();
    }

    @FXML
    private void onLogHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblLogStatus);
    }

    @FXML
    private void onLogExit(MouseEvent event) {
        lblLogStatus.getTooltip().hide();
    }
}
