package com.victorkifer.PhotoEditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends SherlockActivity {
    public static final int PHOTO_FROM_CAMERA_CODE = 0;
    public static final int PHOTO_FROM_GALLERY_CODE = 1;
    public static final int EDIT_PHOTO_CODE = 2;

    public static final String photoDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/myEdit/";

    private ImageView iv;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        iv = (ImageView) findViewById(R.id.image);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               editPhoto();
            }
        });

        Button takePicBtn = (Button) findViewById(R.id.btnPicFromCamera);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromCamera();
            }
        });

        Button selectPicBtn = (Button) findViewById(R.id.btnPicFromGallery);
        selectPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromGallery();
            }
        });
    }

    public void editPhoto() {
        if (imageUri == null)
            return;
        Intent newIntent = new Intent(MainActivity.this, FeatherActivity.class);
        newIntent.setData(imageUri);
        newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, "f3c657cf459affac");
        startActivityForResult(newIntent, EDIT_PHOTO_CODE);
    }

    public void takePictureFromCamera() {
        File photoDir = new File(photoDirPath);
        photoDir.mkdirs();

        String photoFilePath = photoDirPath + random()+".jpg";
        File newfile = new File(photoFilePath);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            return;
        }

        imageUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(cameraIntent, PHOTO_FROM_CAMERA_CODE);
    }

    public void takePictureFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), PHOTO_FROM_GALLERY_CODE);
    }

    public static String random() {
        final int length = 6;
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < length; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    void loadImageByUri() {
        if(imageUri == null)
            return;

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap( getApplicationContext().getContentResolver(), imageUri);
            iv.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_FROM_CAMERA_CODE:
                    loadImageByUri();
                    break;
                case PHOTO_FROM_GALLERY_CODE:
                    imageUri = data.getData();
                    loadImageByUri();
                    break;

                case EDIT_PHOTO_CODE:
                    Bundle extra = data.getExtras();
                    if( null != extra ) {
                        boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
                        if(changed) {
                            imageUri = data.getData();
                            loadImageByUri();
                        }
                    };
                    break;

                default: break;
            }
        }
    }
}
