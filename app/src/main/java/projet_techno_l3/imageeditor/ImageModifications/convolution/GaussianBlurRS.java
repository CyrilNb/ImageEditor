package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Applies a Gaussian Blur using RenderScript to an image.
 * Also called "Filtre Gaussien" in French.
 */
public class GaussianBlurRS extends AbstractImageModificationAsyncTask {


    /**
     * Size of the matrix used
     */
    private final int filterSize;


    public GaussianBlurRS(Bitmap src, BlurValues filterSize, Activity activity) {
        super(src, activity);

        int filterSizeTemp = (filterSize.ordinal() * 5) + 3;
        this.filterSize = (int) ensureRange(filterSizeTemp, 0, 21);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        long startTime = System.currentTimeMillis();

        result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        //Create renderscript
        RenderScript rs = RenderScript.create(mActivity.getApplicationContext());

        //Create allocation from Bitmap
        Allocation allocationIn = Allocation.createFromBitmap(rs, src);
        Allocation allocationOut = Allocation.createFromBitmap(rs, result);

        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Set blur radius
        blurScript.setRadius(filterSize);

        //Set input for script
        blurScript.setInput(allocationIn);
        //Call script for output allocation
        blurScript.forEach(allocationOut);

        //Copy script result into bitmap
        allocationOut.copyTo(result);

        //Destroy everything to free memory
        allocationIn.destroy();
        allocationOut.destroy();
        blurScript.destroy();
        rs.destroy();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        Log.i("MeanBlurRS", "MeanBlurRS Duration: " + elapsedTime);

        return result;

    }
}
