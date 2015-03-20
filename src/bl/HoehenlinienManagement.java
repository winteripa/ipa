/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bl;

import base.DisplayMethods;
import bo.DBFactory;
import bo.HoehenlinienConfig;
import dal.IDAL;
import gui.controller.StatusController;
import java.util.ArrayList;
import javafx.application.Platform;
import module.Startclass;
import tools.FormatTools;

/**
 * Logikklasse
 * @author u203011
 */
public class HoehenlinienManagement implements Runnable {
    
    private HoehenlinienConfig hConfig = null;
    private IDAL dbdal = null;
    private StatusController statusGui = null;
    private String wktRectangle = null;
    private FormatTools formatTool = null;
    private ArrayList<String> arrKachelnr;
    private DisplayMethods logger = null;
    private Startclass moduleStarter = null;
    private DBFactory factory = null;

    /**
     * Konstruktor der Logikklasse
     * @param hConfig Höhenlinien-Konfigurationsobjekt
     * @param statusGui Objekt des StatusGUI-Controllers
     * @param logger Objekt eines DisplayMethods-Logger
     */
    public HoehenlinienManagement(HoehenlinienConfig hConfig, 
            StatusController statusGui, DisplayMethods logger) {
        this.hConfig = hConfig;
        this.statusGui = statusGui;
        this.logger = logger;
        this.factory = new DBFactory();
        formatTool = new FormatTools();
        this.moduleStarter = new Startclass();
        
        /*((StandaloneLogger) this.logger).setOrderNumber(hConfig.getInputModel().getOrderNumber());
        ((StandaloneLogger) this.logger).setLogPath(hConfig.getInputModel().getLogdir().getAbsolutePath());*/
    }
    
    /**
     * Startmethode für den Thread
     */
    @Override
    public void run() {
        this.statusGui.getBtnOrder().setDisable(true);
        //this.statusGui.setProgress(-1);
        
        dbdal = this.factory.getDAL(this.logger);
        
        this.logger.writeLog("Bestellvorgang ist gestartet worden.");
        this.logger.writeLog("");
        
        this.logger.writeStatus("Start des Bestellvorgangs");
        
        convertInputRectangleToWKT();
        
        if(getKachelnummer()) {
            for (String string : arrKachelnr) {
            System.out.println(string);
            }

            if(moduleStarter.startModuleStandalone(hConfig, arrKachelnr, logger)) {
                this.logger.writeLog("Bestellvorgang erfolgreich abgeschlossen");
                this.logger.writeStatus("Bestellvorgang erfolgreich");
            } else {
                this.logger.writeLog("Bestellvorgang nicht erfolgreich abgeschlossen");
                this.logger.writeErrorStatus("Bestellvorgang nicht erfolgreich");
                
                this.statusGui.getLblStatus().setText("Fehler beim Bestellen. Bitte Logfile kontrollieren.");
                this.statusGui.getLblStatus().setVisible(true);
            }
        } else {
            this.logger.writeErrorStatus("Kachelnummern konnten nicht bezogen werden. "
                    + "Bitte kontrollieren Sie dass Logfile.");
            
            this.statusGui.getLblStatus().setText("Fehler beim Bestellen. Bitte Logfile kontrollieren.");
            this.statusGui.getLblStatus().setVisible(true);
        }
        
        this.logger.closeText();
        this.statusGui.getBtnFinish().setDisable(false);
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusGui.setProgress(1);
            }
        });
    }
    
    /**
     * Methode zur Umwandlung des Eingabe-Rechtecks in die WKT-Schreibweise
     */
    private void convertInputRectangleToWKT() {
        this.logger.writeLog("Der Eingabe-Ausschnitt wird die WKT-Schreibweise "
                + "umgewandelt.");
        this.logger.writeLog("-------------------------");
        this.logger.writeLog("");
        
        String inputRectangle = this.hConfig.getInputModel().getCoordRectangle();
        
        wktRectangle = formatTool.inputRectangleToWKT(inputRectangle);
        
        this.logger.writeLog("WKT-Polygon: " + wktRectangle);
        this.logger.writeLog("");
    }
    
    /**
     * Methode zur Beziehung der Kachelnummern von der DAL-Klasse.
     */
    private boolean getKachelnummer() {
        if(wktRectangle != null) {          
            arrKachelnr = dbdal.getKachelnummer(wktRectangle);
            
            if(arrKachelnr != null) {
                this.logger.writeStatus("Kachelnummer wurden bezogen");
                return true;
            }
        } else {
            this.logger.writeErrorStatus("Der Ausschnitt konnte nicht korrekt "
                    + "umgewandelt werden. Bitte kontrollieren Sie das Logfile.");
        }
        
        return false;
    }
}
