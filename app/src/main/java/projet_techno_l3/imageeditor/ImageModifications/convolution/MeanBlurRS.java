package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;
import android.util.Log;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Applies a mean value to the center pixel of a square matrix, taking into account all the other pixels values.
 * Uses RenderScript compared to MeanBlur
 */
public class MeanBlurRS extends AbstractImageModificationAsyncTask {


    // Size of the matrix used for the mean blur (either 3 o 5)
    private final int filterSize;


    // Matrix with values to be applied to each pixel
    private final float[] matrixBlur;


    public MeanBlurRS(Bitmap src, BlurValues filterSize, Activity activity) {
        super(src, activity);

        this.filterSize = filterSize.ordinal() + 3;
        int matrixSize = this.filterSize * this.filterSize;
        matrixBlur = new float[matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            matrixBlur[i] = (1f / matrixSize);
        }
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        long startTime = System.currentTimeMillis();
        result = src.copy(Bitmap.Config.ARGB_8888, true);

        RenderScript renderScript = RenderScript.create(mActivity.getApplicationContext());

        Allocation input = Allocation.createFromBitmap(renderScript, src);
        Allocation output = Allocation.createFromBitmap(renderScript, result);

        if (filterSize == 3) {
            ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3
                    .create(renderScript, Element.U8_4(renderScript));
            convolution.setInput(input);
            convolution.setCoefficients(matrixBlur);
            convolution.forEach(output);
        } else {
            ScriptIntrinsicConvolve5x5 convolution = ScriptIntrinsicConvolve5x5
                    .create(renderScript, Element.U8_4(renderScript));
            convolution.setInput(input);
            convolution.setCoefficients(matrixBlur);
            convolution.forEach(output);
        }

        output.copyTo(result);
        renderScript.destroy();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        Log.i("MeanBlurRS", "MeanBlurRS Duration: " + elapsedTime);

        return result;

    }
}
