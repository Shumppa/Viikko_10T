package com.example.viikko_10t;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class ListInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_info);

        TextView cityText = findViewById(R.id.CityText);
        TextView yearText = findViewById(R.id.YearText);
        TextView carInfoText = findViewById(R.id.CarInfoText);
        Button back = findViewById(R.id.Return2);

        back.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));


        CarDataStorage storage = CarDataStorage.getInstance();
        cityText.setText("Kaupunki: " + storage.getCity());
        yearText.setText("Vuosi: " + storage.getYear());

        StringBuilder carInfo = new StringBuilder();
        ArrayList<CarData> carDataList = storage.getCarData();
        int total = 0;

        for (CarData data : carDataList) {
            carInfo.append(data.getType()).append(": ").append(data.getAmount()).append("\n");
            total += data.getAmount();
        }

        carInfo.append("\nKokonaismäärä: ").append(total);
        carInfoText.setText(carInfo.toString());
        };
}
