/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

import base.DisplayMethods;
import dal.DatabaseDAL;
import dal.IDAL;

/**
 * Factory-Klasse für die Rückgabe einer DAL-Klasse
 * @author u203011
 */
public class DBFactory {
    
    /**
     * Methode um eine DAL-Klasse mit implementierten IDAL-Interface zu erhalten.
     * Die Methode gibt ein Objekt einer DAL-Klasse, welche das IDAL-Interface 
     * implementiert zurück. Vorteil: Die Klasse oder das Objekt, welches ein Objekt 
     * der DAL-Klasse benötigt muss nur das IDAL-Interface importieren. Somit 
     * ist es egal, welche DAL-Klasse hinter dem IDAL-Interface steht.
     * @param logger Logger-Klasse des Projekts
     * @return DAL-Klasse
     */
    public IDAL getDAL(DisplayMethods logger) {
        return new DatabaseDAL(logger);
    }
}
