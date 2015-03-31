/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package module;

import base.DisplayMethods;
import bl.BusinessLogic;
import bo.HoehenlinienConfig;
import bo.LogPrefix;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import tools.FormatTools;
import tools.NumberTools;

/**
 * Startklasse für das Modul "Contourlines"
 * @author u203011
 */
public class Startclass {
    
    private ModulContourlines module;
    
    /**
     * Startmethode für die Ausführung des Moduls in der Standalone-Version.
     * @param hConfig Höhenlinien-Konfigurationsobjekt
     * @param arrKachelnr Liste mit allen Kachelnummern, welche mit dem gewählten Ausschnitt zu tun haben
     * @param logger Objekt eines DisplayMethods-Logger
     * @return Erstellungsstatus des Höhenlinien-Datensatzes
     */
    public boolean startModuleStandalone(HoehenlinienConfig hConfig, 
            ArrayList<String> arrKachelnr, DisplayMethods logger) {
        
        module = new ModulContourlines(true, hConfig, logger, arrKachelnr);
        if(module.prepareGenerateBase()) {
            return module.generateContourlines();
        }
        
        return false;
    }
    
    
    /**
     * Startmethode für die Ausführung des Moduls in der Modul-Version.
     * @param params Zeichenkette mit Parametern im Format '-key=value '
     * @param abrBL BusinessLogic-Objekt des Programms ABRaster (Für den Bezug der Pfade)
     * @param logger Objekt eines DisplayMethods-Logger
     * @return Erstellungsstatus des Höhenlinien-Datensatzes
     */
    public boolean startModule(String params, BusinessLogic abrBL, DisplayMethods logger) {
        HoehenlinienConfig hConfig = new HoehenlinienConfig();
        FormatTools formatTool = new FormatTools();
        NumberTools numberTool = new NumberTools();
        HashMap<String, String> extractedParams = formatTool.extractModuleCommandlineParams(params);
        
        hConfig.getPathModel().setGdalpath(new File(abrBL.getGdalpfad()));
        hConfig.getPathModel().setGrassbinpath(new File(abrBL.getGrassbinPfad()));
        hConfig.getPathModel().setGrasspath(new File(abrBL.getGrassPfad()));
        hConfig.getPathModel().setPythonpath(new File(abrBL.getPythonPfad()));
        
        if(extractedParams == null) {
            logger.writeLog(LogPrefix.LOGERROR + "Fehler bei der Extraktion der Modulparameter, "
                    + "bitte Überprüfen Sie die Parameter auf ihre Gültigkeit");
        }
        
        ArrayList<String> mbr = abrBL.getMbr();
        String strMbr = mbr.get(0) + "," + mbr.get(3) + "," + mbr.get(2) + ","
                + mbr.get(1);
        System.out.println(strMbr);
        
        hConfig.getInputModel().setCoordRectangle(strMbr);
        hConfig.getInputModel().setOrderNumber(abrBL.getBestellnummer());
        hConfig.getInputModel().setAequidistance(numberTool.convertToInteger(extractedParams.get("aequi")));
        hConfig.getInputModel().setForce3D(Boolean.valueOf(extractedParams.get("force3d")));
        hConfig.getInputModel().setLidardatapath(new File(abrBL.getTranslatePath()));
        hConfig.getInputModel().setOutput(new File(abrBL.getDownloadpfad()));
        hConfig.getInputModel().setSmooth(extractedParams.get("smooth"));
        hConfig.getInputModel().setThinning(numberTool.convertToInteger(extractedParams.get("thinning")));
        
        //String baseData = extractedParams.get("basedata");
        
        module = new ModulContourlines(false, hConfig, logger);
        if(module.prepareGenerateBase()) {
            return module.generateContourlines();
        }
        
        return false;
    }
}
