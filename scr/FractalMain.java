package scr;

public class FractalMain {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        //maxIterations: ### is the number of maximum iterations that the program will go through each pixel with
        //scale: #.# is how much to scale down the image quality in order to increase preformance 
        //animate: true/false 
        //if true it disables all functionality aside from escaping the program and slows
        //                  down the process of generating the image in order to watch it
        FractalFrame fractalExplorer = new FractalFrame(100, 2, false);
        //fractalExplorer.calculateAverageTime(100);

        //controls
        //hold left click to drag fractal around
        //mouse wheel to zoom in
        //right click to set seed for julia set fractal
        //up/down arrow keys to increase/decrease max iteration count
        //space to change filter
        //esc to close program


        //it crashes on exit sometimes
    }
}