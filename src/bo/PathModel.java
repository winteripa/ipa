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
public class PathModel implements IMODEL {
    private File pythonpath = null;
    private File gdalpath = null;
    private File grasspath = null;
    private File grassbinpath = null;

    public File getPythonpath() {
        return pythonpath;
    }

    public void setPythonpath(File pythonpath) {
        this.pythonpath = pythonpath;
    }

    public File getGdalpath() {
        return gdalpath;
    }

    public void setGdalpath(File gdalpath) {
        this.gdalpath = gdalpath;
    }

    public File getGrasspath() {
        return grasspath;
    }

    public void setGrasspath(File grasspath) {
        this.grasspath = grasspath;
    }

    public File getGrassbinpath() {
        return grassbinpath;
    }

    public void setGrassbinpath(File grassbinpath) {
        this.grassbinpath = grassbinpath;
    }
    
    
}
