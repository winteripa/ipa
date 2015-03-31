/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import base.DisplayMethods;
import bo.LogPrefix;
import gui.controller.StatusController;

/**
 * Standalone-Loggerklasse
 * @author u203011
 */
public class StandaloneLogger implements DisplayMethods {
    
    private FileTools fileTool;
    private TimeTools timeTool;
    private String timestamp = null;
    private Integer orderNumber = null;
    private String logPath = null;
    private StatusController statusGui = null;
    private String logDatePrefix = null;
    private String logTimePrefix = null;
    
    /**
     * Konstruktor der Klasse.
     * Ein Zeitstempel für den Dateinamen wird bestimmt. Das akutelle Datum wird 
     * für die Einträge in das Logfile wird bestimmt.
     */
    public StandaloneLogger() {
        fileTool = new FileTools();
        timeTool = new TimeTools();
        timestamp = timeTool.getCurrDatetime();
        logDatePrefix = timeTool.getCurrDate() + ";";
    }
    
    /**
     * Methode zum Schreiben eines Eintrags in das Logfile
     * @param text Text, der in das Logfile geschrieben wird
     */
    @Override
    public void writeText(String text) {
        this.writeLog(text);
        System.out.println(text);
    }

    /**
     * Methode für einen Eintrag in das Logfile.
     * Die aktuelle Zeit wird bestimmt und mit dem akutellen Datum vor 
     * die Zeichenkette geschrieben, welche in das Logfile kommt.
     * @param logMessage Nachricht für das Logfile
     */
    @Override
    public void writeLog(String logMessage) {
        if(orderNumber != null && logPath != null) {
            this.logTimePrefix = timeTool.getCurrTime() + ";";
            
            String messageContent = logMessage.replace("\n", "");
            
            String logfilename = logPath + "\\" + orderNumber + "_logfile_" + timestamp + ".log";
            fileTool.writeLogfile(logfilename, logDatePrefix + logTimePrefix + messageContent);
        } else {
            this.writeStatus(logMessage);
        }
        System.out.println(logMessage);
    }

    /**
     * Methode für den letzten Eintrag in das Logfile.
     */
    @Override
    public void closeText() {
        //this.writeLog("Logfile Ende");
        this.writeLog(LogPrefix.LOGEND);
        System.out.println("Logile Ende");
    }

    /**
     * Methode für eine Statusbenachrichtung an den Benutzer.
     * Vor die Statusnachricht wird die Zeichenkette 'Status: ' gehängt, damit 
     * der Benutzer weiss, dass es sich hier um eine Statusnachricht handelt.
     * @param status Statusnachricht
     */
    @Override
    public void writeStatus(String status) {
        //this.statusGui.getTxtStatus().appendText("Status: " + status + "\n");
        this.statusGui.addStatusMessages("Status: " + status + "\n");
        
        System.out.println("Status: " + status);
    }

    /**
     * Methode für eine Fehlerbenachrichtigung an den Benutzer.
     * Vor die Fehlerbenachrichtigung wird die Zeichenkette 'Error: ' gehängt, damit 
     * der Benutzer weiss, dass es sich hier um eine Fehlerbenachrichtigung handelt.
     * @param errStatus Fehlerbenachrichtigung
     */
    @Override
    public void writeErrorStatus(String errStatus) {
        //this.statusGui.getTxtStatus().appendText("Error: " + errStatus + "\n");
        this.statusGui.addStatusMessages("Error: " + errStatus + "\n");
        
        System.out.println("Error: " + errStatus);
    }

    /**
     * Setter-Methode für die Bestellnummer.
     * @param orderNumber Bestellnummer
     */
    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Setter-Methode für den Logfile-Pfad.
     * @param logPath Logfile-Pfad
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    /**
     * Setter-Methode für den StatusController.
     * @param statusGui StatusController
     */
    public void setStatusGui(StatusController statusGui) {
        this.statusGui = statusGui;
    }
}
