package scr;

import java.awt.Point;
import java.util.Queue;

public class FractalBorderTrace extends Thread{
    private FractalMath math;
    private Queue<Point> queue;
    private int[][] data;


    /**
     * @param math where does the fractal get its numbers
     * @param queue how 
     * @param data
     */
    public FractalBorderTrace(FractalMath math, Queue<Point> queue, int[][] data) {
        this.math = math;
        this.queue = queue;
        this.data = data;
    }

    @Override
    public void run() {
        float minReal = math.centerReal - 2.5f / math.zoom;
        float maxReal = math.centerReal + 2.5f / math.zoom;
        float minImag = math.centerImag - 2.0f / math.zoom;
        float maxImag = math.centerImag + 2.0f / math.zoom;
        while(!queue.isEmpty()){
            Point current = queue.poll();
            if(data[current.x][current.y] == -2){
                //System.out.print(current.x+", "+current.y +": "+data[current.x][current.y]);
                float real = minReal + current.x * (maxReal - minReal) / (math.width);
                float imag = minImag + current.y * (maxImag - minImag) / (math.height);
                int iter = math.mandelbrotSet(real, imag);
                data[current.x][current.y] = iter;
                if(iter < math.maxIter){
                    addNeighborsToQueue(current.x, current.y);
                }
            }
        }
    }

    /**
     * checks if the neighbors are a valid to be placed into the queue or not
     * @param x
     * @param y
     */
    private void addNeighborsToQueue(int x, int y) {
        int[] dx = { -1, 0, 1, -1, 1, -1, 0, 1 };
        int[] dy = { -1, -1, -1, 0, 0, 1, 1, 1 };

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidCoordinate(newX, newY) && data[newX][newY] == 0 ) {
                data[newX][newY] = -2;
                queue.add(new Point(newX, newY));
            }
        }
    }

    /**helper method to check if a pixel is within the array or not
     * @param x
     * @param y
     * @return
     */
    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < math.width && y >= 0 && y < math.height;
    }




}
