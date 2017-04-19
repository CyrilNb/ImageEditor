package projet_techno_l3.imageeditor.ImageModifications.incrustation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;
import projet_techno_l3.imageeditor.ImageModifications.Greyscale;

/**
 * Created by Antoine Gagnon
 */

public class EyesIncrustation extends AbstractImageModificationAsyncTask {

    private CascadeClassifier eyeCascade;
    private double absoluteEyesSize;

    public EyesIncrustation(Bitmap src, Activity activity) {
        super(activity);

        result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);


    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        this.eyeCascade = new CascadeClassifier();
        this.eyeCascade.load("file:///android_asset/eyes.xml");



        Mat frame = new Mat();
        Utils.bitmapToMat(src, frame);


        MatOfRect eyes = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (5% of the frame height, in our case)
        if (this.absoluteEyesSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.05f) > 0) {
                this.absoluteEyesSize = Math.round(height * 0.05f);
            }
        }

        // detect eyes
        this.eyeCascade.detectMultiScale(grayFrame, eyes, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteEyesSize, this.absoluteEyesSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = eyes.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);


        Utils.matToBitmap(frame,this.result);

        return result;
    }
}