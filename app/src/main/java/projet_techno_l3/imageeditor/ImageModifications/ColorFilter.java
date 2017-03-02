package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Antoine on 03/03/2017.
 */

public class ColorFilter extends AbstractImageModification {


    private final int color;

    public ColorFilter(Bitmap src, int color) {
        this.src = src;
        this.color = color;
    }

    @Override
    public Object call() throws Exception {

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

            if (!(hsv[0] - 10.0 < hue && hue < hsv[0] + 10.0 && hsv[1] > 0.01)) {
                int greyValue = (int) (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel));
                pixels[i] = Color.rgb(greyValue, greyValue, greyValue);
            }

        }
        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        return result;
    }
}
