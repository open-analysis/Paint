/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.util.Optional;
import java.util.Stack;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import paint.PopUps.PopUpNPolygon;

/**
 * This class will manage everything that happens to the canvas (opening images, drawing on them, etc.).
 * 
 * @author jchic
 */
public class CanvasManager {
    
    // MEMBER VARIABLES
    private Canvas canvas;
    private GraphicsContext gc;
    private Color strokeColor, fillColor;    
    //private WritableImage canvasImage;
    private double mouseX, mouseY;
    private Stack<WritableImage> Undo, Redo;
    private boolean modified = false;
    private ImageManager imgSaver;
    private AutoSaver autoSaver;
    
    
    // CONSTRUCTORS

    /**
     *  Class constructor 
     *  Sets currFile to nothing, sets the argument canvas to the member variable canvas, and saves the GraphicsContext from canvas.
     *  Also sets the file extensions that can be used, inits the undo/redo stacks, and the colors.
     * 
     * @param canvas    the canvas from FXMLDocumentController to manipulate the same canvas
     * @param as auto-saver object so that the canvas manager is able to call a reset on the timer
     */
    public CanvasManager(Canvas canvas, AutoSaver as){
        // init'ing vars
        this.canvas = canvas;
        gc = this.canvas.getGraphicsContext2D();
        mouseX = mouseY = 0.0;
        Undo = new Stack();
        Redo = new Stack();
        
        strokeColor = Color.BLACK;
        fillColor = Color.BLACK;
        
        imgSaver = new ImageManager(this, as);
        
        // setting up the canvas
        fillCanvas(Color.WHITE, 0, 0);
    }
    
    // METHODS
    
    // Drawing
    
    /**
     * grabs the color pixel color of the pixel being clicked
     * 
     * @param posX  the x coordinate of the mouse
     * @param posY  the y coordinate of the mouse
     */
    public void grabColor(int posX, int posY){
        WritableImage img = canvas.snapshot(null, null);
        Color color = img.getPixelReader().getColor(posX, posY);
        strokeColor = fillColor = color;
        // Unit test
        assert color != Color.TRANSPARENT;
    }
    
    /**
     * Creates a line where the user first clicks and ends it at the user's choosing. 
     * 
     * @param endX  is the mouse's ending x-location
     * @param endY  is the mouse's ending y-location
     */
    public void drawLine(double endX, double endY){
        // make the mouse's position where it clicked the starting point of the line
        
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        // drawing the line
        gc.setStroke(strokeColor);
        gc.strokeLine(mouseX, mouseY, endX, endY);
    }
    
    /**
     * Starts drawing a continuous line as the user drags the mouse. 
     * 
     * @param x x position of the mouse, which is where the line will be drawn
     * @param y y position of the moues, which is where the line will be drawn
     * @param newLine tells the function whether or not to start a new line with beginPath
     */
    public void drawBrush(double x, double y, boolean newLine){
        // drawing the line
        gc.setStroke(strokeColor);
        
        // checking to see if the line has been started yet
        if (newLine == true){
            // saving a screenshot of the current canvas to undo
            addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
            
            gc.beginPath();
            gc.moveTo(x, y);
            gc.stroke();
        }
        else{
            gc.lineTo(x,y);
            gc.stroke();
        }
    }
    
    /**
     * Prompts the user for text to input, then draws it on the screen.
     */
    public void drawText(){
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        String text = null;
        
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Text Input for Text Box");
        dialog.setHeaderText(null);
        dialog.setContentText("Text:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            text = result.get();
        }
        
        // unit test
        assert text != null;
        
        //gc.setFont(new Font(gc.getLineWidth()));
        gc.strokeText(text, mouseX, mouseY);
    }
    
