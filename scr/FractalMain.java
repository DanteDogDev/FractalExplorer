package scr;

public class FractalMain {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        //maxIterations: ### is the number of maximum iterations that the program will go through each pixel with
        //scale: #.# is how much to scale down the image quality in order to increase preformance
        //animate: true/false 
        //if true it disables all functionality aside from escaping the program and slows
        //                  down the process of generating the image in order to watch it
        FractalFrame fractalExplorer = new FractalFrame(300, 1, false);
        //fractalExplorer.calculateAverageTime(100);




        //it crashes on exit sometimes
    }
}
