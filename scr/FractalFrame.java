package scr;
//Graphics Library
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
//keyboard/mouse event library
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//Windows frame library
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class FractalFrame extends JFrame implements KeyListener, MouseListener {

    private BufferedImage canvas;
    private int width;
    private int height;
    
    public FractalFrame() {
        super("Fractal Explorer");
        setSize(width, height);
        //sets icon of the window
        Image icon = new javax.swing.ImageIcon("assets/icon.png").getImage();
        setIconImage(icon);
        //when closing window close the program
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //Add MouseListener
        addMouseListener(this);
        //creates window
        setupFrame();
        //sets up the canvas
        setupCanvas();
        setVisible(true);
    }

    public void setupCanvas() {
        //gets size of screen
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolKit.getScreenSize();
        width = (int)screenSize.getWidth();
        height = (int)screenSize.getHeight();
        //sets up the canvas
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        repaint();
    }

    private void setupFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.black);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        //draw the image
        g.drawImage(canvas, 0, 0,null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        System.out.println("Mouse Clicked - Pixel Coordinates: (" + x + ", " + y + ")");

        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(x-5, y-5, 11, 11); // Draw a red rectangle
        g2d.dispose();
        repaint();
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mousePressed'");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }

    public static void main(String[] args) {
        
        FractalFrame frame = new FractalFrame();
        
    }


    
}
