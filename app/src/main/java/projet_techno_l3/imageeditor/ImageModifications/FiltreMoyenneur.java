package projet_techno_l3.imageeditor.ImageModifications;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.style.LineHeightSpan;
import android.util.Log;

/**
 * Created by Antoine Gagnon
 */

public class FiltreMoyenneur extends AbstractImageModification {

    private final int filterSize;

    public FiltreMoyenneur(Bitmap src, int filterSize) {
        this.src = src;
        if(filterSize%2 != 1){
            filterSize = filterSize-1;
        }
        this.filterSize =filterSize;
    }

    private int getAverage(int [][]pixels2D) {
        int arrayHeight = pixels2D.length;
        int arrayWidth = pixels2D[0].length;
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        Double totalPixels = (double) (arrayHeight * arrayWidth);

        for (int y = 0; y < arrayHeight; y++) {
            for (int x = 0; x < arrayWidth; x++) {
                 int pixel = pixels2D[y][x];
                totalRed += Color.red(pixel);
                totalGreen += Color.green(pixel);
                totalBlue += Color.blue(pixel);
            }
        }
        Double finalRed = totalRed/totalPixels;
        Double finalGreen = totalGreen/totalPixels;
        Double finalBlue = totalBlue/totalPixels;

        return Color.rgb(finalRed.intValue(),finalGreen.intValue(),finalBlue.intValue());
    }

    private int[][] get2DPixels(int pixels[],int width, int height){
        int[][] newPixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(pixels, y * width, newPixels[y], 0, width); // Copying each set of "width" as a row
        }
        return newPixels;
    }

    @Override
    public Object call() throws Exception {


        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int imgHeight = result.getHeight();
        int imgWidth = result.getWidth();

        // Getting each pixel in the Bitmap
        int[] pixels = new int[imgWidth * imgHeight];
        result.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        // Copying the array to keep the old pixels on the borders
        int[] newPixels = new int[imgWidth*imgHeight];
        System.arraycopy(pixels,0,newPixels,0,pixels.length);

        // Getting a 2D array to pick a sub array more easily
        int[][] pixels2D = get2DPixels(pixels,imgWidth,imgHeight);
        int offset = (filterSize-1)/2;

        // Pour chaque pixel
        for (int y = 0; y < imgHeight-(offset*2); y++) { // We keep away from the borders
            for (int x = 0; x < imgWidth-(offset*2); x++) { // Same here for the width
                int[][] subPixels;
                if((subPixels = copySubrange(pixels2D,x,y,filterSize,filterSize)) != null){
                    // Get the average of the subrange, otherwise we keep the old non-blurred pixel
                    newPixels[(y+offset)*imgWidth + x+offset] = getAverage(subPixels);
                }
            }
        }

        result.setPixels(newPixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }

    private static int[][] copySubrange(int[][] source, int x, int y, int width, int height) {

        if (source == null) {
            return null;
        }
        if (source.length == 0) {
            return new int[0][0];
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        if (width < 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if ((y + height) > source.length) {
            Log.d("MOY_TESTHEIGHT","Height = " + height);
            return null;
        }
        int[][] dest = new int[height][width];
        for (int destY = 0; destY < height; destY++) {
            int[] srcRow = source[(y + destY)];
            if ((x + width) > srcRow.length) {
                Log.d("MOY_TESTWIDTH","Height = " + height);
                return null;
            }
            System.arraycopy(srcRow, x, dest[destY], 0, width);
        }
        return dest;
    }
}
