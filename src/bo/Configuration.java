/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

/**
 * Klasse, welche die Grundlage für das Combobox-Element des PathGUIs bildet
 * @author u203011
 */
public class Configuration {
    private String configName = null;
    private String pythonpath = null;
    private String gdalpath = null;
    private String grasspath = null;
    private String grassbinpath = null;

    /**
     * Getter-Methode für den Konfigurationsnamen
     * @return Konfigurationsnamen
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * Setter-Methode für den Konfigurationsnamen
     * @param configName Konfigurationsnamen
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * Getter-Methode für den Python-Installationspfad
     * @return Python-Installationspfad
     */
    public String getPythonpath() {
        return pythonpath;
    }

    /**
     * Setter-Methode für den Python-Installationspfad
     * @param pythonpath Python-Installationspfad
     */
    public void setPythonpath(String pythonpath) {
        this.pythonpath = pythonpath;
    }

    /**
     * Getter-Methode für den GDAL-Installationspfad
     * @return GDAL-Installationspfad
     */
    public String getGdalpath() {
        return gdalpath;
    }

    /**
     * Setter-Methode für den GDAL-Installationspfad
     * @param gdalpath GDAL-Installationspfad
     */
    public void setGdalpath(String gdalpath) {
        this.gdalpath = gdalpath;
    }

    /**
     * Getter-Methode für den GRASS-Installationspfad
     * @return GRASS-Installationspfad
     */
    public String getGrasspath() {
        return grasspath;
    }

    /**
     * Setter-Methode für den GRASS-Installationspfad
     * @param grasspath GRASS-Installationspfad
     */
    public void setGrasspath(String grasspath) {
        this.grasspath = grasspath;
    }

    /**
     * Getter-Methode für das GRASS-BIN Bibliotheksverzeichnis
     * @return GRASS-BIN Bibliotheksverzeichnis
     */
    public String getGrassbinpath() {
        return grassbinpath;
    }

    /**
     * Setter-Methode für das GRASS-BIN Bibliotheksverzeichnis
     * @param grassbinpath GRASS-BIN Bibliotheksverzeichnis
     */
    public void setGrassbinpath(String grassbinpath) {
        this.grassbinpath = grassbinpath;
    }
    
    /**
     * Methode, welche die toString()-Standardmethode überschreibt für die richtige Darstellung in der Combobox
     * @return Konfigurationsname
     */
    @Override
    public String toString() {
        return this.configName;
    }
}
