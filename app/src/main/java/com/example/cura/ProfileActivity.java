package com.example.cura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView profileName;
    Button pickdob;
    RadioGroup selectGender;
    Button saveDetails;

    FirebaseUser mUser;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        pickdob = findViewById(R.id.pickdob);
        selectGender = findViewById(R.id.selectGender);
        saveDetails = findViewById(R.id.saveDetails);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("PROFILE DETAILS").child(mUser.getUid()).child("details");

        assert mUser != null;

        try {
            Glide.with(this).load(mUser.getPhotoUrl()).into(profileImage);
            profileName.setText(mUser.getDisplayName());
        } catch (Exception e) {
            Snackbar.make(saveDetails, "error: " + e, Snackbar.LENGTH_INDEFINITE).show();
        }

        pickdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });

        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("dob", pickdob.getText().toString());

                int selectedId = selectGender.getCheckedRadioButtonId();
                RadioButton radioSexButton = findViewById(selectedId);
                hashMap.put("gender", radioSexButton.getText().toString());

                databaseReference.setValue(hashMap).addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        startActivity(new Intent(ProfileActivity.this, UserMainActivity.class));
                        finish();
                    }
                });


            }
        });

    }

    private void getDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
                calendar.set(year, month, dayOfMonth);
                pickdob.setText(sdf.format(calendar.getTime()));
            }
        }, year, month, day);
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();

            }
        });

    }
}
