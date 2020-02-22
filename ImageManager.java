/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import paint.PopUps.PopUpNewFileFormatAlert;

/**
 * Controls the redrawing of the canvas and the select and move feature.
 * 
 * @author jchic
 */
public class ImageManager {
    
    private CanvasManager canvasManager;
    private double sX,sY, dX,dY, width, height;
    private WritableImage canvasImage;
    private SnapshotParameters ssPara;
    private File currFile;
    private final FileChooser fileChooser = new FileChooser();
    private String fileExtention;
    private Canvas canvas;
    private AutoSaver autoSaver;
    
    
    public ImageManager(CanvasManager cm, AutoSaver as){
        this.canvasManager = cm;
        this.autoSaver = as;
        this.canvas = canvasManager.getCanvas();
        
        // init'ing the canvasImage
        canvasImage = new WritableImage( (int) canvasManager.getCanvas().getWidth(), (int) canvasManager.getCanvas().getHeight());
        ssPara = new SnapshotParameters();
        
        // setting up the save parameters
        currFile = null;
        fileExtention = "png";
        // designating what file extentions the fileChooser filters
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.jpeg", "*.jpg",
                "*.png", "*.tiff", "*.tif","*.bmp", "*.JPEG", "*.JPG","*.PNG",
                "*.TIFF","*.TIF", "*.BMP"),
            new FileChooser.ExtensionFilter("JPEG", "*.jpeg", "*.jpg",
                "*.JPEG", "*.JPG"),
            new FileChooser.ExtensionFilter("PNG", "*.png", "*.PNG"),
            new FileChooser.ExtensionFilter("BMP", "*.bmp", "*.BMP"),
            new FileChooser.ExtensionFilter("TIFF", "*.tiff", "*.tif",
                "*.TIFF", "*.tif")
            );
        
    }
    
    /**
     * Takes a screenshot of the canvas to save for in-progress drawing. Takes a snapshot with a custom
     * SnapshotParameters and ViewPort.
     * 
     * @param startX the starting x position of the mouse for the width calculation
     * @param startY the starting y position of the mouse for the height calculation
     * @param endX the ending x position of the mouse for the width calculation
     * @param endY the ending y position of the mouse for the height calculation
     */
    public void savePartialCanvasImage(double startX, double startY, double endX, double endY){        
        sX = startX;
        sY = startY;
        dX = endX;
        dY = endY;
        
        // making sure the width & height are positive
        if (dX > sX)
            width = dX-sX;
        else
            width = sX-dX;
        
        if (dY > sY)
            height = dY-sY;
        else
            height = sY-dY;
        
        
        Rectangle2D viewPort = new Rectangle2D(sX, sY, width, height);
        ssPara.setViewport(viewPort);
        
        canvasImage = canvasManager.getCanvas().snapshot(ssPara, canvasImage);
        
        //System.out.println("Image bounds:\n"+canvasImage.getWidth()+","+canvasImage.getHeight());
        
    }
    
    /**
     * Re draws the image based on the new and old x and y locations.
     * 
     * @param posX x position of the mouse
     * @param posY y position of the mouse
     */
    public void movePartialCanvasImage(double posX, double posY){
        dX = posX;
        dY = posY;
        
//        System.out.println("Pos: " + sX + "," + sY + "," + ","+dX+","+dY);
        
        canvasManager.getGC().setFill(Color.WHITE);
        canvasManager.getGC().fillRect(sX, sY, width, height);
        canvasManager.getGC().drawImage(canvasImage, sX, sY, width, height, dX, dY, width, height);
    }
    
    /**
     * Takes a screenshot of the canvas to save for in-progress drawing.
     * 
     */
    public void saveCanvasImage(){
        canvasImage = canvasManager.getCanvas().snapshot(null, null);
    }
    
    /**
     * Draws the canvas based on what the current canvasImage is.
     */
    public void drawCanvas(){
        canvasManager.getGC().drawImage(canvasImage, 0, 0);
        
    }
    
    // file options
    /**
     *  Opens an image from the computer.
     */
    public void fileOpen(){
        // opens the window to choose where the file is
        File file = fileChooser.showOpenDialog(null);
        currFile = file;
        if (file != null){
            Image img = null;
            try {
                // sets an image obj to the file path's location obj
                img = new Image(new FileInputStream(file.getAbsolutePath()));
                
            } catch (FileNotFoundException ex) {
                System.out.println("Failed to open image");
            }
            canvas.setWidth(img.getWidth());
            canvas.setHeight(img.getHeight());
            canvasManager.getGC().drawImage(img, 0, 0);
        }
    }
    
    /**
     *  Takes a screenshot of the canvas and then auto-saves. 
     *  If it hasn't been saved yet, it calls fileSaveAs.
     */
    public void fileAutoSave(){
        // checks to see if the file has already been saved, if not doesn't save it
        if (currFile == null){
            return;
        }
        
        if (fileExtention.equals("png"))
            savePNG();
        else
            saveOtherImg();
    }
    
    /**
     *  Takes a screenshot of the canvas and then saves. 
     *  If it hasn't been saved yet, it calls fileSaveAs.
     */
    public void fileSave(){
        // opens the window to choose where the file is saved to
        if (currFile == null){
            fileSaveAs();
            return;
        }
        
        if (fileExtention.equals("png"))
            savePNG();
        else
            saveOtherImg();
        
        Platform.runLater(() -> {
            System.out.println("Resetting");
            autoSaver.reset();
        });
    }
    
    /**
     * Takes a screenshot of the canvas and then saves that image to the user's choosing.  
     */
    public void fileSaveAs(){
        // opens the window to choose where the file is saved to
        File outputFile = fileChooser.showSaveDialog(null);
        // setting the currFile to outputFile so that save already has a File object
        currFile = outputFile;
        
        if (fileExtention.equals("png"))
            savePNG();
        else
            saveOtherImg();
        
        Platform.runLater(() -> {
            System.out.println("Resetting");
            autoSaver.reset();
        });
    }
    
    private void savePNG(){
        if (currFile != null) {
            try{ 
                WritableImage writable = new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writable);
                RenderedImage rendered = SwingFXUtils.fromFXImage(writable, null);
                if (!fileExtention.equals(getFileExtention(currFile))){
                    fileExtention = getFileExtention(currFile);
                    PopUpNewFileFormatAlert puAlert = new PopUpNewFileFormatAlert();
                    puAlert.popAlert();
                }
                ImageIO.write(rendered, fileExtention, currFile);
            } catch (IOException io){
                System.out.println("Failed to save image");
            }
        }
    }
    
    private void saveOtherImg(){
        WritableImage writable = new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writable);
        // Get buffered image:
        BufferedImage image = SwingFXUtils.fromFXImage(writable, null); 

        // Remove alpha-channel from buffered image:
        BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE); 

        Graphics2D graphics = imageRGB.createGraphics();

        graphics.drawImage(image, 0, 0, null);

        try {
            ImageIO.write(imageRGB, fileExtention, currFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        graphics.dispose();
    }
    
    // getters
    /**
     * Gets the file current being worked on.
     * @return the file of the image being worked on
     */
    public File getFile() { return currFile; }
    private String getFileExtention(File f) {
        String ext;
        ext = f.getName();
        return ext.substring(ext.lastIndexOf(".")+1);
    }
    
    // setters
    /**
     * Sets the file current being worked on.
     * @param f the file of the image being worked on
     */
    public void setFile(File f) { currFile = f; }
}
