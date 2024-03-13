package scr;

public class FractalThreading extends Thread {
    
    private FractalMath math;
    private int threadNum;
    private int inc;
    private int[][] mandelbrotData; // Array to store Mandelbrot set data

    public FractalThreading(FractalMath math, int threadNum, int inc, int[][] mandelbrotData) {
        this.math = math;
        this.threadNum = threadNum;
        this.inc = inc;
        this.mandelbrotData = mandelbrotData;
    }

    

    @Override
    public void run() {
        float minReal = math.centerReal - 2.5f / math.zoom;
        float maxReal = math.centerReal + 2.5f / math.zoom;
        float minImag = math.centerImag - 2.0f / math.zoom;
        float maxImag = math.centerImag + 2.0f / math.zoom;
        int x = 0;
        int y = 0;
        
        try {
            for (y = 0; y < math.height; y++) {
                for (x = threadNum; x < math.width; x += inc) {
                    float real = minReal + x * (maxReal - minReal) / math.width;
                    float imag = minImag + y * (maxImag - minImag) / math.height;
                    int iter = math.mandelbrotSet(real, imag);
                    mandelbrotData[x][y] = iter; // Store Mandelbrot set data
                }
            }
        } catch (Exception e) {
            System.out.println(x+", "+y);
        }
    }

    public void postRun() {
        for (int y = 0; y < math.height; y++) {
            for (int x = threadNum; x < math.width; x += inc) {
                math.setColor(x, y, mandelbrotData[x][y]);
            }
        }
    }
    
}
