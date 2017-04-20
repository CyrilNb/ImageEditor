package projet_techno_l3.imageeditor.ImageModifications;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.nio.IntBuffer;

/**
 * Creates a pencil sketch effect on an image (colored or not) by using the colod dodge blend method
 */
public class SketchFilter extends AbstractImageModificationAsyncTask {

    /**
     * Constructor
     * @param src
     * @param activity
     */
    public SketchFilter(Bitmap src, Activity activity) {
        super(activity);
        this.src = src;
    }

    /**
     * Performs by AsyncTask
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        /*
         *      ALGO pesudo-code:
         *      GS = Convert to Gray Scale the image source
         *      I = Invert image GS
         *      B = Gaussian blur image I
         *      FINAL IMAGE = Color dodge blend merge images B and GS
         */

        Bitmap copy = src.copy(Bitmap.Config.ARGB_8888, true);
        copy = greyscale(copy);
        copy = invertImage(copy);

        Mat mat = new Mat();
        Utils.bitmapToMat(copy,mat); //convertis notre bitmap en Mat qui est le type d'image utilisé par OpenCV
        org.opencv.core.Size s = new Size(5,5);
        Imgproc.GaussianBlur(mat,mat,s,2);
        Utils.matToBitmap(mat,copy);

        Bitmap copy2 = src.copy(Bitmap.Config.ARGB_8888, true);
        copy2 = greyscale(copy2);

        Bitmap result = colorDodgeBlend(copy, copy2);
        return result;
    }


    /**
     * Method to blend two images using color dodge (densité de couleur)
     */
    public Bitmap colorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);


            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);


            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return base;
    }

    /**
     * get the color dodge for pixel to get the effect
     */
    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }

    /**
     * Method to invert an image
     */
    private Bitmap invertImage(Bitmap source){
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                });

        Bitmap result = source.copy(Bitmap.Config.ARGB_8888,true); //create a mutable copy of the original bitmap

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(result, 0, 0, paint);

        return result;
    }

    /**
     * Method to turn a colored image into grey scale
     */
    private Bitmap greyscale(Bitmap src) {
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
