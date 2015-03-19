/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

import java.io.File;

/**
 * Klasse für die Beschreibung der Konfigurationspfade
 * @author u203011
 */
public class PathModel implements IMODEL {
    private File pythonpath = null;
    private File gdalpath = null;
    private File grasspath = null;
    private File grassbinpath = null;

    /**
     * Getter-Methode für den Python-Installationspfad
     * @return Python-Installationspfad
     */
    public File getPythonpath() {
        return pythonpath;
    }

    /**
     * Setter-Methode für den Python-Installationspfad
     * @param pythonpath Python-Installationspfad
     */
    public void setPythonpath(File pythonpath) {
        this.pythonpath = pythonpath;
    }

    /**
     * Getter-Methode für den GDAL-Installationspfad
     * @return GDAL-Installationspfad
     */
    public File getGdalpath() {
        return gdalpath;
    }

    /**
     * Setter-Methode für den GDAL-Installationspfad
     * @param gdalpath GDAL-Installationspfad
     */
    public void setGdalpath(File gdalpath) {
        this.gdalpath = gdalpath;
    }

    /**
     * Getter-Methode für den GRASS-Installationspfad
     * @return GRASS-Installationspfad
     */
    public File getGrasspath() {
        return grasspath;
    }

    /**
     * Setter-Methode für den GRASS-Installationspfad
     * @param grasspath GRASS-Installationspfad
     */
    public void setGrasspath(File grasspath) {
        this.grasspath = grasspath;
    }

    /**
     * Getter-Methode für das GRASS-BIN Bibliotheksverzeichnis
     * @return GRASS-BIN Bibliotheksverzeichnis
     */
    public File getGrassbinpath() {
        return grassbinpath;
    }

    /**
     * Setter-Methode für das GRASS-BIN Bibliotheksverzeichnis
     * @param grassbinpath GRASS-BIN Bibliotheksverzeichnis
     */
    public void setGrassbinpath(File grassbinpath) {
        this.grassbinpath = grassbinpath;
    }
}
