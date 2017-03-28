package projet_techno_l3.imageeditor.ImageModifications;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Changes the overall contrast of an image using an adjustment value
 */
public class ContrastEditor extends AbstractImageModificationAsyncTask {

    private float value;

    /**
     * @param src   Source image to be modified
     * @param value Contract adjusment value, between -255 and 255
     */
    public ContrastEditor(Bitmap src, float value, Activity activity) {
        super(activity);
        this.src = src;
        this.value = value;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
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

            int newRed = (int) ensureRange(factor * (red - 128) + 128,0,255);
            int newGreen = (int) ensureRange(factor * (green - 128) + 128,0,255);
            int newBlue = (int) ensureRange(factor * (blue - 128) + 128,0,255);
            pixels[i] = Color.rgb(newRed, newGreen, newBlue);
        }

        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }
}
