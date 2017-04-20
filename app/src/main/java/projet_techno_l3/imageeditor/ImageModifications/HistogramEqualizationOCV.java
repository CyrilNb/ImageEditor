package projet_techno_l3.imageeditor.ImageModifications;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.split;

/**
 * Equalizes the histogram of an image using openCV
 */
public class HistogramEqualizationOCV extends AbstractImageModificationAsyncTask {


    public HistogramEqualizationOCV(Bitmap src, Activity activity) {
        super(src, activity);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        long startTime = System.currentTimeMillis();

        Mat oldImg = new Mat();
        result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        Utils.bitmapToMat(src, oldImg);

        // We use YCrCb to separate intensity from colors
        Imgproc.cvtColor(oldImg, oldImg, Imgproc.COLOR_BGR2YCrCb);

        List<Mat> channels = new ArrayList<>();
        split(oldImg, channels);

        // We only equalize the intensity (grey level) channel
        Imgproc.equalizeHist(channels.get(0), channels.get(0));

        Mat newImg = new Mat();
        merge(channels, oldImg);

        Imgproc.cvtColor(oldImg, newImg, Imgproc.COLOR_YCrCb2BGR);


        Utils.matToBitmap(newImg, result);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        Log.i("HistEqualOCV", "HistEqualOCV Duration: " + elapsedTime);

        return result;
    }
}
