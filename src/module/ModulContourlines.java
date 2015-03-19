/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package module;

import base.DisplayMethods;
import bo.HoehenlinienConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.DirectoryScanner;

/**
 * Modul-Klasse für die Erstellung der Höhenlinien
 * @author u203011
 */
public class ModulContourlines {
    
    private static final double KACHEL_CELLSIZE = 0.5;
    private static final int KACHEL_COLS = 1000;
    private boolean standalone;
    private HoehenlinienConfig hConfig = null;
    private DisplayMethods logger = null;
    private String gdalbuildvrt = null;
    private String gdaltranslate = null;
    private String lidardataPath = null;
    private String vrtRectangle = null;
    private String fittedTif = null;
    private String baseData = null;
    private ArrayList<String> arrKachelnr = null;
    private ArrayList<String> kacheln = null;

    /**
     * Konstruktor der Modul-Klasse
     * @param standalone Option für die Festlegung, ob im Standalone-Modus oder nicht
     * @param hConfig Höhenlinien-Konfigurationsobjekt
     * @param logger Objekt eines DisplayMethods-Logger
     * @param arrKachelnr Liste mit allen Kachelnummern, welche mit dem gewählten Ausschnitt zu tun haben
     */
    public ModulContourlines(boolean standalone, HoehenlinienConfig hConfig,
            DisplayMethods logger, ArrayList<String> arrKachelnr) {
        this.standalone = standalone;
        this.hConfig = hConfig;
        this.logger = logger;
        this.arrKachelnr = arrKachelnr;
        this.kacheln = new ArrayList<>();
    }

    /**
     * Start-Methode für die Erstellung eines Höhenlinien-Datensatzes
     * @return Erstellungsstatus (erfolgreich oder nicht)
     */
    public boolean generateContourlines() {
        String errMsg = "";
        boolean baseResult = true;
        
        if(this.standalone) {
            lidardataPath = hConfig.getInputModel().getLidardatapath().getAbsolutePath();
            gdalbuildvrt = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdalbuildvrt.exe";
            gdaltranslate = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdal_translate.exe";
            
            baseResult = this.generateBase();
        }
        
        if(baseResult) {
            if(!makeThinning()) {
                errMsg += "Fehler beim Ausdünnen der Punkte.\n---------------------\n"
                        + "Bitte melden Sie sich beim Administrator und schicken Sie "
                        + "ihm das Logfile.";
                logger.writeErrorStatus(errMsg);

                return false;
            }
        } else {
            errMsg += "Fehler beim Erstellen der Datengrundlage.";
            
            logger.writeErrorStatus(errMsg);
            
            return false;
        }
        
        return true;
    }
    
    private boolean makeThinning() {
        int thinningVal = hConfig.getInputModel().getThinning();
        if(thinningVal != 0) {
            try {
                double percentThinning = ((KACHEL_CELLSIZE * KACHEL_COLS) / thinningVal);
                String source = baseData;
                baseData = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\thin_output.asc";
                
                logger.writeLog("Der Ausschnitt wird ausgedünnt.\n-----------------");
                logger.writeLog("Input: " + source);
                logger.writeLog("Output: " + baseData);
                
                String[] splittedRectangle = hConfig.getInputModel().getCoordRectangle().split(",");
                String rectangle = splittedRectangle[0].trim() + " " + splittedRectangle[3].trim()
                        + " " + splittedRectangle[2].trim() + " " + splittedRectangle[1].trim();
                
                String query = gdaltranslate + " -projwin " + rectangle + " -of AAIGrid "
                        + "-outsize " + percentThinning + " " + percentThinning
                        + " -co force_cellsize=true " + source + " " + baseData;
                
                logger.writeLog("Befehl: " + query);
                
                Process process = Runtime.getRuntime().exec(query);
                
                process.waitFor();
                logger.writeLog("Ausdünnung wurde durchgeführt.\n ----------------");
                return true;
            } catch (IOException ex) {
                String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_translate':\n" + ex.getMessage()
                    + "\n ------------------------------";
                logger.writeErrorStatus(exception);
            } catch (InterruptedException ex) {
                String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
                    ex.getMessage()+ "\n ------------------------------";
                logger.writeErrorStatus(exception);
            }
        } else {
            return true;
        }
        
        return false;
    }
    
    /**
     * Methode, welche die zugeschnitte ASC-Datei zur Verfügung stellt.
     * 
     * @return Erstellungsstatus Ausschnitt
     */
    private boolean generateBase() {
        String errMsg = "Fehler beim Erstellen des Ausschnitts:\n-----------------\n";
        
        if(this.collectKacheln()) {
            if(this.generateVRT()) {
                if(this.translateVRTToTiff()){
                    if(!this.translateTiffToASC()){
                        errMsg += "Die ASC-Datei zum Rechnen der Höhenlinien "
                                + "konnte nicht erstellt werden. Bitte melden Sie sich beim "
                                + "Administrator und schicken Sie ihm das Logfile";
                        logger.writeErrorStatus(errMsg);
                        
                        return false;
                    }
                } else {
                    errMsg += "Die VRT-Datei konnte nicht auf den gewählten Ausschnitt "
                            + "zugeschnitten werden. Bitte melden Sie sich beim "
                            + "Administrator und schicken Sie ihm das Logfile";
                    logger.writeErrorStatus(errMsg);
                    
                    return false;
                }
            } else {
                errMsg += "Die VRT-Datei konnte nicht erstellt werden.\n"
                        + "Bitte prüfen Sie, ob Sie den korrekten GDAL-Installationspfad "
                        + "angegeben haben. Falls dies nicht die Ursache ist, "
                        + "so melden Sie sich bitte beim Administrator und schicken "
                        + "Sie ihm das Logfile";
                logger.writeErrorStatus(errMsg);
                
                return false;
            }
        } else {
            errMsg += "Die LiDAR-Daten konnten nicht bezogen werden.\n" 
                    + "Bitte prüfen Sie, ob der LiDAR-Datenpfad korrekt ist und "
                    + "ob die LiDAR-Daten bezogen werden können.";
            
            logger.writeErrorStatus(errMsg);
            
            return false;
        }
        
        
        return true;
    }
    
