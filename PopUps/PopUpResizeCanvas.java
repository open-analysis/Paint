/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.PopUps;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import paint.CanvasManager;

/**
 * Pops up dialog box that prompts the user for the length and width of the canvas that user is resizing.
 * 
 * @author jchic
 */
public class PopUpResizeCanvas {
    
    // MEMBER VARIABLES
    private CanvasManager canvasManager;
    private double width, height;
    
    // constants
    private final double MAX_SIZE = 10000;
    private final double MIN_SIZE = 100;
    
    // METHODS
    
   
    /**
     * Creates the dialog box for the canvas resize window and then calls the canvasManager's canvas setWidth and setHeight.
     * 
     * @param color the color that fills in the canvas when the canvas gets resized
     * @param title the title of the dialog box, is used to determine where to start filling in the canvas colors ("New File" goes from 0,0 and "Resize Canvas starts from the old width and the old height)
     */
    public void popResizeCanvas(Color color, String title){        
        double oldWidth = canvasManager.getCanvas().getWidth();
        double oldHeight = canvasManager.getCanvas().getHeight();
        
        Dialog<Pair<String, String>> dialog = new  Dialog<>();
        dialog.setTitle(title);
        
        // setting up the buttons
        ButtonType okayButton = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okayButton, ButtonType.CANCEL);
        
        // setting up the rest of the GUI
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField tfWidth = new TextField();
        tfWidth.setPromptText("Width");
        TextField tfHeight = new TextField();
        tfHeight.setPromptText("Height");

        gridPane.add(new Label("Width:"), 0, 0);
        gridPane.add(tfWidth, 0, 1);
        gridPane.add(new Label("Height:"), 1, 0);
        gridPane.add(tfHeight, 1, 1);
        
        dialog.getDialogPane().setContent(gridPane);
        
        // Request focus on the username field by default.
        Platform.runLater(() -> tfWidth.requestFocus());
        
        // Convert the result to a pair when the okay button is pressed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okayButton) {
                return new Pair<>(tfWidth.getText(), tfHeight.getText());
            }
            return null;
        }); 
    
        Optional<Pair<String, String>> result = dialog.showAndWait();

        // getting the return from the dialog box
        result.ifPresent(pair -> {
            // converting the strings entered into the dialog box to doubles
            try{
                width = Double.parseDouble(pair.getKey());
            } catch (Exception e){
                System.out.println("Couldn't convert width");
            }
            try{
                height = Double.parseDouble(pair.getValue());
            } catch (Exception e){
                System.out.println("Couldn't convert height");
            }
        });
        
        // checking to make sure the user didn't set unrealistic bounds
        if (width < MIN_SIZE || width > MAX_SIZE){
            System.out.println("problem with width");
            if (width < MIN_SIZE) width = MIN_SIZE;
            else if (width > MAX_SIZE) width = MAX_SIZE;
        }
        if (height < MIN_SIZE || height > MAX_SIZE){
            System.out.println("problem with height");
            if (height < MIN_SIZE) height = MIN_SIZE;
            else if (height > MAX_SIZE) height = MAX_SIZE;
        }
        // setting the canvas to the width & height based on what was entered
        canvasManager.getCanvas().setWidth(width);
        canvasManager.getCanvas().setHeight(height);
        
        if (title.equals("New File"))
            canvasManager.fillCanvas(color, 0, 0);
        else if (title.equals("Resize Canvas"));
            canvasManager.fillCanvas(color, oldWidth, oldHeight);
    }
    
    // CONSTRUCTORS

    /**
     * Constructor. Sets the input canvas manager to the class private variable, and initializes width and height to 0.
     * @param cm the canvasManager that ensures the canvas being manipulated is the correct one
     */
    public PopUpResizeCanvas(CanvasManager cm){
        this.canvasManager = cm;
        width = height = 0;
    }
    
}
