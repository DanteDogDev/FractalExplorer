package scr;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class FractalMath {
    public int maxIter;
    public int width;
    public int height;

    private BufferedImage canvas;

    private double minReal = -2.2;
    private double maxReal = 1;
    private double minImag = -1.2;
    private double maxImag = 1.2;

    private double offsetReal = 0;
    private double offsetImag = 0;



    public FractalMath(BufferedImage canvas, int maxIter, int width, int height) {
        this.canvas = canvas;
        this.maxIter = maxIter;
        this.width = width;
        this.height = height;
    }

    public void calculateFractal() {
        // Loop through each row of pixels
        for (int y = 0; y < height; y++) {
            // Loop through each column of pixels
            for (int x = 0; x < width; x++) {
                // Get the color of the pixel at position (x, y)
                
                double real = minReal + x * (maxReal - minReal) / width;
                double imag = minImag + y * (maxImag - minImag) / height;

                int iter = MandelbrotSet(real, imag);

                Color color = getColor(iter);
                
                // Set the new color of the pixel
                canvas.setRGB(x, y, color.getRGB());
            }
        }
    }

    private Color getColor(int iterations) {
        // Map the number of iterations to a color
        if (iterations == maxIter) {
            return Color.BLACK; // Point is in the Mandelbrot set, color it black
        } else {
            // Interpolate between two colors based on the number of iterations
            float hue = (float) iterations / maxIter; // Use iteration count for hue
            return Color.getHSBColor(hue, 1, 1); // Convert HSB to RGB color
        }
    }

    public int MandelbrotSet(double real, double imag) {
        int i = 0;
        double zReal = 0;
        double zImag = 0;
    
        for (i = 0; i < maxIter; i++) {
            double zRealTemp = zReal * zReal - zImag * zImag + real;
            zImag = 2 * zReal * zImag + imag;
            zReal = zRealTemp;
    
            if ((zReal * zReal) + (zImag * zImag) > 4) {
                break;
            }
        }
    
        return i;
    }

    /**
     * 
     * updates the offset based on the change in x and change in y
     * @param dx change in x
     * @param dy change in y
     * @see FractalListener#mouseDragged(java.awt.event.MouseEvent)
     * @see FractalFrame
     */
    public void updateOffset(int dx, int dy) {
        offsetReal += dx;
        offsetImag += dy;
    }
    

}
