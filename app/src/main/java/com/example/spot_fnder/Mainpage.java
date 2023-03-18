package com.example.spot_fnder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Mainpage extends AppCompatActivity {

    Button ranan;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        ranan = findViewById(R.id.ran);
        Intent intent = new Intent(Mainpage.this,rananaskatepark.class);

    }
}