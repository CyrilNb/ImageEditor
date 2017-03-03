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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;
import projet_techno_l3.imageeditor.ImageModifications.BrightnessEditor;
import projet_techno_l3.imageeditor.ImageModifications.ColorFilter;
import projet_techno_l3.imageeditor.ImageModifications.ContrastEditor;
import projet_techno_l3.imageeditor.ImageModifications.Greyscale;
import projet_techno_l3.imageeditor.ImageModifications.convolution.BlurValues;
import projet_techno_l3.imageeditor.ImageModifications.convolution.GaussianBlur;
import projet_techno_l3.imageeditor.ImageModifications.convolution.MeanBlur;
import projet_techno_l3.imageeditor.ImageModifications.convolution.SobelFilter;

public class MainActivity extends AppCompatActivity {

    //Constants
    private static final int LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final int LOAD_PICTURE_CAMERA_ACTIVITY_REQUEST_CODE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ZoomAndScrollImageView mainImageView;
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

        Bitmap fruitBasket = BitmapFactory.decodeResource(getResources(),
                R.drawable.fruitbasket);
        // Adding the image programmatically so it gets added to the ZoomAndScrollImageView stack
        mainImageView.setImageBitmap(fruitBasket);
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

        if (requestCode == LOAD_PICTURE_CAMERA_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //Bitmap loadedBitmap = (Bitmap) data.getExtras().get("data");
            //mainImageView.setImageBitmap(loadedBitmap);
            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mainImageView.setImageBitmap(myBitmap);
            }

        }

    }

    public void onMenuBackClicked(View view) {
        clearFilterOptions();
        if (currentActiveFiltersMenu != null) {
            currentActiveFiltersMenu.setVisibility(View.GONE);
            menuPicker.setVisibility(View.VISIBLE);
        }
    }

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

                Callable imageModif = new BrightnessEditor(getImageViewBitmap(), (seekBar.getProgress() - 50) * 2);

                try {
                    mainImageView.setImageBitmap((Bitmap) imageModif.call());
                    clearFilterOptions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        filterOptions.addView(s);
    }

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

                Callable imageModif = new ContrastEditor(getImageViewBitmap(), (seekBar.getProgress() - 255));

                try {
                    mainImageView.setImageBitmap((Bitmap) imageModif.call());
                    clearFilterOptions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        filterOptions.addView(s);
    }

    public void onHistogramEqualizationClicked(View view) {
        clearFilterOptions();
    }

    public void onColorPickerButtonClicked(View view) {
        clearFilterOptions();
        new ColorOMaticDialog.Builder()
                .initialColor(Color.RED)
                .colorMode(ColorMode.RGB) // RGB, ARGB, HVS
                .indicatorMode(IndicatorMode.DECIMAL) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                .onColorSelected(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(@ColorInt int color) {
                        Callable colorFilterCallable = new ColorFilter(getImageViewBitmap(), color);
                        try {
                            mainImageView.setImageBitmap((Bitmap) colorFilterCallable.call());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                .create()
                .show(getSupportFragmentManager(), "ColorOMaticDialog");

    }

    public void onGreyScaleButtonClicked(View view) {
        clearFilterOptions();

        Callable imageModif = new Greyscale(getImageViewBitmap());

        try {
            mainImageView.setImageBitmap((Bitmap) imageModif.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onGaussianBlurButtonClicked(View view) {
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
                        value = BlurValues.MED;
                        break;
                }
                Callable imageModif = new GaussianBlur(getImageViewBitmap(), value);

                try {
                    mainImageView.setImageBitmap((Bitmap) imageModif.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        min.setOnClickListener(optionButtonClick);
        max.setOnClickListener(optionButtonClick);

        if (filterOptions != null) {
            filterOptions.addView(min);
            filterOptions.addView(max);
        }
    }

    public void onMeanBlurButtonClicked(View view) {
        clearFilterOptions();
        Button min = new Button(this);
        min.setText("min");
        Button mid = new Button(this);
        mid.setText("mid");
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
                Callable imageModif = new MeanBlur(getImageViewBitmap(), value);
                try {
                    mainImageView.setImageBitmap((Bitmap) imageModif.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        min.setOnClickListener(optionButtonClick);
        mid.setOnClickListener(optionButtonClick);
        max.setOnClickListener(optionButtonClick);

        if (filterOptions != null) {
            filterOptions.addView(min);
            filterOptions.addView(mid);
            filterOptions.addView(max);
        }
    }

    public void onLaplacianButtonClicked(View view) {
        clearFilterOptions();

    }

    public void onSobelButtonClicked(View view) {
        clearFilterOptions();
        Callable imageModif = new SobelFilter(getImageViewBitmap());

        try {
            mainImageView.setImageBitmap((Bitmap) imageModif.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUndoButtonClicked(View view) {
        mainImageView.undoModification();
    }

    public void onSaveButtonClicked(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.NameSavedImageDialog);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.saveimagedialog, null);
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

    public void onLoadFromGalleryButtonClicked(View view) {
        verifyStoragePermissions(MainActivity.this);
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Open camera and save the photo into a specific directory
     *
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

    private void saveImageIntoStorage(String filename) {
        //TODO in thread because it skips frames.
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
        } catch (Exception e) {
            Log.d("exception", e.getMessage());
        }
    }
}
