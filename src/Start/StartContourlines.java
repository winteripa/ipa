/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Start;

import bo.GuiEnum;
import gui.controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Startklasse des Programms
 * @author u203011
 */
public class StartContourlines extends Application {
    
    /**
     * Startmethode des JavaFX-Projektes.
     * Die Startmethode initialisiert den MainController und lässt ihn das erste 
     * Fenster anzeigen.
     * @param stage Programmfenster
     * @throws Exception Fehler, wenn das JavaFX-Projekt nicht gestartet werden kann
     */
    @Override
    public void start(Stage stage) throws Exception {
        MainController mainController = MainController.getMainController(stage);
        mainController.showWindow(GuiEnum.INPUT);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
