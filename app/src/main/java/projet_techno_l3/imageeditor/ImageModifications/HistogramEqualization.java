package projet_techno_l3.imageeditor.ImageModifications;


import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.concurrent.Callable;

public class HistogramEqualization extends AbstractImageModification {
    private int bitmapWidth;
    private int bitmapHeight;
    private Callable greyScaleCallable;

    public HistogramEqualization(Bitmap src) {
        this.src = src;
        bitmapWidth = src.getWidth();
        bitmapHeight = src.getHeight();
        greyScaleCallable = new Greyscale(this.src);
    }

    @Override
    public Object call() throws Exception {
        Bitmap result = (Bitmap) greyScaleCallable.call();
        Histogram histogram = this.getHistogram(this.src);

        int[] h = new int[256];
        int[] ah = new int[256];
        //remplissage de h...
        ah[0] = h[0];
        for (int i = 1; i < 256; i++) {
            ah[i] = h[i] + ah[i - 1];
        }

        return result;
    }

    /**
     * Get histogram of an image
     *
     * @param bitmap bitmap source
     * @return histogram of the bitmap source
     */
    public Histogram getHistogram(Bitmap bitmap) {
        Histogram histogram;

        int[] values = new int[256];
        int[] pixels = new int[bitmapWidth * bitmapHeight];

        int i, minValueHistogram = 0, maxValueHistogram = 255;

        //first turn bitmap into greyscale bitmap
        try {
            bitmap = (Bitmap) greyScaleCallable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        for (i = 0; i < bitmapWidth * bitmapHeight; i++) {

            int pixel = Color.red(pixels[i]);
            values[pixel]++;
        }

        i = 0;
        while (values[i] == 0) {
            minValueHistogram = i;
            i++;
        }

        for (int j = 255; j > 0; j--) {
            if (values[j] == 0) {
                maxValueHistogram = j;
            } else
                break;
        }

        histogram = new Histogram(values, minValueHistogram, maxValueHistogram);

        return histogram;
    }
}
