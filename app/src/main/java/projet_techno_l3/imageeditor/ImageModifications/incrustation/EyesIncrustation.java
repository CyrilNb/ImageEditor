package projet_techno_l3.imageeditor.ImageModifications.incrustation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Created by Antoine Gagnon
 */

public class EyesIncrustation extends AbstractImageModificationAsyncTask {


    public EyesIncrustation(Activity activity) {
        super(activity);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return null;
    }
}
