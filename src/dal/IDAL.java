/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dal;

import java.util.ArrayList;

/**
 * Interface für das Datenbank-Management
 * @author u203011
 */
public interface IDAL {
    
    /**
     * Interface-Methode für den Erhalt der Kachelnummern aus der Datenbank.
     * @param wktString Rechteckiger Auschnitt im WKT-Format
     * @return Liste mit allen Kachelnummern, welche mit dem gewählten Ausschnitt zu tun haben
     */
    public ArrayList<String> getKachelnummer(String wktString);
}
