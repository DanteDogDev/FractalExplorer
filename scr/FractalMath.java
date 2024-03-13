package scr;

import java.awt.Color;
import java.util.ArrayList;

public class FractalMath {
    public int maxIter;
    public int width;
    public int height;

    private FractalFrame frame;
    public float centerReal = -0.5f;
    public float centerImag = 0;
    public float zoom = 1.0f;

    public FractalMath(FractalFrame frame, int maxIter, int width, int height) {
        this.frame = frame;
        this.maxIter = maxIter;
        this.width = width;
        this.height = height;
    }

    public void setColor(int real, int imag, int iterations) {
        if (iterations == maxIter) {
            frame.canvas.setRGB(real, imag, Color.BLACK.getRGB());
        } else {
            float hue = (float) iterations / maxIter;
            frame.canvas.setRGB(real, imag, Color.getHSBColor(hue, 1, 1).getRGB());
        }
    }

    public void setColor(int real, int imag, Color color) {
        
        frame.canvas.setRGB(real, imag, color.getRGB());
        
    }

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

    public void multiThreadCalculateFractal(int threads) {
        double startTime = System.nanoTime();
        ArrayList<FractalThreading> threadList = new ArrayList<FractalThreading>();
        int[][] arr = new int[width][height];
        for (int i = 0; i < threads; i++) {
            FractalThreading thread = new FractalThreading(this, i, threads, arr);
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
        
        frame.fractalListener.loadingZoom = false;
        System.out.println((System.nanoTime()-startTime));

    }

    public void calculateFractal() {
        double startTime = System.nanoTime();
        float minReal = centerReal - 2.5f / zoom;
        float maxReal = centerReal + 2.5f / zoom;
        float minImag = centerImag - 2.0f / zoom;
        float maxImag = centerImag + 2.0f / zoom;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float real = minReal + x * (maxReal - minReal) / width;
                float imag = minImag + y * (maxImag - minImag) / height;

                int iter = mandelbrotSet(real, imag);
                setColor(x, y, iter);
            }
        }
        System.out.println((System.nanoTime()-startTime));
    }

    public void updateOffset(int dx, int dy) {
        float realIncrement = (2.5f / zoom) / width;
        float imagIncrement = (2.0f / zoom) / height;

        centerReal -= dx * realIncrement;
        centerImag -= dy * imagIncrement;
    }


    public void updateZoomLevel(double zoomFactor) {
        zoom *= zoomFactor;
    }
}
