package scr;

public class FractalThreading extends Thread {
    
    private FractalMath math;
    private int threadNum;
    private int inc;
    private int[][] mandelbrotData;


    /**
     * @param math where does the fractal get its numbers
     * @param threadNum the id of the thread
     * @param inc how many threads are there
     * @param mandelbrotData the array where the fractal is stored
     */
    public FractalThreading(FractalMath math, int threadNum, int inc, int[][] mandelbrotData) {
        this.math = math;
        this.threadNum = threadNum;
        this.inc = inc;
        this.mandelbrotData = mandelbrotData;
    }

    
    /*
     * loops through the canvas and 
     * calcuations the threads fair share of pixels
     */
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
                    mandelbrotData[x][y] = iter;
                }
            }
        } catch (Exception e) {
            System.out.println(x+", "+y);
        }
    }

    /**
     * colors in the pixel on the canvas after it has computed the data
     */
    public void postRun() {
        for (int y = 0; y < math.height; y++) {
            for (int x = threadNum; x < math.width; x += inc) {
                math.setColor(x, y, mandelbrotData[x][y]);
            }
        }
    }
    
}
