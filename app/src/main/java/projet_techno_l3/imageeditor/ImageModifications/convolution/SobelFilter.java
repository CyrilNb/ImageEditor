package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Applies a Sobel filter on an image
 */
public class SobelFilter extends AbstractImageModificationAsyncTask {

    /**
     * Private variables
     */
    private int[][] convolutionMatrix = new int[3][3];
    //private Callable greyScaleCallable;


    /**
     * Constructor
     * @param src
     */
    public SobelFilter(Bitmap src, Activity activity) {
        super(src, activity);

        //greyScaleCallable = new Greyscale(this.src);
    }

    /**
     * Performs the operation on a specific task (thread)
     * @return result bitmap
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        publishProgress("Editing..."); // Calls onProgressUpdate()
        try {

            Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);
            int imgHeight = result.getHeight();
            int imgWidth = result.getWidth();

            for (int x = 1; x < imgWidth-1; x++) {
                for (int y = 1; y < imgHeight-1; y++) {
                    convolutionMatrix[0][0]=Color.green(src.getPixel(x-1,y-1));
                    convolutionMatrix[0][1]=Color.green(src.getPixel(x-1,y));
                    convolutionMatrix[0][2]=Color.green(src.getPixel(x-1,y+1));
                    convolutionMatrix[1][0]=Color.green(src.getPixel(x,y-1));
                    convolutionMatrix[1][2]=Color.green(src.getPixel(x,y+1));
                    convolutionMatrix[2][0]=Color.green(src.getPixel(x+1,y-1));
                    convolutionMatrix[2][1]=Color.green(src.getPixel(x+1,y));
                    convolutionMatrix[2][2]=Color.green(src.getPixel(x+1,y+1));

                    int edge = (int) convolutionSobel(convolutionMatrix);
                    result.setPixel(x,y,(edge<<16 | edge<<8 | edge));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }


    private double convolutionSobel(int[][] pixelMatrix){
        int gy=(pixelMatrix[0][0]*-1)+(pixelMatrix[0][1]*-2)+(pixelMatrix[0][2]*-1)+(pixelMatrix[2][0])+(pixelMatrix[2][1]*2)+(pixelMatrix[2][2]*1);
        int gx=(pixelMatrix[0][0])+(pixelMatrix[0][2]*-1)+(pixelMatrix[1][0]*2)+(pixelMatrix[1][2]*-2)+(pixelMatrix[2][0])+(pixelMatrix[2][2]*-1);
        return Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));
    }



}
