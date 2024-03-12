package scr;
//Graphics Library
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

//Windows frame library
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class FractalFrame extends JFrame {

    private FractalListener fractalListener;
    private BufferedImage canvas;
    private int canvasWidth;
    private int canvasHeight;
    private int imageWidth;
    private int imageHeight;
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
    

    public void setupCanvas() {
        //gets size of screen
        imageWidth = (int) (canvasWidth);
        imageHeight = (int) (canvasHeight);

        //sets up the canvas
        canvas = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        g2d.dispose();

        repaint();
    }

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
            g2D.drawImage(canvas, xOffset, yOffset, null);
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
     */
    public void updateOffset(int dx, int dy) {
        xOffset += dx/zoomLevel;
        yOffset += dy/zoomLevel;
        repaint();

    }

    public int getXOffset() {
        return xOffset;
    }
    public int getYOffset() {
        return yOffset;
    }
    
    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoom) {
        zoomLevel = zoom;
        repaint(); // Recreate the canvas with the new zoom level
    }

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
        SwingUtilities.invokeLater(() -> new FractalFrame());
    }



    
}
