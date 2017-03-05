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

        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        for (int i = 0; i < imgHeight * imgWidth; i++) {
            int pixel = pixels[i];
            pixels[i] = greyOutPixel(pixel);
        }
        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }
}
