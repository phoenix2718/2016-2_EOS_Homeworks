package com.example.phoen.hw3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MyMemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_memo);

        // Get the text from an implicit intent.
        final String text = getIntent().getStringExtra(Intent.EXTRA_TEXT); // Intent.EXTRA_TEXT <-- REMIND!
        TextView textView = (TextView)findViewById(R.id.activity_my_memo_textview);
        textView.setText(text);
    }
}
