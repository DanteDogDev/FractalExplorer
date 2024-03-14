package scr;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FractalMath {
    public int maxIter;
    public int width;
    public int height;

    public FractalFrame frame;
    public float centerReal = -0.5f;
    public float centerImag = 0;
    public float zoom = 1.0f;
    


    /**
     * @param frame frame of the canvas
     * @param maxIter maxium amount of iterations for each calculation of the fractal
     * @param width width of the canvas
     * @param height height of the canvas
     */
    public FractalMath(FractalFrame frame, int maxIter, int width, int height) {
        this.frame = frame;
        this.maxIter = maxIter;
        this.width = width;
        this.height = height;
    }


    /**
     * colors a pixel on the buffered image canvas depending on the 
     * number of iteration it took for the calculation to complete
     * @param real
     * @param imag
     * @param iterations
     */
    public void setColor(int x, int y, int iterations) {
        int color = 0;
        if (iterations == maxIter ) {
            color = Color.BLACK.getRGB();
        } else {
            float hue = (float) (iterations-1) / maxIter;
            color = Color.getHSBColor(hue, 1, 1).getRGB();
        }
        if(frame.canvas.getRGB(x, y) != color){
            frame.canvas.setRGB(x, y, color);
        }
    }


    /**
     * calcualtes the mandelbrot set for the cordinates given
     * @param real
     * @param imag
     * @return
     */
    public int mandelbrotSet(float real, float imag) {
        int i = 0;
        float zReal = 0;
        float zImag = 0;

        for (i = 0; i < maxIter; i++) {
            float zRealTemp = zReal * zReal - zImag * zImag + real;
            zImag = 2 * zReal * zImag + imag;
            zReal = zRealTemp;

            if ((zReal * zReal) + (zImag * zImag) > 4) {
                break;
            }
        }

        return i;
    }

    /**
     * calculates the fractal by flood filling the border around 
     * the canvas until it reaches the black parts of the set and skips then in order to 
     * not have to compute them
     * @param threads
     * @see FractalBorderTrace#run()
     */
    public void borderTraceCalculation() {
        double startTime = System.nanoTime();
        int[][] data = new int[width][height];
        Queue<Point> queue = new LinkedList<Point>();

        for (int x = 0; x < width; x++) {
            queue.add(new Point(x, 0));
            data[x][0] = -2;
            queue.add(new Point(x, height - 1));
            data[x][height - 1] = -2;
        }

        for (int y = 1; y < height - 1; y++) {
            queue.add(new Point(0, y));
            data[0][y] = -2;
            queue.add(new Point(width - 1, y));
            data[width - 1][y] = -2;
        }

        // Start threads for border tracing
        ArrayList<FractalBorderTrace> threadList = new ArrayList<FractalBorderTrace>();
        for (int i = 0; i < 1; i++) {
            FractalBorderTrace thread = new FractalBorderTrace(this, queue, data);
            thread.start();
            threadList.add(thread);
        }
    
        // Wait for all threads to finish
        for (FractalBorderTrace thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        // Set colors on the canvas using the computed Mandelbrot set data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setColor(x, y, data[x][y]);
            }
        }
    
        // Update loading status
        frame.fractalListener.loadingZoom = false;
        System.out.println((System.nanoTime() - startTime));
    }
    

    /**
     * assigs threads to a array to calculate the iterations it takes to solve the 
     * fractal faster than it would take to just loop through every pixel
     * @param threads
     * @see FractalThreading#run()
     */
    public void multiThreadCalculateFractal(int threads) {
        double startTime = System.nanoTime();
        ArrayList<FractalThreading> threadList = new ArrayList<FractalThreading>();
        int[][] data = new int[width][height];
        for (int i = 0; i < threads; i++) {
            FractalThreading thread = new FractalThreading(this, i, threads, data);
            thread.start();
            threadList.add(thread);
        }

        for (FractalThreading thread : threadList) {
            try {
                thread.join();
                thread.postRun();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //new FractalEdgeTrace(this,width,height,data).applyEdgeDetection();
        
        frame.fractalListener.loadingZoom = false;
        System.out.println((System.nanoTime()-startTime));

    }

    public void edgeDetectionFractal() {
        double startTime = System.nanoTime();
        ArrayList<FractalThreading> threadList = new ArrayList<FractalThreading>();
        int[][] data = new int[width][height];

        FractalEdgeTrace tracer = new FractalEdgeTrace(this, data);
        tracer.calculateEdgeFractal();

        //color it
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                setColor(x, y, data[x][y]);
                
            }
        }

        frame.fractalListener.loadingZoom = false;
        System.out.println((System.nanoTime()-startTime));
    }

    /**
     * updates the offset to act as if your 
     * dragging the fractal around your screen
     * @param dx change in x
     * @param dy change in y
     * @see FractalFrame#updateOffset(int, int)
     */
    public void updateOffset(int dx, int dy) {
        float realIncrement = (2.5f / zoom) / width;
        float imagIncrement = (2.0f / zoom) / height;

        centerReal -= dx * realIncrement;
        centerImag -= dy * imagIncrement;
    }


    /**
     * updates how far to zoom into the fractal
     * @param zoomFactor
     * @see FractalFrame#updateZoomLevel(double)
     */
    public void updateZoomLevel(double zoomFactor) {
        zoom *= zoomFactor;
        
    }
}
