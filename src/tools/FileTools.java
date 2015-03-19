/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public void writeLogfile(String logfile, String content) {
        if(!logfile.endsWith(".log")){
            logfile = logfile + ".log";
        }
        
        this.writeFile(true, logfile, content);
    }
    
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
}
