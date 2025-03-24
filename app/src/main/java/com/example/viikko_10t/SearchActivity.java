package com.example.viikko_10t;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SearchActivity extends AppCompatActivity {
    private EditText cityNameEdit, yearEdit;
    private TextView statusText;
    private Button searchButton, listInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cityNameEdit = findViewById(R.id.CityNameEdit);
        yearEdit = findViewById(R.id.YearEdit);
        statusText = findViewById(R.id.StatusText);
        searchButton = findViewById(R.id.SearchButton);
        listInfoButton = findViewById(R.id.ListInfoActivityButton);

        searchButton.setOnClickListener(v -> searchData());

        listInfoButton.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, ListInfoActivity.class));
        });
    }

    private void searchData() {
        String city = cityNameEdit.getText().toString().trim();
        String yearStr = yearEdit.getText().toString().trim();

        if (city.isEmpty() || yearStr.isEmpty()) {
            statusText.setText("Haku epäonnistui: Syötä kaupunki ja vuosi!");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            statusText.setText("Haku epäonnistui: Vuosi ei ole numero!");
            return;
        }

        statusText.setText("Haetaan...");
        new FetchDataTask().execute(city, String.valueOf(year));
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {
        private String city;
        private int year;

        @Override
        protected String doInBackground(String... params) {
            city = params[0];
            year = Integer.parseInt(params[1]);

            try {
                String areaCode = getAreaCode(city);
                if (areaCode == null) {
                    return "Haku epäonnistui: Kaupunkia ei löydy!";
                }

                URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInput = "{ \"query\": [ " +
                        "{ \"code\": \"Ajoneuvoluokka\", \"selection\": { \"filter\": \"item\", \"values\": [ \"01\", \"02\", \"03\", \"04\", \"05\" ] } }, " +
                        "{ \"code\": \"Liikennekäyttö\", \"selection\": { \"filter\": \"item\", \"values\": [ \"0\" ] } }, " +
                        "{ \"code\": \"Vuosi\", \"selection\": { \"filter\": \"item\", \"values\": [ \"" + year + "\" ] } }, " +
                        "{ \"code\": \"Alue\", \"selection\": { \"filter\": \"item\", \"values\": [ \"" + areaCode + "\" ] } } " +
                        "], \"response\": { \"format\": \"json-stat2\" } }";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Haku epäonnistui: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Haku epäonnistui")) {
                statusText.setText(result);
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray values = jsonResponse.getJSONObject("value").getJSONArray("values");
                JSONArray categories = jsonResponse.getJSONObject("dimension").getJSONObject("Ajoneuvoluokka")
                        .getJSONObject("category").getJSONArray("label");

                CarDataStorage storage = CarDataStorage.getInstance();
                storage.setCity(city);
                storage.setYear(year);
                storage.clearData();

                int total = 0;
                for (int i = 0; i < values.length(); i++) {
                    int amount = values.getInt(i);
                    String type = categories.getString(i);
                    storage.addCarData(new CarData(type, amount));
                    total += amount;
                }

                statusText.setText("Haku onnistui! Ajoneuvoja: " + total);
            } catch (Exception e) {
                statusText.setText("Haku epäonnistui: Virhe tietojen käsittelyssä.");
                e.printStackTrace();
            }
        }

        private String getAreaCode(String city) {
            switch (city.toLowerCase()) {
                case "helsinki": return "091";
                case "espoo": return "049";
                case "tampere": return "837";
                case "oulu": return "564";
                case "turku": return "853";
                default: return null;
            }
        }
    }
}