/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.util.ArrayList;

/**
 *
 * @author u203011
 */
public class FormatTools {
    
    public boolean isInputRectangle(String input) {
        String[] args = input.split(",");
        NumberTools numTool = new NumberTools();
        
        if (args.length != 4) {
            return false;
        }
        
        for (String coord : args) {
            String trimmedCoord = coord.trim();
            String numCoord = trimmedCoord;
            
            if(!numTool.isNumeric(numCoord)) {
                return false;
            } else {
                String[] splittedCoord = trimmedCoord.split("\\.");

                if(splittedCoord.length == 2) {
                    if(splittedCoord[1].length() > 3) {
                        return false;
                    }
                }
            }
        }
        
        double arg0 = Double.parseDouble(args[0].trim());
        double arg1 = Double.parseDouble(args[1].trim());
        double arg2 = Double.parseDouble(args[2].trim());
        double arg3 = Double.parseDouble(args[3].trim());
        
        if(arg0 >= arg2) {
            return false;
        }
        
        if(arg1 >= arg3) {
            return false;
        }
        
        return true;
    }
    
    public String inputRectangleToWKT(String inputRectangle) {
        String[] splittedCoords = inputRectangle.split(",");
        double[] coords = new double[4];
        
        coords[0] = Double.parseDouble(splittedCoords[0].trim());
        coords[1] = Double.parseDouble(splittedCoords[1].trim());
        coords[2] = Double.parseDouble(splittedCoords[2].trim());
        coords[3] = Double.parseDouble(splittedCoords[3].trim());
        
        String wktString = "POLYGON((" + coords[0] + " " + coords[1] + ","
                + coords[2] + " " + coords[1] + "," + coords[2] + " "
                + coords[3] + "," + coords[0] + " " + coords[3] + ","
                + coords[0] + " " + coords[1] + "))";
        
        return wktString;
    }
}
