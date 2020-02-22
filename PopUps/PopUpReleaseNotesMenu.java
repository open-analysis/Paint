/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.PopUps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

/**
 * Pops up the release notes for the paint program.
 *
 * @author jchic
 */
public class PopUpReleaseNotesMenu {
    // MEMBER VARIABLES
    private String path = "src/releasenotes.txt";
    private String info;
    
    // CONSTRUCTOR
    
    // METHODS
    
    /**
     * Creates an alert that displays to the user what all of the functionality is of the program.
     */
    public void popReleaseNotesMenu(){
        Alert helpAlert = new Alert(AlertType.INFORMATION);
        helpAlert.setTitle("About Paint");
        helpAlert.setHeaderText(null);
        
        // trying to read in the release notes
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(path), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        
        info = contentBuilder.toString();

        TextArea tArea = new TextArea(info);
        tArea.setEditable(false);
        tArea.setWrapText(true);
        tArea.setPrefWidth(600);
        tArea.setPrefHeight(300);
        helpAlert.getDialogPane().setContent(tArea);
        helpAlert.setResizable(true);
        helpAlert.showAndWait();
    }
}
