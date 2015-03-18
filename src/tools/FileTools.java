/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author u203011
 */
public class FileTools {
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
}
