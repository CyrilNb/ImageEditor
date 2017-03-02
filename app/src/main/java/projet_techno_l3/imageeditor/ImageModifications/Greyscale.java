package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Changes an image to its black and white version
 */
public class Greyscale extends AbstractImageModification {

    public Greyscale(Bitmap src) {
        this.src = src;
    }

    @Override
    public Object call() throws Exception {

        int imgHeight = src.getHeight();
        int imgWidth = src.getWidth();

        int A, R, G, B;

        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        for (int i = 0; i < imgHeight * imgWidth; i++) {
            int pixel = pixels[i];
            A = Color.alpha(pixel);
            R = Color.red(pixel);
            G = Color.green(pixel);
            B = Color.blue(pixel);
            // take conversion up to one single value
            R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
            // set new pixel color to output bitmap
            pixels[i] = Color.argb(A, R, G, B);
        }
        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }
}
