package projet_techno_l3.imageeditor.ImageModifications;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;


/**
 * Equalizes the histogram of an image
 */
public class HistogramEqualization extends AbstractImageModificationAsyncTask {

    /**
     * Private variables
     */
    private int bitmapWidth;
    private int bitmapHeight;
    private float totalSize;
    private int valueHistogram[];
    private int cdfMin = -1;
    private float hsvPixels[][];
    private int cdf[];

    /**
     * Constructor
     * @param src
     */
    public HistogramEqualization(Bitmap src, Activity activity) {
        super(src, activity);
        bitmapWidth = src.getWidth();
        bitmapHeight = src.getHeight();
        valueHistogram = new int[256];
        totalSize = bitmapHeight * bitmapWidth;
        hsvPixels = new float[bitmapWidth * bitmapHeight][3];
    }

    /**
     * Performs the operation on a specific thread
     * @return result bitmap
     */
    @Override
    protected Bitmap doInBackground(String... params) {

        long startTime = System.currentTimeMillis();

        Bitmap result = src.copy(Bitmap.Config.ARGB_8888,true);

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        result.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        for (int i = 0; i < bitmapWidth * bitmapHeight; i++) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            // Store all HSV value to save on computing power
            Color.RGBToHSV(r, g, b, hsvPixels[i]);

            // Save HSV Value in a 0-255 range
            int rangedHSVvalue = hueValueToColorRange(hsvPixels[i][2]);
            valueHistogram[rangedHSVvalue]++;
        }

        generateCDF();

        for (int i = 0; i < bitmapWidth * bitmapHeight; i++) {
            float value = hsvPixels[i][2];
            int range = hueValueToColorRange(value);
            int histoEqualValue = histoEqual(range);
            float changedValue = colorRangeToHueValue(histoEqualValue);
            hsvPixels[i][2] = changedValue;

            float[] hsv = hsvPixels[i];
            pixels[i] = Color.HSVToColor(hsv);
        }

        result.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        Log.i("HistEqual", "HistEqual Duration: " + elapsedTime);
        return result;
    }

    /**
     * Histogram equalization formula, calculating the new value.
     * @param v Original value
     * @return New value
     */
    private int histoEqual(int v) {
        int cdfValue = cdf[v];
        return Math.round(((cdfValue - cdfMin) / totalSize) * 255);
    }

    /**
     * Using a cumulative distribution function, generates a table and saves the min non-zero value
     * of this table
     */
    private void generateCDF() {
        boolean foundMin = false;
        if (cdf == null) {
            cdf = new int[256];
            int total = 0;
            for (int i = 0; i < valueHistogram.length; i++) {
                if(!foundMin && valueHistogram[i] != 0) {
                    cdfMin = valueHistogram[i];
                    foundMin = true;
                }
                total += valueHistogram[i];
                cdf[i] = total;
            }
        }

    }

    /**
     * Transform a value between 0 and 255 to the Value of a HSV color between 0 and 1
     * @param range Value between 0 and 255
     * @return Value of HSV color between 0 and 1
     */
    private float colorRangeToHueValue(int range) {
        return range / 255f;
    }

    /**
     * Transform a HSV value to a value between 0 and 255
     *
     * @param value The HSV value
     * @return Range value between 0 and 255
     */
    private int hueValueToColorRange(float value) {
        return (int) Math.floor(value * 255);
    }


}
