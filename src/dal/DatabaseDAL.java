/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dal;

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
    ArrayList<String> arrKachelnr = null;
    
    /**
     * Konstruktor für die Initialisierung der Datenbankverbindung
     */
    public DatabaseDAL() {
        con = DBConnection.getConnection();
        arrKachelnr = new ArrayList<>();
    }
    
    /**
     * Methode für den Erhalt der Kachelnummern aus der Datenbank.
     * @param wktString Rechteckiger Auschnitt im WKT-Format
     * @return Liste mit allen Kachelnummern, welche mit dem gewählten Ausschnitt zu tun haben
     */
    @Override
    public ArrayList<String> getKachelnummer(String wktString) {
        String query = "select k."+ KACHELNR +" from " + DBSCHEMA + "."
                + TABLE + " as k where IsEmpty(Intersection (k.geom, "
                + "ST_GeomFromText('"+ wktString +"', 2056))) = FALSE "
                + "and k.id_prod = 26";
        
        try {
            Statement stmt = con.createStatement();
            ResultSet rSet = stmt.executeQuery(query);
            
            while(rSet.next()) {
                String kachelnr = rSet.getString("kachel_nr");
                
                arrKachelnr.add(kachelnr);
            }
        } catch(SQLException e) {
            //Log error
        } finally {
            DBConnection.closeConnection();
        }
        
        return arrKachelnr;
    }
    
}
