/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import bl.HoehenlinienManagement;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
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
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mainController = MainController.getMainController(null);
        
        hm = new HoehenlinienManagement(mainController.getHoehenlinienConfig(), this);
        
        mainTitle.setText("Höhenlinien bestellen");
        subTitle.setText("Höhenlinien erstellen");
        lblStatus.setVisible(false);
        btnFinish.setDisable(true);
    }    

    @FXML
    private void onFinish(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void onCancel(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void onOrder(MouseEvent event) {
        Thread th = new Thread(hm);
        th.start();
    }

    public Button getBtnFinish() {
        return btnFinish;
    }

    public Label getLblStatus() {
        return lblStatus;
    }

    public Label getLblAppProgress() {
        return lblAppProgress;
    }

    public TextArea getTxtStatus() {
        return txtStatus;
    }

    public Button getBtnOrder() {
        return btnOrder;
    }
}
