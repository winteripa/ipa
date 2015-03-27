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
import java.util.HashMap;
import org.apache.tools.ant.DirectoryScanner;
import tools.FileTools;

/**
 * Modul-Klasse für die Erstellung der Höhenlinien
 * @author u203011
 */
public class ModulContourlines {

    private static final double KACHEL_CELLSIZE = 0.5;
    private static final int KACHEL_COLS = 1000;
    private static final String GRASSVECTORIMPORT = "vectorimport";
    private static final String GRASSSMOOTH = "grasssmooth";
    private static final String GRASSEXPORT = "grassexport";
    private static final String GRASSVECTORPATH = "glocation\\grassmapset\\vector";
    private static final String GRASSMAPSETDIR = "grassmapset";
    private static String contourlinesshapename = "_contourlines";
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
    private String grassGisrc = null;
    private String grassGisdbase = null;
    private String grassLocation = null;
    private String grassMapset = null;

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
     * Konstruktor der Modul-Klasse für den Modulbetrieb
     * Für den Modulbetrieb werden nicht die gleichen Argumente, wie im 
     * Standalone-Betrieb benötigt. Die betroffenen Kachelnummern werden 
     * durch den Pfad des fertigen Ausschnittes ersetzt.
     * @param standalone Option für die Festlegung, ob im Standalone-Modus oder nicht
     * @param hConfig Höhenlinien-Konfigurationsobjekt
     * @param logger Objekt eines DisplayMethods-Logger
     */
    public ModulContourlines(boolean standalone, HoehenlinienConfig hConfig,
            DisplayMethods logger) {
        this.standalone = standalone;
        this.hConfig = hConfig;
        this.logger = logger;
        this.kacheln = new ArrayList<>();
        this.filesToDelete = new ArrayList<>();
        this.fileTool = new FileTools();
    }

