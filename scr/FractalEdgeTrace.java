/**
 * Desc: calculates the fractal by tracing 
 * the edges of the fractal and skipping a lot of the calculations
 * since when u find the edge of a fractal you just need to fill 
 * everything thats within that edge with whatever color represents it 
 */
package Java.FractalExplorer.scr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FractalEdgeTrace {

    private FractalMath math;
    private int[] data;

    public FractalEdgeTrace(FractalMath math, int[] data) {
        this.math = math;
        this.data = data;
    }

    /**
     * generates 64 rectangles around the screen 
     * in order to maximize its ability to locate the edges
     * @param numThreads number of threads to distribute load
     * @see this{@link #renderRectangle(int, int, int, int)}
     */
    public void calculateEdgeFractal(int numThreads){
        int sectorNum = 8;
        int sectorWidth = (math.width/sectorNum);
        int sectorHeight = (math.height/sectorNum);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < sectorNum*sectorNum; i++) {
            final int startX = i / sectorNum * sectorWidth;
            final int startY = i % sectorNum * sectorHeight;
            //assigns workload to the threads
            executor.execute(() -> renderRectangle(startX, startY, sectorWidth, sectorHeight));
            //renderRectangle(startX, startY, sectorWidth, sectorHeight);
        }


        //waits for all the threads to be finnished
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }

        math.frame.animate = false;

    }
    
    /**
     * uses recursion to search a rectangle for the edge of a fractal when
     * a rectangle detects that there is at least part of an edge of the 
     * fractal inside it it splits up the quadrant untill the line is fully
     * rendered and reducing the amount of unnessesary calculations
     * @param startX starting x
     * @param startY starting y
     * @param sectorWidth how big the sector width is
     * @param sectorHeight how big the sector height is
     * @see this{@link #fullRenderRectangle(int, int, int, int)}
     *    used to fill the rectangle bececause it coulnt go into 
     *    smaller increments to find the exact line
     * @see this{@link #fullRenderRectangle(int, int, int, int, int)} 
     *    used to fill in a rectangle that is 
     *    guaranteed to be one color because all 
     *    the edges of the rectangle had been` a single color
     */
    public void renderRectangle(int startX, int startY, int sectorWidth, int sectorHeight){
        boolean lineDetected = false;
        int controlIteration = math.drawFractal(startX, startY);
        int control = math.getColor(startX, startY);
        int x = 0;
        int y = 0;
        //top edge
        y = startY;
        for(x = startX+1;x < startX+sectorWidth;x++){
            math.drawFractal(x, y);
            
            if(math.getColor(x,y) != control){
                lineDetected = true;
            }
        }
        
        //bottom edge
        y = startY+sectorHeight-1;
        for(x = startX;x < startX+sectorWidth;x++){
            math.drawFractal(x, y);
           
           if(math.getColor(x,y) != control){
               lineDetected = true;
           }
        }

        //left edge
        x = startX;
        for(y = startY;y < startY+sectorHeight;y++){
            math.drawFractal(x, y);
            
            if(math.getColor(x,y) != control){
                lineDetected = true;
            }
        }
        //right edge
        x = startX+sectorWidth-1;
        for(y = startY;y < startY+sectorHeight;y++){
            math.drawFractal(x, y);
            
            if(math.getColor(x,y) != control){
                lineDetected = true;
            }
        }
        
        // Uncomment to see the fractal generate in real time (Dont try to zoom or drag the fractal)
        if(math.frame.animate){
            math.colorData();
            math.frame.repaint();
        }

        //if line is detected in quadrant split up the quadrant and look for the line
        //else
        //render all the pixels acording to the control 
        //in order to save on calculations
        if(lineDetected){
            //if quadrant is too small then just render all the pixels
            if(sectorHeight > 3){
                renderRectangle((startX)+1,                 (startY)+1,                 (sectorWidth/2)-1,    (sectorHeight/2)-1);
                renderRectangle((startX+sectorWidth/2),   (startY)+1,                 (sectorWidth/2),  (sectorHeight/2)-1);
                renderRectangle((startX)+1,                 (startY+sectorHeight/2),  (sectorWidth/2)-1,  (sectorHeight/2));
                renderRectangle((startX+sectorWidth/2),     (startY+sectorHeight/2),    (sectorWidth/2),    (sectorHeight/2));
            } else {
                fullRenderRectangle(startX,startY,sectorWidth,sectorHeight);
            }
        } else {
            if(math.filter == 0 || math.filter == 2){
                fullRenderRectangle(startX,startY,sectorWidth,sectorHeight,controlIteration);
            } else if (math.filter == 1) {
                fullRenderRectangle(startX,startY,sectorWidth,sectorHeight,0);
            }
        }
        
    }
 
    /**
     * renders the rest of the rectangle 
     * using the mandelbrot calcuation
     * @param startX
     * @param startY
     * @param sectorWidth
     * @param sectorHeight
     */
    public void fullRenderRectangle(int startX, int startY, int sectorWidth, int sectorHeight){
        for(int x = startX; x < startX + sectorWidth; x++){
            for(int y = startY; y < startY + sectorHeight; y++){
                if (x != startX && x != startX + sectorWidth - 1 && y != startY && y != startY + sectorHeight - 1) {
                    math.drawFractal(x, y);
                }
            }
        }
    
    }

    /** renders the rest of the rectangle 
     *  by filling up the space with control
     *  as to not cause unnessesary calcuations  
     * @param startX
     * @param startY
     * @param sectorWidth
     * @param sectorHeight
     * @param control
     */
    public void fullRenderRectangle(int startX, int startY, int sectorWidth, int sectorHeight, int control){
        for(int x = startX; x < startX + sectorWidth; x++){
            for(int y = startY; y < startY + sectorHeight; y++){
                if (x != startX && x != startX + sectorWidth - 1 && y != startY && y != startY + sectorHeight - 1) {
                    math.setColor(x,y,control);
                }
            }
        }
    
    }
    
    /**
     * detects if a pixel in the data has an edge or not
     * @param cords in the data
     * @return returns the intensity of the edge
     * @see FractalMath#colorData()
     */
    public int computeEdgeStrength(int x, int y) {
        int color = math.getColor(x, y);   
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int pixelX = x + i;
                int pixelY = y + j;
    
                if (pixelX >= 0 && pixelX < math.width &&
                    pixelY >= 0 && pixelY < math.height) {
                        if(color != data[pixelY * math.width + pixelX]){
                            return 1;
                        }
                }
            }
        }
    
        return 0;
    }
    
    
}