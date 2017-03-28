package projet_techno_l3.imageeditor.ImageModifications;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Invert colors of a bitmap by scaling each channel by -1, and then shifting the result up by 255 to remain in the standard color space.
 */
public class NegativeFilter extends AbstractImageModificationAsyncTask{

    public NegativeFilter(Bitmap src, Activity activity) {
        super(activity);
        this.src = src;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                });

        Bitmap result = this.src.copy(Bitmap.Config.ARGB_8888,true); //create a mutable copy of the original bitmap

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(result, 0, 0, paint);

        return result;
    }
}
