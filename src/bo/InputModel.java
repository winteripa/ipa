/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

import java.io.File;

/**
 * Klasse für die Beschreibung der Steuerungsparameter
 * @author u203011
 */
public class InputModel implements IMODEL {
    
    private Integer orderNumber = null;
    private String coordRectangle = null;
    private File output = null;
    private Integer aequidistance = null;
    private String smooth = null;
    private Integer thinning = null;
    private Boolean force3D = null;
    private File lidardatapath = null;
    private File logdir = null;

    /**
     * Getter-Methode für die Bestellnummer
     * @return Bestellnummer
     */
    public Integer getOrderNumber() {
        return orderNumber;
    }

    /**
     * Setter-Methode für die Bestellnummer
     * @param orderNumber Bestellnummer
     */
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Getter-Methode für das Ausschnitt-Rechteck
     * @return Ausschnitt-Rechteck
     */
    public String getCoordRectangle() {
        return coordRectangle;
    }

    /**
     * Setter-Methode für das Ausschnitt-Rechteck
     * @param coordRectangle Ausschnitt-Rechteck
     */
    public void setCoordRectangle(String coordRectangle) {
        this.coordRectangle = coordRectangle;
    }

    /**
     * Getter-Methode für den Ausgabe-Pfad
     * @return Ausgabe-Pfad
     */
    public File getOutput() {
        return output;
    }

    /**
     * Setter-Methode für den Ausgabe-Pfad
     * @param output Ausgabe-Pfad
     */
    public void setOutput(File output) {
        this.output = output;
    }

    /**
     * Getter-Methode für die Äquidistanz
     * @return Äquidistanz
     */
    public Integer getAequidistance() {
        return aequidistance;
    }

    /**
     * Setter-Methode für die Äquidistanz
     * @param aequidistance Äquidistanz
     */
    public void setAequidistance(Integer aequidistance) {
        this.aequidistance = aequidistance;
    }

    /**
     * Getter-Methode für den Smooth-Algorithmus
     * @return Smooth-Algorithmus
     */
    public String getSmooth() {
        return smooth;
    }

    /**
     * Setter-Methode für den Smooth-Algorithmus
     * @param smooth Smooth-Algorithmus
     */
    public void setSmooth(String smooth) {
        this.smooth = smooth;
    }

    /**
     * Getter-Methode für die Ausdünnung
     * @return Ausdünnung
     */
    public Integer getThinning() {
        return thinning;
    }

    /**
     * Setter-Methode für die Ausdünnung
     * @param thinning Ausdünnung
     */
    public void setThinning(Integer thinning) {
        this.thinning = thinning;
    }

    /**
     * Getter-Methode für die 3D-Visualisierung
     * @return 3D-Visualisierung
     */
    public Boolean isForce3D() {
        return force3D;
    }

    /**
     * Getter-Methode für die 3D-Visualisierung
     * @param force3D 3D-Visualisierung
     */
    public void setForce3D(Boolean force3D) {
        this.force3D = force3D;
    }

    /**
     * Getter-Methode für den LiDAR-Datenpfad
     * @return LiDAR-Datenpfad
     */
    public File getLidardatapath() {
        return lidardatapath;
    }

    /**
     * Setter-Methode für den LiDAR-Datenpfad
     * @param lidardatapath LiDAR-Datenpfad
     */
    public void setLidardatapath(File lidardatapath) {
        this.lidardatapath = lidardatapath;
    }

    /**
     * Getter-Methode für das Logfile-Verzeichnis
     * @return Logfile-Verzeichnis
     */
    public File getLogdir() {
        return logdir;
    }

    /**
     * Setter-Methode für das Logfile-Verzeichnis
     * @param logdir Logfile-Verzeichnis
     */
    public void setLogdir(File logdir) {
        this.logdir = logdir;
    }
}