    /**
     * Starts erasing a continuous line as the user drags the mouse.
     * 
     * @param x x position of the mouse, which is where the line will be drawn
     * @param y y position of the moues, which is where the line will be drawn
     * @param newLine tells the function whether or not to start a new line with beginPath
     */
    public void eraseBrush(double x, double y, boolean newLine){       
        // setting the eraser "color"
        gc.setStroke(Color.WHITE);
        
        // mouse dragging is not being detected
        
        // checking to see if the line has been started yet
        if (newLine == true){
            // saving a screenshot of the current canvas to undo
            addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
            
            gc.beginPath();
            gc.moveTo(x, y);
            gc.stroke();
        }
        else{
            gc.lineTo(x,y);
            gc.stroke();
        }
    }
    
    
    /**
     * Creates a square where the user first clicks and ends it at the user's choosing.    
     * 
     * @param endX  the end point of the drawing, is checked against mouseX to determine which is smaller
     * @param endY  the end point of the drawing, is checked against mouseY to determine which is smaller
     * @param filled    tells which GraphicsContext function to call, either fill in the drawing or leave it as an outline
     */
    public void drawSquare(double endX, double endY, boolean filled){
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        // setting variables for if the user draws from a spot that's not upper left down to bottom right
        double x,y, width, height, side;
        
        if (mouseX < endX){
            x = mouseX;
            width = endX-mouseX;
        }else{
            x = endX;
            width = mouseX-endX;
        }
        
        if (mouseY < endY){
            y = mouseY;
            height = endY-mouseY;
        }
        else{ 
            y = endY;
            height = mouseY-endY;
        }
        
        side = (width + height) / 2;
        
        // setting the colors 
        gc.setFill(fillColor);
        gc.setStroke(strokeColor);
        
        // drawing the square
        if (filled){
            gc.fillRect(x, y, side, side);
        }
        else{
            gc.strokeRect(x, y, side, side);
        }
        
    }
    
    /**
     * Draws a rectangle where the user starts clicking and finishes when the user releases the mouse button.  
     * 
     * @param endX  the end point of the drawing, is checked against mouseX to determine which is smaller
     * @param endY  the end point of the drawing, is checked against mouseY to determine which is smaller
     * @param filled    tells which GraphicsContext function to call, either fill in the drawing or leave it as an outline
     */
    public void drawRectangle(double endX, double endY, boolean filled){
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        // setting variables for if the user draws from a spot that's not upper left down to bottom right
        double x,y, width, height;
        
        if (mouseX < endX){
            x = mouseX;
            width = endX-mouseX;
        }else{
            x = endX;
            width = mouseX-endX;
        }
        
        if (mouseY < endY){
            y = mouseY;
            height = endY-mouseY;
        }
        else{ 
            y = endY;
            height = mouseY-endY;
        }
        
        // setting the colors 
        gc.setFill(fillColor);
        gc.setStroke(strokeColor);
        
        // drawing the ellipse
        if (filled){
            gc.fillRect(x, y, width, height);
        }
        else{
            gc.strokeRect(x, y, width, height);
        }        
    }    
    
    
    /**
     * Draws an ellipse where the user starts clicking and finishes when the user releases the mouse button.  
     * 
     * @param endX  the end point of the drawing, is checked against mouseX to determine which is smaller
     * @param endY  the end point of the drawing, is checked against mouseY to determine which is smaller
     * @param filled    tells which GraphicsContext function to call, either fill in the drawing or leave it as an outline
     */
    public void drawEllipse(double endX, double endY, boolean filled){
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        // setting variables for if the user draws from a spot that's not upper left down to bottom right
        double x,y, width, height;
        
        if (mouseX < endX){
            x = mouseX;
            width = endX-mouseX;
        }else{
            x = endX;
            width = mouseX-endX;
        }
        
        if (mouseY < endY){
            y = mouseY;
            height = endY-mouseY;
        }
        else{ 
            y = endY;
            height = mouseY-endY;
        }
        
        // setting the colors 
        gc.setFill(fillColor);
        gc.setStroke(strokeColor);
        
        // drawing the ellipse
        if (filled){
            gc.fillOval(x, y, width, height);
        }
        else{
            gc.strokeOval(x, y, width, height);
        }        
    }

    /**
     * Creates a circle where the user first clicks and ends it at the user's choosing.
     * 
     * @param endX  the end point of the drawing, is checked against mouseX to determine which is smaller
     * @param endY  the end point of the drawing, is checked against mouseY to determine which is smaller
     * @param filled    tells which GraphicsContext function to call, either fill in the drawing or leave it as an outline
     */
    public void drawCircle(double endX, double endY, boolean filled){
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        // setting variables for if the user draws from a spot that's not upper left down to bottom right
        double x,y, width, height;
        
        if (mouseX < endX){
            x = mouseX;
            width = endX-mouseX;
        }else{
            x = endX;
            width = mouseX-endX;
        }
        
        if (mouseY < endY){
            y = mouseY;
            height = endY-mouseY;
        }
        else{ 
            y = endY;
            height = mouseY-endY;
        }
        
        // setting the colors 
        gc.setFill(fillColor);
        gc.setStroke(strokeColor);
        
        // determining the radius by taking the average of the width & height
        double radius = (width + height) / 2;
        
        // drawing the circle
        if (filled){
            gc.fillOval(x, y, radius, radius);
        }
        else{
            gc.strokeOval(x, y, radius, radius);
        }
    }
    
