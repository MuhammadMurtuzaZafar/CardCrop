package com.example.cardcrop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.canhub.cropper.CropImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    static CropImageView imageView;
    Button selectBtn;
    final int SELECT_CODE = 100;
    Mat mat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, SELECT_CODE);
            }
        });
    }

    public void init() {
        imageView = findViewById(R.id.imageView);
        selectBtn = findViewById(R.id.selectButton);

        if (OpenCVLoader.initLocal()) Log.d("Loaded", "Success");
        else Log.d("Loaded", "Error ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_CODE && data != null) {
            try {
                ImageProcessing imageProcessing = new ImageProcessing();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageProcessing.drawAndCropLargestContour(bitmap);

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}