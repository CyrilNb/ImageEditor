package projet_techno_l3.imageeditor.ImageModifications;


import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.concurrent.Callable;

public class HistogramEqualization extends AbstractImageModification {
    private int bitmapWidth;
    private int bitmapHeight;
    private float totalSize;

    private int valueHistogram[];
    private int cdfMin = -1;

    private float hsvPixels[][];

    public HistogramEqualization(Bitmap src) {
        this.src = src;
        bitmapWidth = src.getWidth();
        bitmapHeight = src.getHeight();
        valueHistogram = new int[256];
        totalSize = bitmapHeight * bitmapWidth;
        hsvPixels = new float[bitmapWidth * bitmapHeight][3];
    }

    @Override
    public Object call() throws Exception {
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888,true);

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        result.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        for (int i = 0; i < bitmapWidth * bitmapHeight; i++) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            Color.RGBToHSV(r, g, b, hsvPixels[i]);

            int rangedHSVvalue = valueToRange(hsvPixels[i][2]);
            valueHistogram[rangedHSVvalue]++;
        }

        for (int i = 0; i < bitmapWidth * bitmapHeight; i++) {
            float value = hsvPixels[i][2];
            int range = valueToRange(value);
            int histoEqualValue = histoEqual(range);
            float changedValue = rangeToValue(histoEqualValue);
            hsvPixels[i][2] = changedValue;

            float[] hsv = hsvPixels[i];
            pixels[i] = Color.HSVToColor(hsv);
        }

        result.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        return result;
    }

    private void generateCdfMin() {
        for (int aValueHistogram : valueHistogram) {
            if (aValueHistogram != 0) {
                cdfMin = aValueHistogram;
                return;
            }
        }
    }

    private int histoEqual(int v) {
        generateCDF();
        int cdfValue = cdf[v];
        return Math.round(((cdfValue - cdfMin) / totalSize) * 255);
    }

    private int cdf[];

    /**
     * Cumulative distribution function
     *
     * @return Total number of pixels with value under v
     */
    private void generateCDF() {
        if (cdf == null) {
            cdf = new int[256];
            int total = 0;
            for (int i = 0; i < valueHistogram.length; i++) {
                total += valueHistogram[i];
                cdf[i] = total;
            }
            generateCdfMin();
        }

    }

    private float rangeToValue(int range) {
        return range / 255f;
    }

    /**
     * Transform a HSV value to a value between 0 and 255
     *
     * @param value The HSV value
     * @return
     */
    private int valueToRange(float value) {
        return (int) Math.floor(value * 255);
    }

}
