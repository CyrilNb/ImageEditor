package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.graphics.Bitmap;
import android.graphics.Color;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModification;

/**
 * Created by Antoine Gagnon
 */

/**
 * Creates a blur filter by using a mean value of the surroundings of a pixel.
 * <p>
 * Also called "Filtre Moyenneur" in French.
 */
public class MeanBlur extends AbstractImageModification {

    private final int filterSize;

    public MeanBlur(Bitmap src, BlurValues filterSize) {
        this.src = src;
        this.filterSize = (filterSize.ordinal() * 2) + 3;
    }


    /**
     * Returns the average Color value of a pixel array
     *
     * @param pixels2D Array of pixels
     * @return Color value
     */
    private int getAverage(int[][] pixels2D) {
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
        Double finalRed = totalRed / totalPixels;
        Double finalGreen = totalGreen / totalPixels;
        Double finalBlue = totalBlue / totalPixels;

        return Color.rgb(finalRed.intValue(), finalGreen.intValue(), finalBlue.intValue());
    }

    /**
     * Turns a single dimension array of pixels into a 2D one
     *
     * @param pixels Single dimension array
     * @param width  Size of rows
     * @param height Size of columns
     * @return A 2D pixel array with the the first index behind the rows and the second the columns
     */
    private int[][] get2DPixels(int pixels[], int width, int height) {
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
        int[] newPixels = new int[imgWidth * imgHeight];
        System.arraycopy(pixels, 0, newPixels, 0, pixels.length);

        // Getting a 2D array to pick a sub array more easily
        int[][] pixels2D = get2DPixels(pixels, imgWidth, imgHeight);
        int offset = (filterSize - 1)/2;

        for (int y = offset; y < imgHeight - offset; y++) { // We keep away from the borders
            for (int x = offset; x < imgWidth - offset; x++) { // Same here for the width
                int[][] subPixels;
                if ((subPixels = getSubTable(pixels2D, x - offset, y - offset, filterSize, filterSize)) != null) {
                    // Get the average of the subrange, otherwise we keep the old non-blurred pixel
                    newPixels[y * imgWidth + x] = getAverage(subPixels);
                }
            }
        }

        result.setPixels(newPixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }
}
