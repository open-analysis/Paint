/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.PopUps;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import paint.CanvasManager;

/**
 *
 * @author jchic
 */
public class PopUpSmartSave {
    // MEMBER VARIABLES
    private CanvasManager canvasManager;
    
    // CONSTRUCTOR
    /**
     * This constructor simply sets canvasManager to cm
     * @param cm the canvasManager that makes sure the canvas being manipulated is the correct one
     */
    public PopUpSmartSave(CanvasManager cm){
        this.canvasManager = cm;
    }
    
    // METHODS  
    
    /**
     * This methods opens a dialog box that asks the user if they want to save their current file.
     */
    public void popSmartSave(){
        Alert saveAlert = new Alert(AlertType.CONFIRMATION);
        saveAlert.setTitle("Smart Save");
        saveAlert.setHeaderText("\"You have made changes to the file.\\nWould you like to save them?\"");
        saveAlert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = saveAlert.showAndWait();
        if (result.get() == ButtonType.OK){
            // user chose OK
            canvasManager.fileSave();
            return;
        } else {
            // user chose CANCEL or closed the dialog
            return;
        }
        
    }
    
}
