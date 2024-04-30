To Slow Down the generation proccess of the fractal to be shown as animated
change a line in FractalMain.java
FractalFrame fractalExplorer = new FractalFrame(100, 2, (false)); making the false parameter true

To increase iteration count in the generation proccess of the fractal
change a line in FractalMain.java
FractalFrame fractalExplorer = new FractalFrame((100), 2, false); making the 100 parameter to whatever number of iterations as you want the bigger the number slower the program

To downscale the image in the generation proccess of the fractal to make it run better on a lower resolution
change a line in FractalMain.java
FractalFrame fractalExplorer = new FractalFrame(100, (2), true); the 2 parameter is the resolution of your screen divided by that number


//controls:
//hold left click to drag fractal around
//mouse wheel to zoom in
//right click to set seed for julia set fractal
//up/down arrow keys to increase/decrease max iteration count
//space to change filter
//esc to close program
