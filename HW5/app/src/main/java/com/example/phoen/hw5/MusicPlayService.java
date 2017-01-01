package com.example.phoen.hw5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by phoen on 12/17/2016.
 */
public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener {
    private MediaPlayer musicPlayer = null;
    private boolean onStarted = false;
    private boolean onPlaying = false;
    private Thread progressBarThread = null;
    private ProgressBar progressBar = null;
    private String musicFileName = null;
    private String fullPath = null;
    private int notificationId = 1;
    private Object lock= new Object();
    private Handler handler = new Handler();
    IMusicPlayService.Stub musicPlayServiceBinder = new IMusicPlayService.Stub() {
        @Override
        public void pause() {
            pauseMusicPlayer();
        }

        @Override
        public void resume() {
            resumeMusicPlayer();
        }

        @Override
        public void back() {
            backMusicPlayer();
        }

        @Override
        public boolean isPlaying() {
            return musicPlayer.isPlaying();
        }

        @Override
        public boolean isStarted() {
            if(musicPlayer.getCurrentPosition() > 0) {
                return true;
            } else {
                return false;
            }
        }
    };

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("hw5", "onStartCommand()");

        musicFileName = intent.getStringExtra("MUSIC_FILE_NAME");
        fullPath = intent.getStringExtra("FULL_PATH");

        if(musicPlayer == null) {
            musicPlayer = new MediaPlayer();
            try {
                musicPlayer.setDataSource(fullPath);
            } catch (Exception e) {
                Log.d("tag", "musicPlayer.setDataSource error!");
            }

            musicPlayer.setOnPreparedListener(this);
            musicPlayer.prepareAsync();
        }
        return START_STICKY;
    }

    public void onDestroy() {
        Log.i("hw5", "onDestroy()");
        //progressBarThread.interrupt(); // It should be before musicPlayer.release();
        synchronized (lock) {
            if(musicPlayer != null) {
                musicPlayer.release();
            }
            onStarted = false;
            onPlaying = false;
        }
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
//        unregisterReceiver(musicPlayerReceiver);

        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("superdroid", "onBind()"+ intent);
        return musicPlayServiceBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.i("superdroid", "onUnbind()");
        return super.onUnbind(intent);
    }

    public void onPrepared(MediaPlayer  player) {
        synchronized (lock) {
            musicPlayer.start();
            onStarted = true;
            onPlaying = true;
        }
        sendNotification(R.integer.play);
//        Toast.makeText(getApplicationContext(),
//                "Before Thread",
//                Toast.LENGTH_SHORT).show();
        progressBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(onStarted) {
                    handler.post(new Runnable() {
                       public void run() {
                           if(onPlaying) {
                               sendNotification(R.integer.play);
                           } else {
                               sendNotification(R.integer.pause);
                           }
                       }
                    });
                    SystemClock.sleep(500); // 0.5 sec = 500 msec
                }
            }
        });
        progressBarThread.start();
    }

    private void sendNotification(int state) {
        // Set two remote views: a normal one and a big one.
        RemoteViews remoteViewNormal = new RemoteViews(getPackageName(),
                R.layout.notification_music_player_normal);
        RemoteViews remoteViewBig = new RemoteViews(getPackageName(),
                R.layout.notification_music_player_big);
        remoteViewNormal.setTextViewText(R.id.noti_normal_textview_music_title, musicFileName);
        remoteViewBig.setTextViewText(R.id.noti_big_textview_file_name, musicFileName);
        if (onStarted) {
            remoteViewBig.setProgressBar(R.id.noti_big_progressbar,
                    musicPlayer.getDuration(), musicPlayer.getCurrentPosition(), false);
        }
        // Set the action of click on pause icon
        Intent pauseResumeIntent = new Intent("com.example.phoen.hw5.Broadcasting.action.MUSIC_PAUSE_RESUME");
        PendingIntent pendingPauseResumeIntent = PendingIntent.getBroadcast(
                getBaseContext(), 0, pauseResumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViewBig.setOnClickPendingIntent(R.id.noti_big_imageview_pause, pendingPauseResumeIntent);

        // Set the action of click on back icon
        Intent backIntent = new Intent("com.example.phoen.hw5.Broadcasting.action.MUSIC_BACK");
        PendingIntent pendingBackIntent = PendingIntent.getBroadcast(
                getBaseContext(), 0, backIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViewBig.setOnClickPendingIntent(R.id.noti_big_imageview_back, pendingBackIntent);

        // Set the action of click on stop icon
        Intent stopIntent = new Intent("com.example.phoen.hw5.Broadcasting.action.MUSIC_STOP");
        PendingIntent pendingStopIntent = PendingIntent.getBroadcast(
                getBaseContext(), 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViewBig.setOnClickPendingIntent(R.id.noti_big_imageview_stop, pendingStopIntent);

        // Set the action of click on Elsewhere

        Intent actionIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        Notification.Builder notiBuilder = new Notification.Builder(
                getApplicationContext());
        switch(state) {
            case R.integer.play:
                notiBuilder.setSmallIcon(R.drawable.play);
                remoteViewBig.setImageViewResource(R.id.noti_big_imageview_pause, R.drawable.pause);
                break;
            case R.integer.pause:
                notiBuilder.setSmallIcon(R.drawable.pause);
                remoteViewBig.setImageViewResource(R.id.noti_big_imageview_pause, R.drawable.play);
                break;
        }
        notiBuilder.setOngoing(true);
        notiBuilder.setContentIntent(pi);
        Notification noti = notiBuilder.build();
        noti.contentView = remoteViewNormal;
        noti.bigContentView = remoteViewBig;

        // Notify
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, noti);
    }

    private void pauseMusicPlayer() {
//        try {
//            synchronized (progressBarThread) {
//                progressBarThread.wait();
//            }
//        }
//        catch (InterruptedException e) {
//
//        }
        synchronized (lock) {
            musicPlayer.pause();
            onPlaying = false;
        }
        sendNotification(R.integer.pause);
    }

    private void resumeMusicPlayer() {
        synchronized (lock) {
            musicPlayer.start();
            onPlaying = true;
        }
//        synchronized (progressBarThread) {
//            progressBarThread.notify();
//        }
        sendNotification(R.integer.play);
    }

    private void backMusicPlayer() {
        int seekTerm = 10000; // 10 sec. = 10 000 msec.
        if(musicPlayer.getCurrentPosition() < seekTerm) {
            musicPlayer.seekTo(0);
        } else {
            musicPlayer.seekTo(musicPlayer.getCurrentPosition() - seekTerm);
        }
    }
}
