/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dal;

import base.DisplayMethods;
import bo.LogPrefix;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Klasse für das Datenbank-Management
 * @author u203011
 */
public class DatabaseDAL implements IDAL {

    private Connection con = null;
    private static final String DBSCHEMA = "\"raster\"";
    private static final String TABLE = "\"abraster_kachel_2056\"";
    private static final String KACHELNR = "kachel_nr";
    private ArrayList<String> arrKachelnr = null;
    private DisplayMethods logger = null;
    
    /**
     * Konstruktor für die Initialisierung der Datenbankverbindung.
     */
    public DatabaseDAL(DisplayMethods logger) {
        this.logger = logger;
        
//        this.logger.writeLog("Datenbank-Verbindung wird aufgebaut");
//        this.logger.writeLog("-------------------------");
//        this.logger.writeLog("");
        this.logger.writeLog(LogPrefix.LOGINFO + "Datenbank-Verbindung wird aufgebaut");
        this.logger.writeStatus("Datenbank-Verbindung wird aufgebaut");
        
        con = DBConnection.getConnection();
        arrKachelnr = new ArrayList<>();
    }
    
    /**
     * Methode für den Erhalt der Kachelnummern aus der Datenbank.
     * Alle Kachelnummern der Kacheln, welche sich mit dem vom Benutzer ausgewählten 
     * Ausschnitt schneiden werden aus der PostGis-Datenbank zurückgegeben.
     * @param wktString Rechteckiger Auschnitt im WKT-Format
     * @return Liste mit allen Kachelnummern, welche mit dem gewählten Ausschnitt zu tun haben
     */
    @Override
    public ArrayList<String> getKachelnummer(String wktString) {
//        this.logger.writeLog("Kachelnummern werden bezogen");
//        this.logger.writeLog("-------------------------");
//        this.logger.writeLog("");
        this.logger.writeLog(LogPrefix.LOGINFO + "Kachelnummern werden bezogen");
        
        String query = "select k."+ KACHELNR +" from " + DBSCHEMA + "."
                + TABLE + " as k where IsEmpty(Intersection (k.geom, "
                + "ST_GeomFromText('"+ wktString +"', 2056))) = FALSE "
                + "and k.id_prod = 26";
        
//        this.logger.writeLog("SQL-Query: " + query);
//        this.logger.writeLog("");
        this.logger.writeLog(LogPrefix.LOGSQL + query);
        
        try {
            Statement stmt = con.createStatement();
            ResultSet rSet = stmt.executeQuery(query);
            
            while(rSet.next()) {
                String kachelnr = rSet.getString("kachel_nr");
                
                arrKachelnr.add(kachelnr);
            }
        } catch(SQLException e) {
//            this.logger.writeLog("Beim Abfragen der Kachelnummern ist ein SQL-Fehler "
//                    + "aufgetreten: " + e.getMessage());
//            this.logger.writeLog("");
            
            this.logger.writeLog(LogPrefix.LOGERROR + "Beim Abfragen der Kachelnummern ist ein SQL-Fehler "
                    + "aufgetreten: " + e.getMessage());
        } finally {
//            this.logger.writeLog("Die Datenbank-Verbindung wird geschlossen.");
//            this.logger.writeLog("");
            this.logger.writeLog(LogPrefix.LOGINFO + "Die Datenbank-Verbindung wird geschlossen.");
            
            DBConnection.closeConnection();
        }
        
        return arrKachelnr;
    }
    
}
