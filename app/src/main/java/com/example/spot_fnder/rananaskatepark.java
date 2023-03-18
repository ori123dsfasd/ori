package com.example.spot_fnder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class rananaskatepark extends AppCompatActivity {
Button main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rananaskatepark);
        main = findViewById(R.id.gomain);
        Intent intent = new Intent(rananaskatepark.this,Mainpage.class);
        startActivity(intent);
    }
}