    /**
     *  Draws a polygon of n sides, which is determined by a dialog box.
     */
    public void drawPolygon(){
        double currX = mouseX;
        double currY = mouseY;
        PopUpNPolygon puNPoly = new PopUpNPolygon();
        puNPoly.popNPolygon();
        
        // todo
        // drawing the actual shape
        // interior angle = 360/numSides
        double angle = (2*Math.PI) / puNPoly.getNumSides();
        double radius = puNPoly.getLength();
        
        for (int i = 1; i <= puNPoly.getNumSides(); i++){
            gc.moveTo(currX, currY);
            double newX = radius * Math.cos(i * angle)+currX;
            double newY = radius * Math.sin(i * angle)+currY;
            gc.lineTo(newX, newY);
            gc.stroke();
            currX = newX;
            currY = newY;
        }
        
    }
    
    /**
     * Creates a shape of the user's design by clicking around the canvas.
     * 
     * @param endX  the end point of the drawing, is checked against mouseX to determine which is smaller
     * @param endY  the end point of the drawing, is checked against mouseY to determine which is smaller
     * @param shapeStatus tells at what point in drawing the user is at (0 if beginning, 1 drawing, 2 done)
     */
    public void drawShape(double endX, double endY, int shapeStatus){
        // saving a screenshot of the current canvas to undo
        addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
        
        // drawing the line
        gc.setStroke(strokeColor);
        
        // checking to see if the line has been started yet
        if (shapeStatus == 0){
            // saving a screenshot of the current canvas to undo
            addUndo(new WritableImage( (int) canvas.getWidth(), (int) canvas.getHeight()));
            
            gc.beginPath();
            gc.moveTo(endX, endY);
            gc.stroke();
        } 
        else if (shapeStatus == 1){
            gc.lineTo(endX, endY);
            gc.stroke();
        }
        else if (shapeStatus == 2){
            gc.closePath();
        }
    }
        
    
    /**
     * Fills the whole canvas with a single color.  
     * 
     * @param color the color to set the fill in medium to
     * @param x the x position to start filling in at
     * @param y  the y position to start filling in at
     */
    public void fillCanvas(Color color, double x, double y){
        gc.setFill(color);
        gc.fillRect(x, 0, canvas.getWidth(), canvas.getHeight());
        gc.fillRect(0, y, canvas.getWidth(), canvas.getHeight());
        gc.setFill(strokeColor);
    }
    
    /**
     * Calls imgSaver's savePartialCanvasImage to save a screenshot of the canvas.
     * 
     * @param posX x position of the mouse
     * @param posY y position of the mouse
     */
    public void selectCanvas(double posX, double posY){
       imgSaver.savePartialCanvasImage(mouseX, mouseY, posX, posY);
    }
    
    /**
     * Calls imgSaver's movePartialCanvasImage based on where the mouse currently is.
     * 
     * @param posX x position of the mouse
     * @param posY y position of the mouse
     */
    public void moveSelected(double posX, double posY){
        imgSaver.movePartialCanvasImage(posX, posY);
    }
    
    /**
     * Draws a rectangle around where the user is selecting.  
     * 
     * @param endX  the end point of the drawing, is checked against mouseX to determine which is smaller
     * @param endY  the end point of the drawing, is checked against mouseY to determine which is smaller
     */
    public void drawSelectRectangle(double endX, double endY){        
        // setting variables for if the user draws from a spot that's not upper left down to bottom right
        double x,y, width, height;
        
        if (mouseX < endX){
            x = mouseX;
            width = endX-mouseX;
        }else{
            x = endX;
            width = mouseX-endX;
        }
        
        if (mouseY < endY){
            y = mouseY;
            height = endY-mouseY;
        }
        else{ 
            y = endY;
            height = mouseY-endY;
        }
        
        // setting the colors 
        gc.setFill(fillColor);
        gc.setStroke(strokeColor);
        
        // drawing the rectangle
        gc.strokeRect(x, y, width, height);
    }  
    
    /**
     * Adds WritableImage to the undo stack.
     * 
     * @param image WritableImage that is added to the stack of Undo stack
     */
    public void addUndo(WritableImage image){
        canvas.snapshot(null, image);
        Undo.push(image);
    }
    
    
    /**
     * Adds WritableImage to the redo stack.
     * 
     * @param image WritableImage that is added to the stack of Undo stack
     */
    public void addRedo(WritableImage image){
        Redo.push(image);
    }
    
    /**
     * Replaces the current canvas with the snapshot on the top of the undo stack.
     */
    public void undoAction(){
        if (Undo.empty())
            return;
        
        Image img = Undo.pop();
        
        canvas.setWidth(img.getWidth());
        canvas.setHeight(img.getHeight());
        gc.drawImage(img, 0, 0);
    }
    
