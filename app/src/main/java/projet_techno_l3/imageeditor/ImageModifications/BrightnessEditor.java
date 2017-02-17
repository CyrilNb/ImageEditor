package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by gagno on 2/3/2017.
 */

public class BrightnessEditor extends AbstractImageModification {

    private float value;

    /**
     *
     * @param src Source image to be modified
     * @param value Brightness adjusment value, between -100 and 100
     */
    public BrightnessEditor(Bitmap src, float value) {
        this.src = src;
        this.value = value;
    }

    @Override
    public Bitmap call() throws Exception {

        if (value == 0) {
            return src;
        }
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int imgHeight = result.getHeight();
        int imgWidth = result.getWidth();

        // Getting each pixel in the Bitmap
        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);


        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = new float[3];
            Color.colorToHSV(pixels[i],hsv);
            hsv[2] = hsv[2] * value/100;
            pixels[i] = Color.HSVToColor(hsv);
        }

        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;

    }

}
