/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bl;

import bo.HoehenlinienConfig;
import dal.DatabaseDAL;
import gui.controller.StatusController;
import java.util.ArrayList;
import tools.FormatTools;

/**
 *
 * @author u203011
 */
public class HoehenlinienManagement implements Runnable {
    
    private HoehenlinienConfig hConfig = null;
    private DatabaseDAL dbdal = null;
    private StatusController statusGui = null;
    private String wktRectangle = null;
    private FormatTools formatTool = null;
    private ArrayList<String> arrKachelnr;

    public HoehenlinienManagement(HoehenlinienConfig hConfig, 
            StatusController statusGui) {
        this.hConfig = hConfig;
        this.statusGui = statusGui;
        formatTool = new FormatTools();
        dbdal = new DatabaseDAL();
    }
    
    @Override
    public void run() {
        this.statusGui.getBtnOrder().setDisable(true);
        convertInputRectangleToWKT();
        getKachelnummer();
        
        for (String string : arrKachelnr) {
            System.out.println(string);
        }
        
        this.statusGui.getBtnFinish().setDisable(false);
    }
    
    private void convertInputRectangleToWKT() {
        String inputRectangle = this.hConfig.getInputModel().getCoordRectangle();
        
        wktRectangle = formatTool.inputRectangleToWKT(inputRectangle);
    }
    
    private void getKachelnummer() {
        if(wktRectangle != null) {
            arrKachelnr = dbdal.getKachelnummer(wktRectangle);
        } else {
            //Log error
        }
    }
}
