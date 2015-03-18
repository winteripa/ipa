/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

/**
 *
 * @author u203011
 */
public class Configuration {
    private String configName = null;
    private String pythonpath = null;
    private String gdalpath = null;
    private String grasspath = null;
    private String grassbinpath = null;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getPythonpath() {
        return pythonpath;
    }

    public void setPythonpath(String pythonpath) {
        this.pythonpath = pythonpath;
    }

    public String getGdalpath() {
        return gdalpath;
    }

    public void setGdalpath(String gdalpath) {
        this.gdalpath = gdalpath;
    }

    public String getGrasspath() {
        return grasspath;
    }

    public void setGrasspath(String grasspath) {
        this.grasspath = grasspath;
    }

    public String getGrassbinpath() {
        return grassbinpath;
    }

    public void setGrassbinpath(String grassbinpath) {
        this.grassbinpath = grassbinpath;
    }
    
    @Override
    public String toString() {
        return this.configName;
    }
}