    public boolean prepareGenerateBase() {
        lidardataPath = hConfig.getInputModel().getLidardatapath().getAbsolutePath();
        gdalbuildvrt = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdalbuildvrt.exe";
        gdaltranslate = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdal_translate.exe";
        contourlinesshapename = hConfig.getInputModel().getOrderNumber() + contourlinesshapename;
        
        if(!generateBase()) {
            //logger.writeLog(errMsg);
            logger.writeLog(LogPrefix.LOGERROR + "Fehler beim Erstellen der Datengrundlage.");
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Start-Methode für die Erstellung eines Höhenlinien-Datensatzes
     * @return Erstellungsstatus (erfolgreich oder nicht)
     */
    public boolean generateContourlines() {
        String errMsg = "";
        //boolean baseResult = true;
        
        System.out.println(this.clearOutputDir());
        
        /*if(this.standalone) {
            lidardataPath = hConfig.getInputModel().getLidardatapath().getAbsolutePath();
            gdalbuildvrt = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdalbuildvrt.exe";
            gdaltranslate = hConfig.getPathModel().getGdalpath().getAbsolutePath() + "\\gdal_translate.exe";
            
            baseResult = this.generateBase();
        }*/
        
        //if(baseResult) {
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
            } else {
                if(hConfig.getInputModel().getSmooth() != null || 
                        hConfig.getInputModel().isForce3D()) {
                    if(this.initializeGrass()) {
                        if(this.makeGrassVectorImport()) {
                            if(hConfig.getInputModel().getSmooth() != null) {
                                if(this.makeGrassSmooth()) {
                                    if(!this.makeGrassShpExport(GRASSSMOOTH)) {
                                        this.clearGrass();
                                        this.deleteOldFiles();
                        
                                        logger.writeErrorStatus("Fehler der Smooth-Alogrithmus konnte nicht "
                                                + "angwendet werden.");
                                        return false;
                                    }
                                } else {
                                    this.clearGrass();
                                    this.deleteOldFiles();
                                    
                                    logger.writeErrorStatus("Fehler beim Anwenden des Smooth-Algorithmus (GRASS-Smooth)");
                                    return false;
                                }
                            }
                        } else {
                            this.clearGrass();
                            this.deleteOldFiles();
                            
                            logger.writeErrorStatus("Fehler beim Anwenden des Smooth-Algorithmus (GRASS-Import)");
                            return false;
                        }
                    } else {
                        this.clearGrass();
                        this.deleteOldFiles();
                        
                        logger.writeErrorStatus("Fehler beim Anwenden des Smooth-Algorithmus");
                        return false;
                    }
                }
            }
        /*} else {
            errMsg += "Fehler beim Erstellen der Datengrundlage.";
            
            //logger.writeLog(errMsg);
            logger.writeLog(LogPrefix.LOGERROR + errMsg);
            
            return false;
        }*/
        
        this.clearGrass();
        this.deleteOldFiles();
        return true;
    }
    
    /**
     * Methode zum Export eines GRASS-Vektorshapes in ein ESRI Shapefile.
     * Diese Methode exportiert den gesmoothen Höhenlinien-Datensatz. 
     * Aus diesem Grund werden zuerst die alten Höhenlinien-Datensätze im 
     * Zielverzeichnis gelöscht. Anschliessend wird die Batch-Datei für die Ausführung
     * des Python-Moduls und das Python-Modul für den Export selber in das 
     * Zielverzeichnis kopiert und ausgeführt. Nach erfolgreicher Ausführung 
     * werden das Batch-File und das Python-File wieder gelöscht
     * @param inputToExport Name des GRASS-Vektorshapes, welches exportiert werden soll
     * @return Erstellungsstatus des Shapefiles
     */
    private boolean makeGrassShpExport(String inputToExport) {
        String baseOutputDir = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\";
        String output = baseOutputDir + contourlinesshapename + ".shp";
        int errNumber = 0;
        HashMap<String, String> grassExportArgs = new HashMap<>();
        grassExportArgs.put("input", inputToExport);
        grassExportArgs.put("type", "line");
        grassExportArgs.put("layer", "1");
        grassExportArgs.put("format", "ESRI_Shapefile");
        grassExportArgs.put("output", output);
        grassExportArgs.put("olayer", "default");
        grassExportArgs.put("flags", "ecz");
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Das GRASS-Vektorshape wird in "
                + "eine Vektorshape-Datei konvertiert.");
        
        if(!fileTool.deleteExistingFile(baseOutputDir + contourlinesshapename + ".shp")) {
            errNumber += 1;
        }
        
        if(!fileTool.deleteExistingFile(baseOutputDir + contourlinesshapename + ".shx")) {
            errNumber += 1;
        }
        
        if(!fileTool.deleteExistingFile(baseOutputDir + contourlinesshapename + ".dbf")) {
            errNumber += 1;
        }
        
        if(errNumber > 0) {
            this.logger.writeLog(LogPrefix.LOGERROR + "Fehler beim Löschen des "
                    + "alten Höhenlinien-Datensatzes. Bitte löschen Sie die Dateien "
                    + "von Hand.");
            
            this.logger.writeErrorStatus("Fehler beim Löschen des alten Höhenlinien-Datensatzes");
            
            return false;
        }
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei zum Anstossen "
                + "des Exports wird in das Zielverzeichnis kopiert");
        
        if(this.writeGrassExportBatch(grassExportArgs)) {
            
            this.logger.writeLog(LogPrefix.LOGINFO + "Die Python-Datei wird in das "
                    + "Zielverzeichnis kopiert");
            
            if(fileTool.copyInsideFileToDisk(GRASSEXPORT + ".py", baseOutputDir)) {
                try {
                    this.logger.writeLog(LogPrefix.LOGINFO + "Beginn des Exports");
                    
                    String query = "cmd /c \"" + baseOutputDir
                            + GRASSEXPORT + ".bat\"";
                    
                    Process process = Runtime.getRuntime().exec(query);
                    
                    process.waitFor();
                    
                    //Der Thread wird hier gestoppt, da die Dateierstellung noch 
                    //ein wenig Zeit benötigt
                    Thread.sleep(3000);
                    
                    String errMsg = "Fehler beim Exportieren des GRASS-Vektors in "
                            + "ein Shapevektor.";
                    
                    if(this.doesResultExits(output, errMsg)) {
                        this.filesToDelete.add(baseOutputDir + GRASSEXPORT + ".bat");
                        this.filesToDelete.add(baseOutputDir + GRASSEXPORT + ".py");
                        
                        this.logger.writeLog(LogPrefix.LOGINFO + "Der Export war "
                                + "erfolgreich");
                        
                        return true;
                    }
                } catch (IOException ex) {
                    //Log error
                } catch (InterruptedException ex) {
                    //Log error
                }
            } else {
                this.logger.writeLog(LogPrefix.LOGERROR + "Die Python-Datei konnte "
                        + "nicht erfolgreich in das Zielverzeichnis kopiert werden");
            }
        }
        
