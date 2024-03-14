package scr;
//Graphics Library
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

//misc
import java.awt.Toolkit;

//Windows frame library
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class FractalFrame extends JFrame {

    public FractalListener fractalListener;
    public FractalMath fractalMath;
    public BufferedImage canvas;
    private int canvasWidth;
    private int canvasHeight;
    private BufferStrategy bufferStrategy;

    public FractalFrame() {
        super("Fractal Explorer");
        // sets icon of the window
        Image icon = new javax.swing.ImageIcon("assets/icon.png").getImage();
        setIconImage(icon);
    
        // Add Mouse/Key Listener
        fractalListener = new FractalListener(this);
        addKeyListener(fractalListener);
        addMouseListener(fractalListener);
        addMouseMotionListener(fractalListener);
        addMouseWheelListener(fractalListener);
    
        // creates window
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolKit.getScreenSize();
        setupFrame();
    
        // sets up the canvas
        canvasWidth = screenSize.width;
        canvasHeight = screenSize.height;
        setupCanvas();
    
        // Make the frame visible
        setVisible(true);
    
        // Enable double bufferinga
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

        // calculating fractals
        fractalMath = new FractalMath(this, 50, canvasWidth, canvasHeight);
        calculateFractal();
    }
    

    /**
     * Sets up the buffered image
     * to be as large as your screen
     */
    public void setupCanvas() {
        // gets size of screen

        // sets up the canvas
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);
        g2d.dispose();

    }

    /**
     * Sets up the frame to be
     * Fullscreen, not resizeable, 
     * and without the top bar's
     */
    private void setupFrame() {
        setSize(canvasWidth, canvasHeight);
        setUndecorated(true);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) bufferStrategy.getDrawGraphics();
        try {
            super.paint(g2D);
            g2D.drawImage(canvas, 0, 0, this);
        } finally {
            g2D.dispose();
        }
        bufferStrategy.show();
    }
    
   
    /**
     * 
     * updates the offset based on the change in x and change in y
     * @param dx change in x
     * @param dy change in y
     * @see FractalListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void updateOffset(int dx, int dy) {
        fractalMath.updateOffset(dx, dy);
        calculateFractal();

    }

    /**
     * updates the zoom on the fractal
     * @param zoomFactor how much to multiply the zoom by
     * @see FractalListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void updateZoomLevel(double zoomFactor) {
        fractalMath.updateZoomLevel(zoomFactor);
        calculateFractal();
    }

    /**
     * calculates the data for the canvas then paints the canvas
     */
    public void calculateFractal() {
        //fractalMath.multiThreadCalculateFractal(6);
        //fractalMath.borderTraceCalculation();
        fractalMath.edgeDetectionFractal();
        repaint();
    }


    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FractalFrame frame = new FractalFrame();
        });
    }

    

    
}
