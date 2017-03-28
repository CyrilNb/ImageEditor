package projet_techno_l3.imageeditor.ImageModifications;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Greys out all the part of an image except for the one with a specific color
 */
public class ColorFilter extends AbstractImageModificationAsyncTask {

    // Accepted color range
    private int COLOR_RANGE = 25;

    private final int color;

    public ColorFilter(Bitmap src, int color, Activity activity) {
        super(activity);
        this.src = src;
        this.color = color;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int imgHeight = result.getHeight();
        int imgWidth = result.getWidth();

        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float hue = hsv[0];
        for (int i = 0; i < imgHeight * imgWidth; i++) {

            int pixel = pixels[i];
            Color.colorToHSV(pixel, hsv);

            if (!(hsv[0] - COLOR_RANGE < hue && hue < hsv[0] + COLOR_RANGE && hsv[1] > 0.10)) {
                pixels[i] = greyOutPixel(pixel);
            }

        }
        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        return result;
    }
}
