package com.example.cura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        if(mUser!=null)
        {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
