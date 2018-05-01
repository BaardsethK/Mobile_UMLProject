package com.example.kristoffer.graphimaging;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int COMPRESS_MIN = 1;

    Button mTakeImageButton;
    Button mCompressImageButton;
    SeekBar mQualitySlider;
    TextView mQualitySliderValue;
    TextView mImageDataText;
    ImageView mImageView;

    String mCurrentPhotoPath;
    File imgFile = null;
    Bitmap imgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTakeImageButton = findViewById(R.id.photoButton);
        mCompressImageButton = findViewById(R.id.imageCompressButton);
        mQualitySlider = findViewById(R.id.qualitySlider);
        mQualitySliderValue = findViewById(R.id.sliderValueText);
        mImageDataText = findViewById(R.id.imgDataText);
        mImageView = findViewById(R.id.imageView);

        mQualitySlider.setProgress(100);
        mQualitySlider.incrementProgressBy(1);
        mQualitySlider.setMax(100);
        mQualitySliderValue.setText(String.valueOf(mQualitySlider.getProgress()));


        mTakeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgFile != null) {
                    imgFile.delete();
                }
                try {
                    imgFile = createImageFile();
                } catch (IOException e) {
                    Log.e("IMG", "Error creating image file.");
                }
                if (imgFile != null) {
                    Uri imgUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.kristoffer.graphimaging.fileprovider",
                            imgFile);
                    Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(imageIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });

        mCompressImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                int compressValue = mQualitySlider.getProgress();
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                imgBitmap.compress(Bitmap.CompressFormat.JPEG, compressValue, out);
                byte[] bitmapdata = out.toByteArray();
                imgBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                mImageView.setImageBitmap(imgBitmap);

                mImageDataText.setText(imgBitmap.getAllocationByteCount() / 1024 + " kb");
                */

                //This turns the image B/W - Credits due : https://stackoverflow.com/questions/9377786/android-converting-a-bitmap-to-a-monochrome-bitmap-1-bit-per-pixel
                int compressValue = mQualitySlider.getProgress();
                Bitmap scaled = Bitmap.createScaledBitmap(imgBitmap, imgBitmap.getWidth() / 100 * compressValue, imgBitmap.getHeight() / 100 * compressValue, false);
                imgBitmap = scaled;
                Bitmap monochrome = Bitmap.createBitmap(imgBitmap.getWidth(), imgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(monochrome);
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                canvas.drawBitmap(imgBitmap, 0,0,paint);
                Bitmap bwBitmap = Bitmap.createBitmap(imgBitmap.getWidth(), imgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                float[] hsv = new float[3];
                for(int i = 0; i < imgBitmap.getWidth(); i++) {
                    for(int j = 0; j < imgBitmap.getHeight(); j++) {
                        Color.colorToHSV(imgBitmap.getPixel(i,j), hsv);
                        if (hsv[2] > 0.5f ) {
                            bwBitmap.setPixel(i,j,0xffffffff);
                        } else {
                            bwBitmap.setPixel(i,j,0xff000000);
                        }
                    }
                }

                imgBitmap = bwBitmap;
                mImageView.setImageBitmap(imgBitmap);
                updateImageData();
            }
        });



        mQualitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mQualitySlider.getProgress() < COMPRESS_MIN) {
                    mQualitySlider.setProgress(COMPRESS_MIN);
                }
                mQualitySliderValue.setText(String.valueOf(mQualitySlider.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        long fileSize = imgFile.length() / 1024;
        mImageDataText.setText(fileSize + " kb");
        */
        imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        mImageView.setImageBitmap(imgBitmap);
        updateImageData();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void updateImageData() {
        mImageDataText.setText(imgBitmap.getAllocationByteCount() / 1024 + " kb\n" + imgBitmap.getWidth() + "x" + imgBitmap.getHeight());
    }
}
