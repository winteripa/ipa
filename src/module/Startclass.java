/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package module;

import base.DisplayMethods;
import bo.HoehenlinienConfig;
import java.util.ArrayList;

/**
 * Startklasse für das Modul "Contourlines"
 * @author u203011
 */
public class Startclass {
    
    private ModulContourlines module;
    
    /**
     * Startmethode für die Ausführung des Moduls in der Standalone-Version
     * @param hConfig Höhenlinien-Konfigurationsobjekt
     * @param arrKachelnr Liste mit allen Kachelnummern, welche mit dem gewählten Ausschnitt zu tun haben
     * @param logger Objekt eines DisplayMethods-Logger
     * @return Erfolgsstatus der Erstellung eines Höhenlinien-Datensatzes
     */
    public boolean startModuleStandalone(HoehenlinienConfig hConfig, 
            ArrayList<String> arrKachelnr, DisplayMethods logger) {
        
        module = new ModulContourlines(true, hConfig, logger, arrKachelnr);
        return module.generateContourlines();
    }
    
    
    //Start-Methode für Modulbetrieb
    /*public boolean startModule(String params, DisplayMethods logger) {
        
        return false;
    }*/
}
