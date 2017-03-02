package projet_techno_l3.imageeditor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import projet_techno_l3.imageeditor.ImageModifications.convolution.BlurValues;
import projet_techno_l3.imageeditor.ImageModifications.convolution.GaussianBlur;
import projet_techno_l3.imageeditor.ImageModifications.convolution.MeanBlur;

public class MainActivity extends AppCompatActivity {

    ZoomAndScrollImageView mainImageView;
    LinearLayout menuPicker;
    RelativeLayout mainLayout;
    String pictureImagePath = "";


    //Constants
    private static final int LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final int LOAD_PICTURE_CAMERA_ACTIVITY_REQUEST_CODE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainImageView = (ZoomAndScrollImageView) findViewById(R.id.imageView);
        menuPicker = (LinearLayout) findViewById(R.id.menuPickerLinearLayout);
        mainLayout = (RelativeLayout) findViewById(R.id.content_main);

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
        ((LinearLayout) view.getParent()).setVisibility(View.GONE);
        menuPicker.setVisibility(View.VISIBLE);
    }

    public void onCategoriesMenuButtonClicked(View view) {
        menuPicker.setVisibility(View.INVISIBLE);
        switch (view.getId()) {
            case R.id.adjustButton:
                LinearLayout adjustLinearLayout = (LinearLayout) findViewById(R.id.adjustLinearLayout);
                if (adjustLinearLayout != null) {
                    adjustLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.filterButton:
                LinearLayout filterLinearLayout = (LinearLayout) findViewById(R.id.filterLinearLayout);
                if (filterLinearLayout != null) {
                    filterLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.convolutionButton:
                LinearLayout convolutionLinearLayout = (LinearLayout) findViewById(R.id.convolutionLinearLayout);
                if (convolutionLinearLayout != null) {
                    convolutionLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private Bitmap getImageViewBitmap() {
        if (mainImageView != null) {
            return ((BitmapDrawable) mainImageView.getDrawable()).getBitmap();
        }
        return null;
    }

    private void clearFilterOptions() {
        LinearLayout filterOptions = (LinearLayout) findViewById(R.id.filterOptionsLinearLayout);

        if (filterOptions != null) {
            filterOptions.removeAllViews();
        }
    }

    public void onBrightnessButtonClicked(View view) {
    }

    public void onContrastButtonClicked(View view) {
    }

    public void onHistogramEqualizationClicked(View view) {
    }

    public void onColorPickerButtonClicked(View view) {
    }

    public void onGreyScaleButtonClicked(View view) {
    }

    public void onGaussianBlurButtonClicked(View view) {
        clearFilterOptions();
        Button min = new Button(this);
        min.setText("min");
        Button max = new Button(this);
        max.setText("max");

        LinearLayout filterOptions = (LinearLayout) findViewById(R.id.filterOptionsLinearLayout);

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

        LinearLayout filterOptions = (LinearLayout) findViewById(R.id.filterOptionsLinearLayout);

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
    }

    public void onSobelButtonClicked(View view) {
    }

    public void onUndoButtonClicked(View view) {
        mainImageView.undoModification();
    }

    public void onSaveButtonClicked(View view) {
        Bitmap bmp = getImageViewBitmap();
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

}
