package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Changes the overall contrast of an image using an adjustment value
 */
public class ContrastEditor extends AbstractImageModification {

    private float value;

    /**
     * @param src   Source image to be modified
     * @param value Contract adjusment value, between -255 and 255
     */
    public ContrastEditor(Bitmap src, float value) {
        this.src = src;
        this.value = value;
    }

    @Override
    public Bitmap call() throws Exception {

        if (value > 255 || value < -255) {
            return src;
        }

        float factor = (259 * (value + 255)) / (255 * (259 - value));

        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int imgHeight = result.getHeight();
        int imgWidth = result.getWidth();

        // Getting each pixel in the Bitmap
        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];

            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            int newRed = pixelTruncate(factor * (red - 128) + 128);
            int newGreen = pixelTruncate(factor * (green - 128) + 128);
            int newBlue = pixelTruncate(factor * (blue - 128) + 128);
            pixels[i] = Color.rgb(newRed, newGreen, newBlue);
        }

        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;

    }

    /**
     * Truncate a pixel value between 0 and 255
     *
     * @param v original color value
     * @return truncated value between 0 and 255
     */
    private int pixelTruncate(float v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return (int) v;
    }

}
