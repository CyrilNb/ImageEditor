package projet_techno_l3.imageeditor;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.Callable;

import projet_techno_l3.imageeditor.ImageModifications.BrightnessEditor;

public class MainActivity extends AppCompatActivity {

    ZoomAndScrollImageView mainImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainImageView = (ZoomAndScrollImageView) findViewById(R.id.imageView);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.content_main);

        SeekBar brightnessSeekBar = (SeekBar) findViewById(R.id.brightnessSeekBar);
        final TextView brightnessValue = (TextView) findViewById(R.id.labelBrightnessSeekBar);

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightnessValue.setText(String.valueOf(i-100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Callable imageModif = new BrightnessEditor(((BitmapDrawable) mainImageView.getDrawable()).getBitmap(),seekBar.getProgress()-100);

                try {
                    mainImageView.setImageBitmap((Bitmap) imageModif.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onTestButtonClicked(View view) {
        Callable imageModif = new BrightnessEditor(((BitmapDrawable) mainImageView.getDrawable()).getBitmap(),80);

        try {
            mainImageView.setImageBitmap((Bitmap) imageModif.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
