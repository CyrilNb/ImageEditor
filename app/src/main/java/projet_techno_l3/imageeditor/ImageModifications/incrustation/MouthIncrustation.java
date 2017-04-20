package projet_techno_l3.imageeditor.ImageModifications.incrustation;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * Used to find mouths in a picture
 */
public class MouthIncrustation extends HaarCascadeIncrustation {


    public MouthIncrustation(Bitmap src, Activity activity, int incrustationID) {
        super(src, activity, incrustationID);
        this.fileName = "mouth.xml";
        this.ratioMinElementSize = 0.10f;
        TAG = "Nose Incrustation";
    }
}
