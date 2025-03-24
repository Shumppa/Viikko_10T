package com.example.viikko_10t;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


public class SearchActivity extends AppCompatActivity {

    private Button listInfoButton, searchButton, back;
    private EditText cityNameEdit, yearEdit;
    private TextView statusText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cityNameEdit = findViewById(R.id.CityNameEdit);
        yearEdit = findViewById(R.id.YearEdit);
        statusText = findViewById(R.id.StatusText);
        searchButton = findViewById(R.id.SearchButton);
        listInfoButton = findViewById(R.id.ListInfoActivityButton);
        back = findViewById(R.id.Return1);

        back.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        searchButton.setOnClickListener(v -> startSearch());
        listInfoButton.setOnClickListener(v -> startActivity(new Intent(SearchActivity.this, ListInfoActivity.class)));
    }
}
