package com.example.phoen.hw4;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class AirQualityActivity extends AppCompatActivity {
    private DataParser dataParser;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Set the data to TextViews
            TextView t = null;
            t =(TextView)findViewById(R.id.air_quality_textview_no2);
            t.setText(dataParser.getNo2());
            t =(TextView)findViewById(R.id.air_quality_textview_o3);
            t.setText(dataParser.getO3());
            t = (TextView)findViewById(R.id.air_quality_textview_co);
            t.setText(dataParser.getCo());
            t = (TextView)findViewById(R.id.air_quality_textview_so2);
            t.setText(dataParser.getSo2());
            t = (TextView)findViewById(R.id.air_quality_textview_pm10);
            t.setText(dataParser.getPm10());
            t = (TextView)findViewById(R.id.air_quality_textview_pm25);
            t.setText(dataParser.getPm25());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        new Thread() {
            public void run() {
                // From intent, get date and place
                Intent intent = getIntent();
                String date = intent.getStringExtra("DATE");
                String place = intent.getStringExtra("PLACE");

                // Get the air quality data.
                dataParser = new DataParser();
                dataParser.loadData(date, place);

                // Set the data to TextViews
                new Thread() {
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }
        }.start();
    }
}
