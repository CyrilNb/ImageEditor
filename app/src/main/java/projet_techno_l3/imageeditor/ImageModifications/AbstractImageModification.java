package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;

import java.util.concurrent.Callable;

/**
 * Created by gagno on 2/3/2017.
 */

public abstract class AbstractImageModification implements Callable{

    protected Bitmap src;

    protected int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
}
