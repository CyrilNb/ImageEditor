package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModification;
import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Applies a Gaussian Blur to an image.
 * Also called "Filtre Gaussien" in French.
 */
public class GaussianBlur extends AbstractImageModificationAsyncTask {

    private final int filterSize;

    private int[][] gaussianMatrix =
            {{1, 2, 3, 2, 1},
                    {2, 6, 8, 6, 2},
                    {3, 8, 10, 8, 3},
                    {2, 6, 8, 6, 2},
                    {1, 2, 3, 2, 1}};

    private int[] gaussianValue = {0, 10, 0, 66, 0, 98};

    public GaussianBlur(Bitmap src, BlurValues filterSize, Activity activity) {
        super(activity);
        this.src = src;
        this.filterSize = (filterSize.ordinal() * 2) + 3;
    }

    /**
     * Returns the gaussian average pixel value of a pixel array
     *
     * @param pixels2D Array of pixels
     * @return Color value
     */
    private int getGaussianAverage(int[][] pixels2D) {
        int arrayHeight = pixels2D.length;
        int arrayWidth = pixels2D[0].length;

        int gaussianOffset = (gaussianMatrix.length - arrayHeight) / 2;

        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        int gaussianTotal = gaussianValue[arrayHeight];

        for (int y = 0; y < arrayHeight; y++) {
            for (int x = 0; x < arrayWidth; x++) {
                int pixel = pixels2D[y][x];
                int gaussianValue = gaussianMatrix[y + gaussianOffset][x + gaussianOffset];
                totalRed += gaussianValue * Color.red(pixel);
                totalGreen += gaussianValue * Color.green(pixel);
                totalBlue += gaussianValue * Color.blue(pixel);
            }
        }

        // Divide by the full gaussian matrix sum
        int finalRed = totalRed / gaussianTotal;
        int finalGreen = totalGreen / gaussianTotal;
        int finalBlue = totalBlue / gaussianTotal;

        return Color.rgb(finalRed, finalGreen, finalBlue);
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
    protected Bitmap doInBackground(String... params) {
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
                    newPixels[y * imgWidth + x] = getGaussianAverage(subPixels);
                }
            }
        }

        result.setPixels(newPixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return result;
    }
}
