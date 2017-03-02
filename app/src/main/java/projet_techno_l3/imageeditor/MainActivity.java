package projet_techno_l3.imageeditor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import projet_techno_l3.imageeditor.ImageModifications.convolution.MeanFilter;

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
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

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
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mainImageView.setImageBitmap(myBitmap);

            }

        }

    }

    public void onMenuBackPressed(View view) {
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

    private Bitmap getImageViewBitmap(){
        if(mainImageView != null) {
            return ((BitmapDrawable) mainImageView.getDrawable()).getBitmap();
        }
        return null;
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
    }

    public void onMeanBlurButtonClicked(View view) {
        Button min = new Button(this);
        min.setText("min");
        Button mid = new Button(this);
        mid.setText("mid");
        Button max = new Button(this);
        max.setText("max");

        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.ABOVE,view.getId());

        LinearLayout optionsLayout = new LinearLayout(this);
        optionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        optionsLayout.setGravity(Gravity.CENTER);
        optionsLayout.addView(min);
        optionsLayout.addView(mid);
        optionsLayout.addView(max);
        mainLayout.addView(optionsLayout, relativeParams);


        Callable imageModif = new MeanFilter(getImageViewBitmap(), 7);

        try {
            mainImageView.setImageBitmap((Bitmap) imageModif.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLaplacianButtonClicked(View view) {
    }

    public void onSobelButtonClicked(View view) {
    }

    public void onUndoButtonPressed(View view){

    }

    public void onSaveButtonClicked(View view) {
        Bitmap bmp = getImageViewBitmap();
    }

    public void onLoadFromGalleryButtonPressed(View view){
        verifyStoragePermissions(MainActivity.this);
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, LOAD_PICTURE_GALLERY_ACTIVITY_REQUEST_CODE);
    }

    /**
     *
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

    /**
     * Open camera and save the photo into a specific directory
     * @param view performs the operation
     */
    public void onLoadFromCamera(View view){
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

}
