/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Klasse mit nützlichen Methoden für den Umgang mit Dateien
 * @author u203011
 */
public class FileTools {

    /**
     * Methode für das parsen einer XML-Datei
     * @param path Pfad zur XML-Datei
     * @return DOM-Objekt der XML-Datei oder null
     */
    public Document parseXMLFile(String path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(path);
            dom.getDocumentElement().normalize();
            
            return dom;
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            //Log exception
        } catch (SAXException ex) {
            ex.printStackTrace();
            //Log exception
        } catch (IOException ex) {
            ex.printStackTrace();
            //Log exception
        }
        
        return null;
    }
    
    /**
     * Methode zum Schreiben des Logfiles
     * @param logfile Logfile-Pfad
     * @param content Inhalt, der in das Logfile geschrieben wird
     */
    public void writeLogfile(String logfile, String content) {
        if(!logfile.endsWith(".log")){
            logfile = logfile + ".log";
        }
        
        this.writeFile(true, logfile, content);
    }
    
    /**
     * Methode zum Schreiben einer Datei 
     * @param append Flag, ob der Text einer Datei hinzugefügt werden soll
     * @param filepath Pfad zur Datei
     * @param content Inhalt, der in die Datei geschrieben wird
     */
    private void writeFile(boolean append, String filepath, String content) {
        try {
            File file = new File(filepath);

            if(!file.exists()) {
                file.createNewFile();
            }
        
            FileWriter fw = new FileWriter(file, append);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content + "\n");
            bw.close();
        } catch(IOException e) {
            //Log error
        }
    }
    
    /**
     * Methode für das Löschen einer Datei
     * @param filepath Pfad zur Datei
     * @return Löschstatus
     */
    public boolean deleteExistingFile(String filepath) {
        File file = new File(filepath);
        
        if(file.exists()) {
            if(file.delete()) {
                return true;
            }
        } else {
            return true;
        }
        
        return false;
    }
    
    /**
     * Methode zum Prüfen, ob eine Datei existiert
     * @param filepath Pfad zur Datei
     * @return Existenzstatus
     */
    public boolean doesFileExists(String filepath) {
        File file = new File(filepath);
        
        if(file.exists()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Methode zum Schreiben einer Datei innerhalb der Jar-Datei in ein bestimmtes Verzeichnis
     * @param insideRes Dateiname für die Datei innerhalb des 'res'-Packages
     * @param outputPath Ausgabe-Verzeichnis
     * @return Erstellungsstatus
     */
    public boolean copyInsideFileToDisk(String insideRes, String outputPath) {
        OutputStream resOutStream = null;
        InputStream resInStream = null;
        
        String output = outputPath + insideRes;
        try {
            resInStream = FileTools.class.getResourceAsStream(insideRes);
            resOutStream = new FileOutputStream(output);
            
            IOUtils.copy(resInStream, resOutStream);
            
            if(this.doesFileExists(output)) {
                return true;
            }
        } catch (FileNotFoundException ex) {
            //Log error
        } finally {
            try {
                resInStream.close();
                resOutStream.close();
            } catch (IOException ex) {
                //Log error
            }
        }
        
        return false;
    }
}
