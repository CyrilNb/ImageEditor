package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.concurrent.Callable;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModification;
import projet_techno_l3.imageeditor.ImageModifications.Greyscale;

/**
 * Created by Cyril
 */

public class SobelFilter extends AbstractImageModification {

    private int[][] convolutionMatrix = new int[3][3];
    private Callable greyScaleCallable;

    public SobelFilter(Bitmap src) {
        this.src = src;
        greyScaleCallable = new Greyscale(this.src);
    }

    @Override
    public Object call() throws Exception {

        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);
        result = (Bitmap) greyScaleCallable.call();
        int imgHeight = result.getHeight();
        int imgWidth = result.getWidth();

        for (int x = 1; x < imgWidth - 1; x++) {
            for (int y = 1; y < imgHeight - 1; y++) {
                convolutionMatrix[0][0] = Color.red(src.getPixel(x - 1, y - 1));
                convolutionMatrix[0][1] = Color.red(src.getPixel(x - 1, y));
                convolutionMatrix[0][2] = Color.red(src.getPixel(x - 1, y + 1));
                convolutionMatrix[1][0] = Color.red(src.getPixel(x, y - 1));
                convolutionMatrix[1][2] = Color.red(src.getPixel(x, y + 1));
                convolutionMatrix[2][0] = Color.red(src.getPixel(x + 1, y - 1));
                convolutionMatrix[2][1] = Color.red(src.getPixel(x + 1, y));
                convolutionMatrix[2][2] = Color.red(src.getPixel(x + 1, y + 1));

                int edge = (int) convolution(convolutionMatrix);
                result.setPixel(x, y, (edge << 16 | edge << 8 | edge));
            }
        }

        return result;
    }

    private double convolution(int[][] pixelMatrix) {
        int gy = (pixelMatrix[0][0] * -1) + (pixelMatrix[0][1] * -2) + (pixelMatrix[0][2] * -1) + (pixelMatrix[2][0]) + (pixelMatrix[2][1] * 2) + (pixelMatrix[2][2]);
        int gx = (pixelMatrix[0][0]) + (pixelMatrix[0][2] * -1) + (pixelMatrix[1][0] * 2) + (pixelMatrix[1][2] * -2) + (pixelMatrix[2][0]) + (pixelMatrix[2][2] * -1);
        return Math.sqrt(Math.pow(gy, 2) + Math.pow(gx, 2));
    }

}
