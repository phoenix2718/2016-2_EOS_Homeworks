package com.example.phoen.hw4;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FineDustActivity extends AppCompatActivity {
    private DataParser dataParser;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Set the data to TextViews
            TextView t = null;
            t = (TextView)findViewById(R.id.fine_dust_textview_pm10);
            t.setText(dataParser.getPm10());
            t = (TextView)findViewById(R.id.fine_dust_textview_pm25);
            t.setText(dataParser.getPm25());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_dust);

        new Thread() {
            public void run() {
                // From intent, get date and place
                Intent intent = getIntent();
                String place = intent.getStringExtra("PLACE");

                // Get the air quality data.
                dataParser = new DataParser();
                dataParser.loadData("today", place);

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
