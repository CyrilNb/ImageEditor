package projet_techno_l3.imageeditor.ImageModifications.convolution;


import android.graphics.Bitmap;
import android.graphics.Color;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModification;

/**
 * Colorizes the image with a new hue
 */
public class HueColorize extends AbstractImageModification{

    //TODO put height and width in abstract class as it's used in several subclass

    /**
     * Private variables
     */
    private float[] hsv = new float[3];
    private int[] pixels;
    private int color;
    private int height;
    private int width;

    /**
     * Constructor
     * @param src
     */
    public HueColorize(Bitmap src, int color){
        this.src = src;
        this.color = color;
        this.height = src.getHeight();
        this.width = src.getWidth();
        pixels = new int[this.src.getHeight() * this.src.getWidth()];
    }

    /**
     * Performs the operation on a specific thread
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888,true); //create a mutable copy of the original bitmap
        result.getPixels(pixels,0,width,0,0,width,height);

        float h = getHueValue(color);
        for(int i = 0; i < this.height * this.width ; i++){
            Color.RGBToHSV(Color.red(pixels[i]),Color.green(pixels[i]),Color.blue(pixels[i]),hsv);
            hsv[0] = h;
            pixels[i] = Color.HSVToColor(hsv); //conversion
        }
        result.setPixels(pixels,0,this.width,0,0,this.width,this.height);
        return result;
    }

    /**
     * Gets the hue value from a color
     *
     * @param integerColor Integer representation of a color
     * @return Hue value from a color
     */
    private static float getHueValue (int integerColor) {
        float[] hsvValues = new float[3];
        Color.colorToHSV(integerColor, hsvValues);
        return hsvValues[0];
    }
}
