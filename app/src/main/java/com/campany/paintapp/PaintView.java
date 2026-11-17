package com.campany.paintapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class PaintView extends View {

    ArrayList<Stroke> strokes = new ArrayList<>();
    Stroke currentStroke;
    int currentColor = Color.BLACK;
    float strokeWidth = 10f;
    boolean isEraserMode = false;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        for (Stroke stroke : strokes) {
            canvas.drawPath(stroke.path, stroke.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int drawColor = isEraserMode ? Color.WHITE : currentColor;
                currentStroke = new Stroke(drawColor, strokeWidth);
                currentStroke.path.moveTo(x, y);
                strokes.add(currentStroke);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (currentStroke != null) {
                    currentStroke.path.lineTo(x, y);
                }
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    public void changeColor() {
        isEraserMode = false;
        Random random = new Random();
        currentColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public boolean toggleEraser() {
        isEraserMode = !isEraserMode;
        return isEraserMode;
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }

    public void clearCanvas() {
        strokes.clear();
        invalidate();
    }

    public void saveToGallery(Context context) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        draw(canvas);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PaintApp");

        ContentResolver resolver = context.getContentResolver();
        try {
            Uri uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUrl = resolver.insert(uri, values);

            if (imageUrl != null) {
                try (OutputStream outputStream = resolver.openOutputStream(imageUrl)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }
            }
            Toast.makeText(context, "Сохранено в галерею", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}