package projet_techno_l3.imageeditor;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.view.View;

import java.util.Stack;


/**
 *
 */
public class ZoomAndScrollImageView extends ImageView implements View.OnTouchListener {

    private Stack<Bitmap> bitmapStack = new Stack<>();


    public ZoomAndScrollImageView(Context context) {
        super(context);
    }

    public ZoomAndScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomAndScrollImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if(bm != bitmapStack.peek()) {
            bitmapStack.add(bm);
        }
    }


    /**
     * Removes a modification by going back to the previous image.
     */
    public void undoModification(){
        if(bitmapStack.size() > 1) {
            bitmapStack.pop();
            setImageBitmap(bitmapStack.peek());
        }
    }

    /**
     * Resets the image to the first one.
     */
    public void resetPicture(){
        Bitmap firstBM = bitmapStack.firstElement();
        if(firstBM != bitmapStack.peek()){
             bitmapStack.removeAllElements();
             bitmapStack.add(firstBM);
        }
        setImageBitmap(firstBM);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZoomAndScrollImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
