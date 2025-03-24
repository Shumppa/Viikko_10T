package com.example.viikko_10t;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchActivityButton = findViewById(R.id.SearchActivityButton);
        Button listInfoActivtyButton = findViewById(R.id.ListInfoActivtyButton);

        searchActivityButton.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        listInfoActivtyButton.setOnClickListener(v -> startActivity(new Intent(this, ListInfoActivity.class)));
        };
}
