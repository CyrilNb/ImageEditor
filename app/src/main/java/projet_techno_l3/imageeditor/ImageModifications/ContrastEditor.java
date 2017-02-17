package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by gagno on 2/10/2017.
 */

public class ContrastEditor extends AbstractImageModification {

    private float value;

    /**
     *
     * @param src Source image to be modified
     * @param value Contract adjusment value, between -100 and 100
     */
    public ContrastEditor(Bitmap src, float value) {
        this.src = src;
        this.value = value;
    }

    @Override
    public Bitmap call() throws Exception {

        if (value == 0) {
            return src;
        }

        float rgbValue = (value/100)*255;
        Bitmap result;
        int height = src.getHeight();
        int width = src.getWidth();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
                1/255, 0, 0, 0, 0, //red
                0, 1/255, 0, 0, 0, //green
                0, 0, 1/255, 0, 0, //blue
                0, 0, 0, 1, 0 //alpha
        });

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        result = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, paint);

        return result;

    }

}
