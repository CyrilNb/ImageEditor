package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.concurrent.Callable;

/**
 * Abstract modification class based on a threaded element
 */
public abstract class AbstractImageModification implements Callable {

    /**
     * Source image
     */
    protected Bitmap src;

    /**
     * Truncate a floating value with inclusive bounds.
     * @param value
     * @param min Minimum value
     * @param max Maximum value
     * @return Truncated value between min and max
     */
    float ensureRange(float value, float min, float max) {
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

    /**
     * Gets a part of a 2D table
     *
     * @param source The original 2D table
     * @param x      The top left corner point x value
     * @param y      The top left corner point y value
     * @param width  The width of the resulting table
     * @param height The height of the resulting table
     * @return Resulting table
     */
    protected int[][] getSubTable(int[][] source, int x, int y, int width, int height) {

        if (source == null) {
            return null;
        }
        if (source.length == 0) {
            return new int[0][0];
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        if (width < 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if ((y + height) > source.length) {
            return null;
        }
        if((x+width) > source[0].length){
            return null;
        }
        int[][] dest = new int[height][width];

        for (int destY = 0; destY < height; destY++) {
            System.arraycopy(source[y + destY], x, dest[destY], 0, width);
        }
        return dest;
    }
}
