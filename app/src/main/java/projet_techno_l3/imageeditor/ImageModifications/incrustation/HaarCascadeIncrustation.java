package projet_techno_l3.imageeditor.ImageModifications.incrustation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Abstract class used to ease the creation of different incrustations.
 */
abstract class HaarCascadeIncrustation extends AbstractImageModificationAsyncTask {

    static String TAG = "Incrustation";
    private final int incrustationID;
    String fileName = "face.xml";
    double ratioMinElementSize = 0.10f;
    private double absoluteElementSize;


    HaarCascadeIncrustation(Bitmap src, Activity mActivity, int incrustationID) {
        super(src, mActivity);
        this.incrustationID = incrustationID;
        result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        File mCascadeFile;
        final InputStream is;
        FileOutputStream os;
        try {
            is = mActivity.getResources().getAssets().open(fileName);
            File cascadeDir = mActivity.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, fileName);

            os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();
        } catch (IOException e) {
            Log.i(TAG, fileName + " cascade not found");
            return src;
        }

        CascadeClassifier elementCascade = new CascadeClassifier();
        elementCascade.load(mCascadeFile.getAbsolutePath());


        Mat frame = new Mat();
        Utils.bitmapToMat(src, frame);


        MatOfRect elements = new MatOfRect();
        Mat grayFrame = new Mat();

        // Convert the frame to gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // Set minimum element size according to height and size
        if (this.absoluteElementSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * ratioMinElementSize) > 0) {
                this.absoluteElementSize = Math.round(height * ratioMinElementSize);
            }
        }

        // Detect elements
        elementCascade.detectMultiScale(grayFrame, elements, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteElementSize, this.absoluteElementSize), new Size());

        // Drawing rectangles over elements
        Rect[] elementsArray = elements.toArray();
        Mat incrustationElement;
        try {
            incrustationElement = Utils.loadResource(mActivity.getApplicationContext(), incrustationID);
        } catch (IOException e) {
            Log.e(TAG,"Couldn't load incrustation element");
            e.printStackTrace();
            return src;
        }

        for (Rect anElementsArray : elementsArray) {
            Size incrustationSize = new Size(anElementsArray.width,anElementsArray.height);
            Mat resizedIncrustationElement = new Mat();
            Imgproc.resize(incrustationElement,resizedIncrustationElement,incrustationSize);
            resizedIncrustationElement.copyTo(frame.rowRange((int) anElementsArray.tl().y, (int) anElementsArray.br().y).colRange((int) anElementsArray.tl().x,(int)anElementsArray.br().x));
        }

        Utils.matToBitmap(frame,this.result);

        return result;
    }
}
