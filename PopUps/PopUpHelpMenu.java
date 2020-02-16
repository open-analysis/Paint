/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.PopUps;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

/**
 *
 * @author jchic
 */
public class PopUpHelpMenu {
    // MEMBER VARIABLES
    private String info = "---------------------------------------------\n"
            + "Help for Paint                               Updated: 2/1/2020\n"
            + "---------------------Menu------------------------\n"
            + "\t\tFILE\n"
            + "Create a new file (File->New):\n"
            + "\tThis creates a new blank canvas for you to work on.\n"
            + "Open a file (File->Open):\n"
            + "\tThis allows you to open a pre-existing image to edit.\nThis program can open \".jpg\", \".jpg\", \".png\", \".tiff\", \".bmp\" files.\n"
            + "Save a file (File->Save OR File->Save As):\n"
            + "\tThese options allow you to save your work. Note: Save as allows you to create a new saved image file.\nThis program can save \".jpg\", \".jpg\", \".png\", \".tiff\", \".bmp\" file types.\n"
            + "To exit (File->Exit):\n"
            + "\tThis feature saves your work and then allows you to exit the application.\n"
            + "\t\tEDIT\n"
            + "Copy (Edit->Copy):\n"
            + "\tThis feature allows you to copy what is currently selected to the clipboard to be pasted somewhere eles.\n"
            + "Cut (Edit->Cut):\n"
            + "\tThis feature acts similarly to copy but also removes the selection from the screen.\n"
            + "Paste (Edit->Paste):\n"
            + "\tThis feature places the items in the clipboard back onto the screen.\n"
            + "Delete (Edit->Delete):\n"
            + "\tThis feaure deletes whatever is currently selected.\n"
            + "Undo (Edit->Undo):\n"
            + "\tThis action undoes the last canvas manipulation (eg removes a line that was just drawn).\n"
            + "Redo (Edit->Redo):\n"
            + "\tThis action does the opposite of undo.\n"
            + "\t\tVIEW\n"
            + "Resize Canvas (View->Resize Canvas):\n"
            + "\tThis feature lets you adjust the size of the canvas but entering in a width and height.\n"
            + "Rotate the Canvas (View->Rotate Image):\n"
            + "\tThis feature will rotate the canvas 90 degrees following the unit circle (ie the top left corner -> bottom left corner).\n"
            + "\t\tHELP\n"
            + "Help (Help->Help):\n"
            + "\tThis feature opens up this menu!\n"
            + "---------------------Drawing------------------------\n"
            + "Pan around the canvas: Clicking the \"Pan\" tool gives you the ability to click and drag your way across the canvas.\n"
            + "Select objects: Clicking the \"Selection\" tool allows you to drag a square over the canvs to select that portion to then manipulate with features such as copy or delete.\n"
            + "Draw a line: Clicking the \"Line\" tool allows you to draw lines on the canvas.\nThe width and color the line can be manipulated by clicking the \"Line Width\" and \"Line Color\" values respectively.\n"
            + "Draw with a paint brush: Clicking the \"Paint Brush\" tool gives you the ability to free draw lines on the canvas. These are also manipulated with \"Line Color\" and \"Line Width\" as well.\n"
            + "Draw with a paint brush: Clicking the \"Paint Bucket\" tool gives you the ability to fill in the canvas with a solid color. This feature is also manipulated with \"Fill Color\".\n"
            + "Erase portions of the canvas: Clicking the \"Eraser\" tool allows you to remove portions of your drawing at will, returning that part of the canvas to a blank white space.\n";
    
    // CONSTRUCTOR
    
    // METHODS
    
    /**
     * Creates an alert that displays to the user what all of the functionality is of the program.
     */
    public void popHelpMenu(){
        Alert helpAlert = new Alert(AlertType.INFORMATION);
        helpAlert.setTitle("Helpful Information");
        helpAlert.setHeaderText("Help");
        TextArea tArea = new TextArea(info);
        tArea.setEditable(false);
        tArea.setPrefWidth(600);
        tArea.setPrefHeight(300);
        helpAlert.getDialogPane().setContent(tArea);
        helpAlert.setResizable(true);
        helpAlert.showAndWait();
    }
}
