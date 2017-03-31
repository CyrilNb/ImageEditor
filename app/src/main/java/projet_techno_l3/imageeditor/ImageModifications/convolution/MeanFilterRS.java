package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;
import android.util.Log;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModification;
import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Created by Antoine Gagnon
 */

public class MeanFilterRS extends AbstractImageModificationAsyncTask {


    private final int filterSize;

    private final float[] matrixBlur;

    private Context activitiyContext;


    public MeanFilterRS(Bitmap src, BlurValues filterSize, Activity activity) {
        super(activity);
        this.src = src;
        this.filterSize = filterSize.ordinal() + 3;
        int matrixSize = this.filterSize*this.filterSize;
        matrixBlur = new float[matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            matrixBlur[i] = (1f/matrixSize);
        }
        this.activitiyContext = activity.getApplicationContext();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        result = src.copy(Bitmap.Config.ARGB_8888, true);

        RenderScript renderScript = RenderScript.create(activitiyContext);

        Allocation input = Allocation.createFromBitmap(renderScript, src);
        Allocation output = Allocation.createFromBitmap(renderScript, result);

        if(filterSize == 3){
            ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3
                    .create(renderScript, Element.U8_4(renderScript));
            convolution.setInput(input);
            convolution.setCoefficients(matrixBlur);
            convolution.forEach(output);
        }else{
            ScriptIntrinsicConvolve5x5 convolution = ScriptIntrinsicConvolve5x5
                    .create(renderScript, Element.U8_4(renderScript));
            convolution.setInput(input);
            convolution.setCoefficients(matrixBlur);
            convolution.forEach(output);
        }

        output.copyTo(result);
        renderScript.destroy();
        return result;

    }
}
