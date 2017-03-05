package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.concurrent.Callable;

/**
 * Abstract
 */
public abstract class AbstractImageModification implements Callable {

    protected Bitmap src;

    /**
     * Truncate a floating value with inclusive bounds.
     * @param value
     * @param min Minimum value
     * @param max Maximum value
     * @return Truncated value between min and max
     */
    protected float ensureRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Turn a colored pixel into its grey value
     * @param pixel Original pixel
     * @return Grey pixel
     */
    int greyOutPixel(int pixel){
        int greyValue = (int) (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel));
        return Color.rgb(greyValue, greyValue, greyValue);
    }
}
