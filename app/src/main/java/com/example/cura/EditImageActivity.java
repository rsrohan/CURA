package com.example.cura;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class EditImageActivity extends AppCompatActivity {

    private static final int CROP_PIC_REQUEST_CODE = 1;
    private Button saveDetails;
    private ImageView imageView;
    TextView textView;
    private String TAG;

    DatabaseReference dref;
    FirebaseUser mUser;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        imageView = findViewById(R.id.croppedImage);
        textView = findViewById(R.id.textView);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        uid = mUser.getEmail();
        uid = uid.substring(0, uid.indexOf("@"));

        dref = FirebaseDatabase.getInstance().getReference("PROFILE DETAILS").child(uid).child("food");


        saveDetails = findViewById(R.id.saveDetails);
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .start(this);


        saveDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long childCount = dataSnapshot.getChildrenCount();

                            String ing = textView.getText().toString();
                            try {
                                ing = ing.substring(ing.indexOf(":") + 1);
                            } catch (Exception e) {
                                Log.d(TAG, "onDataChange: ingredient word not found" + e);
                            }
                            ing = ing.replace("\n", " ");

                            String[] ingList = ing.split(",");

                            ArrayList<String> ingListToUpload = new ArrayList<>(Arrays.asList(ingList));
                            dref.child("" + childCount).setValue(ingListToUpload);
                            startActivity(new Intent(getApplicationContext(), UserMainActivity.class).putExtra("ingredients details", ing));
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "onClick: " + e);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).asBitmap().load(resultUri).addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(resource);
                        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                textView.setText(firebaseVisionText.getText());
                                Log.d("Tag", "onSuccess: " + Arrays.toString(firebaseVisionText.getTextBlocks().toArray()));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        return false;
                    }
                }).into(imageView);
//
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
