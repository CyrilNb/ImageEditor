package projet_techno_l3.imageeditor;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;
import projet_techno_l3.imageeditor.ImageModifications.BrightnessEditor;
import projet_techno_l3.imageeditor.ImageModifications.ColorFilter;
import projet_techno_l3.imageeditor.ImageModifications.ContrastEditor;
import projet_techno_l3.imageeditor.ImageModifications.Greyscale;
import projet_techno_l3.imageeditor.ImageModifications.HistogramEqualization;
import projet_techno_l3.imageeditor.ImageModifications.NegativeFilter;
import projet_techno_l3.imageeditor.ImageModifications.Sepia;
import projet_techno_l3.imageeditor.ImageModifications.SketchFilter;
import projet_techno_l3.imageeditor.ImageModifications.convolution.BlurValues;
import projet_techno_l3.imageeditor.ImageModifications.convolution.GaussianBlur;
import projet_techno_l3.imageeditor.ImageModifications.HueColorize;
import projet_techno_l3.imageeditor.ImageModifications.convolution.GaussianBlurRS;
import projet_techno_l3.imageeditor.ImageModifications.convolution.LaplacianFilter;
import projet_techno_l3.imageeditor.ImageModifications.convolution.MeanBlur;
import projet_techno_l3.imageeditor.ImageModifications.convolution.MeanBlurRS;
import projet_techno_l3.imageeditor.ImageModifications.convolution.SobelFilter;
import projet_techno_l3.imageeditor.ImageModifications.incrustation.EyesIncrustation;

public class MainActivity extends AppCompatActivity {

    /**
     * Constants
     */
    private static final int LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final int LOAD_PICTURE_CAMERA_ACTIVITY_REQUEST_CODE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "MainActivityLog";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ZoomAndScrollImageView mainImageView;

    /**
     * Private variables
     */
    private LinearLayout menuPicker;
    private LinearLayout filterOptions;
    private String pictureImagePath = "";
    private LinearLayout currentActiveFiltersMenu;

