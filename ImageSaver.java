/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author jchic
 */
public class ImageSaver {
    
    private CanvasManager canvasManager;
    private double sX,sY, dX,dY, width, height;
    private WritableImage canvasImage;
    private SnapshotParameters ssPara;
    
    
    public ImageSaver(CanvasManager cm){
        this.canvasManager = cm;
        canvasImage = new WritableImage( (int) canvasManager.getCanvas().getWidth(), (int) canvasManager.getCanvas().getHeight());
        ssPara = new SnapshotParameters();
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
     * @param posX
     * @param posY 
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
    
    
}
