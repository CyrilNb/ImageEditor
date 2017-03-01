package projet_techno_l3.imageeditor;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.Callable;

import projet_techno_l3.imageeditor.ImageModifications.BrightnessEditor;
import projet_techno_l3.imageeditor.ImageModifications.convolution.MeanFilter;

public class MainActivity extends AppCompatActivity {

    ZoomAndScrollImageView mainImageView;

    LinearLayout menuPicker;

    RelativeLayout mainLayout;


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


        Callable imageModif = new MeanFilter(((BitmapDrawable) mainImageView.getDrawable()).getBitmap(), 7);

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


}