    /**
     * If APK >= 23, we need to check at runtime for user permissions
     * Checks if the app has permission to write to device storage
     * If the app does not has permissions required then the user will be prompted to grant permissions
     *
     * @param activity which performs the operation where permissions are requested
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if the application has write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // If not, prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mainImageView = (ZoomAndScrollImageView) findViewById(R.id.imageView);
        menuPicker = (LinearLayout) findViewById(R.id.menuPickerLinearLayout);
        filterOptions = (LinearLayout) findViewById(R.id.filterOptionsLinearLayout);
        Button undoButton = (Button) findViewById(R.id.undoButton);

        // The onLongClick event isn't available in XML, so we define it here.
        if (undoButton != null) {
            undoButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mainImageView.resetPicture();
                    return true;
                }
            });
        }

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Bitmap basedImage = BitmapFactory.decodeResource(getResources(),
                R.drawable.fruitbasket);
        // Adding the image programmatically so it gets added to the ZoomAndScrollImageView stack
        mainImageView.setImageBitmap(basedImage);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearFilterOptions();
        onMenuBackClicked(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO switch ici

        if (requestCode == LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath);
            mainImageView.setImageBitmap(loadedBitmap);
            mainImageView.setAdjustViewBounds(true);
            //mainImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        }

        if (requestCode == LOAD_PICTURE_CAMERA_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mainImageView.setImageBitmap(myBitmap);
            }

        }

    }

    /**
     * Runs when the arrow to get back on previous menu is clicked in the bottom menu
     * @param view
     */
    public void onMenuBackClicked(View view) {
        clearFilterOptions();
        if (currentActiveFiltersMenu != null) {
            currentActiveFiltersMenu.setVisibility(View.GONE);
            menuPicker.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Runs when a category is clicked in the bottom menu
     * @param view
     */
    public void onCategoriesMenuButtonClicked(View view) {
        menuPicker.setVisibility(View.INVISIBLE);
        switch (view.getId()) {
            case R.id.adjustButton:
                LinearLayout adjustLinearLayout = (LinearLayout) findViewById(R.id.adjustLinearLayout);
                if (adjustLinearLayout != null) {
                    adjustLinearLayout.setVisibility(View.VISIBLE);
                    currentActiveFiltersMenu = adjustLinearLayout;
                }
                break;
            case R.id.filterButton:
                LinearLayout filterLinearLayout = (LinearLayout) findViewById(R.id.filterLinearLayout);
                if (filterLinearLayout != null) {
                    filterLinearLayout.setVisibility(View.VISIBLE);
                    currentActiveFiltersMenu = filterLinearLayout;
                }
                break;
            case R.id.convolutionButton:
                LinearLayout convolutionLinearLayout = (LinearLayout) findViewById(R.id.convolutionLinearLayout);
                if (convolutionLinearLayout != null) {
                    convolutionLinearLayout.setVisibility(View.VISIBLE);
                    currentActiveFiltersMenu = convolutionLinearLayout;
                }
                break;
        }
    }

    /**
     * Get the bitmap image in the main imageView
     *
     * @return The bitmap
     */
    private Bitmap getImageViewBitmap() {
        if (mainImageView != null) {
            return ((BitmapDrawable) mainImageView.getDrawable()).getBitmap();
        }
        return null;
    }

    /**
     * Removes all filter options
     */
    private void clearFilterOptions() {

        if (filterOptions != null) {
            filterOptions.removeAllViews();
        }
    }

    /**
     * Runs when the brightness button is clicked from the bottom menu
     * @param view
     */
    public void onBrightnessButtonClicked(View view) {
        clearFilterOptions();
        SeekBar s = new SeekBar(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        s.setProgress(50);

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                try {
                    BrightnessEditor brightnessEditor = new BrightnessEditor(getImageViewBitmap(), (seekBar.getProgress() - 50) * 2, MainActivity.this);
                    brightnessEditor.execute();
                    clearFilterOptions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        filterOptions.addView(s);
    }

    /**
     * * Runs when the contrast button is clicked from the bottom menu
     * @param view
     */
    public void onContrastButtonClicked(View view) {
        clearFilterOptions();
        SeekBar s = new SeekBar(this);
        s.setMax(255 * 2);
        s.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        s.setProgress(255);

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                ContrastEditor contrastEditor = new ContrastEditor(getImageViewBitmap(), (seekBar.getProgress() - 255),MainActivity.this);
                contrastEditor.execute();
            }
        });

        filterOptions.addView(s);
    }

    /**
     * * Runs when the histogram equalization button is clicked from the bottom menu
     * @param view
     */
    public void onHistogramEqualizationClicked(View view) {
        clearFilterOptions();

        HistogramEqualization histogramEqualization = new HistogramEqualization(getImageViewBitmap(),this);
        histogramEqualization.execute();
    }

    /**
     * * Runs when the color picker button is clicked from the bottom menu
     * @param view
     */
    public void onColorPickerButtonClicked(View view) {
        clearFilterOptions();
        ColorFilter colorFilterCallable = null;
        this.showColorPickerDialog(false);

    }


    /**
     * Runs when the hue colorize button is clicked from the bottom menu
     * @param view
     */
    public void onHueColorizeButtonClicked(View view) {
        clearFilterOptions();
        HueColorize hueColorizeCallable = null;
        this.showColorPickerDialog(true);

    }

    /**
     * * Runs when the greyscale button is clicked from the bottom menu
     * @param view
     */
    public void onGreyScaleButtonClicked(View view) {
        clearFilterOptions();

        Greyscale asynctask = new Greyscale(getImageViewBitmap(),this);
        asynctask.execute();

    }

    /**
     * Runs when the sepia button is clicked from the bottom menu and performs sepia filter
     * @param view
     */
    public void onSepiaButtonClicked(View view) {
        clearFilterOptions();

        Sepia sepia = new Sepia(getImageViewBitmap(),this);
        sepia.execute();

    }

    /**
     * Runs when the sketch button is clicked from the bottom menu
     * @param view
     */
    public void onSketchButtonClicked(View view) {
        clearFilterOptions();

        SketchFilter sketchFilter = new SketchFilter(getImageViewBitmap(),this);
        sketchFilter.execute();
    }

    /**
     * Runs when the negative button is clicked from the bottom menu and performs negative filter
     * @param view
     */
    public void onNegativeButtonClicked(View view){
        clearFilterOptions();

        NegativeFilter negativeFilter = new NegativeFilter(getImageViewBitmap(), this);
        negativeFilter.execute();
    }


    /**
     * * Runs when the Gaussian blur button is clicked from the bottom menu and performs Gaussian blur
     * @param view
     */
    public void onGaussianBlurButtonClicked(View view) {
        clearFilterOptions();
        Button min = new Button(this);
        min.setText("min");
        Button med = new Button(this);
        med.setText("med");
        Button max = new Button(this);
        max.setText("max");


        View.OnClickListener optionButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                BlurValues value = BlurValues.MIN;
                switch (b.getText().toString()) {
                    case "min":
                        value = BlurValues.MIN;
                        break;
                    case "med":
                        value = BlurValues.MED;
                        break;
                    case "max":
                        value = BlurValues.MAX;
                        break;
                }

                GaussianBlurRS gaussianBlur = new GaussianBlurRS(getImageViewBitmap(), value,MainActivity.this);
                gaussianBlur.execute();
            }
        };

