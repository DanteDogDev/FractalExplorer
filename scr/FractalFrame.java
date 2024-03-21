/**
 * Name:Dante Harper
 * Date:2024 / 3 / 13
 * Desc: generates a frame with a buffered image that 
 * has a fractal where you could freely explore
 */
package scr;
//Graphics Library
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

//misc
import java.awt.Toolkit;

//Windows frame library
import javax.swing.JFrame;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class FractalFrame extends JFrame {
    //fractal math required to generate the fractal 
    public FractalListener fractalListener;
    
    //listeners so that i can check for keyboard/mouse inputs
    public FractalMath fractalMath;

    //When animating disable all possible player input aside from esc to leave program and update the 
    //canvas during the generation in order to visualize how the program generates the fractal
    public boolean animate;

    //The image the fractal is stored in
    public BufferedImage canvas;
    private BufferStrategy bufferStrategy;

    //how many times smaller will the acutual resoulution of the picture be compared to the screen resolution
    public double scale;

    //dimensions of the screen
    public int canvasWidth;
    public int canvasHeight;

    /**
     * @param maxIterations the maxiumum amount of iterations
     */
    public FractalFrame(int maxIterations, double scale, boolean animate) {
        super("Fractal Explorer");
        this.scale = scale;
        this.animate = animate;
        // sets icon of the window
        Image icon = new javax.swing.ImageIcon("assets/icon.png").getImage();
        setIconImage(icon);

        // Add Mouse/Key Listener
        fractalListener = new FractalListener(this);

        // creates window
        setupFrame();

        //sets up the buffered image
        setupCanvas();

        // calculating fractals
        fractalMath = new FractalMath(this, maxIterations, canvasWidth, canvasHeight);
        //instead of just calling the calculate fractal method i just call these so that i can animate the process 
        //and not let the user to interact with the program after that to prevent bugs

        fractalMath.edgeDetectionFractal();
        fractalMath.colorData();
        repaint();
    }
    

    /**
     * sets up the buffered image into the frame so that it has double buffering enable
     * and starts out white
     */
    public void setupCanvas() {
        // sets up the image
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        //sets up default image
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);
        g2d.dispose();
        //sets up double buffering
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

    }

    /**
     * Sets up the frame to be
     * Fullscreen, not resizeable, 
     * and without the top bar's
     * and generates the screen dimensions
     */
    private void setupFrame() {
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolKit.getScreenSize();
        canvasWidth = (int)(screenSize.width/scale);
        canvasHeight = (int)(screenSize.height/scale);
        setSize(canvasWidth, canvasHeight);
        setUndecorated(true);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    /**
     * @Override the JFrame paint method 
     * in order to get the buffered image to show up on the frame
     */
    public void paint(Graphics g) {
        Graphics2D g2D = null;
        try {
            g2D = (Graphics2D) bufferStrategy.getDrawGraphics();
            g2D.scale(scale, scale);
            super.paint(g2D);
            g2D.drawImage(canvas, 0, 0, this);
        } catch (Exception e) {
            g2D.dispose();
        }
        bufferStrategy.show();
    }

    /**
     * updates the maximum iteration of the fractal
     * @param d how much to change it by
     */
    public void updateMaxIter(int d){
        if(animate == false){
            fractalMath.maxIter += d;
            calculateFractal();
        }
    }
   
    /**
     * updates the offset based on the change in x and change in y
     * @param dx change in x
     * @param dy change in y
     * @see FractalListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void updateOffset(int dx, int dy) {
        if(animate == false){
            fractalMath.updateOffset(dx, dy);
            calculateFractal();
        }
        

    }

    /**
     * updates the zoom on the fractal
     * @param zoomFactor how much to change the zoom by
     * @see FractalListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void updateZoomLevel(double zoomFactor) {
        if(animate == false){
            fractalMath.updateZoomLevel(zoomFactor);
            calculateFractal();
        }
        
    }

    /**
    * generates a new fractal based on a cordinate from the mandelbrot set translated into the julia set
    * @param x px on the screen
    * @param y px on the screen
    */
    public void setFractalSeed(int x, int y){
        if(animate == false){
            fractalMath.seedReal = fractalMath.xToReal(x);
            fractalMath.seedImag = fractalMath.yToImag(y);
            calculateFractal();
        }
        
    }

    public void setFilter(int filter) {
        if(animate == false){
            fractalMath.filter = filter;
            calculateFractal();
        }
    }

    /**
     * calculates the data for the 
     * canvas then paints the canvas
     * to display the fractal in its current position
     */
    public void calculateFractal() {
        if(animate == false){
            fractalMath.edgeDetectionFractal();
            fractalMath.colorData();
            repaint();
        }
    }

    /**
     * resets the fractal to be in default position
     */
    public void resetFractal(){
        if(animate == false){
            fractalMath.resetFractal();
            calculateFractal();
        }
        
    }

    /**
     * generates the line that forms on that 
     * spot on the fractal and draws the path for it
     * @param x px on the screen
     * @param y px on the screen
     */
    public void calculateFractalPath(int x, int y){
        if(animate == false){
            fractalMath.colorData();
            fractalMath.drawFractalPath(x,y);
            repaint();
        }
    }

    /**
     * calcualtes the average time to generate the mandelbrot set inital image
     * and averages out the time and displays it
     * @param iterations the number of times the image is calculated
     */
    public void calculateAverageTime(int iterations) {
        //makes the frame not appear
        setVisible(false);
        long totalTime = 0;
        //for every iteration
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            fractalMath.edgeDetectionFractal();
            fractalMath.colorData();
            repaint();
            long endTime = System.nanoTime();
            //add the total time to the sum
            totalTime += ((endTime - startTime) / 1000000);//add the amount in millisecounds to the total time
        }
        //divide the sum by the number of iterations
        double averageDuration = totalTime / ((double)iterations);
        System.out.println("Average Time for "+iterations+" iterations of "+fractalMath.maxIter+" precision for: " + averageDuration + " milliseconds");
        //close the program when done
        dispose();

    }

    /**
     * draws a line from point A to point B on the image
     * @param p1 point A
     * @param p2 point B
     */
    public void drawLine(Point p1, Point p2) {
        if(p2 == null || p1 == null){
            return;
        }
        Graphics g = canvas.getGraphics();
        g.setColor(Color.GRAY);
        g.drawLine(p1.x, p1.y, p2.x, p2.y); 
        g.dispose();
    }

    /**
     * draws a small circle on the screen at point A
     * @param p point A
     * @param color color of the point
     */
    public void drawDot(Point p,Color color) {
        if(p == null){
            return;
        }
        Graphics g = canvas.getGraphics();
        g.setColor(color);
        g.drawOval(p.x, p.y, 2, 2); 
        g.dispose();
    }    
}