    /**
     * Methode zum Umwandeln der zugeschnittenen TIFF-Datei zu einer ASC-Datei.
     * 
     * @return Erstellungsstatus Umwandlung
     */
    private boolean translateTiffToASC() {
        try {
            logger.writeLog("Die TIFF-Datei wird in eine ASC-Datei umgewandelt:"
                    + " \n ----------------");
            
            baseData = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\output.asc";
            
            logger.writeLog("Output: " + baseData);
            
            String query = gdaltranslate + " -of GTiff " + fittedTif + " " + baseData;
            
            logger.writeLog("Befehl: " + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            logger.writeLog("TIFF-Datei wurde umgewandelt.\n ----------------");
            return true;
        } catch (IOException ex) {
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_translate':\n" + ex.getMessage()
                    + "\n ------------------------------";
            logger.writeErrorStatus(exception);
        } catch (InterruptedException ex) {
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
                    ex.getMessage()+ "\n ------------------------------";
            logger.writeErrorStatus(exception);
        }
        
        return false;
    }
    
    /**
     * Methode zum Zuschneiden einer VRT-Datei. Output ist eine TIFF-Datei
     * 
     * @return Erstellungsstatus Ausschnitt
     */
    private boolean translateVRTToTiff(){
        try {
            logger.writeLog("Die VRT-Datei wird in eine TIFF-Datei umgewandelt "
                    + "und zugeschnitten: \n ----------------");
            
            fittedTif = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\fittedoutput.tif";
            
            logger.writeLog("Output: " + fittedTif);
            
            String[] splittedRectangle = hConfig.getInputModel().getCoordRectangle().split(",");
            String rectangle = splittedRectangle[0].trim() + " " + splittedRectangle[3].trim()
                    + " " + splittedRectangle[2].trim() + " " + splittedRectangle[1].trim();
            
            String query = gdaltranslate + " -projwin " + rectangle + " -of GTiff "
                    + vrtRectangle + " " + fittedTif;
            
            logger.writeLog("Befehl: " + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            logger.writeLog("VRT-Datei wurde umgewandelt und zugeschnitten.\n ----------------");
            return true;
        } catch (IOException ex) {
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_translate':\n" + ex.getMessage()
                    + "\n ------------------------------";
            logger.writeErrorStatus(exception);
        } catch (InterruptedException ex) {
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
                    ex.getMessage()+ "\n ------------------------------";
            logger.writeErrorStatus(exception);
        }
        
        return false;
    }
    
    /**
     * Methode zur Erstellung einer VRT-Datei aus den Kacheln
     * 
     * @return Erstellungstatus VRT-Datei
     */
    private boolean generateVRT() {
        try {
            logger.writeLog("Eine VRT-Datei wird erstellt: \n ----------------");
            
            vrtRectangle = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\output.vrt";
            
            logger.writeLog("Output: " + vrtRectangle);
            
            String query = gdalbuildvrt + " " + vrtRectangle + " ";
            
            for (String kachel : kacheln) {
                query += lidardataPath + "\\" + kachel + " ";
            }
            
            logger.writeLog("Befehl: " + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            logger.writeLog("VRT-Datei ist erstellt worden.\n ----------------");
            
            return true;
        } catch (IOException ex) {
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdalbuildvrt':\n" + ex.getMessage()
                    + "\n ------------------------------";
            
            logger.writeErrorStatus(exception);
        } catch (InterruptedException ex) {
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdalbuildvrt', das Kommando wurde abgebrochen:" +
                    ex.getMessage()+ "\n ------------------------------";
            logger.writeErrorStatus(exception);
        }
        
        return false;
    }
    
    /**
     * Methode zur Ermittlung der Kachelnnamen für die Erstellung einer VRT-Datei
     * 
     * @return Erstellungsstatus der Ermittlung der Kachelnamen.
     */
    private boolean collectKacheln() {
        DirectoryScanner scanner = new DirectoryScanner();
        
        logger.writeLog("Die Kachelnnamen werden bezogen.\n ----------------");
        
        for (String kachelnr : arrKachelnr) {
            String[] splittedKachelnr = kachelnr.split("_");
            
            if(splittedKachelnr.length == 2) {
                String xcoord = splittedKachelnr[0];
                String ycoord = splittedKachelnr[1];
                
                String filename = xcoord + "*_" + ycoord + "*_t.asc";
                
                scanner.setIncludes(new String[]{"**/" + filename});
                scanner.setBasedir(lidardataPath);
                scanner.setCaseSensitive(false);
                scanner.scan();
                String[] files = scanner.getIncludedFiles();
                
                this.kacheln.addAll(Arrays.asList(files));
            } else {
                logger.writeErrorStatus("Eine Falsche Kachelnummer wurde registriert.");
                return false;
            }
        }
        
        return true;
    }
}
