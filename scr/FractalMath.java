/**
 * Name:Dante Harper
 * Date:2024 / 3 / 13
 * Desc: calculates the math required to render a fractal on the screen
 */
package scr;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class FractalMath {
    public FractalFrame frame;
    private FractalEdgeTrace tracer;
    public int maxIter;
    public int width;
    public int height;

    
    //data used by the program
    public int[][] data;
    private List<Color> colors;

    // where on the fractal to view
    public double centerReal = -0.5f;
    public double centerImag = 0;
    public double zoom = 1.0f;

    private double minReal;
    private double maxReal;
    private double minImag;
    private double maxImag;

    // seed for the julia set fractal
    public double seedReal;
    public double seedImag;

    /*
     * Filter 0: Normal
     * Filter 1: Normal But without filling in the empty quadrants
     * Filter 2: Edge Dectection Mode
     */
    public int filter = 1;


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
        this.data = new int[width][height];
        this.tracer = new FractalEdgeTrace(this, data);
        colors = generateColorPattern(50);

        minReal = centerReal - 2.5f / zoom;
        maxReal = centerReal + 2.5f / zoom;
        minImag = centerImag - 2.0f / zoom;
        maxImag = centerImag + 2.0f / zoom;

        seedReal = 0;
        seedImag = 0;
    }

    /**
     * Resets the fractal to default position
     * @see FractalFrame#resetFractal()
     * @see FractalListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void resetFractal(){
        seedReal = 0;
        seedImag = 0;
        centerReal = -0.5f;
        centerImag = 0;
        zoom = 1.0f;
        minReal = centerReal - 2.5f / zoom;
        maxReal = centerReal + 2.5f / zoom;
        minImag = centerImag - 2.0f / zoom;
        maxImag = centerImag + 2.0f / zoom;
        filter = 0;
    }

    /**
     * colors a pixel on the buffered image canvas depending on the 
     * number of iteration it took for the calculation to complete
     * @param cords on the canvas
     * @param iterations number of iteration it took to solve fractal
     * @see this{@link #generateColorPattern(int)}
     * @see this{@link #data}
     */
    public void setColor(int x, int y, int iterations) {
        int color = 0;
        if (iterations == maxIter ) {
            color = Color.BLACK.getRGB(); // color pixel black
        } else if (iterations == 0){
            color = Color.WHITE.getRGB(); // color pixel white
        }else {
            color = colors.get(iterations%colors.size()).getRGB(); // color pixel based on a gradient
        }
        if(frame.canvas.getRGB(x, y) != color){
            frame.canvas.setRGB(x, y, color);
        }
    }

    /**
     * loops through the data and sets all the pixels 
     * in the canvas as pixels arcording to the data
     * and if the filter is set to it apply a edge filter
     * @see this{@link #setColor(int, int, int)
     * @see FractalFrame#canvas
     * @see this{@link #data}
     */
    public void colorData(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(filter == 0 || filter == 1) {
                    setColor(x, y, data[x][y]);
                } else {
                    int edgeStrength = tracer.computeEdgeStrength(x, y);
                    int edge = edgeStrength != 0 ? 255 : 0;
                    setColor(x, y, edge);
                } 
                
            }
        }
        frame.fractalListener.loadingZoom = false;
    }

    /**
     * @param numColors number of colors in the list more colors the smoother gradient
     * @return a list of colors based on the HSB color list
     */
    public static List<Color> generateColorPattern(int numColors) {
        List<Color> colors = new ArrayList<>();

        for (int i = 0; i < numColors; i++) {
            double hue = (double) i / numColors; 
            double saturation = 1; 
            double brightness = 1;
            
            // create a color using the HSB set
            Color color = Color.getHSBColor((float)hue, (float)saturation, (float)brightness);
            //add the color
            colors.add(color);
        }

        return colors;
    }

    /**
     * chooses which fractal to use to draw the fractal
     * @param cords to start the path
     * @see this{@link #mandelbrotSet(int, int)}
     * @see this{@link #juliaSet(int, int)}
     */
    public int drawFractal(int x, int y){
        if(seedImag == 0 || seedReal == 0){
            return mandelbrotSet(x, y);
        } else {
            return juliaSet(x,y);
        }
    }

    
    /**
     * chooses which fractal to use to draw the path
     * @param cords to start the path
     * @see this{@link #drawMandelBrotSetPath(int, int)}
     * @see this{@link #drawJuliaSetPath(int, int)}
     */
    public void drawFractalPath(int x, int y){
        if(seedImag == 0 || seedReal == 0){
            drawMandelBrotSetPath(x, y);
        } else {
            drawJuliaSetPath(x,y);
        }
    }



    /**
     * calcualtes the mandelbrot set for the cordinates given
     * @param cordinate on the canvas
     * @return the number of iterations it takes to complete calculation
     */
    public int mandelbrotSet(int x, int y) {
        double real = minReal + x * (maxReal - minReal) / width;
        double imag = minImag + y * (maxImag - minImag) / height;
        int i = 1;
        double zReal = 0;
        double zImag = 0;
        double realSqr = 0;
        double imagSqr = 0;

        //run as long as it does not escape the fractal or exeeds the iteration limit
        while ((realSqr + imagSqr) < 4 && i < maxIter) {
            //iterate through the fractal
            zImag = 2 * zReal * zImag + imag;
            zReal = realSqr - imagSqr + real;
            //only calc the squared real and imag for optimization
            realSqr = zReal*zReal;
            imagSqr = zImag*zImag;

            i++;
        }

        return i;
    }

    /**
    * Calculates the Julia set for the given coordinates.
    * @param y cordinates on the canvas
    * @return The number of iterations it takes to complete the calculation
    */
    public int juliaSet(int x, int y) {
        double zReal = minReal + x * (maxReal - minReal) / width;
        double zImag = minImag + y * (maxImag - minImag) / height;
        int i = 1;
       // double realSqr = 0;
       // double imagSqr = 0;
        //run as long as it does not escape the fractal or exeeds the iteration limit
        while ((zReal * zReal + zImag * zImag) < 4 && i < maxIter) {
            //iterate through the fractal
            double zRealTemp = zReal * zReal - zImag * zImag + seedReal;
            zImag = 2 * zReal * zImag + seedImag;
            zReal = zRealTemp;
            
            //zImag = 2 * zReal * zImag + seedImag;
            //zReal = realSqr - imagSqr + seedReal;
            //only calc the squared real and imag for optimization
            //realSqr = zReal*zReal;
            //imagSqr = zImag*zImag;

           i++;
        }

        return i;
    }


    /**
     * draws the path the fractal takes at that point and draws it on the canvas
     * @param cordinate on the canvas
     */
    public void drawMandelBrotSetPath(int x, int y) {
        double real = minReal + x * (maxReal - minReal) / width;
        double imag = minImag + y * (maxImag - minImag) / height;
        double zReal = 0;
        double zImag = 0;
        int i = 0;
        Point prev = null;
        Point current = null;

        //run as long as it does not escape the fractal or exeeds the iteration limit
        while ((zReal * zReal) + (zImag * zImag) < 4 && i < maxIter) {
            //iterate through the fractal
            double zRealTemp = zReal * zReal - zImag * zImag + real;
            zImag = 2 * zReal * zImag + imag;
            zReal = zRealTemp;

            current = imagToPixel(zReal, zImag);
            frame.drawLine(current, prev);
            prev = current;

            i++;
        }
    }

     /**
     * draws the path the fractal takes at that point and draws it on the canvas
     * @param cordinate on the canvas
     */
    public void drawJuliaSetPath(int x, int y) {
        double zReal = minReal + x * (maxReal - minReal) / width;
        double zImag = minImag + y * (maxImag - minImag) / height;
        int i = 0;
        Point prev = null;
        Point current = null;
        //run as long as it does not escape the fractal or exeeds the iteration limit
        while ((zReal * zReal) + (zImag * zImag) < 4 && i < maxIter) {
            //iterate through the fractal
            double zRealTemp = zReal * zReal - zImag * zImag + seedReal;
            zImag = 2 * zReal * zImag + seedImag;
            zReal = zRealTemp;

            current = imagToPixel(zReal, zImag);
            frame.drawLine(current, prev);
            prev = current;
            
            i++;
        }
    }

    /**
     * @param point on the fractal
     * @return point on the canvas
     */
     public Point imagToPixel(double real, double imag) {
        int x = (int) ((real - minReal) / (maxReal - minReal) * width);
        int y = (int) ((imag - minImag) / (maxImag - minImag) * height);
        return new Point(x, y);
    }

    /**
     * @param point on the fractal
     * @return point on the canvas
     */
     public double xToReal(int x) {
        double real = minReal + x * (maxReal - minReal) / width;
        return real;
    }

    /**
     * @param point on the fractal
     * @return point on the canvas
     */
    public double yToImag(int y) {
        double imag = minImag + y * (maxImag - minImag) / height;
        return imag;
    }
    
    

    /**
     * Calculates the fractal distributing the load between 16 threads
     * @see FractalEdgeTrace#calculateEdgeFractal(int)
     */
    public void edgeDetectionFractal() {
        tracer.calculateEdgeFractal(1);
    }

    /**
     * updates the offset to act as if your 
     * dragging the fractal around your screen
     * @param dx change in x
     * @param dy change in y
     * @see FractalFrame#updateOffset(int, int)
     */
    public void updateOffset(int dx, int dy) {
        double realIncrement = (2.5f / zoom) / width;
        double imagIncrement = (2.0f / zoom) / height;

        centerReal -= dx * realIncrement;
        centerImag -= dy * imagIncrement;

        //recalculates the position of the fractal
        minReal = centerReal - 2.5f / zoom;
        maxReal = centerReal + 2.5f / zoom;
        minImag = centerImag - 2.0f / zoom;
        maxImag = centerImag + 2.0f / zoom;
    }


    /**
     * updates how far to zoom into the fractal
     * @param zoomFactor
     * @see FractalFrame#updateZoomLevel(double)
     */
    public void updateZoomLevel(double zoomFactor) {
        zoom *= zoomFactor;

        //recalculates the position of the fractal
        minReal = centerReal - 2.5f / zoom;
        maxReal = centerReal + 2.5f / zoom;
        minImag = centerImag - 2.0f / zoom;
        maxImag = centerImag + 2.0f / zoom;
        
    }
}
