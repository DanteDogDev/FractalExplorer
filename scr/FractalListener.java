package scr;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class FractalListener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{

    FractalFrame frame;

    public FractalListener(FractalFrame frame) {
        this.frame = frame;
    }

    private int mouseX;
    private int mouseY;
    private Point mousePoint;
    private Point onDrag;

    @Override
    public void mouseDragged(MouseEvent e) {
        if(onDrag == null) {
            onDrag = e.getPoint();
        }
        Point current = e.getPoint();
        int dx = current.x - onDrag.x;
        int dy = current.y - onDrag.y;
        frame.updateOffset(dx, dy);
        onDrag = current;
    }

    /* 
     * updates the mouses position every time its moved 
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mousePoint = new Point(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse Clicked - ("+mouseX+", "+mouseY+")");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onDrag = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        onDrag = null;
    }

    /*
     * Closes program when pressing esc
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            frame.dispose();
            System.exit(0);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    
    /*
     * updates zoom level based on scroll wheel movement
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {
        int notches = arg0.getWheelRotation();
        if(notches < 0){
            frame.updateZoomLevel(0.9);
        } else {
            frame.updateZoomLevel(1.1);
        }
    }
    
}
