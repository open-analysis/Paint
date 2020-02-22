/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.PopUps;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Displays a warning to the user that they are changing file format types.
 * @author jchic
 */
public class PopUpNewFileFormatAlert {
    // METHODS
    /**
     * Creates a warning dialog box to tell the user that they are changing file format types.
     */
    public void popAlert(){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Changing file format types!");
        alert.setHeaderText(null);
        alert.setContentText("You're changing image formats! This may cause loss of data!");

        alert.showAndWait();
    }        
}
