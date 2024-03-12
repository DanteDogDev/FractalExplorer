package scr;
//Graphics Library
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

//misc
import java.awt.Point;
import java.awt.Toolkit;

//Windows frame library
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class FractalFrame extends JFrame {

    private FractalListener fractalListener;
    private BufferedImage canvas;
    private int canvasWidth;
    private int canvasHeight;
    private int xOffset = 0;
    private int yOffset = 0;
    private double zoomLevel = 1.0;
    private BufferStrategy bufferStrategy;

    public FractalFrame() {
        super("Fractal Explorer");
        //sets icon of the window
        Image icon = new javax.swing.ImageIcon("assets/icon.png").getImage();
        setIconImage(icon);
    
        //Add Mouse/Key Listener
        fractalListener = new FractalListener(this);
        addKeyListener(fractalListener);
        addMouseListener(fractalListener);
        addMouseMotionListener(fractalListener);
        addMouseWheelListener(fractalListener);
    
        //creates window
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolKit.getScreenSize();
        setupFrame();
    
        //sets up the canvas
        canvasWidth = screenSize.width;
        canvasHeight = screenSize.height;
        setupCanvas();
    
        // Make the frame visible
        setVisible(true);
    
        // Enable double buffering
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }
    

    /**
     * Sets up the buffered image
     * to be as large as your screen
     */
    public void setupCanvas() {
        //gets size of screen

        //sets up the canvas
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);
        g2d.dispose();

        repaint();
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
            g2D.scale(zoomLevel, zoomLevel);
            g2D.drawImage(canvas, xOffset, yOffset, this);
        } finally {
            g2D.dispose();
        }
        bufferStrategy.show(); // Show the drawn buffer
    }
    
   
    /**
     * 
     * updates the offset based on the change in x and change in y
     * @param dx change in x
     * @param dy change in y
     * @see FractalListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void updateOffset(int dx, int dy) {
        xOffset += dx/zoomLevel;
        yOffset += dy/zoomLevel;
        repaint();

    }

    /**
     * @return XOffset
     */
    public int getXOffset() {
        return xOffset;
    }
    /**
     * @return YOffset
     */
    public int getYOffset() {
        return yOffset;
    }
    
    /**
     * @return Zoom Level
     */
    public double getZoomLevel() {
        return zoomLevel;
    }

    /**
     * Sets the zoom level and then updates the canvas
     * @param zoom
     * @see FractalListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void setZoomLevel(double zoom) {
        if(zoom == zoomLevel) {
            return;
        }
        Point mousePos = fractalListener.getMousePointToCanvas();
        zoomLevel = zoom;
        Point postMousePos = fractalListener.getMousePointToCanvas();
        xOffset += postMousePos.x - mousePos.x;
        yOffset += postMousePos.y - mousePos.y;
        repaint(); // Recreate the canvas with the new zoom level
    }

    /** temp
     * @param p
     */
    public void drawDot(Point p){
        int x = p.x;
        int y = p.y;
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, 11, 11); // Draw a red rectangle
        g2d.dispose();
        repaint();

    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FractalFrame frame = new FractalFrame();
        });
    }

    

    
}
