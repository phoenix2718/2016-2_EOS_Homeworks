package com.example.phoen.hw5;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String musicFileName = "mymusic.mp3";
    private String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/"+ musicFileName;
    private boolean onStarted = false;
    private boolean onPlaying = false;
    private IMusicPlayService musicPlayServiceBinder = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service ) {
            Log.d( "hw5", "onServiceConnected()");
            musicPlayServiceBinder = IMusicPlayService.Stub.asInterface(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d( "hw5", "onServiceDisconnected()");
        }
    };
    private final BroadcastReceiver musicPlayerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "com.example.phoen.hw5.Broadcasting.action.MUSIC_PAUSE_RESUME":
                    pause();
                    break;
                case "com.example.phoen.hw5.Broadcasting.action.MUSIC_BACK":
                    back();
                    break;
                case "com.example.phoen.hw5.Broadcasting.action.MUSIC_STOP":
                    stop();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.phoen.hw5.Broadcasting.action.MUSIC_PAUSE_RESUME");
        intentFilter.addAction("com.example.phoen.hw5.Broadcasting.action.MUSIC_BACK");
        intentFilter.addAction("com.example.phoen.hw5.Broadcasting.action.MUSIC_STOP");
        registerReceiver(musicPlayerReceiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(musicPlayerReceiver);
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        // To make back key like home key
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.activitiy_imageview_play:
                play();

                break;
            case R.id.activitiy_imageview_stop:
                stop();

                break;
            case R.id.activitiy_imageview_pause:
                pause();

                break;
            case R.id.activitiy_imageview_back:
                back();

                break;
        }
    }

    private void play() {
        if(onStarted) {
            return;
        }
        Intent serviceIntent = new Intent(this, MusicPlayService.class);
        serviceIntent.putExtra("MUSIC_FILE_NAME", musicFileName);
        serviceIntent.putExtra("FULL_PATH", fullPath);
        startService(serviceIntent);
        onStarted = true;
        onPlaying = true;
        ((TextView)findViewById(R.id.activitiy_textview_file_name)).setText(musicFileName);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
    }

    private void stop() {
        if(!onStarted) {
            return;
        }
        Intent serviceIntent = new Intent(this, MusicPlayService.class);
        unbindService(serviceConnection);
        stopService(serviceIntent);
        onStarted = false;
        onPlaying = false;
        ((ImageView)findViewById(R.id.activitiy_imageview_pause)).setImageResource(R.drawable.pause);
        ((TextView)findViewById(R.id.activitiy_textview_file_name)).setText(R.string.no_file_playing);
        Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
    }

    private void pause() {
        if(!onStarted) {
            return;
        }
        if(onPlaying) {
            try {
                musicPlayServiceBinder.pause();
                onPlaying = false;
                ((ImageView)findViewById(R.id.activitiy_imageview_pause)).setImageResource(R.drawable.play);
                Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                Log.d( "hw5", "Error on musicPlayServiceBinder.pause()");
            }
        } else {
            try {
                musicPlayServiceBinder.resume();
                onPlaying = true;
                ((ImageView)findViewById(R.id.activitiy_imageview_pause)).setImageResource(R.drawable.pause);
                Toast.makeText(getApplicationContext(), "Resume", Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                Log.d( "hw5", "Error on musicPlayServiceBinder.resume()");
            }
        }
    }

    private void back() {
        if(!onStarted) {
            return;
        }
        Intent serviceIntent = new Intent(this, MusicPlayService.class);
        try {
            musicPlayServiceBinder.back();
            Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            Log.d( "hw5", "Error on musicPlayServiceBinder.back()");
        }
    }

/*    private boolean isStarted()
    {
        boolean result = false;
        try {
            result = musicPlayServiceBinder.isStarted();
        } catch (RemoteException e){

        }
        return result;
    }

    private boolean isPlaying()
    {
        boolean result = false;
        try {
            result = musicPlayServiceBinder.isPlaying();
        } catch (RemoteException e){

        }
        return result;
    }*/
}
