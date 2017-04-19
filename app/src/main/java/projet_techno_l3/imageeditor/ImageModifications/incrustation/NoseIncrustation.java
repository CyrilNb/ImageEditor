package projet_techno_l3.imageeditor.ImageModifications.incrustation;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * Used to find a nose in a picture
 */
public class NoseIncrustation extends HaarCascadeIncrustation {

    public NoseIncrustation(Bitmap src, Activity activity) {
        super(src, activity);
        this.fileName = "nose.xml";
        this.ratioMinElementSize = 0.08f;
        TAG = "Nose Incrustation";
    }

}
