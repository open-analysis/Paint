/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jchic
 */
public class ToolLogger {
    
    // MEMBER VARIABLES
    private CanvasManager canvasManager;
    private Thread thread;
    private final int SLEEPTIME = 1000;
    private int counter = 0;
    private int writeTime = 0;
    private boolean sameTool = true;
    private InteractMode oldTool, tool;
    private PrintWriter writer;
    
    // CONSTRUCTOR
    
    /**
     * Constructor. Sets up the thread for the writing to the logging.
     * 
     */
    public ToolLogger() {        
        // setting the old tool to the default tool
        tool = oldTool = InteractMode.PAN;
        
        // creating the new thread
        thread = new Thread(() -> {
            //CanvasManager canvasMan;
            while(true)
                handleLog();
        });
        thread.setDaemon(true);
    }
    
    // METHODS
    /**
     *  Starts the timer thread.
     */
   public void start(){
       thread.start();
   }
   
   /**
    * Stops the timer thread by joining it.
    */
   public void stop(){
        try {
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
   }
   
   private void handleLog(){
       // every second it'll update it's counter
       while (sameTool){
            try {
               Thread.sleep(SLEEPTIME);
           } catch (InterruptedException ex) {
               ex.printStackTrace();
           }
            counter++;
       }
       
       sameTool = true;
       
       String toolName;
       switch(oldTool){
           case PAN:
               toolName = "Pan";
               break;
           case SELECT:
               toolName = "Select";
               break;
           case COLOR_GRABBER:
               toolName = "Color Grabber";
               break;
           case LINE:
               toolName = "Line";
               break;
           case BRUSH:
               toolName = "Brush";
               break;
           case BUCKET:
               toolName = "Bucket";
               break;
           case ERASER:
               toolName = "Eraser";
               break;
           case TEXT:
               toolName = "Text";
               break;
           case SQUARE_OUTLINE:
               toolName = "Square Outline";
               break;
           case SQUARE_FILL:
               toolName = "Square Fill";
               break;
           case RECTANGLE_OUTLINE:
               toolName = "Rectangle Outline";
               break;
           case RECTANGLE_FILL:
               toolName = "Rectangle Fill";
               break;
           case ELLIPSE_OUTLINE:
               toolName = "Ellipse Outline";
               break;
           case ELLIPSE_FILL:
               toolName = "Ellipse Fill";
               break;
           case CIRCLE_OUTLINE:
               toolName = "Circle Outline";
               break;
           case CIRCLE_FILL:
               toolName = "Circle Fill";
               break;
           case SHAPE:
               toolName = "Shape";
               break;  
           default:
               toolName = "Unknown";
               break;
       }
       
       // setting up the logger's writing out
       FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("logs/tool.txt");
        } catch (IOException ex) {
            fileWriter = null;
        }
       writer = new PrintWriter(fileWriter);
       
        // Writing out the tool & the time used
        /*try{
            
        } catch (IOException ex){
            ex.printStackTrace();
        }*/
        writer.printf("%s",toolName);
        writer.printf("%d",writeTime);
        writer.print("--------------------------------------");
        writer.close();
        
   }
   
   public void update(InteractMode mode){
       oldTool = tool;
       tool = mode;
       writeTime = counter;
       counter = 0;
       sameTool = false;
   }
   
}