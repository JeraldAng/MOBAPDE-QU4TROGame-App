package com.example.qu4trogame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    private final IBinder musicBind = new MusicBinder();
    public MediaPlayer player;

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.bg_music);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void onStop() {

    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
    @Override
    public void onLowMemory() {

    }

    public class MusicBinder extends Binder {
        BackgroundSoundService getService() {
            return BackgroundSoundService.this;
        }
    }

    public MediaPlayer getPlayer(){
        return this.player;
    }

    public void pausePlayer(){
        player.pause();
    }

    public void resumePlayer() {player.start();}
}