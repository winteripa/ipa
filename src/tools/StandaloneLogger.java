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
 *
 * @author u203011
 */
public class StandaloneLogger implements DisplayMethods {
    
    private FileTools fileTool;
    private TimeTools timeTool;
    private String timestamp = null;
    private Integer orderNumber = null;
    private String logPath = null;
    private StatusController statusGui = null;
    private String logPrefix = null;
    
    public StandaloneLogger() {
        fileTool = new FileTools();
        timeTool = new TimeTools();
        timestamp = timeTool.getCurrDatetime();
        logPrefix = timeTool.getCurrDate() + ";" + timeTool.getCurrTime();
    }
    
    @Override
    public void writeText(String text) {
        this.writeLog(text);
        System.out.println(text);
    }

    @Override
    public void writeLog(String logMessage) {
        if(orderNumber != null && logPath != null) {
            String logfilename = logPath + "\\" + orderNumber + "_logfile_" + timestamp + ".log";
            fileTool.writeLogfile(logfilename, logPrefix + ";" + logMessage);
        } else {
            this.writeStatus(logMessage);
        }
        System.out.println(logMessage);
    }

    @Override
    public void closeText() {
        //this.writeLog("Logfile Ende");
        this.writeLog(LogPrefix.LOGEND);
        System.out.println("Logile Ende");
    }

    @Override
    public void writeStatus(String status) {
        this.statusGui.getTxtStatus().appendText("Status: " + status + "\n");
        
        System.out.println("Status: " + status);
    }

    @Override
    public void writeErrorStatus(String errStatus) {
        this.statusGui.getTxtStatus().appendText("Error: " + errStatus + "\n");
        
        System.out.println("Error: " + errStatus);
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setStatusGui(StatusController statusGui) {
        this.statusGui = statusGui;
    }
}
