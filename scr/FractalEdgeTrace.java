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

    public void calculateEdgeFractal(int numThreads){
        int sectorWidth = (math.width/8);
        int sectorHeight = (math.height/8);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < 64; i++) {
            final int startX = i / 8 * sectorWidth;
            final int startY = i % 8 * sectorHeight;
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
 
    public void fullRenderRectangle(int startX, int startY, int sectorWidth, int sectorHeight){
        for(int x = startX+1; x < startX + sectorWidth-1; x++){
            for(int y = startY+1; y < startY + sectorHeight-1; y++){ // Fixed loop condition
                //float real = minReal + x * (maxReal - minReal) / math.width;
                //float imag = minImag + y * (maxImag - minImag) / math.height;
                data[x][y] = math.drawFractal(x, y);
            }
        }
    }
    public void fullRenderRectangle(int startX, int startY, int sectorWidth, int sectorHeight, int control){
        for(int x = startX+1; x < startX + sectorWidth-1; x++){
            for(int y = startY+1; y < startY + sectorHeight-1; y++){ // Fixed loop condition
                data[x][y] = control;
            }
        }
    }
    
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
    
    public void applyEdgeDetection() {
        for (int y = 0; y < math.height; y++) {
            for (int x = 0; x < math.width; x++) {
                int edgeStrength = computeEdgeStrength(x, y);

                int edge = edgeStrength != 0 ? 255 : 0;

                math.setColor(x, y, edge);
            }
        }
    }


}