    /**
     * Replaces the current canvas with the snapshot on the top of the redo stack.
     */
    public void redoAction(){
        if (Redo.empty())
            return;
        
        Image img = Redo.pop();
        
        canvas.setWidth(img.getWidth());
        canvas.setHeight(img.getHeight());
        gc.drawImage(img, 0, 0);
    }
    
    /**
     * Scales the x and y axes of the canvas.
     * 
     * @param zoomLevel the level to which the canvas is zoomed in to, it's cumulative with whatever level is already set
     * @param posX  the x position of the mouse that centers the canvas at that point
     * @param posY  the y position of the mouse that centers the canvas at that point
     */
    public void zoom(double zoomLevel, double posX, double posY) { 
        if (zoomLevel > 20)
            zoomLevel = 20;
        
        // making sure the canvas doesn't get too zoomed in
        if (canvas.getScaleX() <= 20 && canvas.getScaleX() >= 1){
            canvas.setScaleX(canvas.getScaleX() + zoomLevel);
            canvas.setScaleY(canvas.getScaleY() + zoomLevel);
            canvas.setTranslateX(posX);
            canvas.setTranslateY(posY);
        }
        
        if (canvas.getScaleX() <= 1){
            canvas.setScaleX(1);
            canvas.setScaleY(1);
            canvas.setTranslateX(0);
            canvas.setTranslateY(0);
        } else if (canvas.getScaleX() > 20) {
            canvas.setScaleX(20);
            canvas.setScaleY(20);
        }
    }
    
    /**
     * Pans the x and y axes of the canvas.
     * 
     * @param posX x position of the mouse the start moving the canvas in the correct direction
     * @param posY y position of the mouse the start moving the canvas in the correct direction
     */
    public void pan(double posX, double posY) { 
        if (posX < 0){
            posX = 0;
        } else if (posX > canvas.getWidth()){
            posX = canvas.getWidth();
        }
        
        if (posY < 0){
            posY = 0;
        } else if (posY > canvas.getHeight()){
            posY = canvas.getHeight();
        }
        
        canvas.setTranslateX(posX);
        canvas.setTranslateY(posY);
    }
    
    // Getters
    /**
     * Returns the x position of the mouse.
     * @return x position of the mouse
     */
    public double getMouseX(){ return mouseX; }
    /**
     * Returns the y position of the mouse.
     * @return y position of the mouse
     */
    public double getMouseY(){ return mouseY; }
    /**
     * Gets the canvas that is being worked on.
     * @return the canvas currently being worked on
     */
    public Canvas getCanvas() { return canvas; }
    /**
     * Gets the color of the GraphicContext's stroke.
     * @return color that GraphicsContext uses to stroke lines
     */
    public Color getStrokeColor() { return strokeColor; }
    /**
     * Gets the color of the GraphicContext's fill.
     * @return color that GraphicsContext uses to fill shapes
     */
    public Color getFillColor() { return fillColor; }
    /**
     * Gets if the current file has been modified by the user.
     * @return boolean if the user has interacted with the current image
     */
    public boolean getModified() { return modified; }
    /**
     * Gets the current zoom level of the canvas.
     * @return the double of the x scale of the canvas
     */
    public double getZoom() { return canvas.getScaleX(); } // both ScaleX & ScaleY will be the same
    /**
     * Gets the GraphicsContext of the Canvas.
     * @return the GraphicsContext of current canvas
     */
    public GraphicsContext getGC() { return gc; }
    /**
     * Gets the ImageSaver that controls the saving and opening of images onto the canvas.
     * @return ImageSaver object that is used to control some of the canvas manipulations
     */
    public ImageManager getImageManager() { return imgSaver; }
    
    // Setters
    /**
     * Set the x position of the mouse.
     * @param x x position of the mouse
     */
    public void setMouseX(double x){ mouseX = x; }
    /**
     * Set the y position of the mouse.
     * @param y y position of the mouse
     */
    public void setMouseY(double y){ mouseY = y; }
    /**
     * Set the stroke color for the GraphicsContext.
     * @param c the color that will be used in GraphicsContext.stroke
     */
    public void setStrokeColor(Color c) { strokeColor = c; }
    /**
     * Set the fill color for the GraphicsContext.
     * @param c the color that will be used in GraphicsContext.fill
     */
    public void setFillColor(Color c) { fillColor = c; }
    /**
     * Sets the modified variable for if the user has modified the current image.
     * @param mod boolean that determines if the user has modified the current image
     */
    public void setModified(boolean mod) { modified = mod; }
}
