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
public class HoehenlinienConfig {
    private String version = null;
    private InputModel inputModel = null;
    private PathModel pathModel = null;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public InputModel getInputModel() {
        return inputModel;
    }

    public void setInputModel(InputModel inputModel) {
        this.inputModel = inputModel;
    }

    public PathModel getPathModel() {
        return pathModel;
    }

    public void setPathModel(PathModel pathModel) {
        this.pathModel = pathModel;
    }
}
