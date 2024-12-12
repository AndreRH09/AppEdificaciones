package com.example.appedificaciones;

import android.content.Context;
import android.content.Intent;

public class AudioPlayer {

    private Context context;

    public AudioPlayer(Context context) {
        this.context = context;
    }

    public void play() {
        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.setAction("PLAY");
        context.startService(intent);
    }

    public void pause() {
        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.setAction("PAUSE");
        context.startService(intent);
    }

    public void stop() {
        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.setAction("STOP");
        context.startService(intent);
    }

    public void seek(int position) {
        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.setAction("SEEK");
        intent.putExtra("seek_position", position);
        context.startService(intent);
    }
}