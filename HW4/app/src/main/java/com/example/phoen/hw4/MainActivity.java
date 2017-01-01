package com.example.phoen.hw4;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onClick(View v) {
        Intent intent = null;
        ComponentName componentName;
        switch(v.getId()) {
            case R.id.main_button_air_quality:
                // Execute AirQualityActivity

                EditText editTextAirQualityDate = (EditText)findViewById(R.id.main_edittext_air_quality_date);
                EditText editTextAirQualityPlace = (EditText)findViewById(R.id.main_edittext_air_quality_place);
                if(editTextAirQualityDate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "날짜를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextAirQualityPlace.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "지역명 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent();
                intent.putExtra("DATE", DateParser.parse(editTextAirQualityDate.getText().toString()));
                intent.putExtra("PLACE", editTextAirQualityPlace.getText().toString());
                componentName = new ComponentName(
                        "com.example.phoen.hw4",
                        "com.example.phoen.hw4.AirQualityActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                break;
            case R.id.main_button_fine_dust:
                // Execute FineDustActivity
                EditText editTextFineDustPlace = (EditText)findViewById(R.id.main_edittext_fine_dust_place);
                if(editTextFineDustPlace.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "지역명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent();
                intent.putExtra("PLACE", editTextFineDustPlace.getText().toString());
                componentName = new ComponentName(
                        "com.example.phoen.hw4",
                        "com.example.phoen.hw4.FineDustActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                break;
            default:
                // empty
                break;
        }
    }
}
