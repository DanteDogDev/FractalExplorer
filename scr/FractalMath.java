package scr;

import java.awt.Color;
import java.awt.Point;

public class FractalMath {
    public int maxIter;
    public int width;
    public int height;

    public FractalFrame frame;
    public float centerReal = -0.5f;
    public float centerImag = 0;
    public float zoom = 1.0f;
    
    private int[][] data;
    private FractalEdgeTrace tracer;

    private float minReal;
    private float maxReal;
    private float minImag;
    private float maxImag;

    public float seedReal;
    public float seedImag;

    /*
     * Filter 0: Normal
     * Filter 1: Normal But without filling in the empty quadrants
     * Filter 2: Edge Dectection Mode
     */
    public int filter = 0;


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

        minReal = centerReal - 2.5f / zoom;
        maxReal = centerReal + 2.5f / zoom;
        minImag = centerImag - 2.0f / zoom;
        maxImag = centerImag + 2.0f / zoom;

        seedReal = 0;
        seedImag = 0;
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
        } else if (iterations == 0){
            color = Color.WHITE.getRGB();
        }else {
            float hue = (float) (iterations-1) / maxIter;
            color = Color.getHSBColor(hue, 1, 1).getRGB();
        }
        if(frame.canvas.getRGB(x, y) != color){
            frame.canvas.setRGB(x, y, color);
        }
    }

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

    public int drawFractal(int x, int y){
        if(seedImag == 0 || seedReal == 0){
            return mandelbrotSet(x, y);
        } else {
            return juliaSet(x,y);
        }
    }

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
        float real = minReal + x * (maxReal - minReal) / width;
        float imag = minImag + y * (maxImag - minImag) / height;
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
    * Calculates the Julia set for the given coordinates.
    * @param y cordinates on the canvas
    * @return The number of iterations it takes to complete the calculation
    */
    public int juliaSet(int x, int y) {
        float real = minReal + x * (maxReal - minReal) / width;
        float imag = minImag + y * (maxImag - minImag) / height;
        int i = 0;
        float zReal = real;
        float zImag = imag;

        for (i = 0; i < maxIter; i++) {
            float zRealTemp = zReal * zReal - zImag * zImag + seedReal;
            zImag = 2 * zReal * zImag + seedImag;
            zReal = zRealTemp;

            if ((zReal * zReal) + (zImag * zImag) > 4) {
                break;
            }
        }

        return i;
    }


    /**
     * draws the path the fractal takes at that point and draws it on the canvas
     * @param cordinate on the canvas
     */
    public void drawMandelBrotSetPath(int x, int y) {
        float real = minReal + x * (maxReal - minReal) / width;
        float imag = minImag + y * (maxImag - minImag) / height;
        float zReal = 0;
        float zImag = 0;
        Point prev = null;
        Point current = null;
        for (int i = 0; i < 100; i++) {
            float zRealTemp = zReal * zReal - zImag * zImag + real;
            zImag = 2 * zReal * zImag + imag;
            zReal = zRealTemp;

            current = imagToPixel(zReal, zImag);
            frame.drawLine(current, prev);
            prev = current;
            if ((zReal * zReal) + (zImag * zImag) > 4) {
                break;
            }
        }
    }

     /**
     * draws the path the fractal takes at that point and draws it on the canvas
     * @param cordinate on the canvas
     */
    public void drawJuliaSetPath(int x, int y) {
        float real = minReal + x * (maxReal - minReal) / width;
        float imag = minImag + y * (maxImag - minImag) / height;
        float zReal = real;
        float zImag = imag;
        Point prev = null;
        Point current = null;
        for (int i = 0; i < 100; i++) {
            float zRealTemp = zReal * zReal - zImag * zImag + seedReal;
            zImag = 2 * zReal * zImag + seedImag;
            zReal = zRealTemp;

            current = imagToPixel(zReal, zImag);
            frame.drawLine(current, prev);
            prev = current;
            if ((zReal * zReal) + (zImag * zImag) > 4) {
                break;
            }
        }
    }
    /**
     * @param point on the fractal
     * @return point on the canvas
     */
     public Point imagToPixel(float real, float imag) {
        int x = (int) ((real - minReal) / (maxReal - minReal) * width);
        int y = (int) ((imag - minImag) / (maxImag - minImag) * height);
        return new Point(x, y);
    }

    /**
     * @param point on the fractal
     * @return point on the canvas
     */
     public float xToReal(int x) {
        float real = minReal + x * (maxReal - minReal) / width;
        return real;
    }

    /**
     * @param point on the fractal
     * @return point on the canvas
     */
    public float yToImag(int y) {
        float imag = minImag + y * (maxImag - minImag) / height;
        return imag;
    }
    
    

    public void edgeDetectionFractal() {
        tracer.calculateEdgeFractal(16);
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

        minReal = centerReal - 2.5f / zoom;
        maxReal = centerReal + 2.5f / zoom;
        minImag = centerImag - 2.0f / zoom;
        maxImag = centerImag + 2.0f / zoom;
        
    }
}
