/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import bo.Configuration;
import bo.GuiEnum;
import bo.PathModel;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tools.FileTools;

/**
 * Controller-Klasse für das PathGUI
 *
 * @author u203011
 */
public class PathController implements Initializable {
    @FXML
    private Button btnNext;
    @FXML
    private Label mainTitle;
    @FXML
    private Label subTitle;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblConfig;
    @FXML
    private Label lblConfigName;
    @FXML
    private Label lblPython;
    @FXML
    private Label lblGdal;
    @FXML
    private ComboBox<Configuration> cbConfig;
    @FXML
    private Button btnConfigNew;
    @FXML
    private TextField txtConfigName;
    @FXML
    private TextField txtPython;
    @FXML
    private TextField txtGdal;
    @FXML
    private Label lblConfigStatus;
    @FXML
    private Label lblConfigNameStatus;
    @FXML
    private Label lblPythonStatus;
    @FXML
    private Label lblGdalStatus;
    @FXML
    private Label lblGrass;
    @FXML
    private TextField txtGrass;
    @FXML
    private Label lblGrassStatus;
    @FXML
    private Label lblGrassBin;
    @FXML
    private TextField txtGrassBin;
    @FXML
    private Label lblGrassBinStatus;
    @FXML
    private Button btnPython;
    @FXML
    private Button btnGdal;
    @FXML
    private Button btnGrass;
    @FXML
    private Button btnGrassBin;
    @FXML
    private Label lblStatus;
    
    private MainController mainController;
    private FileTools fileTool;
    private Document configXML;
    private String xmlpath;
    private ObservableList<Configuration> xmlConfigs;
    private boolean newConfig;
    @FXML
    private Button btnBack;
    
