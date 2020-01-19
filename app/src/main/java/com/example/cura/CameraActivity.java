package com.example.cura;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cura.Camera2Kit.Camera2Fragment;
import com.example.cura.Camera2Kit.CameraConstants;

public class CameraActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Button cameraBtn, detectBtn;
    TextView textView;
    Bitmap imageBitmap;

    private static final String FRAGMENT_DIALOG = "aspect_dialog";
    private static final int PICK_IMAGE = 98;


    private static final int[] FLASH_OPTIONS = {
            CameraConstants.FLASH_AUTO,
            CameraConstants.FLASH_OFF,
            CameraConstants.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    private int mCurrentFlashIndex;


    private Camera2Fragment mCamera2Fragment;


    private ImageView mPictureButton;
    private boolean canClick = true;
    private boolean enableMenuItems = true;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        //setSupportActionBar(toolbar);

        if (null == savedInstanceState) {
            mCamera2Fragment = Camera2Fragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mCamera2Fragment)
                    .commit();
        } else {
            mCamera2Fragment = (Camera2Fragment) getFragmentManager().findFragmentById(R.id.container);
        }



        mPictureButton =  findViewById(R.id.picture);
        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera2Fragment.takePicture();
                findViewById(R.id.picture).setEnabled(false);
                findViewById(R.id.skip).setEnabled(false);
                findViewById(R.id.uploadImage).setEnabled(false);
                enableMenuItems = false;
                invalidateOptionsMenu();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                        }catch (Exception e){}

                    }
                }, 1000);
            }

        });
        Button skipButton=findViewById(R.id.skip);

        setOnTouchListnerToView(skipButton);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
            }
        });
        findViewById(R.id.uploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            startActivity(new Intent(this, UserMainActivity.class)
                    .putExtra("imagePath", getPath(getApplicationContext(), imageUri)));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                recreate();

            }

            //recreate();


        }
    }
    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    private void setOnTouchListnerToView(final Button target)
    {
        target.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    target.setTextColor(Color.parseColor("#80ffffff"));

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    target.setTextColor(Color.parseColor("#ffffff"));
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_camera, menu);
        if (enableMenuItems)
        {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(true);
            return true;

        }else{
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
            return false;

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_flash:
                mCurrentFlashIndex = (mCurrentFlashIndex + 1) % FLASH_OPTIONS.length;
                item.setTitle(FLASH_TITLES[mCurrentFlashIndex]);
                item.setIcon(FLASH_ICONS[mCurrentFlashIndex]);
                mCamera2Fragment.setFlash(FLASH_OPTIONS[mCurrentFlashIndex]);
                return true;
            case R.id.switch_camera:
                int facing = mCamera2Fragment.getFacing();
                mCamera2Fragment.setFacing(facing == CameraConstants.FACING_FRONT ?
                        CameraConstants.FACING_BACK : CameraConstants.FACING_FRONT);

                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        boolean flash=mCamera2Fragment.isFlashSupported();

        menu.findItem(R.id.switch_camera)
                .setVisible(mCamera2Fragment.isFacingSupported());

        menu.findItem(R.id.switch_flash)
                .setVisible(flash)
                .setTitle(FLASH_TITLES[mCurrentFlashIndex])
                .setIcon(FLASH_ICONS[mCurrentFlashIndex]);


        return super.onPrepareOptionsMenu(menu);
    }



}



