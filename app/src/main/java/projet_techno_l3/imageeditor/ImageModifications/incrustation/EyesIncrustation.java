package projet_techno_l3.imageeditor.ImageModifications.incrustation;

import android.app.Activity;
import android.graphics.Bitmap;


/**
 * Used to find eyes in a picture
 */
public class EyesIncrustation extends HaarCascadeIncrustation {


    public EyesIncrustation(Bitmap src, Activity activity, int incrustationID) {
        super(src, activity, incrustationID);
        this.fileName = "eyes.xml";
        this.ratioMinElementSize = 0.07f;
        TAG = "Eyes Incrustation";
    }

}