        min.setOnClickListener(optionButtonClick);
        med.setOnClickListener(optionButtonClick);
        max.setOnClickListener(optionButtonClick);

        if (filterOptions != null) {
            filterOptions.addView(min);
            filterOptions.addView(med);
            filterOptions.addView(max);
        }
    }

    /**
     * Runs when the Mean blur button is clicked from the bottom menu
     * @param view
     */
    public void onMeanBlurButtonClicked(View view) {
        clearFilterOptions();
        Button min = new Button(this);
        min.setText("min");
        Button max = new Button(this);
        max.setText("max");


        View.OnClickListener optionButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                BlurValues value = BlurValues.MIN;
                switch (b.getText().toString()) {
                    case "min":
                        value = BlurValues.MIN;
                        break;
                    case "max":
                        value = BlurValues.MAX;
                        break;
                }


                MeanBlurRS meanBlur = new MeanBlurRS(getImageViewBitmap(),value, MainActivity.this);
                meanBlur.execute();

            }
        };

        min.setOnClickListener(optionButtonClick);
        max.setOnClickListener(optionButtonClick);

        if (filterOptions != null) {
            filterOptions.addView(min);
            filterOptions.addView(max);
        }
    }

    /**
     * Runs when the Laplacian button is clicked from the bottom menu
     * @param view
     */
    public void onLaplacianButtonClicked(View view) {
        clearFilterOptions();
        LaplacianFilter laplacianFilter = new LaplacianFilter(getImageViewBitmap(),this);
        laplacianFilter.execute();
    }

    /**
     * Runs when the Sobel button is clicked from the bottom menu
     * @param view
     */
    public void onSobelButtonClicked(View view) {
        clearFilterOptions();

        SobelFilter asynctask = new SobelFilter(getImageViewBitmap(),this);
        asynctask.execute();

        /*Callable imageModif = new SobelFilter(getImageViewBitmap());

        try {
            mainImageView.setImageBitmap((Bitmap) imageModif.call());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Runs when the undo button is clicked from the top menu
     * Perfoms tne undo modification
     * @param view
     */
    public void onUndoButtonClicked(View view) {
        mainImageView.undoModification();
    }

    /**
     * Runs when the save button is clicked from the bottom menu
     * Displays a dialog to name the image and perfoms the save of it
     * @param view
     */
    public void onSaveButtonClicked(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.NameSavedImageDialog);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.saveimagedialog,null);
        builder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTxtDialogSaveImage);
        builder.setTitle("Nom de l'image");
        builder.setPositiveButton("TERMINER",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveImageIntoStorage(editTextName.getText().toString());
                    }
                });
        builder.setNegativeButton("ANNULER",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Runs when the gallery button is clicked from the bottom menu
     * Performs the load of an image from the gallery
     * @param view
     */
    public void onLoadFromGalleryButtonClicked(View view) {
        verifyStoragePermissions(MainActivity.this);
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Run intent for camera, creates new file stored in external storage, puts photo taken in this file.
     * @param view performs the operation
     */
    public void onLoadFromCameraButtonClicked(View view) {
        verifyStoragePermissions(MainActivity.this);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, LOAD_PICTURE_CAMERA_ACTIVITY_REQUEST_CODE);

    }

    /**
     * Saves an image into intenal storage of th device.
     * @param filename
     */
    private void saveImageIntoStorage(String filename) {
        verifyStoragePermissions(this);
        Bitmap bmp = getImageViewBitmap();
        OutputStream fOut = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "ImageEditor" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, filename + ".png");
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("exception", e.getMessage());
            Toast.makeText(this, R.string.image_not_saved, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays a color picker and runs the callable
     * @param isHue  Is for HueColorize or not
     */
    private void showColorPickerDialog(final boolean isHue){
        new ColorOMaticDialog.Builder()
                .initialColor(Color.RED)
                .colorMode(ColorMode.RGB) // RGB, ARGB, HVS
                .indicatorMode(IndicatorMode.DECIMAL) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                .onColorSelected(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(@ColorInt int color) {
                        try {
                            if(!isHue){
                                final ColorFilter colorFilter = new ColorFilter(getImageViewBitmap(),color, MainActivity.this);
                                colorFilter.execute();
                            }else{
                                final HueColorize hueColorize = new HueColorize(getImageViewBitmap(),color,MainActivity.this);
                                hueColorize.execute();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                .create()
                .show(getSupportFragmentManager(), "ColorOMaticDialog");
    }

    public void onEyesIncrustationButtonClicked(View view) {

        clearFilterOptions();

        EyesIncrustation asynctask = new EyesIncrustation(getImageViewBitmap(),this);
        asynctask.execute();

    }
}
