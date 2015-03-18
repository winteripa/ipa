/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.controller;

import bo.GuiEnum;
import bo.HoehenlinienConfig;
import bo.IMODEL;
import bo.InputModel;
import bo.PathModel;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author u203011
 */
public class MainController {
    
    private static MainController mainController = null;
    private static Stage stage = null;
    private static HoehenlinienConfig hConfig = null;
    
    private MainController(){
    }
    
    public static MainController getMainController(Stage stage){
        if(mainController == null) {
            mainController = new MainController();
            mainController.stage = stage;
            hConfig = new HoehenlinienConfig();
        }
        
        return mainController;
    }
    
    public void showWindow(GuiEnum windowToShow) {
        try {
            Parent root = null;
            
            if(windowToShow.equals(GuiEnum.INPUT)) {
                root = FXMLLoader.load(getClass().getResource("/gui/view/InputGUI.fxml"));
            } else if (windowToShow.equals(GuiEnum.PATH)) {
                root = FXMLLoader.load(getClass().getResource("/gui/view/PathGUI.fxml"));
            } else if (windowToShow.equals(GuiEnum.STATUS)) {
                root = FXMLLoader.load(getClass().getResource("/gui/view/StatusGUI.fxml"));
            } else {
                //Log Error in Logfile
            }
            
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
        } catch(IOException e) {
            //Log exception
        }
    }
    
    public Stage getPrimaryStage() {
        return stage;
    }
    
    public void addHoehenlinienConfig(GuiEnum currWindow, IMODEL model){
        if(currWindow.equals(GuiEnum.INPUT)) {
            hConfig.setInputModel((InputModel) model);
        } else if (currWindow.equals(GuiEnum.PATH)) {
            hConfig.setPathModel((PathModel) model);
        }
    }
    
    public HoehenlinienConfig getHoehenlinienConfig() {
        return hConfig;
    }
}
