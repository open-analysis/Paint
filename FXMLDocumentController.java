/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import paint.PopUps.PopUpHelpMenu;
import paint.PopUps.PopUpSmartSave;
import paint.PopUps.PopUpResizeCanvas;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import paint.PopUps.PopUpReleaseNotesMenu;


/**
 * This object controls the FXML document, as well as acts as an initializer for EventHandlers and the objects being used
 * 
 * @author jchic
 */
public class FXMLDocumentController implements Initializable {
    
    // MEMBER VARIABLES
    @FXML private Canvas canvas;
    @FXML private ColorPicker strokeColorPicker;
    @FXML private ColorPicker fillColorPicker;
    @FXML private Spinner spinLineWidth;
    @FXML private ScrollPane scrollPane;
    @FXML private MenuBar menuBar;
    @FXML private BorderPane borderPane;
    @FXML private Label timerLabel;
    private Image cursorImage = null;
    private static CanvasManager canvasManager;
    private InteractMode currInteractMode;
    private int shapeStatus = 0; // to determine if the shape that the user is drawing is or still being drawn
    private boolean selected = false;
    private AutoSaver autoSaver;
    private ToolLogger toolLogger;

    
    /**
     *  Handles mouse event on the canvas.
     * 
     * @params mouse used to determine which MouseEvent was called
     */
    EventHandler mouseHandler = new EventHandler<MouseEvent>(){
                    public void handle(MouseEvent mouse){
                        // preemptively setting the MouseX & MouseY coords because all-almost all of the functions will need it
                        if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.setMouseX(mouse.getX());
                                canvasManager.setMouseY(mouse.getY());
                                
                        }
                        // Panning around the canvas
                        if (currInteractMode == InteractMode.PAN){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.pan(mouse.getScreenX(), mouse.getScreenY());
                                borderPane.setCursor(Cursor.CLOSED_HAND);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.pan(mouse.getScreenX(), mouse.getScreenY());
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.pan(mouse.getScreenX(), mouse.getScreenY());
                                borderPane.setCursor(Cursor.OPEN_HAND);
                            }
                        }
                        if (currInteractMode == InteractMode.SELECT){
                            if (selected){
                                if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                    canvasManager.getImageManager().saveCanvasImage();
                                }
                                else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                    canvasManager.getImageManager().drawCanvas();
                                    canvasManager.moveSelected(mouse.getScreenX(), mouse.getScreenY());
                                }
                                else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                    canvasManager.getImageManager().drawCanvas();
                                    canvasManager.moveSelected(mouse.getScreenX(), mouse.getScreenY());
                                    selected = false;
                                    borderPane.setCursor(Cursor.CROSSHAIR);
                                }
                            }
                            else {
                                if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                    canvasManager.getImageManager().saveCanvasImage();
                                }
                                else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                    canvasManager.getImageManager().drawCanvas();
                                    canvasManager.drawSelectRectangle(mouse.getX(), mouse.getY());
                                }
                                else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                    canvasManager.getImageManager().drawCanvas();
                                    canvasManager.selectCanvas(mouse.getX(), mouse.getY());
                                    borderPane.setCursor(Cursor.MOVE);
                                    selected = true;
                                }
                            }
                        }
                        // Color grabber
                        else if (currInteractMode == InteractMode.COLOR_GRABBER){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.grabColor((int) mouse.getX(), (int) mouse.getY());
                                strokeColorPicker.setValue(canvasManager.getStrokeColor());
                                fillColorPicker.setValue(canvasManager.getFillColor());
                            }
                        }
                        // Drawing a line
                        else if (currInteractMode == InteractMode.LINE){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                //drawProgress.drawShape(mouse.getX(), mouse.getY());
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawLine(mouse.getX(), mouse.getY());
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawLine(mouse.getX(), mouse.getY());
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // Paint brush
                        else if (currInteractMode == InteractMode.BRUSH){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.drawBrush(mouse.getX(), mouse.getY(), true);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.drawBrush(mouse.getX(), mouse.getY(), false);
                            }
                        }
                        // Text
                        else if (currInteractMode == InteractMode.TEXT){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.drawText();
                            }
                        }
                        // Eraser
                        else if (currInteractMode == InteractMode.ERASER){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.eraseBrush(mouse.getX(), mouse.getY(), true);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.eraseBrush(mouse.getX(), mouse.getY(), false);
                            }
                        }
                        // Draw square outline
                        else if (currInteractMode == InteractMode.SQUARE_OUTLINE){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawSquare(mouse.getX(), mouse.getY(), false);
                                //drawProgress.drawShape(mouse.getX(), mouse.getY());
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawSquare(mouse.getX(), mouse.getY(), false);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // draw filled square
                        else if (currInteractMode == InteractMode.SQUARE_FILL){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawSquare(mouse.getX(), mouse.getY(), true);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawSquare(mouse.getX(), mouse.getY(), true);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // Draw ellipse outline
                        else if (currInteractMode == InteractMode.ELLIPSE_OUTLINE){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawEllipse(mouse.getX(), mouse.getY(), false);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawEllipse(mouse.getX(), mouse.getY(), false);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // draw filled ellipse
                        else if (currInteractMode == InteractMode.ELLIPSE_FILL){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawEllipse(mouse.getX(), mouse.getY(), true);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawEllipse(mouse.getX(), mouse.getY(), true);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // draw circle outline
                        else if (currInteractMode == InteractMode.CIRCLE_OUTLINE){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawCircle(mouse.getX(), mouse.getY(), false);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawCircle(mouse.getX(), mouse.getY(), false);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // Draw filled circle
                        else if (currInteractMode == InteractMode.CIRCLE_FILL){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawCircle(mouse.getX(), mouse.getY(), true);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawCircle(mouse.getX(), mouse.getY(), true);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // Draw rectangle outline
                        else if (currInteractMode == InteractMode.RECTANGLE_OUTLINE){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawRectangle(mouse.getX(), mouse.getY(), false);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawRectangle(mouse.getX(), mouse.getY(), false);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // draw filled square
                        else if (currInteractMode == InteractMode.RECTANGLE_FILL){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_DRAGGED){
                                canvasManager.getImageManager().drawCanvas();
                                canvasManager.drawRectangle(mouse.getX(), mouse.getY(), true);
                            }
                            else if (mouse.getEventType() == MouseEvent.MOUSE_RELEASED){
                                canvasManager.drawRectangle(mouse.getX(), mouse.getY(), true);
                                canvasManager.getImageManager().saveCanvasImage();
                            }
                        }
                        // Draw shape
                        else if (currInteractMode == InteractMode.SHAPE){
                            if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED)
                                canvasManager.drawPolygon();
                            /*if (mouse.getEventType() == MouseEvent.MOUSE_PRESSED){
                                canvasManager.drawShape(mouse.getX(), mouse.getY(), shapeStatus);
                                if (shapeStatus == 0)
                                    shapeStatus = 1;
                                else if (shapeStatus == 2)
                                    shapeStatus = 0;
                            }*/
                        }
                        canvasManager.setModified(true);
                    }
                };
    
    /**
     *  Handles scroll event on the canvas.
     * 
     * @params scroll to determine which ScrollEvent was called
     */
    EventHandler scrollHandler = new EventHandler<ScrollEvent>(){
        public void handle(ScrollEvent scroll){
            if (scroll.getEventType() == ScrollEvent.SCROLL){
                //System.out.println("Delta: " + ((scroll.getDeltaX() + scroll.getDeltaY()) / 2) * .125 );
                // getting the average of the changes across X & Y & multiplying it by 1/8 to scale down the zoom speed
                canvasManager.zoom(((scroll.getDeltaX() + scroll.getDeltaY()) / 2) * .125 , scroll.getX(), scroll.getY());
            }
        }
    };
    
    // METHODS
    
    
    // Interaction Modes
    
    
    /**
     * Sets currInteractMode to whichever was clicked.
     */
    @FXML
    private void selectPan(){
        currInteractMode = InteractMode.PAN;
        borderPane.setCursor(Cursor.OPEN_HAND);
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectSelect(){
        currInteractMode = InteractMode.SELECT;
        borderPane.setCursor(Cursor.CROSSHAIR);
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectColorGrabber(){
        currInteractMode = InteractMode.COLOR_GRABBER;
        File file = new File("src/paint/icons/eye_dropper.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        borderPane.setCursor(new ImageCursor(cursorImage));
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectLine(){
        currInteractMode = InteractMode.LINE;
        File file = new File("src/paint/icons/line.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        borderPane.setCursor(new ImageCursor(cursorImage));
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectBrush(){
        currInteractMode = InteractMode.BRUSH;
        File file = new File("src/paint/icons/paint_brush.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectBucket(){
        currInteractMode = InteractMode.BUCKET;
        File file = new File("src/paint/icons/paint_bucket.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectEraser(){
        currInteractMode = InteractMode.ERASER;
        File file = new File("src/paint/icons/eraser.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }       
        // updating the cursor 
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectText(){
        currInteractMode = InteractMode.TEXT;
        File file = new File("src/paint/icons/text.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
     @FXML
    private void selectSquareOutline(){
        currInteractMode = InteractMode.SQUARE_OUTLINE;
        File file = new File("src/paint/icons/square_outline.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectEllipseOutline(){
        currInteractMode = InteractMode.ELLIPSE_OUTLINE;
        File file = new File("src/paint/icons/ellipse_outline.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectCircleOutline(){
        currInteractMode = InteractMode.CIRCLE_OUTLINE;
        File file = new File("src/paint/icons/circle_outline.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectRectOutline(){
        currInteractMode = InteractMode.RECTANGLE_OUTLINE;
        File file = new File("src/paint/icons/rect_outline.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectSquareFill(){
        currInteractMode = InteractMode.SQUARE_FILL;
        File file = new File("src/paint/icons/square_fill.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectEllipseFill(){
        currInteractMode = InteractMode.ELLIPSE_FILL;
        File file = new File("src/paint/icons/ellipse_fill.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectCircleFill(){
        currInteractMode = InteractMode.CIRCLE_FILL;
        File file = new File("src/paint/icons/circle_fill.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectRectFill(){
        currInteractMode = InteractMode.RECTANGLE_FILL;
        File file = new File("src/paint/icons/rect_fill.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    @FXML
    private void selectShape(){
        currInteractMode = InteractMode.SHAPE;
        File file = new File("src/paint/icons/pentagon.png");
        try {
            cursorImage = new Image(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        // updating the cursor
        borderPane.setCursor(new ImageCursor(cursorImage));
        // updating the logger
        Platform.runLater( () -> {
            toolLogger.update(currInteractMode);
        });
    }
    
    // Menu Operations

    /**
     * Calls the CanvasManager's fileOpen() function.
     */
    @FXML
    private void menuNewFile(){
        PopUpResizeCanvas puCanvas = new PopUpResizeCanvas(canvasManager);
        puCanvas.popResizeCanvas(Color.WHITE, "New File");
        canvasManager.getImageManager().setFile(null);
        canvasManager.setModified(true);
    }
    
   
    /**
     * Calls the CanvasManager's fileOpen() function.
     */
    @FXML
    private void menuOpenFile(){
        if (canvasManager.getModified()){
            System.out.println("Asking for save");
            PopUpSmartSave puSmartSave = new PopUpSmartSave(canvasManager);
            puSmartSave.popSmartSave();
        }
        canvasManager.getImageManager().fileOpen();
        canvasManager.setModified(true);
    }
    
    /**
     * Calls the CanvasManager's fileSave() function.
     */
    @FXML
    private void menuSaveFile(){
        canvasManager.getImageManager().fileSave();
        canvasManager.setModified(false);
    }
    
    /**
     * Calls the CanvasManager's fileSaveAs function.
     */
    @FXML
    private void menuSaveAsFile(){
        canvasManager.getImageManager().fileSaveAs();
        canvasManager.setModified(false);
    }
    
    @FXML
    private void handleViewAutoTimer() {
        timerLabel.setVisible(!timerLabel.isVisible());
    }
    
    /**
     * Calls the help pop up dialog box.
     */
    @FXML
    private void handleHelpPopUp() {
        PopUpHelpMenu hm = new PopUpHelpMenu();
        hm.popHelpMenu();
    }
    
    /**
     * Calls the about pop up dialog box.
     */
    @FXML
    private void handleReleaseNotesPopUp() {
        PopUpReleaseNotesMenu puAbout = new PopUpReleaseNotesMenu();
        puAbout.popReleaseNotesMenu();
    }
    
    /**
     * Calls CanvasManager's undo function.
     */
    @FXML
    private void handleUndo() {
        canvasManager.undoAction();
    }
    
    
    /**
     * Calls Canvas Manager's redo function.
     */
    @FXML
    private void handleRedo() {
        canvasManager.redoAction();
    }
    
    /**
     * Calls Canvas Manager's zoom function with a zoomLevel that's higher than what's allowed.
     */
    @FXML
    private void handleZoomInMax(){
        canvasManager.zoom(20, 0, 0);
    }
    
    /**
     * Calls Canvas Manager's zoom function with a zoomLevel that's 1 so it's zoomed out.
     */
    @FXML
    private void handleZoomOutMax(){
        canvasManager.zoom(-100, 0, 0);
    }
    
    /**
     * Gracefully quits the program, after asking to quit.
     */
    @FXML
    private void quitProgram() {
        if (canvasManager.getModified()){
            PopUpSmartSave puSmartSave = new PopUpSmartSave(canvasManager);
            puSmartSave.popSmartSave();
        }
//        autoSaver.stop();
        toolLogger.stop();
        Platform.exit();
    }
    
    // Other
    
    /**
     * Pops up a dialog box to ask the user for the size of the canvas that is desired.
     */
    @FXML
    private void resizeCanvas() {
        PopUpResizeCanvas puCanvas = new PopUpResizeCanvas(canvasManager);
        puCanvas.popResizeCanvas(Color.WHITE, "Resize Canvas");
    }
    
    /**
     * Handles the response to the color picker & sets the stroke color.
     */
    @FXML
    private void handleStrokeColorPicker(){
        canvasManager.setStrokeColor(strokeColorPicker.getValue());
    }
    
    /**
     * Handles the response to the color picker & sets the stroke color.
     */
    @FXML
    private void handleFillColorPicker(){
        canvasManager.setFillColor(fillColorPicker.getValue());
    }
    
    // Init Methods

    /**
     * Initializes the CanavasManager, sets the event handlers, scroll pane, and the interact mode.
     * 
     * @param location not being used, required
     * @param resources not being used, required
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // starting the auto-save timer
        autoSaver = new AutoSaver(timerLabel);
        
        // setting up the canvas & canvas manager        
        canvasManager = new CanvasManager(canvas, autoSaver);
        
        autoSaver.setCanvasManager(canvasManager);
        autoSaver.start();
        
        toolLogger = new ToolLogger();
        
//        borderPane = new BorderPane();
        borderPane.setCursor(Cursor.OPEN_HAND);
        
        // setting up the event handlers
        canvas.setOnMousePressed(mouseHandler);
        canvas.setOnMouseDragged(mouseHandler);
        canvas.setOnMouseReleased(mouseHandler);
        canvas.setOnScroll(scrollHandler);
        
        canvas.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent key) -> {
            System.out.println("Esapce");
            if (key.getCode() == KeyCode.ESCAPE){
                shapeStatus = 2;
            }
        });
        
        scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        
        // setting the init interactaction mode
        currInteractMode = InteractMode.PAN;
        
        // init'ing the spinner for the line width
        spinLineWidth.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(.1, 100.0, 1.0, 0.5));
        spinLineWidth.valueProperty().addListener((e) -> canvasManager.getGC().setLineWidth((double) spinLineWidth.getValue()));
        strokeColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.BLACK);
        canvasManager.getGC().setLineWidth((double) spinLineWidth.getValue());
    }
    
}