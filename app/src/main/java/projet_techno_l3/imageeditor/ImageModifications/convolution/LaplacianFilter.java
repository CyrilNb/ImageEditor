package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.concurrent.Callable;
import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModification;
import projet_techno_l3.imageeditor.ImageModifications.Greyscale;

/**
 * Created by Cyril
 */

//TO DO
public class LaplacianFilter extends AbstractImageModification {
    private Callable greyScaleCallable;
    private int[][] convolutionMatrix = {
            {1,1,1},
            {1,-8,1},
            {1,1,1}
    };

    public LaplacianFilter(Bitmap src) {
        this.src = src;
        greyScaleCallable = new Greyscale(this.src);
    }

    @Override
    public Object call() throws Exception {
        int r,g,b;
        int rtotal, gtotal, btotal; rtotal = gtotal = btotal = 0;

        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);
        //result = (Bitmap) greyScaleCallable.call();

        for(int i = 1; i<result.getWidth()-1; i++) {
            for (int j = 1; j < result.getHeight() - 1; j++) {
                for (int y = -1; y <= 1; y++)
                {
                    for (int x = -1; x <= 1; x++)
                    {
                        // get each channel pixel value
                        int pixel = result.getPixel(i+x,j+y);
                        //todo get pixels

                        r = Color.red(pixel);
                        g = Color.green(pixel);
                        b = Color.blue(pixel);

                        // calculate each channel surrouding neighbour pixel value base
                        rtotal += r * convolutionMatrix[y + 1][x + 1];
                        gtotal += g * convolutionMatrix[y + 1][x + 1];
                        btotal += b * convolutionMatrix[y + 1][x + 1];
                    }

                }

                // range checking
                if(rtotal <= 0) rtotal = 255;
                else{
                    rtotal = 0;
                }

                if (rtotal > 255) rtotal = 255;

                else if (rtotal < 0) rtotal = 0;

                if (btotal > 255) btotal = 255;

                else if (btotal < 0) btotal = 0;

                if (gtotal > 255) gtotal = 255;

                else if (gtotal < 0) gtotal = 0;

                // commit new pixel value
                result.setPixel(i,j,Color.rgb(rtotal,btotal,gtotal));
            }
        }
        return result;
    }

}
