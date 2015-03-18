/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

import java.io.File;

/**
 *
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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCoordRectangle() {
        return coordRectangle;
    }

    public void setCoordRectangle(String coordRectangle) {
        this.coordRectangle = coordRectangle;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Integer getAequidistance() {
        return aequidistance;
    }

    public void setAequidistance(Integer aequidistance) {
        this.aequidistance = aequidistance;
    }

    public String getSmooth() {
        return smooth;
    }

    public void setSmooth(String smooth) {
        this.smooth = smooth;
    }

    public Integer getThinning() {
        return thinning;
    }

    public void setThinning(Integer thinning) {
        this.thinning = thinning;
    }

    public Boolean isForce3D() {
        return force3D;
    }

    public void setForce3D(Boolean force3D) {
        this.force3D = force3D;
    }

    public File getLidardatapath() {
        return lidardatapath;
    }

    public void setLidardatapath(File lidardatapath) {
        this.lidardatapath = lidardatapath;
    }

    public File getLogdir() {
        return logdir;
    }

    public void setLogdir(File logdir) {
        this.logdir = logdir;
    }
}
