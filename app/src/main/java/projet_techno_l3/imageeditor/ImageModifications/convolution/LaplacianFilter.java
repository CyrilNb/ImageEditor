package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Applies a Laplacian filter on the image
 */
public class LaplacianFilter extends AbstractImageModificationAsyncTask {
    /**
     * Constructor
     * @param src bitmap to be modified
     */
    public LaplacianFilter(Bitmap src, Activity activity) {
        super(src, activity);
    }

    /**
     * Performs the operation on a specific thread by using the Laplacian method provided by openCV
     * @return result bitmap
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);
        Mat mat = new Mat();
        //Turn Bitmap into Mat which is the Object type used by OpenCV
        Utils.bitmapToMat(result,mat);
        //Perform the laplacian filter using openCV method with a matrix of size 3.
        Imgproc.Laplacian(mat, mat, CvType.CV_8U, 3, 1, 0);
        //Turn Mat into Bitmap object to get the result as a Bitmap
        Utils.matToBitmap(mat,result);
        return result;
    }
}
