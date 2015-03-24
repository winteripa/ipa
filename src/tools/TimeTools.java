/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author u203011
 */
public class TimeTools {
    
    /**
     * Methode zur Rückgabe eines Zeitstempels im Format: yyyyMMddHHmmss
     * @return Zeitstempel
     */
    public String getCurrDatetime() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        
        return df.format(cal.getTime());
    }
    
    /**
     * Methode zur Rückgabe des aktuellen Datums im Format: dd-MM-yyyy
     * @return aktuelles Datum
     */
    public String getCurrDate() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        
        return df.format(cal.getTime());
    }
    
    /**
     * Methode zur Rückgabe der aktuellen Zeit im Format: HH-mm-ss
     * @return aktuelle Zeit
     */
    public String getCurrTime() {
        DateFormat df = new SimpleDateFormat("HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        
        return df.format(cal.getTime());
    }
}
