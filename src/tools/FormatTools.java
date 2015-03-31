/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Klasse mit nützlichen Methoden für den Umgang mit Formaten
 * @author u203011
 */
public class FormatTools {
    
    /**
     * Methode zur Überprüfung des eingegeben Ausschnitts.
     * Die Eingabe wird darauf überprüft, dass sie im Format xmin, ymin, xmax, ymax ist.
     * @param input Auschnitt-Rechteck
     * @return Validierungsstatus
     */
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
    
    /**
     * Methode zur Umwandlung des Rechteck-Ausschnitts in die WKT-Schreibweise
     * @param inputRectangle Rechteck-Auschnitt
     * @return Rechteck-Ausschnitt in der WKT-Schreibweise
     */
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
    
    /**
     * Methode zur Extraktion der Argumente für das Modul.
     * In dieser Methode werden die Parameter aus der Zeichenkette extrahiert.
     * Das Format der Parameter in der Zeichenkette muss lauten: '-key=value '.
     * @param params Zeichenkette mit Parametern
     * @return Liste mit den Werten für die Konfigurationen
     */
    public HashMap<String, String> extractModuleCommandlineParams(String params) {
        Pattern pattern = Pattern.compile("-(.*?) ");
        Matcher matcher = pattern.matcher(params);
        HashMap<String, String> extractedParams = new HashMap<>();
        NumberTools numberTool = new NumberTools();
        FileTools fileTool = new FileTools();
        
        while(matcher.find()) {
            int strLen = matcher.group(1).length();
            //String keyValPair = matcher.group(1).substring(0, strLen - 1);
            String keyValPair = matcher.group(1);
            System.out.println(keyValPair);
            String[] extractedKeyVal = keyValPair.split("=");
            
            if(extractedKeyVal.length != 2) {
                extractedParams = null;
                break;
            } else {
                String key = extractedKeyVal[0];
                String val = extractedKeyVal[1];
                
                if(key.equals("aequi") || key.equals("thinning")) {
                    if(numberTool.isNumeric(val)) {
                        extractedParams.put(key, val);
                    } else {
                        extractedParams = null;
                        break;
                    }
                } else if(key.equals("force3d")) {
                    if(val.equals("true") || val.equals("false")) {
                        extractedParams.put(key, val);
                    } else {
                        extractedParams = null;
                        break;
                    }
                /*} else if(key.equals("lidardata") || key.equals("output") || 
                        key.equals("basedata")) {
                    if(fileTool.doesFileExists(val)) {
                        extractedParams.put(key, val);
                    } else {
                        extractedParams = null;
                        break;
                    }*/
                } else if(key.equals("smooth")) {
                    if(val != null) {
                        extractedParams.put(key, val);
                    } else {
                        extractedParams = null;
                        break;
                    }
                } 
            }
        }
        
        if(extractedParams != null) {
            if(extractedParams.size() != 4) {
                extractedParams = null;
            }
        }
        
        
        return extractedParams;
    }
    
    /**
     * Methode zur Bestimmung der Länge eines Ausschnittes.
     * @param inputRectangle Rechteck-Auschnitt
     * @return Länge des Auschnittes
     */
    public double getBoundaryWidth(String inputRectangle) {
        String[] splittedInput = inputRectangle.split(",");
        
        double xOne = Double.parseDouble(splittedInput[0].trim());
        double xTwo = Double.parseDouble(splittedInput[2].trim());
        
        return this.getEdgeLength(xOne, xTwo);
    }
    
    /**
     * Methode zur Bestimmung der Breite eines Ausschnittes.
     * @param inputRectangle Rechteck-Auschnitt
     * @return Breite des Auschnittes
     */
    public double getBoundaryHeight(String inputRectangle) {
        String[] splittedInput = inputRectangle.split(",");
        
        double yOne = Double.parseDouble(splittedInput[1].trim());
        double yTwo = Double.parseDouble(splittedInput[3].trim());
        
        return this.getEdgeLength(yOne, yTwo);
    }
    
    /**
     * Methode zur Bestimmung der Kantenlänge.
     * @param coordOne X- oder Y-Koordinate des ersten Punktes
     * @param coordTwo X- oder Y-Koordinate des zweiten Punktes
     * @return Kantelänge
     */
    private double getEdgeLength(double coordOne, double coordTwo) {
        double length = Math.abs(coordTwo - coordOne);
        
        return length;
    }
    
    /**
     * Methode zum Entfernen der Leerzeichen im gewählten Auschnitt.
     * @param rectangle Rechteck-Auschnitt mit Leerzeichen
     * @return Rechteck-Auschnitt ohne Leerzeichen
     */
    public String removeWhitespaceFromRectangle(String rectangle) {
        String[] splittedRectangle = rectangle.split(",");
        String properRectangle = splittedRectangle[0].trim() + "," 
                + splittedRectangle[1].trim() + "," + splittedRectangle[2].trim()
                + "," + splittedRectangle[3].trim();
        
        return properRectangle;
    }
}