    /**
     * Initialisierungsmethode des PathControllers. Das Konfigurations-XML wird 
     * eingelesen und die Elemente werden der Combobox hinzugefügt.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mainController = MainController.getMainController(null);
        
        cbConfig.getItems().clear();
        
        mainTitle.setText("Höhenlinien bestellen");
        subTitle.setText("Konfiguration bestimmen");
        lblStatus.setVisible(false);
        txtConfigName.setDisable(true);
        txtGdal.setDisable(true);
        txtGrass.setDisable(true);
        txtGrassBin.setDisable(true);
        txtPython.setDisable(true);
        
        lblConfigNameStatus.setText("");
        lblConfigStatus.setText("");
        lblPythonStatus.setText("");
        lblGdalStatus.setText("");
        lblGrassStatus.setText("");
        lblGrassBinStatus.setText("");
        
        mainController.addInterrogationPic(lblConfigNameStatus);
        mainController.addInterrogationPic(lblConfigStatus);
        mainController.addInterrogationPic(lblPythonStatus);
        mainController.addInterrogationPic(lblGdalStatus);
        mainController.addInterrogationPic(lblGrassStatus);
        mainController.addInterrogationPic(lblGrassBinStatus);
        
        mainController.setTooltip(lblConfigStatus, "Bitte wählen Sie eine "
                + "Konfiguration aus oder erstellen Sie mittels 'Neu' eine Neue");
        mainController.setTooltip(lblConfigNameStatus, "Bitte geben Sie einen "
                + "eindeutigen Konfigurationsnamen an");
        mainController.setTooltip(lblPythonStatus, "Pfad zur Python-Installation");
        mainController.setTooltip(lblGdalStatus, "Pfad zur GDAL-Installation");
        mainController.setTooltip(lblGrassStatus, "Pfad zur GRASS-Installation");
        mainController.setTooltip(lblGrassBinStatus, "Pfad zum GRASS-BIN Bibliotheksverzeichnis");
        
        fileTool = new FileTools();
        xmlConfigs = FXCollections.observableArrayList();
        newConfig = false;
        xmlpath = System.getProperty("user.dir") + "\\config\\config.xml";
        System.out.println(xmlpath);
        configXML = fileTool.parseXMLFile(xmlpath);
        readConfigXML();
        //System.getProperty("user.dir")
    }    

    /**
     * Methode, welches das nächste Fenster anzeigen soll. Alle Konfigurationspfade 
     * werden kontrolliert und der Höhenlinienkonfiguration hinzugefügt.
     * @param event Click-Event
     */
    @FXML
    private void onNextWindow(MouseEvent event) {
        if(checkValues()) {
            if(newConfig) {
                saveNewConfig();
            } else {
                saveModifiedConfig();
            }
            
            PathModel pathModel = new PathModel();
            pathModel.setGdalpath(new File(txtGdal.getText()));
            pathModel.setGrassbinpath(new File(txtGrassBin.getText()));
            pathModel.setGrasspath(new File(txtGrass.getText()));
            pathModel.setPythonpath(new File(txtPython.getText()));
            
            mainController.addHoehenlinienConfig(GuiEnum.PATH, pathModel);
            
            mainController.getStandaloneLogger().writeLog("Konfigurationspfad: ");
            mainController.getStandaloneLogger().writeLog("-------------------------");
            mainController.getStandaloneLogger().writeLog("");
            mainController.getStandaloneLogger().writeLog("Konfigurationsname: " 
                    + txtConfigName.getText());
            mainController.getStandaloneLogger().writeLog("GDAL-Pfad: " + txtGdal.getText());
            mainController.getStandaloneLogger().writeLog("GRASS-Pfad: " + txtGrass.getText());
            mainController.getStandaloneLogger().writeLog("GRASS-BIN-Pfad: " + txtGrassBin.getText());
            mainController.getStandaloneLogger().writeLog("Python-Pfad: " + txtPython.getText());
            mainController.getStandaloneLogger().writeLog("");
            
            mainController.showWindow(GuiEnum.STATUS);
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
     * Methode, welche ermöglicht eine neue Konfiguration zu erfassen.
     * @param event Click-Event
     */
    @FXML
    private void onConfigNew(MouseEvent event) {
        newConfig = true;
        
        txtConfigName.clear();
        txtGdal.clear();
        txtGrass.clear();
        txtGrassBin.clear();
        txtPython.clear();
        
        txtConfigName.setDisable(false);
        txtGdal.setDisable(false);
        txtGrass.setDisable(false);
        txtGrassBin.setDisable(false);
        txtPython.setDisable(false);
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des Python-Installationspfades aufruft.
     * @param event Click-Event
     */
    @FXML
    private void onPythonChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Python-Installationspfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtPython.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des GDAL-Installationspfades aufruft.
     * @param event Click-Event
     */
    @FXML
    private void onGdalChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("GDAL-Installationspfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtGdal.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des GRASS-Installationspfades aufruft.
     * @param event Click-Event
     */
    @FXML
    private void onGrassChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("GRASS-Installationspfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtGrass.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Methode, welche einen Dialog zum Auswählen des GRASS-BIN Bibliothekspfades aufruft.
     * @param event Click-Event
     */
    @FXML
    private void onGrassBinChoose(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("GRASS-BIN-Bibliohekspfad auswählen");
        File selectedDirectory = chooser.showDialog(mainController.getPrimaryStage());
        
        if(selectedDirectory != null) {
            txtGrassBin.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    /**
     * Methode, welche Änderungen nach der Auswahl einer neuen Konfiguration ausführt.
     * @param event Click-Event
     */
    @FXML
    private void onConfigChanged(ActionEvent event) {
        Configuration config = cbConfig.getSelectionModel().getSelectedItem();
        
        txtConfigName.setText(config.getConfigName());
        txtGdal.setText(config.getGdalpath());
        txtGrass.setText(config.getGrasspath());
        txtGrassBin.setText(config.getGrassbinpath());
        txtPython.setText(config.getPythonpath());
    }
    
    /**
     * Methode, welche das Konfiguration-XML einliest
     */
    private void readConfigXML() {
        System.out.println(configXML.getDocumentElement().getNodeName());
        System.out.println(configXML.getElementsByTagName("hoehenlinienconfig"));
        System.out.println(configXML.getElementsByTagName("hoehenlinienconfig").item(0));
        System.out.println(configXML.getElementsByTagName("hoehenlinienconfig").item(0).getFirstChild());
        NodeList nList = configXML.getElementsByTagName("config");
        
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            System.out.println(nNode);
            
            if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                System.out.println(eElement);
                
                String configname = eElement.getElementsByTagName("name").item(0).getTextContent();
                String pythonpath = eElement.getElementsByTagName("python").item(0).getTextContent();
                String gdalpath = eElement.getElementsByTagName("gdal").item(0).getTextContent();
                String grasspath = eElement.getElementsByTagName("grass").item(0).getTextContent();
                String grassbinpath = eElement.getElementsByTagName("grassbin").item(0).getTextContent();
                
                Configuration config = new Configuration();
                
                if(configname == null || configname.isEmpty()) {
                    //config.setConfigName("");
                    //Log error
                    System.err.println("gall");
                } else {
                    config.setConfigName(configname);
                }
                
                if(pythonpath == null || pythonpath.isEmpty()) {
                    config.setPythonpath("");
                } else {
                    config.setPythonpath(pythonpath);
                }
                
                if(gdalpath == null || gdalpath.isEmpty()) {
                    config.setGdalpath("");
                } else {
                    config.setGdalpath(gdalpath);
                }
                
                if(grasspath == null || grasspath.isEmpty()) {
                    config.setGrasspath("");
                } else {
                    config.setGrasspath(grasspath);
                }
                
                if(grassbinpath == null || grassbinpath.isEmpty()) {
                    config.setGrassbinpath("");
                } else {
                    config.setGrassbinpath(grassbinpath);
                }
                
                xmlConfigs.add(config);
                //cbConfig.getItems().add(config);
            }
        }
        
        cbConfig.setItems(xmlConfigs);
    }
    
    /**
     * Methode zur Überprüfung der Konfigurationspfade
     * @return Validierungsstatus
     */
    private boolean checkValues() {
        boolean error = false;
        this.resetStatusColors();
        
        if(txtConfigName == null || txtConfigName.getText().isEmpty()) {
            error = true;
            //this.changeStatus(lblConfigNameStatus);
            mainController.addExclamationPic(lblConfigNameStatus);
        } else {
            mainController.addInterrogationPic(lblConfigNameStatus);
        }
        
        File pythonpath = new File(txtPython.getText());
        
        if(txtPython == null || !pythonpath.isDirectory()) {
            error = true;
            //this.changeStatus(lblPythonStatus);
            mainController.addExclamationPic(lblPythonStatus);
        } else {
            mainController.addInterrogationPic(lblPythonStatus);
        }
        
        File gdalpath = new File(txtGdal.getText());
        
        if(txtGdal == null || !gdalpath.isDirectory()) {
            error = true;
            //this.changeStatus(lblGdalStatus);
            mainController.addExclamationPic(lblGdalStatus);
        } else {
            mainController.addInterrogationPic(lblGdalStatus);
        }
        
        File grasspath = new File(txtGrass.getText());
        
        if(txtGrass == null || !grasspath.isDirectory()) {
            error = true;
            //this.changeStatus(lblGrassStatus);
            mainController.addExclamationPic(lblGrassStatus);
        } else {
            mainController.addInterrogationPic(lblGrassStatus);
        }
        
        File grassbinpath = new File(txtGrassBin.getText());
        
        if(txtGrassBin == null || !grassbinpath.isDirectory()) {
            error = true;
            //this.changeStatus(lblGrassBinStatus);
            mainController.addExclamationPic(lblGrassBinStatus);
        } else {
            mainController.addInterrogationPic(lblGrassBinStatus);
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
        /*lblConfigStatus.setTextFill(Color.web("#000000"));
        lblConfigNameStatus.setTextFill(Color.web("#000000"));
        lblPythonStatus.setTextFill(Color.web("#000000"));
        lblGdalStatus.setTextFill(Color.web("#000000"));
        lblGrassStatus.setTextFill(Color.web("#000000"));
        lblGrassBinStatus.setTextFill(Color.web("#000000"));*/
        lblStatus.setVisible(false);
    }
    
    /**
     * Methode zur Speicherung einer neuen Konfiguration
     */
    private void saveNewConfig() {
        Element rootElement = configXML.getDocumentElement();
        
        Element eConfig = configXML.createElement("config");
        rootElement.appendChild(eConfig);
        
        Element configName = configXML.createElement("name");
        configName.appendChild(configXML.createTextNode(txtConfigName.getText()));
        eConfig.appendChild(configName);
        
        Element pythonpath = configXML.createElement("python");
        pythonpath.appendChild(configXML.createTextNode(txtPython.getText()));
        eConfig.appendChild(pythonpath);
        
        Element gdalpath = configXML.createElement("gdal");
        gdalpath.appendChild(configXML.createTextNode(txtGdal.getText()));
        eConfig.appendChild(gdalpath);
        
        Element grasspath = configXML.createElement("grass");
        grasspath.appendChild(configXML.createTextNode(txtGrass.getText()));
        eConfig.appendChild(grasspath);
        
        Element grassbinpath = configXML.createElement("grassbin");
        grassbinpath.appendChild(configXML.createTextNode(txtGrassBin.getText()));
        eConfig.appendChild(grassbinpath);
        
        rootElement.appendChild(eConfig);
        
        transformXML();
    }
    
    /**
     * Methode zur Überprüfung, ob eine vorhandene Konfiguration editiert wurde.
     * @return Modifizierungsstatus
     */
    private boolean isModifiedConfig() {
        Configuration config = cbConfig.getSelectionModel().getSelectedItem();
        int modified = 0;
        
        if(config.getConfigName().equals(txtConfigName.getText())){
            modified += 1;
        }
        
        if(config.getGdalpath().equals(txtGdal.getText())){
            modified += 1;
        }
        
        if(config.getGrassbinpath().equals(txtGrassBin.getText())){
            modified += 1;
        }
        
        if(config.getGrasspath().equals(txtGrass.getText())){
            modified += 1;
        }
        
        if(config.getPythonpath().equals(txtPython.getText())){
            modified += 1;
        }
        
        if(modified > 0) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Methode, welche eine modifizierte Konfiguration in die XML-Datei speichert.
     */
    private void saveModifiedConfig() {
        if(isModifiedConfig()) {
            //try {
                NodeList nList = configXML.getElementsByTagName("config");

                for (int i = 0; i < nList.getLength(); i++)  {
                    Node nNode = nList.item(i);

                    if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        String configName = eElement.getElementsByTagName("name").item(0).getTextContent();

                        if(configName.equals(txtConfigName.getText())) {
                            /*eElement.setAttribute("python", txtPython.getText());
                            eElement.setAttribute("gdal", txtGdal.getText());
                            eElement.setAttribute("grass", txtGrass.getText());
                            eElement.setAttribute("grassbin", txtGrassBin.getText());*/
                            eElement.getElementsByTagName("python").item(0).setTextContent(txtPython.getText());
                            eElement.getElementsByTagName("gdal").item(0).setTextContent(txtGdal.getText());
                            eElement.getElementsByTagName("grass").item(0).setTextContent(txtGrass.getText());
                            eElement.getElementsByTagName("grassbin").item(0).setTextContent(txtGrassBin.getText());
                        }
                    }
                }

                /*TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                DOMSource source = new DOMSource(configXML);
                StreamResult result = new StreamResult(new File(xmlpath));
                transformer.transform(source, result);*/
                transformXML();
            /*} catch(TransformerException e) {
                //Log error
            } catch(TransformerFactoryConfigurationError e){
                //Log error
            }*/
        }
    }
    
    /**
     * Methode, welche eine XML-Datei nach dem Editieren transformiert
     */
    private void transformXML() {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(configXML);
            StreamResult result = new StreamResult(new File(xmlpath));
            transformer.transform(source, result);
        } catch(TransformerException ex) {
            //log error
        } catch (TransformerFactoryConfigurationError ex) {
            //Log error
        }
    }

    @FXML
    private void onConfigHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblConfigStatus);
    }

    @FXML
    private void onConfigExit(MouseEvent event) {
        lblConfigStatus.getTooltip().hide();
    }

    @FXML
    private void onConfigNameHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblConfigNameStatus);
    }

    @FXML
    private void onConfigNameExit(MouseEvent event) {
        lblConfigNameStatus.getTooltip().hide();
    }

    @FXML
    private void onPythonHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblPythonStatus);
    }

    @FXML
    private void onPythonExit(MouseEvent event) {
        lblPythonStatus.getTooltip().hide();
    }

    @FXML
    private void onGdalHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblGdalStatus);
    }

    @FXML
    private void onGdalExit(MouseEvent event) {
        lblGdalStatus.getTooltip().hide();
    }

    @FXML
    private void onGrassHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblGrassStatus);
    }

    @FXML
    private void onGrassExit(MouseEvent event) {
        lblGrassStatus.getTooltip().hide();
    }

    @FXML
    private void onGrassBinHover(MouseEvent event) {
        mainController.showTooltipOnPosition(lblGrassBinStatus);
    }

    @FXML
    private void onGrassBinExit(MouseEvent event) {
        lblGrassBinStatus.getTooltip().hide();
    }

    @FXML
    private void onBack(MouseEvent event) {
        mainController.showWindow(GuiEnum.INPUT);
    }
}
