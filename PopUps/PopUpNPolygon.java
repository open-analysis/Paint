/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.PopUps;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 *
 * @author jchic
 */
public class PopUpNPolygon {
    // MEMBER VARIABLES
    private int length, numSides;
    
    // CONSTUCTOR
    public PopUpNPolygon(){
        
    }
    
    // METHODS
    public void popNPolygon(){
        Dialog<Pair<String, String>> dialog = new  Dialog<>();
        dialog.setTitle("Polygon");
        
        // setting up the buttons
        ButtonType okayButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okayButton, ButtonType.CANCEL);
        
        // setting up the rest of the GUI
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField tfLength = new TextField();
        tfLength.setPromptText("Length:");
        TextField tfNumSides = new TextField();
        tfNumSides.setPromptText("Number of sides:");

        gridPane.add(new Label("Length:"), 0, 0);
        gridPane.add(tfLength, 0, 1);
        gridPane.add(new Label("Number of sides:"), 1, 0);
        gridPane.add(tfNumSides, 1, 1);
        
        dialog.getDialogPane().setContent(gridPane);
        
        // Request focus on the username field by default.
        Platform.runLater(() -> tfLength.requestFocus());
        
        // Convert the result to a pair when the okay button is pressed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okayButton) {
                return new Pair<>(tfLength.getText(), tfNumSides.getText());
            }
            return null;
        }); 
    
        Optional<Pair<String, String>> result = dialog.showAndWait();

        // getting the return from the dialog box
        result.ifPresent(pair -> {
            // converting the strings entered into the dialog box to ints
            try{
                length = Integer.parseInt(pair.getKey());
            } catch (Exception e){
                System.out.println("Couldn't convert width");
            }
            try{
                numSides = Integer.parseInt(pair.getValue());
            } catch (Exception e){
                System.out.println("Couldn't convert height");
            }
        });
        
    }
    
    // GETTERS
    public int getLength() { return length; }
    public int getNumSides() { return numSides; }
}
