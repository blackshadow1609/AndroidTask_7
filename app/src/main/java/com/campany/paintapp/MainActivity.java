package com.campany.paintapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView;
    private Button btnSave, btnClear, btnColor, btnEraser;
    private SeekBar seekBarSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paintView);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
        btnColor = findViewById(R.id.btnColor);
        btnEraser = findViewById(R.id.btnEraser);
        seekBarSize = findViewById(R.id.seekBarSize);

        btnSave.setOnClickListener(v -> paintView.saveToGallery(MainActivity.this));
        btnClear.setOnClickListener(v -> paintView.clearCanvas());

        btnColor.setOnClickListener(v -> {
            paintView.changeColor();
            btnColor.setBackgroundColor(paintView.currentColor);
            btnEraser.setBackgroundColor(Color.TRANSPARENT);
        });

        btnEraser.setOnClickListener(v -> {
            boolean isEraser = paintView.toggleEraser();
            if (isEraser) {
                btnEraser.setBackgroundColor(Color.LTGRAY);
            } else {
                btnEraser.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                paintView.setStrokeWidth(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}