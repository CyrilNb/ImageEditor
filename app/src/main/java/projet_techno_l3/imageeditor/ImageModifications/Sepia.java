package projet_techno_l3.imageeditor.ImageModifications;


import android.graphics.Bitmap;
import android.graphics.Color;

public class Sepia extends AbstractImageModification {

    private int SEPIA_DEPTH = 20;

    public Sepia(Bitmap src) {
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
            int r = Color.red(pixel);
            int g = Color.green(pixel);
            int b = Color.blue(pixel);

            int gry = (r + g + b) / 3;
            r = g = b = gry;
            r = r + (SEPIA_DEPTH * 2);
            g = g + SEPIA_DEPTH;
            b -= 80;

            r = (int) ensureRange(r,0,255);
            g = (int) ensureRange(g,0,255);
            b = (int) ensureRange(b,0,255);

            pixels[i] = Color.rgb(r, g, b);

        }
        result.setPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }
}
