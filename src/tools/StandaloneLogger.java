/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import base.DisplayMethods;

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
    
    public StandaloneLogger() {
        fileTool = new FileTools();
        timeTool = new TimeTools();
        timestamp = timeTool.getCurrDatetime();
    }
    
    @Override
    public void writeText(String string) {
        System.out.println(string);
    }

    @Override
    public void writeLog(String logMessage) {
        if(orderNumber != null && logPath != null) {
            String logfilename = logPath + "\\" + orderNumber + "_logfile_" + timestamp + ".log";
            fileTool.writeLogfile(logfilename, logMessage);
        } else {
            this.writeStatus(logMessage);
        }
        System.out.println(logMessage);
    }

    @Override
    public void closeText() {
        System.out.println("Logile Ende");
    }

    @Override
    public void writeStatus(String string) {
        System.out.println(string);
    }

    @Override
    public void writeErrorStatus(String string) {
        System.err.println(string);
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