        return false;
    }
    
    /**
     * Methode zur Anwendung eines Smooth-Algorithmus auf das GRASS-Vektorshape.
     * Anhand des gewählten Smooth-Algorithmus werden die Parameter für die 
     * Ausführung des Smooths bestimmt. Anschliessend wird die Batch-Datei zur 
     * Ausführung des Python-Moduls und das Python-Modul für den Smooth-Algorithmus 
     * in das Zielverzeichnis kopiert und ausgeführt. Nach erfolgreicher Ausführung 
     * werden das Batch-File und das Python-File wieder gelöscht.
     * @return Erstellungsstatus des gesmoothen GRASS-Vektorshapes
     */
    private boolean makeGrassSmooth() {
        HashMap<String, String> grassSmoothArgs = new HashMap<>();
        String smoothAlogChosen = this.hConfig.getInputModel().getSmooth();
        String baseOutputDir = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\";
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Der gewählte Smooth-Algorithmus "
                + "wird auf das GRASS-Vektorshape angewandt");
        
        if(smoothAlogChosen.equals("Chaiken")) {
            grassSmoothArgs.put("input", GRASSVECTORIMPORT);
            grassSmoothArgs.put("type", ChaikenSmooth.TYPE);
            grassSmoothArgs.put("layer", ChaikenSmooth.LAYER);
            grassSmoothArgs.put("flags", ChaikenSmooth.FLAGS);
            grassSmoothArgs.put("method", ChaikenSmooth.METHOD);
            grassSmoothArgs.put("threshold", ChaikenSmooth.THRESHOLD);
            grassSmoothArgs.put("lookAhead", ChaikenSmooth.LOOK_AHEAD);
            grassSmoothArgs.put("reduction", ChaikenSmooth.REDUCTION);
            grassSmoothArgs.put("slide", ChaikenSmooth.SLIDE);
            grassSmoothArgs.put("angleThresh", ChaikenSmooth.ANGLE_THRESH);
            grassSmoothArgs.put("degreeThresh", ChaikenSmooth.DEGREE_THRESH);
            grassSmoothArgs.put("closenessThresh", ChaikenSmooth.CLOSENESS_THRESH);
            grassSmoothArgs.put("betweenessThresh", ChaikenSmooth.BETWEENESS_THRESH);
            grassSmoothArgs.put("alpha", ChaikenSmooth.ALPHA);
            grassSmoothArgs.put("beta", ChaikenSmooth.BETA);
            grassSmoothArgs.put("iterations", ChaikenSmooth.ITERATIONS);
            grassSmoothArgs.put("output", GRASSSMOOTH);
        } else if(smoothAlogChosen.equals("Hermite")) {
            grassSmoothArgs.put("input", GRASSVECTORIMPORT);
            grassSmoothArgs.put("type", HermiteSmooth.TYPE);
            grassSmoothArgs.put("layer", HermiteSmooth.LAYER);
            grassSmoothArgs.put("flags", HermiteSmooth.FLAGS);
            grassSmoothArgs.put("method", HermiteSmooth.METHOD);
            grassSmoothArgs.put("threshold", HermiteSmooth.THRESHOLD);
            grassSmoothArgs.put("lookAhead", HermiteSmooth.LOOK_AHEAD);
            grassSmoothArgs.put("reduction", HermiteSmooth.REDUCTION);
            grassSmoothArgs.put("slide", HermiteSmooth.SLIDE);
            grassSmoothArgs.put("angleThresh", HermiteSmooth.ANGLE_THRESH);
            grassSmoothArgs.put("degreeThresh", HermiteSmooth.DEGREE_THRESH);
            grassSmoothArgs.put("closenessThresh", HermiteSmooth.CLOSENESS_THRESH);
            grassSmoothArgs.put("betweenessThresh", HermiteSmooth.BETWEENESS_THRESH);
            grassSmoothArgs.put("alpha", HermiteSmooth.ALPHA);
            grassSmoothArgs.put("beta", HermiteSmooth.BETA);
            grassSmoothArgs.put("iterations", HermiteSmooth.ITERATIONS);
            grassSmoothArgs.put("output", GRASSSMOOTH);
        } else if(smoothAlogChosen.equals("Boyle")) {
            grassSmoothArgs.put("input", GRASSVECTORIMPORT);
            grassSmoothArgs.put("type", BoyleSmooth.TYPE);
            grassSmoothArgs.put("layer", BoyleSmooth.LAYER);
            grassSmoothArgs.put("flags", BoyleSmooth.FLAGS);
            grassSmoothArgs.put("method", BoyleSmooth.METHOD);
            grassSmoothArgs.put("threshold", BoyleSmooth.THRESHOLD);
            grassSmoothArgs.put("lookAhead", BoyleSmooth.LOOK_AHEAD);
            grassSmoothArgs.put("reduction", BoyleSmooth.REDUCTION);
            grassSmoothArgs.put("slide", BoyleSmooth.SLIDE);
            grassSmoothArgs.put("angleThresh", BoyleSmooth.ANGLE_THRESH);
            grassSmoothArgs.put("degreeThresh", BoyleSmooth.DEGREE_THRESH);
            grassSmoothArgs.put("closenessThresh", BoyleSmooth.CLOSENESS_THRESH);
            grassSmoothArgs.put("betweenessThresh", BoyleSmooth.BETWEENESS_THRESH);
            grassSmoothArgs.put("alpha", BoyleSmooth.ALPHA);
            grassSmoothArgs.put("beta", BoyleSmooth.BETA);
            grassSmoothArgs.put("iterations", BoyleSmooth.ITERATIONS);
            grassSmoothArgs.put("output", GRASSSMOOTH);
        } else {
            this.logger.writeLog(LogPrefix.LOGERROR + "Der gewählte Smooth-Algorithmus "
                    + "ist nicht in diesem Programm implementiert");
            
            return false;
        }
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei zum Anstossen "
                + "des Smooth-Alogrithmus wird in das Zielverzeichnis kopiert");
        if(this.writeGrassSmoothBatch(grassSmoothArgs)) {
            
            this.logger.writeLog(LogPrefix.LOGINFO + "Die Python-Datei wird in das "
                    + "Zielverzeichnis kopiert");
            
            if(fileTool.copyInsideFileToDisk(GRASSSMOOTH + ".py", baseOutputDir)) {
                try {
                    this.logger.writeLog(LogPrefix.LOGINFO + "Beginn der Smooth-Anwendung");
                    
                    String query = "cmd /c \"" + baseOutputDir
                            + GRASSSMOOTH + ".bat\"";
                    
                    Process process = Runtime.getRuntime().exec(query);
                    
                    process.waitFor();
                    
                    String output = this.grassGisdbase + "\\" + GRASSVECTORPATH + "\\"
                            + GRASSSMOOTH;
                    
                    String errMsg = "Fehler beim Durchführen des Smooth-Algorithmus";
                    if(this.doesResultExits(output, errMsg)) {
                        this.filesToDelete.add(baseOutputDir + GRASSSMOOTH + ".bat");
                        this.filesToDelete.add(baseOutputDir + GRASSSMOOTH + ".py");
                        
                        this.logger.writeLog(LogPrefix.LOGINFO + "Der Smooth-Algorithmus "
                                + "wurde erfolgreich angewandt");
                        
                        return true;
                    }
                } catch (IOException ex) {
                    //Log error
                } catch (InterruptedException ex) {
                    //Log error
                }
            } else {
                this.logger.writeLog(LogPrefix.LOGERROR + "Die Python-Datei konnte "
                        + "nicht erfolgreich in das Zielverzeichnis kopiert werden");
            }
        } 
        
        return false;
    }
    
    /**
     * Methode zum Import des Höhenlinien-Datensatzes (ESRI Shapefile) nach GRASS.
     * In dieser Methode wird der bestehende Höhenlinien-Datensatz in ein GRASS-
     * Vektorshape konvertiert. 
     * @return 
     */
    private boolean makeGrassVectorImport() {
        HashMap<String, String> vectorImportArgs = new HashMap<>();
        vectorImportArgs.put("input", baseData);
        vectorImportArgs.put("output", GRASSVECTORIMPORT);
        String baseOutputDir = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\";
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Das Vektorshape wird in ein GRASS-"
                + "Vektorshape konvertiert");
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei zum Anstossen "
                + "der Konvertierung wird in das Zielverzeichnis kopiert");
        if(this.writeGrassVectorImportBatch(vectorImportArgs)) {
            
            this.logger.writeLog(LogPrefix.LOGINFO + "Die Python-Datei wird in das "
                    + "Zielverzeichnis kopiert");
            
            if(fileTool.copyInsideFileToDisk(GRASSVECTORIMPORT + ".py", baseOutputDir)) {
                try {
                    
                    this.logger.writeLog(LogPrefix.LOGINFO + "Beginn der Konvertierung");
                    
                    String query = "cmd /c \"" + baseOutputDir
                            + GRASSVECTORIMPORT + ".bat\"";
                    
                    Process process = Runtime.getRuntime().exec(query);
                    
                    process.waitFor();
                    
                    String output = this.grassGisdbase + "\\" + GRASSVECTORPATH + "\\"
                            + GRASSVECTORIMPORT;
                    
                    String errMsg = "Fehler bei der Konvertierung der Vektorshape-Datei "
                            + "in ein GRASS-Vektorshape.";
                    if(this.doesResultExits(output, errMsg)) {
                        this.filesToDelete.add(baseOutputDir + GRASSVECTORIMPORT + ".bat");
                        this.filesToDelete.add(baseOutputDir + GRASSVECTORIMPORT + ".py");
                        
                        this.logger.writeLog(LogPrefix.LOGINFO + "Die Vektorshape-Datei "
                                + "wurde erfolgreich konvertiert");
                        
                        return true;
                    }
                } catch (IOException ex) {
                    //Log error
                } catch (InterruptedException ex) {
                    //Log error
                }
            } else {
                this.logger.writeLog(LogPrefix.LOGERROR + "Die Python-Datei konnte "
                        + "nicht erfolgreich in das Zielverzeichnis kopiert werden");
            }
        }
        
        return false;
    }
    
    private boolean writeGrassExportBatch(HashMap<String, String> args) {
        String batchname = GRASSEXPORT + ".bat";
        String pythonmodule = GRASSEXPORT + ".py";
        ArrayList<String> properArgs = new ArrayList<>();
        properArgs.add(args.get("input"));
        properArgs.add(args.get("type"));
        properArgs.add(args.get("layer"));
        properArgs.add(args.get("format"));
        properArgs.add(args.get("output"));
        properArgs.add(args.get("olayer"));
        properArgs.add(args.get("flags"));
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei wird in das "
                + "Zielverzeichnis kopiert");
        
        return this.writeGrassBatch(batchname, pythonmodule, properArgs);
    }
    
    private boolean writeGrassSmoothBatch(HashMap<String, String> args) {
        String batchname = GRASSSMOOTH + ".bat";
        String pythonmodule = GRASSSMOOTH + ".py";
        ArrayList<String> properArgs = new ArrayList<>();
        properArgs.add(args.get("input"));
        properArgs.add(args.get("type"));
        properArgs.add(args.get("layer"));
        properArgs.add(args.get("flags"));
        properArgs.add(args.get("method"));
        properArgs.add(args.get("threshold"));
        properArgs.add(args.get("lookAhead"));
        properArgs.add(args.get("reduction"));
        properArgs.add(args.get("slide"));
        properArgs.add(args.get("angleThresh"));
        properArgs.add(args.get("degreeThresh"));
        properArgs.add(args.get("closenessThresh"));
        properArgs.add(args.get("betweenessThresh"));
        properArgs.add(args.get("alpha"));
        properArgs.add(args.get("beta"));
        properArgs.add(args.get("iterations"));
        properArgs.add(args.get("output"));
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei wird in das "
                + "Zielverzeichnis kopiert");
        
        return this.writeGrassBatch(batchname, pythonmodule, properArgs);
    }
    
    private boolean writeGrassVectorImportBatch(HashMap<String, String> args) {
        String batchname = GRASSVECTORIMPORT + ".bat";
        String pythonmodule = GRASSVECTORIMPORT + ".py";
        ArrayList<String> properArgs = new ArrayList<>();
        properArgs.add(args.get("input"));
        properArgs.add(args.get("output"));
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei wird in das "
                + "Zielverzeichnis kopiert");
        
        return this.writeGrassBatch(batchname, pythonmodule, properArgs);
    }
    
    private boolean writeGrassBatch(String batchname, String pythonmodule, 
            ArrayList<String> args) {
        if(pythonmodule.endsWith(".py")) {
            String output = this.hConfig.getInputModel().getOutput().getAbsolutePath() + "\\"
                    + batchname;

            String grassGisbase = this.hConfig.getPathModel().getGrasspath().getAbsolutePath();
            String grassPath = grassGisbase + "\\bin;" + grassGisbase + "\\scripts;"
                    + grassGisbase + "\\lib;" + this.hConfig.getPathModel().getGrassbinpath()
                            .getAbsolutePath();

            String batchContent = "@echo off \n"
                    + "set GISBASE=" + grassGisbase + "\n" 
                    + "set GISRC=" + this.grassGisrc + "\n"
                    + "set Path=" + grassPath + "\n" 
                    + "\"" + this.hConfig.getPathModel().getPythonpath().getAbsolutePath() + "\\python.exe\" "
                    + "\"" + this.hConfig.getInputModel().getOutput().getAbsolutePath() + "\\"
                    + pythonmodule + "\" \"" + grassGisbase + "\" \"" + grassGisdbase + "\" \""
                    + grassLocation + "\" \"" + grassMapset + "\" ";

            for (String grassArg : args) {
                batchContent += "\"" + grassArg + "\" ";
            }

            if(fileTool.writeBatchFile(output, batchContent)) {
                this.logger.writeLog(LogPrefix.LOGINFO + "Die Batch-Datei wurde erfolgreich "
                        + "in das Zielverzeichnis kopiert");
                
                return true;
            } else {
                this.logger.writeLog(LogPrefix.LOGERROR + "Die Batch-Datei konnte nicht "
                        + "in das Zielverzeichnis kopiert werden. Bitte überprüfen Sie, "
                        + "ob Sie auf das Zielverzeichnis Schreibbrechtigungen haben oder "
                        + "kontaktieren Sie Ihren Administrator und senden Sie ihm Ihr"
                        + "Logfile");
            }
        }
        return false;
    }
    
    private boolean initializeGrass() {
        grassGisdbase = this.hConfig.getInputModel().getOutput().getAbsolutePath() + "\\"
                + "grassmapset";
        grassLocation = "glocation";
        grassMapset = "grassmapset";
        grassGisrc = this.hConfig.getInputModel().getOutput().getAbsolutePath() + "\\"
                + "grassrc6";
        
        String grassrcContent = "GISBASE: " + grassGisdbase + "\nLOCATION: " 
                + grassLocation + "\nMAPSET: " + grassMapset + "\n";
        
        this.logger.writeLog(LogPrefix.LOGINFO + "Die grassrc6-Datei wird in das "
                + "Zielverzeichnis kopiert.");
        
        if(fileTool.writeGrassrcFile(grassGisrc, grassrcContent)) {
            /*if(fileTool.copyInsideFileToDisk("grassmapset", this.hConfig
                    .getInputModel().getOutput().getAbsolutePath())) {
                return true;
            }*/
            
            this.logger.writeLog(LogPrefix.LOGINFO + "Die grassrc6-Datei wurde in "
                    + "das Zielverzeichnis geschrieben");
            
            this.logger.writeLog(LogPrefix.LOGINFO + "Das GRASS-Mapset wird in das "
                    + "Zielverzeichnis kopiert");
            if(fileTool.copyInsideDirToDisc("grassmapset.zip", this.hConfig
                    .getInputModel().getOutput().getAbsolutePath())) {
                return true;
            } else {
                this.logger.writeLog(LogPrefix.LOGERROR + "Das GRASS-Mapset konnte nicht "
                    + "in das Zielverzeichnis kopiert werden, bitte schauen Sie "
                    + "ob Sie auf das Verzeichnis Schreibberechtigungen haben oder "
                    + "melden Sie sich bei Ihrem Administrator und senden Sie ihm "
                    + "Ihr Logfile");
            }
        } else {
            this.logger.writeLog(LogPrefix.LOGERROR + "Die grassrc6-Datei konnte nicht "
                    + "in das Zielverzeichnis kopiert werden, bitte schauen Sie "
                    + "ob Sie auf das Verzeichnis Schreibberechtigungen haben oder "
                    + "melden Sie sich bei Ihrem Administrator und senden Sie ihm "
                    + "Ihr Logfile");
        }
        
        return false;
    }
    
    public void clearGrass() {
        String baseOutputDir = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\";
        String mapsetDir = baseOutputDir + GRASSMAPSETDIR;
        this.fileTool.deleteExistingDir(mapsetDir);
        this.fileTool.deleteExistingFile(grassGisrc);
    }
    
    /**
     * Methode zum Erstellen der Höhenlinien aus dem ASC-Auschnitt
     * @return Erstellungsstatus
     */
    private boolean makeContourlines() {
        try {
            String flag3D = "";
            
//            logger.writeLog("Höhenlinien werden aus dem Auschnitt erstellt");
//            logger.writeLog("-------------------------");
//            logger.writeLog("");
            logger.writeLog(LogPrefix.LOGINFO + "Höhenlinien werden aus dem Auschnitt erstellt");
            
            String source = baseData;
            baseData = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\" + contourlinesshapename + ".shp";
            
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
            
            String query = "\""+ gdalcontour + "\" -i " + hConfig.getInputModel().getAequidistance()
                    + " -a hoehe " + flag3D + " \"" + source + "\" \"" + baseData + "\"";
            
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
                
                this.filesToDelete.add(source);
                
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
    
    /**
     * Methode zum Ausdünnen der Punkte des ASC-Ausschnittes
     * @return Erstellungsstatus
     */
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
                
                String query = "\"" + gdaltranslate + "\" -projwin " + rectangle + " -of AAIGrid "
                        + "-outsize " + percentThinning + " " + percentThinning
                        + " -co force_cellsize=true \"" + source + "\" \"" + baseData + "\"";
                
                System.out.println(query);
                
                //logger.writeLog("Befehl: " + query);
                logger.writeLog(LogPrefix.LOGQUERY + query);
                
                Process process = Runtime.getRuntime().exec(query);
                
                process.waitFor();
                
                String errResult = "Der Ausschnitt konnte nicht korrekt ausgedünnt werden.";
            
                if(this.doesResultExits(baseData, errResult)) {
                    //logger.writeLog("Ausdünnung wurde durchgeführt.\n ----------------");
                    logger.writeLog(LogPrefix.LOGINFO + "Ausdünnung wurde durchgeführt.");
                    
                    this.logger.writeStatus("Ausschnitt wurde ausgedünnt.");
                    
                    this.filesToDelete.add(source);
                    
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
                this.logger.writeStatus("Virtuelles Format wurde erstellt.");
                
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
                this.logger.writeErrorStatus("Virtuelles Format konnte nicht erstellt werden.");
                
//                errMsg += "Die VRT-Datei konnte nicht erstellt werden.\n"
//                        + "Bitte prüfen Sie, ob Sie den korrekten GDAL-Installationspfad "
//                        + "angegeben haben. Falls dies nicht die Ursache ist, "
//                        + "so melden Sie sich bitte beim Administrator und schicken "
//                        + "Sie ihm das Logfile";
//                logger.writeLog(errMsg);
                errMsg += "Die VRT-Datei konnte nicht erstellt werden. "
                        + "Bitte prüfen Sie, ob Sie den korrekten GDAL-Installationspfad "
                        + "angegeben haben oder ob das von Ihnen gewählte Verzeichnis "
                        + "beschreibbar ist. Falls dies nicht die Ursache ist, "
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
            
            String query = "\"" + gdaltranslate + "\" -of GTiff \"" 
                    + fittedTif + "\" \"" + baseData + "\"";
            
            //logger.writeLog("Befehl: " + query);
            logger.writeLog(LogPrefix.LOGQUERY + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            String errResult = "Die TIFF-Datei konnte nicht korrekt umgewandelt werden.";
            
            if(this.doesResultExits(baseData, errResult)) {
                //logger.writeLog("TIFF-Datei wurde umgewandelt.\n ----------------");
                logger.writeLog(LogPrefix.LOGINFO + "TIFF-Datei wurde umgewandelt.");
                this.filesToDelete.add(fittedTif);
                
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
            
            String query = "\"" + gdaltranslate + "\" ";
            
            if(this.standalone) {
                String[] splittedRectangle = hConfig.getInputModel().getCoordRectangle().split(",");
                String rectangle = splittedRectangle[0].trim() + " " + splittedRectangle[3].trim()
                        + " " + splittedRectangle[2].trim() + " " + splittedRectangle[1].trim();
                
                query += "-projwin " + rectangle + " ";
            } 
            
            query += "-of GTiff \"" + vrtRectangle + "\" \"" + fittedTif + "\"";
//            String query = gdaltranslate + " -projwin " + rectangle + " -of GTiff "
//                    + vrtRectangle + " " + fittedTif;
            
            //logger.writeLog("Befehl: " + query);
            logger.writeLog(LogPrefix.LOGQUERY + query);
            
            Process process = Runtime.getRuntime().exec(query);
            
            process.waitFor();
            
            String errResult = "Die VRT-Datei konnte nicht korrekt umgewandelt und "
                    + "zugeschnitten werden.";
            
            if(this.doesResultExits(fittedTif, errResult)) {
                //logger.writeLog("VRT-Datei wurde umgewandelt und zugeschnitten.\n ----------------");
                logger.writeLog(LogPrefix.LOGINFO + "VRT-Datei wurde umgewandelt und zugeschnitten.");
                this.filesToDelete.add(vrtRectangle);
                
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
            
            System.out.println(vrtRectangle);
            
            //logger.writeLog("Output: " + vrtRectangle);
            logger.writeLog(LogPrefix.LOGDATAOUTPUT + vrtRectangle);
            
            String query = "\"" + gdalbuildvrt + "\" \"" + vrtRectangle + "\" ";
            
            for (String kachel : kacheln) {
                query += "\"" + lidardataPath + "\\" + kachel + "\" ";
            }
            
            //logger.writeLog("Befehl: " + query);
            System.out.println(query);
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
        if(this.standalone) {
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
        } else {
            String filename = "*.tif";
            
            System.out.println(lidardataPath);
            
            scanner.setIncludes(new String[]{"**/" + filename});
            scanner.setBasedir(lidardataPath);
            scanner.setCaseSensitive(false);
            scanner.scan();
            String[] files = scanner.getIncludedFiles();

            this.kacheln.addAll(Arrays.asList(files));
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
    
    /**
     * Methode zum Prüfen ob das Resultat auf der Festplatte existiert
     * @param filepath Pfad zur Datei
     * @param errMsg Fehlermeldung
     * @return Existenzstatus
     */
    private boolean doesResultExits(String filepath, String errMsg) {
        if(!this.fileTool.doesFileExists(filepath)) {
//            this.logger.writeLog(errMsg);
//            this.logger.writeLog("");
            this.logger.writeLog(LogPrefix.LOGERROR + errMsg);
            
            return false;
        }
        
        return true;
    }
    
    private boolean clearOutputDir() {
        String baseOutputDir = hConfig.getInputModel().getOutput().getAbsolutePath() + "\\";
        String mapsetDir = baseOutputDir + GRASSMAPSETDIR;
        String contourlinesDBF = baseOutputDir + contourlinesshapename + ".dbf";
        String contourlinesSHX = baseOutputDir + contourlinesshapename + ".shx";
        String contourlinesSHP = baseOutputDir + contourlinesshapename + ".shp";
        
        int errNumber = 0;
        
        if(!fileTool.deleteExistingDir(mapsetDir)) {
            errNumber += 1;
        }
        
        if(!fileTool.deleteExistingFile(contourlinesDBF)) {
            errNumber += 1;
        }
        
        if(!fileTool.deleteExistingFile(contourlinesSHP)) {
            errNumber += 1;
        }
        
        if(!fileTool.deleteExistingFile(contourlinesSHX)) {
            errNumber += 1;
        }
        
        if(errNumber > 0) {
            return false;
        }
        
        return true;
    }
    
    private boolean deleteOldFiles() {
        for (String fileToDelete : filesToDelete) {
            if(!fileTool.deleteExistingFile(fileToDelete)) {
                return false;
            }
        }
        
        return true;
    }
}
