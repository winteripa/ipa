/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Klasse mit nützlichen Methoden für den Umgang mit Dateien
 * @author u203011
 */
public class FileTools {

    private ArrayList<String> filesToZip = null;
    
    /**
     * Methode für das parsen einer XML-Datei.
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
     * Methode zum Schreiben des Logfiles.
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
     * Methode zum Schreiben einer Datei.
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
     * Methode für das Löschen einer Datei.
     * Diese Methode löscht eine existierende Datei auf dem Dateisystem. Falls 
     * die Datei nicht vorhanden ist, gilt sie als gelöscht.
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
     * Methode für das Löschen eines Verzeichnisses.
     * Diese Methode löscht ein existierendes Verzeichnis auf dem Dateisystem. Falls 
     * das Verzeichnis nicht vorhanden ist, gilt es als gelöscht.
     * @param dirpath
     * @return 
     */
    public boolean deleteExistingDir(String dirpath) {
        try {
            File file = new File(dirpath);
            
            if(file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            
            if(!file.exists()) {
                return true;
            }
        } catch (IOException ex) {
            //Hier kommt kein Code rein, da dieser Error nur geworfen wird, 
            //falls die Datei zum Löschen nicht vorhanden wäre
        }
        
        return false;
    }
    
    /**
     * Methode zum Prüfen, ob eine Datei existiert.
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
     * Methode zum Schreiben einer Datei innerhalb der Jar-Datei in ein bestimmtes Verzeichnis.
     * Diese Methode kopiert die Datei, welche sich im JAR befindet und fügt 
     * sie im Zielverzeichnis ein.
     * @param insideRes Dateiname für die Datei innerhalb des 'res'-Packages
     * @param outputPath Ausgabe-Verzeichnis
     * @return Erstellungsstatus
     */
    public boolean copyInsideFileToDisk(String insideRes, String outputPath) {
        OutputStream resOutStream = null;
        InputStream resInStream = null;
        
        String output = outputPath + "\\" + insideRes;
        try {
            resInStream = FileTools.class.getResourceAsStream("/res/" + insideRes);
            resOutStream = new FileOutputStream(output);
            
            IOUtils.copy(resInStream, resOutStream);
            
            if(this.doesFileExists(output)) {
                return true;
            }
        } catch (FileNotFoundException ex) {
            //Log error
        } catch (IOException ex) {
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
    
    /**
     * Methode zum Schreiben eines Verzeichnisses innerhalb der Jar-Datei in ein bestimmtes Verzeichnis.
     * Das Verzeichnis muss gezippt sein. Der Inhalt der ZIP-Datei (ein Verzeichnis) 
     * wird in das Zielverzeichnis entpackt.
     * @param insideRes Dateiname für die Datei innerhalb des 'res'-Packages
     * @param outputPath Ausgabe-Verzeichnis
     * @return Erstellungsstatus
     */
    public boolean copyInsideDirToDisc(String insideRes, String outputPath) {
        try {
            InputStream resInStream = FileTools.class.getResourceAsStream("/res/" + insideRes);
            ZipInputStream zis = new ZipInputStream(resInStream);
            byte[] buffer = new byte[1024];
            ZipEntry zipContent = zis.getNextEntry();
            
            while (zipContent != null) {
                String filename = zipContent.getName();
                File fileToWrite = new File(outputPath + "\\" + filename);
                
                //Diese Zeile ist nötig damit keine FileNotFoundExceptions produziert werden
                //new File(fileToWrite.getParent()).mkdirs();
                if(zipContent.isDirectory()) {
                    if(!fileToWrite.mkdirs()) {
                        return false;
                    }
                    zipContent = zis.getNextEntry();
                    continue;
                }
                
                FileOutputStream resOutStream = new FileOutputStream(fileToWrite);
                
                int filelen;
                while((filelen = zis.read(buffer)) > 0) {
                    resOutStream.write(buffer, 0, filelen);
                }
                
                resOutStream.close();
                zis.closeEntry();
                zipContent = zis.getNextEntry();
            }
            
            zis.close();
        } catch (IOException ex) {
            //Log error
        }
        
        return true;
    }
    
    /**
     * Methode zum Schreiben eines Batch-Files in ein Verzeichnis.
     * @param batchpath Pfad + Dateiname des Batches, welches angelegt werden soll.
     * @param content Inhalt des Batch-Files
     * @return Erstellungsstatus
     */
    public boolean writeBatchFile(String batchpath, String content) {
        if(deleteExistingFile(batchpath)) {
            this.writeFile(false, batchpath, content);
            if(doesFileExists(batchpath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Methode zum Schreiben einer Gisrc-Datei in ein Verzeichnis.
     * @param grassrcpath Pfad + Dateiname der Gisrc-Datei, welche angelegt werden soll.
     * @param content Inhalt der Gisrc-Datei
     * @return Erstellungsstatus
     */
    public boolean writeGrassrcFile(String grassrcpath, String content) {
        if(deleteExistingFile(grassrcpath)) {
            this.writeFile(false, grassrcpath, content);
            if(doesFileExists(grassrcpath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Methode zur Erstellung eines Verzeichnisses.
     * In dieser Methode wird ein neues Verzeichnis erstellt. Falls das Verzeichnis 
     * schon vorhanden sein sollte, so gilt es als erstellt.
     * @param dirname Pfad zum neuen Verzeichnis
     * @return Erstellungsstatus
     */
    public boolean createDir(String dirname) {
        File dir = new File(dirname);
        
        if(dir.exists()) {
            return true;
        }
        
        return dir.mkdir();
    }
    
    /**
     * Methode zum Speichern eines Bildes aus einer URL.
     * Das Bild aus der URL wird im Zielverzeichnis als PNG-Datei abgespeichert.
     * @param strUrl URL zum Bild
     * @param outputFile Pfad zum zu erstellenden Bild
     * @return Erstellungsstatus
     */
    public boolean saveImgFromUrl(String strUrl, String outputFile) {
        BufferedImage img = null;

        try {
            URL url = new URL(strUrl);

            img = ImageIO.read(url);

            if(img == null) {
                //Es wird kein Bild zurückgegeben, sondern eine Logdatei 
                //mit WMS-Fehlermeldungen
                return false;
            }
            
            ImageIO.write(img, "png", new File(outputFile));

            if(this.doesFileExists(outputFile)) {
                return true;
            }
        } catch(IOException ex) {
            //Log error
        }
        
        return false;
    }
    
    /**
     * Methode zur Erstellung einer ZIP-Datei.
     * In dieser Methode wird ein Verzeichnis in eine ZIP-Datei verpackt. 
     * Alle Unterverzeichnisse sind enthalten.
     * @param folderToZip Zu zippendes Verzeichnis
     * @param zipfile Pfad zur zu erstellenden ZIP-Datei
     * @return Erstellungsstatus
     */
    public boolean makeZip(String folderToZip, String zipfile) {
        this.filesToZip = new ArrayList<>();
        this.addFilesToZip(new File(folderToZip), folderToZip);
        
        byte[] buffer = new byte[1024];
        
        try {
            FileOutputStream fileOutStream = new FileOutputStream(zipfile);
            ZipOutputStream zipOutStream = new ZipOutputStream(fileOutStream);
            
            for (String fileToZip : this.filesToZip) {
                ZipEntry zEntry = new ZipEntry(fileToZip);
                zipOutStream.putNextEntry(zEntry);
                
                FileInputStream fileInStream = new FileInputStream(folderToZip + "\\"
                        + fileToZip);
                
                int filelen;
                while((filelen = fileInStream.read(buffer)) > 0) {
                    zipOutStream.write(buffer, 0, filelen);
                }
                
                fileInStream.close();
            }
            zipOutStream.closeEntry();
            zipOutStream.close();
            
            if(this.doesFileExists(zipfile)) {
                return true;
            }
        } catch (IOException e) {
            //Log error
        }
        
        return false;
    }
    
    /**
     * Methode zum Hinzufügen von Dateien in ein ZIP-Archiv.
     * In dieser Methode werden alle Pfade mit zu zippenden Dateien gesammelt. 
     * Wichtig: Der Pfad geht nur bis Zum Verzeichnis, das gezippt werden soll.
     * @param currDir Momentanes Unterverzeichnis (oder Datei) im Order, der gezippt werden soll
     * @param source Verzeichnis, das gezippt werden soll
     */
    private void addFilesToZip(File currDir, String source) {
        if(this.filesToZip != null) {
            if(currDir.isFile()) {
                this.filesToZip.add(getZipPath(currDir.getAbsoluteFile().toString(), source));
            }

            if(currDir.isDirectory()) {
                String[] subDirs = currDir.list();
                for (String subDir : subDirs) {
                    addFilesToZip(new File(currDir, subDir), source);
                }
            }
        }
    }
    
    /**
     * Methode zum Erhalt des Pfades zur Datei innerhalb des zu zippenden Verzeichnisses.
     * Diese Methode gibt den Pfad bis zum Verzeichnis, welches gezippt werden soll, 
     * zurück, nicht den absoluten Pfad.
     * @param absFile Absoluter Pfad bis zur Datei
     * @param source Verzeichnis, das gezippt werden soll
     * @return Pfad zur Datei
     */
    private String getZipPath(String absFile, String source) {
        return absFile.substring(source.length()+1, absFile.length());
    }
}
