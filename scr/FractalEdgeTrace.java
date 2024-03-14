package scr;

public class FractalEdgeTrace {

    private FractalMath math;
    private int[][] data;

    public FractalEdgeTrace(FractalMath math, int[][] data) {
        this.math = math;
        this.data = data;
    }

    public void calculateEdgeFractal(){
        int sectorWidth = (math.width/8);
        int sectorHeight = (math.height/8);
        for(int i = 0; i < 64;i++){
            try {
                renderRectangle((i/8*(sectorWidth)),(i%8*(sectorHeight)),sectorWidth,sectorHeight);
            } catch (Exception e) {
                System.out.println((i/8*(sectorWidth)+sectorWidth)+", "+(i%8*(sectorHeight)+sectorHeight));
                // TODO: handle exception
            }
            
        }
    }
    
    public void renderRectangle(int startX, int startY, int sectorWidth, int sectorHeight){
        boolean lineDetected = false;
        float minReal = math.centerReal - 2.5f / math.zoom;
        float maxReal = math.centerReal + 2.5f / math.zoom;
        float minImag = math.centerImag - 2.0f / math.zoom;
        float maxImag = math.centerImag + 2.0f / math.zoom;

        float real = minReal + startX * (maxReal - minReal) / math.width;
        float imag = minImag + startY * (maxImag - minImag) / math.height;
        int control = math.mandelbrotSet(real, imag);

        int x = 0;
        int y = 0;
        //top edge
        y = startY;
        for(x = startX;x < startX+sectorWidth;x++){
            real = minReal + x * (maxReal - minReal) / math.width;
            imag = minImag + y * (maxImag - minImag) / math.height;
            data[x][y] = math.mandelbrotSet(real, imag);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //bottom edge
        y = startY+sectorHeight-1;
        for(x = startX;x < startX+sectorWidth;x++){
            real = minReal + x * (maxReal - minReal) / math.width;
            imag = minImag + y * (maxImag - minImag) / math.height;
            data[x][y] = math.mandelbrotSet(real, imag);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //left edge
        x = startX;
        for(y = startY;y < startY+sectorHeight;y++){
            real = minReal + x * (maxReal - minReal) / math.width;
            imag = minImag + y * (maxImag - minImag) / math.height;
            data[x][y] = math.mandelbrotSet(real, imag);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        //right edge
        x = startX+sectorWidth-1;
        for(y = startY;y < startY+sectorHeight;y++){
            real = minReal + x * (maxReal - minReal) / math.width;
            imag = minImag + y * (maxImag - minImag) / math.height;
            data[x][y] = math.mandelbrotSet(real, imag);
            if(data[x][y] != control){
                lineDetected = true;
            }
        }
        if(lineDetected){
            if(sectorHeight > 2){
                renderRectangle(startX,                 startY,                 sectorWidth/2,sectorHeight/2);
                renderRectangle(startX+sectorWidth/2,   startY,                 sectorWidth/2,sectorHeight/2);
                renderRectangle(startX,                 startY+sectorHeight/2,  sectorWidth/2,sectorHeight/2);
                renderRectangle(startX+sectorWidth/2,   startY+sectorHeight/2,  sectorWidth/2,sectorHeight/2);
            } else {
                fullRenderRectangle(startX,startY,sectorWidth,sectorHeight);
            }
        } else {
            //fullRenderRectangle(startX,startY,sectorWidth,sectorHeight,control);
        }
        
    }
 
    public void fullRenderRectangle(int startX, int startY, int sectorWidth, int sectorHeight){
        float minReal = math.centerReal - 2.5f / math.zoom;
        float maxReal = math.centerReal + 2.5f / math.zoom;
        float minImag = math.centerImag - 2.0f / math.zoom;
        float maxImag = math.centerImag + 2.0f / math.zoom;
        for(int x = startX; x < startX + sectorWidth; x++){
            for(int y = startY; y < startY + sectorHeight; y++){ // Fixed loop condition
                float real = minReal + x * (maxReal - minReal) / math.width;
                float imag = minImag + y * (maxImag - minImag) / math.height;
                data[x][y] = math.mandelbrotSet(real, imag);
            }
        }
    }
    public void fullRenderRectangle(int startX, int startY, int sectorWidth, int sectorHeight, int control){
        for(int x = startX; x < startX + sectorWidth; x++){
            for(int y = startY; y < startY + sectorHeight; y++){ // Fixed loop condition
                data[x][y] = control;
            }
        }
    }
    


    private int computeEdgeStrength(int x, int y) {
        int[][] gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    
        int sumX = 0;
        int sumY = 0;
    
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int pixelX = x + i;
                int pixelY = y + j;
    
                if (pixelX >= 0 && pixelX < math.width &&
                    pixelY >= 0 && pixelY < math.height) {
                    sumX += data[pixelX][pixelY] * gx[j + 1][i + 1];
                    sumY += data[pixelX][pixelY] * gy[j + 1][i + 1];
                }
            }
        }
    
        return (int) Math.sqrt(sumX * sumX + sumY * sumY);
    }
    
    public void applyEdgeDetection() {
        for (int y = 0; y < math.height; y++) {
            for (int x = 0; x < math.width; x++) {
                int edgeStrength = computeEdgeStrength(x, y);

                int edge = edgeStrength != 0 ? 255 : 0;

                math.setColor(x, y, edge);
            }
        }
    }


}
