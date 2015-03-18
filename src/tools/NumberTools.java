/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

/**
 *
 * @author u203011
 */
public class NumberTools {
    
    public boolean isNumeric(String numVal) {
        try {
            double numStr = Double.parseDouble(numVal);
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public int convertToInteger(String numVal) {
        return Integer.parseInt(numVal);
    }
}
