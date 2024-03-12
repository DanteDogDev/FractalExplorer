package scr;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class FractalMath {
    public int maxIter;
    public int width;
    public int height;

    private BufferedImage canvas;

    private double centerReal = -0.5;
    private double centerImag = 0;
    private double zoom = 1.0;

    public FractalMath(BufferedImage canvas, int maxIter, int width, int height) {
        this.canvas = canvas;
        this.maxIter = maxIter;
        this.width = width;
        this.height = height;
    }

    private Color getColor(int iterations) {
        if (iterations == maxIter) {
            return Color.BLACK; 
        } else {
            float hue = (float) iterations / maxIter;
            return Color.getHSBColor(hue, 1, 1); 
        }
    }

    public int mandelbrotSet(double real, double imag) {
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

    public void calculateFractal() {
        double minReal = centerReal - 2.5 / zoom;
        double maxReal = centerReal + 2.5 / zoom;
        double minImag = centerImag - 2.0 / zoom;
        double maxImag = centerImag + 2.0 / zoom;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double real = minReal + x * (maxReal - minReal) / width;
                double imag = minImag + y * (maxImag - minImag) / height;

                int iter = mandelbrotSet(real, imag);
                Color color = getColor(iter);
                canvas.setRGB(x, y, color.getRGB());
            }
        }
    }

    public void updateOffset(int dx, int dy) {
        double realIncrement = (2.5 / zoom) / width;
        double imagIncrement = (2.0 / zoom) / height;

        centerReal -= dx * realIncrement;
        centerImag -= dy * imagIncrement;
    }


    public void updateZoomLevel(double zoomFactor) {
        zoom *= zoomFactor;
    }
}
