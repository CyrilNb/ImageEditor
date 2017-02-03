package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import static android.R.attr.width;

/**
 * Created by gagno on 2/3/2017.
 */

public class BrightnessEditor extends AbstractImageModification {

    private int value;

    /**
     *
     * @param src Source image to be modified
     * @param value Brightness adjusment value, between -100 and 100
     */
    public BrightnessEditor(Bitmap src, int value) {
        this.src = src;
        this.value = value;
    }

    @Override
    public Bitmap call() throws Exception {
        float rgbValue = (value/100)*255;
        Bitmap result;
        int height = src.getHeight();
        int width = src.getWidth();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
                1, 0, 0, 0, rgbValue, //red
                0, 1, 0, 0, rgbValue, //green
                0, 0, 1, 0, rgbValue, //blue
                0, 0, 0, 1, 0 //alpha
        });

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        result = Bitmap.createBitmap(src, 0, 0, width, height);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(result, 0, 0, paint);

        return result;

    }

}
