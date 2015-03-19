/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

/**
 * Klasse mit nützlichen Methoden für den Umgang mit Zahlen
 * @author u203011
 */
public class NumberTools {
    
    /**
     * Methode zur Überprüfung einer Zahl in Form eines Strings
     * @param numVal Zahl im Stringformat
     * @return Validierungsstatus
     */
    public boolean isNumeric(String numVal) {
        try {
            double numStr = Double.parseDouble(numVal);
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Methode zur Umwandlung eines Strings in eine Ganzzahl
     * @param numVal Zahl im Stringformat
     * @return Zahl
     */
    public int convertToInteger(String numVal) {
        return Integer.parseInt(numVal);
    }
}
