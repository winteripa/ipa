/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import base.DisplayMethods;
import bo.GuiEnum;
import bo.HoehenlinienConfig;
import bo.IMODEL;
import bo.InputModel;
import bo.LogPrefix;
import bo.PathModel;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tools.StandaloneLogger;

/**
 * Hauptcontroller-Klasse
 *
 * @author u203011
 */
public class MainController {
    
    private static MainController mainController = null;
    private static Stage stage = null;
    private static HoehenlinienConfig hConfig = null;
    private static DisplayMethods logger = null;
    
    /**
     * Private-deklarierter Konstruktor für das Singleton-Pattern
     */
    private MainController(){
    }
    
    /**
     * Methode zum Erhalt eines eindeutigen Hauptcontrollers
     * @param stage Hauptstage
     * @return Hauptcontroller
     */
    public static MainController getMainController(Stage stage){
        if(mainController == null) {
            mainController = new MainController();
            mainController.stage = stage;
            hConfig = new HoehenlinienConfig();
            logger = new StandaloneLogger();
        }
        
        return mainController;
    }
    
    /**
     * Methode zum Austausch der GUIs
     * @param windowToShow Fenster, welches gezeigt werden soll
     */
    public void showWindow(GuiEnum windowToShow) {
        try {
            Parent root = null;
            
            if(windowToShow.equals(GuiEnum.INPUT)) {
                root = FXMLLoader.load(getClass().getResource("/gui/view/InputGUI.fxml"));
            } else if (windowToShow.equals(GuiEnum.PATH)) {
                root = FXMLLoader.load(getClass().getResource("/gui/view/PathGUI.fxml"));
            } else if (windowToShow.equals(GuiEnum.STATUS)) {
                root = FXMLLoader.load(getClass().getResource("/gui/view/StatusGUI.fxml"));
            } else {
                //Log Error in Logfile
            }
            
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
        } catch(IOException e) {
            //Log exception
        }
    }
    
    /**
     * Getter-Methode für die Hauptstage
     * @return Hauptstage
     */
    public Stage getPrimaryStage() {
        return stage;
    }
    
    /**
     * Methode zum Hinzufügen der Konfigurations-Models zur Höhenlinienkonfiguration
     * @param currWindow Derzeit angezeigtes Fenster
     * @param model Model, welches hinzugefügt werden soll
     */
    public void addHoehenlinienConfig(GuiEnum currWindow, IMODEL model){
        if(currWindow.equals(GuiEnum.INPUT)) {
            hConfig.setInputModel((InputModel) model);
            initializeLogger((InputModel) model);
        } else if (currWindow.equals(GuiEnum.PATH)) {
            hConfig.setPathModel((PathModel) model);
        }
    }
    
    /**
     * Getter-Methode für die Höhenlinien-Konfiguration
     * @return Höhenlinien-Konfiguration
     */
    public HoehenlinienConfig getHoehenlinienConfig() {
        return hConfig;
    }
    
    /**
     * Getter-Methode für den Standalone-Logger
     * @return Standalone-Logger
     */
    public DisplayMethods getStandaloneLogger() {
        return logger;
    }
    
    protected void addInterrogationPic(Label lbl) {
        lbl.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/interrogation.png"))));
    }
    
    protected void addExclamationPic(Label lbl) {
        lbl.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/attention.png"))));
    }
    
    protected void setTooltip(Label lbl, String text) {
        Tooltip t = new Tooltip();
        t.setText(text);
        lbl.setTooltip(t);
    }
    
    protected void showTooltipOnPosition(Label lbl) {
        Point2D p = lbl.localToScene(0.0, 0.0);
        lbl.getTooltip().show(lbl,
            p.getX() + lbl.getScene().getX() + lbl.getScene().getWindow().getX(),
            p.getY() + lbl.getScene().getY() + lbl.getScene().getWindow().getY() + 20);
    }
    
    private void initializeLogger(InputModel model) {
        ((StandaloneLogger) logger).setOrderNumber(model.getOrderNumber());
        ((StandaloneLogger) logger).setLogPath(model.getLogdir().getAbsolutePath() + "\\");
        logger.writeLog(LogPrefix.LOGSTART);
    }
    
    public void setLoggerStatusGui(StatusController statusGui) {
        ((StandaloneLogger) logger).setStatusGui(statusGui);
    }
}
