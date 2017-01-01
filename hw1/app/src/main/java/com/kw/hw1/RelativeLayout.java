package com.kw.hw1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by whahn on 2016-09-06.
 */
public class RelativeLayout extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_layout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        Log.d("hw1", "onTouchEvent() >> " + ev.getAction());

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(this, TableLayout.class);
            startActivity(intent);
        }
        return super.onTouchEvent(ev);
    }
}
