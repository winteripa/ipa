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
 * Hauptcontroller-Klasse.
 * Zentrale Stelle für Interaktion zwischen den einzelnen GUI-Controllern.
 * @author u203011
 */
public class MainController {
    
    private static MainController mainController = null;
    private static Stage stage = null;
    /** Zentrale Höhenlinienkonfiguration */
    private static HoehenlinienConfig hConfig = null;
    /** Zentrale Loggerklasse der Standalone-Version */
    private static DisplayMethods logger = null;
    
    /**
     * Private-deklarierter Konstruktor für das Singleton-Pattern.
     * Nur ein Objekt des MainControllers darf vorhanden sein. Darum darf 
     * keine Objekt von aussen erstellt werden.
     */
    private MainController(){
    }
    
    /**
     * Methode zum Erhalt eines eindeutigen Hauptcontrollers.
     * Ein einzigartiges MainController-Objekt wird erstellt und zurückgegeben.
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
     * Methode zum Austausch der GUIs.
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
     * Methode zum Hinzufügen der Konfigurations-Models zur Höhenlinienkonfiguration.
     * Hinzufügen der eingegebenen Steuerungsparameter oder der Pfad-Konfiguration 
     * zur zentralen Höhenlinien-Konfiguration.
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
     * Getter-Methode für die Höhenlinien-Konfiguration.
     * @return Höhenlinien-Konfiguration
     */
    public HoehenlinienConfig getHoehenlinienConfig() {
        return hConfig;
    }
    
    /**
     * Getter-Methode für den Standalone-Logger.
     * @return Standalone-Logger
     */
    public DisplayMethods getStandaloneLogger() {
        return logger;
    }
    
    /**
     * Methode für das Hinzufügen eines Fragezeichen-Icons für ein Label.
     * @param lbl Label ohne Fragezeichen-Icon
     */
    protected void addInterrogationPic(Label lbl) {
        lbl.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/interrogation.png"))));
    }
    
    /**
     * Methode für das Hinzufügen eines Ausrufezeichen-Icons für ein Label.
     * @param lbl Label ohne Ausrufezeichen-Icon
     */
    protected void addExclamationPic(Label lbl) {
        lbl.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/attention.png"))));
    }
    
    /**
     * Methode, welche einem Label einen Tooltip mit wählbarem Text hinzufügt.
     * @param lbl Label ohne Tooltip
     * @param text Tooltip-Text
     */
    protected void setTooltip(Label lbl, String text) {
        Tooltip t = new Tooltip();
        t.setText(text);
        lbl.setTooltip(t);
    }
    
    /**
     * Methode um den Tooltip eines Label an der richtigen Position anzuzeigen.
     * @param lbl Label mit Tooltip
     */
    protected void showTooltipOnPosition(Label lbl) {
        Point2D p = lbl.localToScene(0.0, 0.0);
        lbl.getTooltip().show(lbl,
            p.getX() + lbl.getScene().getX() + lbl.getScene().getWindow().getX(),
            p.getY() + lbl.getScene().getY() + lbl.getScene().getWindow().getY() + 20);
    }
    
    /**
     * Methode, welche dem Logger-Klassen-Objekt wichtige Daten übergibt.
     * Initialisierung der Standalone-Loggerklasse, damit die Gui-Controller 
     * Meldungen in das Logfile schreiben können.
     * @param model Model mit Steuerungsparameter
     */
    private void initializeLogger(InputModel model) {
        ((StandaloneLogger) logger).setOrderNumber(model.getOrderNumber());
        ((StandaloneLogger) logger).setLogPath(model.getLogdir().getAbsolutePath() + "\\");
        logger.writeLog(LogPrefix.LOGSTART);
    }
    
    /**
     * Methode zum Setzen des Status-GUIs im Logger-Klassen-Objekt
     * um Nachrichten darauf auszugeben.
     * Vor der Bestellung muss die Applikation wissen, in welchem Fenster sie 
     * die Statusmeldungen für den Benutzer anzeigen kann. Dafür wird der 
     * Standalone-Loggerklasse ein Objekt des StatusControllers übergeben.
     * @param statusGui Status-GUI
     */
    public void setLoggerStatusGui(StatusController statusGui) {
        ((StandaloneLogger) logger).setStatusGui(statusGui);
    }
}
