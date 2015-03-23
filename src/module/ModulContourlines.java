/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package module;

import base.DisplayMethods;
import bo.HoehenlinienConfig;
import bo.LogPrefix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.DirectoryScanner;
import tools.FileTools;

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
    private String gdalcontour = null;
    private String lidardataPath = null;
    private String vrtRectangle = null;
    private String fittedTif = null;
    private String baseData = null;
    private ArrayList<String> arrKachelnr = null;
    private ArrayList<String> kacheln = null;
    private ArrayList<String> filesToDelete = null;
    private FileTools fileTool = null;

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
        this.filesToDelete = new ArrayList<>();
        this.fileTool = new FileTools();
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
            gdalcontour = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdal_contour.exe";
            
            if(!makeThinning()) {
//                errMsg += "Fehler beim Ausdünnen der Punkte.\n---------------------\n"
//                        + "Bitte melden Sie sich beim Administrator und schicken Sie "
//                        + "ihm das Logfile.";
//                logger.writeLog(errMsg);
                
                errMsg += "Fehler beim Ausdünnen der Punkte."
                        + "Bitte melden Sie sich beim Administrator und schicken Sie "
                        + "ihm das Logfile.";
                logger.writeLog(LogPrefix.LOGERROR + errMsg);

                logger.writeErrorStatus("Ausschnitt konnte nicht ausgedünnt werden.");
                
                return false;
            }
            
            if(!makeContourlines()) {
//                errMsg += "Fehler beim Erstellen der Höhenlinien.\n-------------------------\n"
//                        + "Bitte kontrollieren Sie den ausgeführten Befehl auf "
//                        + "seine Funktionalität oder wenden Sie sich danach an "
//                        + "Ihren Administrator und senden ihm Ihr Logfile.";
//                logger.writeLog(errMsg);
//                logger.writeLog("");
                errMsg += "Fehler beim Erstellen der Höhenlinien."
                        + "Bitte kontrollieren Sie den ausgeführten Befehl auf "
                        + "seine Funktionalität oder wenden Sie sich danach an "
                        + "Ihren Administrator und senden ihm Ihr Logfile.";
                logger.writeLog(LogPrefix.LOGERROR + errMsg);
                
                logger.writeErrorStatus("Höhenlinien konnten nicht erstellt werden.");
                
                return false;
            }
        } else {
            errMsg += "Fehler beim Erstellen der Datengrundlage.";
            
            //logger.writeLog(errMsg);
            logger.writeLog(LogPrefix.LOGERROR + errMsg);
            
            return false;
        }
        
        return true;
    }
    
    private boolean makeContourlines() {
        try {
            String flag3D = "";
            
//            logger.writeLog("Höhenlinien werden aus dem Auschnitt erstellt");
//            logger.writeLog("-------------------------");
//            logger.writeLog("");
            logger.writeLog(LogPrefix.LOGINFO + "Höhenlinien werden aus dem Auschnitt erstellt");
            
            String source = baseData;
            baseData = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\contourlines.shp";
            
            if(!this.fileTool.deleteExistingFile(baseData)) {
//                logger.writeLog("Es existiert eine alte 'contourlines.shp'-Datei. "
//                        + "Das Programm kann diese nicht löschen. Bitte löschen "
//                        + "Sie die Datei manuell und versuchen Sie den Bestellvorgang "
//                        + "erneut auszuführen");
//                logger.writeLog("");
                logger.writeLog(LogPrefix.LOGERROR + "Es existiert eine alte 'contourlines.shp'-Datei. "
                        + "Das Programm kann diese nicht löschen. Bitte löschen "
                        + "Sie die Datei manuell und versuchen Sie den Bestellvorgang "
                        + "erneut auszuführen");
                
                return false;
            }
            
            if(hConfig.getInputModel().isForce3D()) {
                flag3D = "-3d";
            }
            
            logger.writeLog(LogPrefix.LOGQUERYTITLE + "gdalcontour Höhenlinien erstellen");
            logger.writeLog(LogPrefix.LOGDATAINPUT + source);
            logger.writeLog(LogPrefix.LOGDATAOUTPUT + baseData);
            
//            logger.writeLog("Input: " + source);
//            logger.writeLog("Output: " + baseData);
//            logger.writeLog("");
            
            String query = gdalcontour + " -i " + hConfig.getInputModel().getAequidistance()
                    + " -a hoehe " + flag3D + " " + source + " " + baseData;
            
//            logger.writeLog("Befehl: " + query);
//            logger.writeLog("");
            logger.writeLog(LogPrefix.LOGQUERY + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            String errResult = "Die Höhenlinien konnten nicht korrekt erstellt werden.";
            
            if(this.doesResultExits(baseData, errResult)) {
                //logger.writeLog("Höhenlinien wurden erstellt.");
                logger.writeLog(LogPrefix.LOGINFO + "Höhenlinien wurden erstellt.");

                logger.writeStatus("Höhenlinien wurden erstellt.");
                
                return true;
            }
            
            return false;
        } catch (IOException ex) {
//            String exception = "Eine Exception ist aufgetreten beim Ausführen "
//                    + "des Kommandozeilen-Befehls 'gdal_contour':\n" + ex.getMessage()
//                    + "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_contour':" + ex.getMessage();
            logger.writeLog(LogPrefix.LOGERROR + exception);
        } catch (InterruptedException ex) {
//            String exception = "Eine Exception ist aufgetreten während dem "
//                    + "Ausführen von 'gdal_contour', das Kommando wurde abgebrochen:" +
//                    ex.getMessage()+ "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_contour', das Kommando wurde abgebrochen:" +
                    ex.getMessage()+ "";
            logger.writeLog(LogPrefix.LOGERROR + exception);
        }
        
        return false;
    }
    
    private boolean makeThinning() {
        int thinningVal = hConfig.getInputModel().getThinning();
        if(thinningVal != 0) {
            try {
                double percentThinning = ((KACHEL_CELLSIZE * KACHEL_COLS) / thinningVal);
                String source = baseData;
                baseData = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\thin_output.asc";
                
                
                logger.writeLog(LogPrefix.LOGINFO + "Der Ausschnitt wird ausgedünnt.");
                logger.writeLog(LogPrefix.LOGQUERYTITLE + "gdaltranslate Ausdünnen");
                logger.writeLog(LogPrefix.LOGDATAINPUT + source);
                logger.writeLog(LogPrefix.LOGDATAOUTPUT + baseData);
                
//                logger.writeLog("Der Ausschnitt wird ausgedünnt.\n-----------------");
//                logger.writeLog("Input: " + source);
//                logger.writeLog("Output: " + baseData);
                
                String[] splittedRectangle = hConfig.getInputModel().getCoordRectangle().split(",");
                String rectangle = splittedRectangle[0].trim() + " " + splittedRectangle[3].trim()
                        + " " + splittedRectangle[2].trim() + " " + splittedRectangle[1].trim();
                
                String query = gdaltranslate + " -projwin " + rectangle + " -of AAIGrid "
                        + "-outsize " + percentThinning + " " + percentThinning
                        + " -co force_cellsize=true " + source + " " + baseData;
                
                //logger.writeLog("Befehl: " + query);
                logger.writeLog(LogPrefix.LOGQUERY + query);
                
                Process process = Runtime.getRuntime().exec(query);
                
                process.waitFor();
                
                String errResult = "Der Ausschnitt konnte nicht korrekt ausgedünnt werden.";
            
                if(this.doesResultExits(baseData, errResult)) {
                    //logger.writeLog("Ausdünnung wurde durchgeführt.\n ----------------");
                    logger.writeLog(LogPrefix.LOGINFO + "Ausdünnung wurde durchgeführt.");

                    this.logger.writeStatus("Ausschnitt wurde ausgedünnt.");
                    
                    return true;
                }
                
                return false;
            } catch (IOException ex) {
//                String exception = "Eine Exception ist aufgetreten beim Ausführen "
//                    + "des Kommandozeilen-Befehls 'gdal_translate':\n" + ex.getMessage()
//                    + "\n ------------------------------";
//                logger.writeLog(exception);
                String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_translate':" + ex.getMessage();
                logger.writeLog(LogPrefix.LOGERROR + exception);
            } catch (InterruptedException ex) {
//                String exception = "Eine Exception ist aufgetreten während dem "
//                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
//                    ex.getMessage()+ "\n ------------------------------";
//                logger.writeLog(exception);
                String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
                    ex.getMessage();
                logger.writeLog(LogPrefix.LOGERROR + exception);
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
        String errMsg = LogPrefix.LOGERROR + "Fehler beim Erstellen des Ausschnitts:\n";
        
        if(this.collectKacheln()) {
            this.logger.writeStatus("Kacheln wurden bezogen.");
            
            if(this.generateVRT()) {
                this.logger.writeStatus("Virtuelles Raster wurde erstellt.");
                
                if(this.translateVRTToTiff()){
                    this.logger.writeStatus("Raster wurde ausgeschnitten.");
                    
                    if(!this.translateTiffToASC()){
                        errMsg += "Die ASC-Datei zum Rechnen der Höhenlinien "
                                + "konnte nicht erstellt werden. Bitte melden Sie sich beim "
                                + "Administrator und schicken Sie ihm das Logfile";
                        //logger.writeLog(errMsg);
                        logger.writeLog(LogPrefix.LOGERROR + errMsg);
                        
                        return false;
                    }
                } else {
                    this.logger.writeErrorStatus("Raster konnte nicht ausgeschnitten werden.");
                    
                    errMsg += "Die VRT-Datei konnte nicht auf den gewählten Ausschnitt "
                            + "zugeschnitten werden. Bitte melden Sie sich beim "
                            + "Administrator und schicken Sie ihm das Logfile";
                    //logger.writeLog(errMsg);
                    logger.writeLog(LogPrefix.LOGERROR + errMsg);
                    
                    return false;
                }
            } else {
                this.logger.writeErrorStatus("Virtuelles Raster konnte nicht erstellt werden.");
                
//                errMsg += "Die VRT-Datei konnte nicht erstellt werden.\n"
//                        + "Bitte prüfen Sie, ob Sie den korrekten GDAL-Installationspfad "
//                        + "angegeben haben. Falls dies nicht die Ursache ist, "
//                        + "so melden Sie sich bitte beim Administrator und schicken "
//                        + "Sie ihm das Logfile";
//                logger.writeLog(errMsg);
                errMsg += "Die VRT-Datei konnte nicht erstellt werden. "
                        + "Bitte prüfen Sie, ob Sie den korrekten GDAL-Installationspfad "
                        + "angegeben haben. Falls dies nicht die Ursache ist, "
                        + "so melden Sie sich bitte beim Administrator und schicken "
                        + "Sie ihm das Logfile";
                logger.writeLog(LogPrefix.LOGERROR + errMsg);
                
                return false;
            }
        } else {
            this.logger.writeErrorStatus("Kacheln konnten nicht bezogen werden.");
            
//            errMsg += "Die LiDAR-Daten konnten nicht bezogen werden.\n" 
//                    + "Bitte prüfen Sie, ob der LiDAR-Datenpfad korrekt ist und "
//                    + "ob die LiDAR-Daten bezogen werden können.";
//            
//            logger.writeLog(errMsg);
            errMsg += "Die LiDAR-Daten konnten nicht bezogen werden. " 
                    + "Bitte prüfen Sie, ob der LiDAR-Datenpfad korrekt ist und "
                    + "ob die LiDAR-Daten bezogen werden können.";
            
            logger.writeLog(LogPrefix.LOGERROR + errMsg);
            
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
//            logger.writeLog("Die TIFF-Datei wird in eine ASC-Datei umgewandelt:"
//                    + " \n ----------------");
            logger.writeLog(LogPrefix.LOGINFO + "Die TIFF-Datei wird in eine ASC-Datei umgewandelt");
            logger.writeLog(LogPrefix.LOGQUERYTITLE + "gdaltranslate TIFF zu ASC Konvertierung");
            
            baseData = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\output.asc";
            
            //logger.writeLog("Output: " + baseData);
            logger.writeLog(LogPrefix.LOGDATAOUTPUT + baseData);
            
            String query = gdaltranslate + " -of GTiff " + fittedTif + " " + baseData;
            
            //logger.writeLog("Befehl: " + query);
            logger.writeLog(LogPrefix.LOGQUERY + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            String errResult = "Die TIFF-Datei konnte nicht korrekt umgewandelt werden.";
            
            if(this.doesResultExits(baseData, errResult)) {
                //logger.writeLog("TIFF-Datei wurde umgewandelt.\n ----------------");
                logger.writeLog(LogPrefix.LOGINFO + "TIFF-Datei wurde umgewandelt.");
                
                return true;
            }
            
            return false;
        } catch (IOException ex) {
//            String exception = "Eine Exception ist aufgetreten beim Ausführen "
//                    + "des Kommandozeilen-Befehls 'gdal_translate':\n" + ex.getMessage()
//                    + "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_translate': " + ex.getMessage();
            logger.writeLog(LogPrefix.LOGERROR + exception);
        } catch (InterruptedException ex) {
//            String exception = "Eine Exception ist aufgetreten während dem "
//                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
//                    ex.getMessage()+ "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
                    ex.getMessage();
            logger.writeLog(LogPrefix.LOGERROR + exception);
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
//            logger.writeLog("Die VRT-Datei wird in eine TIFF-Datei umgewandelt "
//                    + "und zugeschnitten: \n ----------------");
            logger.writeLog(LogPrefix.LOGINFO + "Die VRT-Datei wird in eine TIFF-Datei umgewandelt "
                    + "und zugeschnitten");
            logger.writeLog(LogPrefix.LOGQUERYTITLE + "gdaltranslate VRT zuschneiden");
            
            fittedTif = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\fittedoutput.tif";
            
            //logger.writeLog("Output: " + fittedTif);
            logger.writeLog(LogPrefix.LOGDATAOUTPUT + fittedTif);
            
            String[] splittedRectangle = hConfig.getInputModel().getCoordRectangle().split(",");
            String rectangle = splittedRectangle[0].trim() + " " + splittedRectangle[3].trim()
                    + " " + splittedRectangle[2].trim() + " " + splittedRectangle[1].trim();
            
            String query = gdaltranslate + " -projwin " + rectangle + " -of GTiff "
                    + vrtRectangle + " " + fittedTif;
            
            //logger.writeLog("Befehl: " + query);
            logger.writeLog(LogPrefix.LOGQUERY + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            String errResult = "Die VRT-Datei konnte nicht korrekt umgewandelt und "
                    + "zugeschnitten werden.";
            
            if(this.doesResultExits(fittedTif, errResult)) {
                //logger.writeLog("VRT-Datei wurde umgewandelt und zugeschnitten.\n ----------------");
                logger.writeLog(LogPrefix.LOGINFO + "VRT-Datei wurde umgewandelt und zugeschnitten.");
                
                return true;
            }
            
            return false;
        } catch (IOException ex) {
//            String exception = "Eine Exception ist aufgetreten beim Ausführen "
//                    + "des Kommandozeilen-Befehls 'gdal_translate':\n" + ex.getMessage()
//                    + "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdal_translate': " + ex.getMessage();
            logger.writeLog(LogPrefix.LOGERROR + exception);
        } catch (InterruptedException ex) {
//            String exception = "Eine Exception ist aufgetreten während dem "
//                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
//                    ex.getMessage()+ "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdal_translate', das Kommando wurde abgebrochen:" +
                    ex.getMessage();
            logger.writeLog(LogPrefix.LOGERROR + exception);
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
            //logger.writeLog("Eine VRT-Datei wird erstellt: \n ----------------");
            logger.writeLog(LogPrefix.LOGINFO + "Eine VRT-Datei wird erstellt");
            logger.writeLog(LogPrefix.LOGQUERYTITLE + "VRT erstellen");
            
            vrtRectangle = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\output.vrt";
            
            //logger.writeLog("Output: " + vrtRectangle);
            logger.writeLog(LogPrefix.LOGDATAOUTPUT + vrtRectangle);
            
            String query = gdalbuildvrt + " " + vrtRectangle + " ";
            
            for (String kachel : kacheln) {
                query += lidardataPath + "\\" + kachel + " ";
            }
            
            //logger.writeLog("Befehl: " + query);
            logger.writeLog(LogPrefix.LOGQUERY + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            String errResult = "Die VRT-Datei konnte nicht korrekt erstellt werden.";
            
            if(this.doesResultExits(vrtRectangle, errResult)) {
                //logger.writeLog("VRT-Datei ist erstellt worden.\n ----------------");
                logger.writeLog(LogPrefix.LOGINFO + "VRT-Datei ist erstellt worden.");
                
                return true;
            }
            
            return false;
        } catch (IOException ex) {
//            String exception = "Eine Exception ist aufgetreten beim Ausführen "
//                    + "des Kommandozeilen-Befehls 'gdalbuildvrt':\n" + ex.getMessage()
//                    + "\n ------------------------------";
//            
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten beim Ausführen "
                    + "des Kommandozeilen-Befehls 'gdalbuildvrt': " + ex.getMessage();
            
            logger.writeLog(LogPrefix.LOGERROR + exception);
        } catch (InterruptedException ex) {
//            String exception = "Eine Exception ist aufgetreten während dem "
//                    + "Ausführen von 'gdalbuildvrt', das Kommando wurde abgebrochen:" +
//                    ex.getMessage()+ "\n ------------------------------";
//            logger.writeLog(exception);
            String exception = "Eine Exception ist aufgetreten während dem "
                    + "Ausführen von 'gdalbuildvrt', das Kommando wurde abgebrochen:" +
                    ex.getMessage();
            logger.writeLog(LogPrefix.LOGERROR + exception);
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
        
        //logger.writeLog("Die Kachelnnamen werden bezogen.\n ----------------");
        logger.writeLog(LogPrefix.LOGINFO + "Die Kachelnnamen werden bezogen.");
        
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
                //logger.writeLog("Eine Falsche Kachelnummer wurde registriert.");
                logger.writeLog(LogPrefix.LOGERROR + "Eine Falsche Kachelnummer wurde registriert.");
                return false;
            }
        }
        
        if(kacheln.isEmpty()) {
//            logger.writeLog("Für den Ausschnitt wurden keine Kacheln gefunden. "
//                    + "Bitte überprüfen Sie, ob der Ausschnitt im Kanton "
//                    + "Baselland liegt. Falls dies nicht weiterhilft, melden "
//                    + "Sie sich bitte beim Administrator und schicken Sie ihm "
//                    + "Ihr Logfile.");
//            logger.writeLog("");
            logger.writeLog(LogPrefix.LOGERROR + "Für den Ausschnitt wurden keine Kacheln gefunden. "
                    + "Bitte überprüfen Sie, ob der Ausschnitt im Kanton "
                    + "Baselland liegt. Falls dies nicht weiterhilft, melden "
                    + "Sie sich bitte beim Administrator und schicken Sie ihm "
                    + "Ihr Logfile.");
            
            return false;
        }
        
        return true;
    }
    
    private boolean doesResultExits(String filepath, String errMsg) {
        if(!this.fileTool.doesFileExists(filepath)) {
//            this.logger.writeLog(errMsg);
//            this.logger.writeLog("");
            this.logger.writeLog(LogPrefix.LOGERROR + errMsg);
            
            return false;
        }
        
        return true;
    }
}
