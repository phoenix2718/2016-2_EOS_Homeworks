package com.kw.hw1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class LinearLayout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        Log.d("hw1", "onTouchEvent() >> " + ev.getAction());

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(this, RelativeLayout.class);
            startActivity(intent);
        }
        return super.onTouchEvent(ev);
    }
}
