package projet_techno_l3.imageeditor.ImageModifications;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Used to edit the brightness of an image using the Value in HSV colors
 */
public class BrightnessEditor extends AbstractImageModificationAsyncTask {

    private float value;

    /**
     * @param src   Source image to be modified
     * @param value Brightness adjusment value, between -100 and 100
     */
    public BrightnessEditor(Bitmap src, float value, Activity activity) {
        super(src, activity);

        this.value = value;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        publishProgress("Editing..."); // Calls onProgressUpdate()
        if (value == 0 || value > 100 || value < -100) {
            return src;
        }
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int imgHeight = result.getHeight();
        int imgWidth = result.getWidth();

        // Getting each pixel in the Bitmap
        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);


        // Increasing the V of HSV to increase brightness
        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = new float[3];
            Color.colorToHSV(pixels[i], hsv);
            hsv[2] = hsv[2] + (value / 100);

            // No need to ensure range, HSVToColor does it.
            pixels[i] = Color.HSVToColor(hsv);
        }

        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }

}
