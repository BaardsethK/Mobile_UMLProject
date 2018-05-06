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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1111;


    private Toolbar toolbar;

    private ImageButton mTakeImageButton;
    private Button mScaleImageButton;
    private Button mSaveImageButton;
    private Button mDecolorImageButton;

    private TextView mImageDataText;
    private EditText mXScaleValue;
    private EditText mYScaleValue;

    private ImageView mImageView;

    private String mCurrentPhotoPath;
    private File imgFile = null;
    private Bitmap imgBitmap;
    private final float darkColorThreshold = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTakeImageButton = findViewById(R.id.photoButton);
        mScaleImageButton = findViewById(R.id.imageCompressButton);
        mDecolorImageButton = findViewById(R.id.removeColorButton);
        mSaveImageButton = findViewById(R.id.saveImageButton);

        mImageDataText = findViewById(R.id.imgDataText);
        mXScaleValue = findViewById(R.id.xScaleEditText);
        mYScaleValue = findViewById(R.id.yScaleEditText);

        mImageView = findViewById(R.id.imageView);


        mTakeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgFile != null) {
                    if (!imgFile.delete()) {
                        Log.e("IMGDEL", "Deleting existing image file failed.");
                    }
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
                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(imageIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });

        mScaleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String xString = mXScaleValue.getText().toString();
                String yString = mYScaleValue.getText().toString();
                int xValue = Integer.parseInt(xString);
                int yValue = Integer.parseInt(yString);
                scaleBitmap(xValue, yValue);
                updateImageData();
            }
        });

        mDecolorImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                decolorBitmap();
                mImageView.setImageBitmap(imgBitmap);

                updateImageData();

            }
        });

        mSaveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageSavePermission();
                writeMapToFile();
            }
        });
    }

    /**
     *  Define/create action bar/menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Action Bar options switch.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_info:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Result from image-function.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        mImageView.setImageBitmap(imgBitmap);
        updateImageData();
    }

    /**
     *  Result from requesting permission to write image to external storage.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImageToGallery();
                } else {
                    Log.i("PERM", "Request for permission failed.");
                }
            }
        }
    }

    /**
     * Scale the bitmap to given height/width, preserving aspect ratio.
     *
     * @param maxWidth
     * @param maxHeight
     */
    private void scaleBitmap(int maxWidth, int maxHeight) {
        int width = imgBitmap.getWidth();
        int height = imgBitmap.getHeight();
        float ratio;

        if (width > height) {
            ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)( height / ratio);
        } else if (height > width) {
            ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            width = maxWidth;
            height = maxHeight;
        }

        imgBitmap = Bitmap.createScaledBitmap(imgBitmap, width, height, false);
        mImageView.setImageBitmap(imgBitmap);
    }


    /**
     * Removes all color from bitmap, reduces to black/white.
     * Should only be used after images have been scaled to 1000x1000 or below.
     */
    private void decolorBitmap() {
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
                if (hsv[2] > darkColorThreshold) {
                    bwBitmap.setPixel(i,j,0xffffffff);
                } else{
                    bwBitmap.setPixel(i,j,0xff000000);
                }
            }
        }

        imgBitmap = bwBitmap;
    }

    /**
     * Create a file after taking image with camera.
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());
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

    /**
     * Updates the current image data in the data text area.
     * Show dimensions and filesize in KB
     */
    private void updateImageData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        mImageDataText.setText(getString(R.string.image_values,imgBitmap.getWidth(), imgBitmap.getHeight(), baos.size() / 1024));
        mImageDataText.setVisibility(View.VISIBLE);
    }

    /**
     * Get permission from user to access/write to external storage.
     * If permission is already given, file is written.
     */
    private void getImageSavePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            saveImageToGallery();
        }
    }

    /**
     * Create image file, set directory, and save.
     * Calls unit media library to alert that file has been added.
     */
    private void saveImageToGallery() {
        String root = Environment.getExternalStorageDirectory().toString() + "/SketchPhotos";
        File dir = new File(root);
        String fileName = imgFile.getName();
        final File finalFile = new File(dir, fileName);
        if (finalFile.exists()) {
            if (!finalFile.delete()) {
                Log.e("SAVEIMG", "End bitimage failed to delete.");
            }
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("SAVEIMG" ,"Creating image directory failed");
            }
        }

        Log.i("SAVEIMG", root + fileName);
        try {
            FileOutputStream out = new FileOutputStream(finalFile);
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            new SingleMediaScanner(this, finalFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Save image and path to bundle, if user changes activity.
     * @param bundle
     */
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        ByteArrayOutputStream bmStream = new ByteArrayOutputStream();
        if (imgBitmap != null) {
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bmStream);
            byte[] bmByteArray = bmStream.toByteArray();

            bundle.putByteArray("image", bmByteArray);
            bundle.putString("imagePath", mCurrentPhotoPath);
        }
    }

    /**
     * Create 2D-array of 1's and 0's from Bitmap, write to .txt-file.
     * Created so it can be integrated with a game. (Map drawing tool)
     */
    private void writeMapToFile() {
        int map[][] = new int[imgBitmap.getWidth() * 2][imgBitmap.getHeight()];
        float[]  hsv = new float[3];
        for (int i = 0; i < imgBitmap.getWidth(); i++) {
            for (int j = 0; j < imgBitmap.getHeight(); j++) {
                Color.colorToHSV(imgBitmap.getPixel(i,j), hsv);
                if (hsv[2] > 0.5f) {
                    map[i][j] = 1;
                } else {
                    map[i][j] = 0;
                }

            }
        }

        Log.i("WRITEMAP", map.toString());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imgBitmap.getHeight(); i++) {
            for (int j = 0; j < imgBitmap.getWidth(); j++) {
                builder.append(map[j][i]);
            }
            builder.append("\n");
        }

        String root = Environment.getExternalStorageDirectory().toString() + "/maps/";
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());
        String fileName = "map_" + timeStamp + ".txt";

        try {
            BufferedWriter bof = new BufferedWriter(new FileWriter(new File(root + fileName)));
            bof.write(builder.toString());
            bof.flush();
            bof.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
