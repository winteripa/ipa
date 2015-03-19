/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bl;

import base.DisplayMethods;
import bo.HoehenlinienConfig;
import dal.DatabaseDAL;
import gui.controller.StatusController;
import java.util.ArrayList;
import module.Startclass;
import tools.FormatTools;
import tools.StandaloneLogger;

/**
 * Logikklasse
 * @author u203011
 */
public class HoehenlinienManagement implements Runnable {
    
    private HoehenlinienConfig hConfig = null;
    private DatabaseDAL dbdal = null;
    private StatusController statusGui = null;
    private String wktRectangle = null;
    private FormatTools formatTool = null;
    private ArrayList<String> arrKachelnr;
    private DisplayMethods logger = null;
    private Startclass moduleStarter = null;

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
        formatTool = new FormatTools();
        dbdal = new DatabaseDAL();
        this.logger = logger;
        this.moduleStarter = new Startclass();
        
        ((StandaloneLogger) this.logger).setOrderNumber(hConfig.getInputModel().getOrderNumber());
        ((StandaloneLogger) this.logger).setLogPath(hConfig.getInputModel().getLogdir().getAbsolutePath());
    }
    
    /**
     * Startmethode für den Thread
     */
    @Override
    public void run() {
        this.statusGui.getBtnOrder().setDisable(true);
        convertInputRectangleToWKT();
        getKachelnummer();
        
        for (String string : arrKachelnr) {
            System.out.println(string);
        }
        
        moduleStarter.startModuleStandalone(hConfig, arrKachelnr, logger);
        
        this.statusGui.getBtnFinish().setDisable(false);
    }
    
    /**
     * Methode zur Umwandlung des Eingabe-Rechtecks in die WKT-Schreibweise
     */
    private void convertInputRectangleToWKT() {
        String inputRectangle = this.hConfig.getInputModel().getCoordRectangle();
        
        wktRectangle = formatTool.inputRectangleToWKT(inputRectangle);
    }
    
    /**
     * Methode zur Beziehung der Kachelnummern von der DAL-Klasse.
     */
    private void getKachelnummer() {
        if(wktRectangle != null) {
            arrKachelnr = dbdal.getKachelnummer(wktRectangle);
        } else {
            //Log error
        }
    }
}
