/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Starts and runs a thread that keeps a count down of every 5 minutes to save the current project.
 * 
 * @author jchic
 */
public class AutoSaver {
    // MEMBER VARIABLES
    private CanvasManager canvasManager;
    private Thread thread;
    private final long SLEEPTIME = 1000;
    private Label timerLabel;
    private int counter = 5;
    // CONSTRUCTOR
    
    /**
     * Constructor. Sets the CanvasManager to the one being used in the document controller.
     * 
     * @param l the Label on the canvas called timerLabel that displays how much time is left before the auto-save
     */
    public AutoSaver(Label l) {
        this.timerLabel = l;
        
        thread = new Thread(() -> {
            //CanvasManager canvasMan;
            while(true)
                handleAutoSave();
        });
        thread.setDaemon(true);
        
        // unit test
//        assert thread.isDaemon() == true;
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
   
   private void handleAutoSave(){
        try {
            // wait for 5 minutes
            for (counter = 5*60; counter > 0; counter--){
                Platform.runLater(() -> {
                    timerLabel.setText(String.valueOf(counter/60));
                });
                Thread.sleep(SLEEPTIME);
                if (counter <= 1) {
                    Platform.runLater(() -> {
                        try{
                            canvasManager.getImageManager().fileAutoSave();
                        } catch (NullPointerException ex){
                            System.out.println("Failed");
                            //ex.printStackTrace();
                        }
                        //canvasManager.setModified(false);
                    });
                    //counter = 5;
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
   }
   
   /**
    * Resets the timer on the auto-saver after a manual save has been called.
    */
   public void reset(){
       counter = 5;
   }
   
   public void setCanvasManager(CanvasManager cm) { canvasManager = cm; }
    
}
