/**
 * Name:Dante Harper
 * Date:2024 / 3 / 13
 * Desc: calculates the fractal by tracing 
 * the edges of the fractal and skipping a lot of the calculations
 * since when u find the edge of a fractal you just need to fill 
 * everything thats within that edge with whatever color represents it 
 */
package scr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FractalEdgeTrace {

    private FractalMath math;
    private int[][] data;

    public FractalEdgeTrace(FractalMath math, int[][] data) {
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
        }


        //waits for all the threads to be finnished
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // will force the working thread to close
            Thread.currentThread().interrupt();
        }



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
     *    the edges of the rectangle had been a single color
     */
    public void renderRectangle(int startX, int startY, int sectorWidth, int sectorHeight){
        boolean lineDetected = false;
        int control = math.drawFractal(startX, startY);

        int x = 0;
        int y = 0;
        //top edge
        y = startY;
        for(x = startX;x < startX+sectorWidth;x++){
            data[x][y] = math.drawFractal(x, y);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //bottom edge
        y = startY+sectorHeight-1;
        for(x = startX;x < startX+sectorWidth;x++){
            data[x][y] = math.drawFractal(x, y);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //left edge
        x = startX;
        for(y = startY;y < startY+sectorHeight;y++){
            data[x][y] = math.drawFractal(x, y);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //right edge
        x = startX+sectorWidth-1;
        for(y = startY;y < startY+sectorHeight;y++){
            data[x][y] = math.drawFractal(x, y);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //if line is detected in quadrant split up the quadrant and look for the line
        //else
        //render all the pixels acording to the control 
        //in order to save on calculations
        if(lineDetected){
            //if quadrant is too small then just render all the pixels
            if(sectorHeight > 2){
                renderRectangle(startX,                 startY,                 sectorWidth/2,sectorHeight/2);
                renderRectangle(startX+sectorWidth/2,   startY,                 sectorWidth/2,sectorHeight/2);
                renderRectangle(startX,                 startY+sectorHeight/2,  sectorWidth/2,sectorHeight/2);
                renderRectangle(startX+sectorWidth/2,   startY+sectorHeight/2,  sectorWidth/2,sectorHeight/2);
            } else {
                fullRenderRectangle(startX,startY,sectorWidth,sectorHeight);
            }
        } else {
            if(math.filter == 0 || math.filter == 2){
                fullRenderRectangle(startX,startY,sectorWidth,sectorHeight,control);
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
        for(int x = startX+1; x < startX + sectorWidth-1; x++){
            for(int y = startY+1; y < startY + sectorHeight-1; y++){ // Fixed loop condition
                data[x][y] = math.drawFractal(x, y);
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
        for(int x = startX+1; x < startX + sectorWidth-1; x++){
            for(int y = startY+1; y < startY + sectorHeight-1; y++){ // Fixed loop condition
                data[x][y] = control;
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
        int[][] gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    
        int sumX = 0;
        int sumY = 0;
    
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int pixelX = x + i;
                int pixelY = y + j;
    
                if (pixelX >= 0 && pixelX < math.width &&
                    pixelY >= 0 && pixelY < math.height) {
                    sumX += data[pixelX][pixelY] * gx[j + 1][i + 1];
                    sumY += data[pixelX][pixelY] * gy[j + 1][i + 1];
                }
            }
        }
    
        return (int) Math.sqrt(sumX * sumX + sumY * sumY);
    }
    

}
