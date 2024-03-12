package scr;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javafx.scene.paint.Color;

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

    @Override
    public void mouseMoved(MouseEvent e) {
        double zoom = frame.getZoomLevel();
        mouseX = (int)((e.getX() / zoom)-frame.getXOffset());
        mouseY = (int)((e.getY() / zoom)-frame.getYOffset());
        mousePoint = new Point(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        frame.drawDot(mousePoint);

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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // Close the program
            frame.dispose(); // Close the window
            System.exit(0); // Exit the application
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {
        int notches = arg0.getWheelRotation();
        if(notches < 0){
            frame.setZoomLevel(frame.getZoomLevel()+0.1);
        } else {
            frame.setZoomLevel(Math.max(0.1, frame.getZoomLevel()-0.1));
        }
        System.out.println(frame.getZoomLevel());
    }
    
}
