/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

/**
 * Klasse, welche die Höhenlinienkonfiguration repräsentiert.
 * @author u203011
 */
public class HoehenlinienConfig {
    private InputModel inputModel = null;
    private PathModel pathModel = null;

    /**
     * Konstrukter, um leere Models zu erstellen
     */
    public HoehenlinienConfig() {
        this.inputModel = new InputModel();
        this.pathModel = new PathModel();
    }

    /**
     * Getter-Methode für das Model mit allen Steuerungsparametern
     * @return Model mit allen Steuerungsparametern
     */
    public InputModel getInputModel() {
        return inputModel;
    }

    /**
     * Setter-Methode für das Model mit allen Steuerungsparametern
     * @param inputModel Model mit allen Steuerungsparametern
     */
    public void setInputModel(InputModel inputModel) {
        this.inputModel = inputModel;
    }

    /**
     * Getter-Methode für das Model mit allen Programmpfaden
     * @return Model mit allen Programmpfaden
     */ 
    public PathModel getPathModel() {
        return pathModel;
    }

    /**
     * Setter-Methode für das Model mit allen Programmpfaden
     * @param pathModel Model mit allen Programmpfaden
     */
    public void setPathModel(PathModel pathModel) {
        this.pathModel = pathModel;
    }
}
