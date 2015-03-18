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
 * FXML Controller class
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
     * Initializes the controller class.
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
        
        numTool = new NumberTools();
        formatTool = new FormatTools();
        inputModel = new InputModel();
    }

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

    @FXML
    private void onLidarChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("LiDAR-Datenpfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtLidar.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void onLogChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Logdatei-Pfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtLog.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void onCancel(MouseEvent event) {
        System.exit(0);
    }
    
    private boolean checkValues(){
        boolean error = false;
        this.resetStatusColors();
        
        if(txtOrdercode == null || !numTool.isNumeric(txtOrdercode.getText())) {
            error = true;
            this.changeStatus(lblOrderCodeStatus);
        }
        
        if(txtInput == null || !formatTool.isInputRectangle(txtInput.getText())) {
            error = true;
            this.changeStatus(lblInputStatus);
        }
        
        File output = new File(txtOutput.getText());
        
        if(txtOutput == null || !output.isDirectory()) {
            error = true;
            this.changeStatus(lblOutputStatus);
        }
        
        if(txtAequi == null || !numTool.isNumeric(txtAequi.getText())) {
            error = true;
            this.changeStatus(lblAequiStatus);
        } else {
            int aequi = numTool.convertToInteger(txtAequi.getText());
            
            if(aequi <= 0 || aequi > 100) {
                error = true;
                this.changeStatus(lblAequiStatus);
            }
        }
        
        if(txtThinning == null || !numTool.isNumeric(txtThinning.getText())) {
            error = true;
            this.changeStatus(lblThinningStatus);
        }
        
        File lidar = new File(txtLidar.getText());
        
        if(txtLidar == null || !lidar.isDirectory()) {
            error = true;
            this.changeStatus(lblLidarStatus);
        }
        
        File log = new File(txtLog.getText());
        
        if(txtLog == null || !log.isDirectory()) {
            error = true;
            this.changeStatus(lblLogStatus);
        }
        
        if (error) {
            lblStatus.setText("Bitte korrigieren Sie die rot markierten Felder!");
            lblStatus.setVisible(true);
            
            return false;
        }
        
        return true;
    }
    
    private void changeStatus(Label lbl) {
        lbl.setTextFill(Color.web("#FF0101"));
    }
    
    private void resetStatusColors() {
        lbl3DStatus.setTextFill(Color.web("#000000"));
        lblAequiStatus.setTextFill(Color.web("#000000"));
        lblInputStatus.setTextFill(Color.web("#000000"));
        lblLidarStatus.setTextFill(Color.web("#000000"));
        lblLogStatus.setTextFill(Color.web("#000000"));
        lblOrderCodeStatus.setTextFill(Color.web("#000000"));
        lblOutputStatus.setTextFill(Color.web("#000000"));
        lblSmoothStatus.setTextFill(Color.web("#000000"));
        lblThinningStatus.setTextFill(Color.web("#000000"));
        lblStatus.setVisible(false);
    }
}
