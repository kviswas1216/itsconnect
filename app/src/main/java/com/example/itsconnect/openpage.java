package com.example.itsconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.itsconnect.utilities.Firebaseutil;

public class openpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openpage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Firebaseutil.isLoggedIn()) {
                    Intent intent = new Intent(openpage.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(openpage.this, loginp.class);
                    startActivity(intent);
                }
                finish();
            }
        },1000);

    }

}