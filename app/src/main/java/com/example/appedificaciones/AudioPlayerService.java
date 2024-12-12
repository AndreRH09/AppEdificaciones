package com.example.appedificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AudioPlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "AudioPlayerChannel";
    private boolean isForeground = false;
    private Handler handler = new Handler();
    private Runnable updateProgressRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa el MediaPlayer con un archivo vacío (se cargará dinámicamente más tarde)
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        String audio = intent.getStringExtra("audio");  // Obtener el nombre del archivo de audio desde el Intent


        // Verificamos si 'audio' no es null antes de proceder
        if (audio != null) {
            String nombreArchivo = audio.split("\\.")[0];  // Eliminar la extensión .mp3 si está presente
            int resourceId = getResources().getIdentifier(nombreArchivo.toLowerCase().replace(" ", "_"), "raw", getPackageName());

            if ("PLAY".equals(action)) {
                if (!mediaPlayer.isPlaying() && resourceId != 0) {
                    mediaPlayer.reset();
                    mediaPlayer = MediaPlayer.create(this, resourceId);
                    mediaPlayer.start();
                    startForegroundService();
                } else if (resourceId == 0) {
                    Log.e("AudioPlayerService", "Archivo de audio no encontrado: " + audio);
                }
            } else if ("STOP".equals(action)) {
                // Solo intentar detener el audio si el mediaPlayer está realmente en reproducción
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    stopSelf(); // Detener el servicio
                } else {
                    Log.w("AudioPlayerService", "Intento de detener el audio cuando no está en reproducción.");
                }
            }

            updateNotification();
        } else {
            Log.e("AudioPlayerService", "Error: 'audio' no se ha pasado en el Intent.");
        }

        return START_STICKY;
    }

    private void startForegroundService() {
        if (!isForeground) {
            Log.d("AudioPlayerService", "Iniciando servicio en primer plano");
            Notification notification = createNotification();


            startForeground(1,notification);
            isForeground = true;
        }
    }

    private void stopForegroundService() {
        if (isForeground) {
            Log.d("AudioPlayerService", "Deteniendo servicio en primer plano");
            stopForeground(true);
            isForeground = false;
        }
    }

    private Notification createNotification() {
        Log.d("AudioPlayerService", "Creando notificación");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Si el SDK es Oreo o superior, se debe crear un canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        int progress = (mediaPlayer.isPlaying()) ? mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration() : 0;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Reproducción de Audio")
                .setContentText("Reproduciendo audio en segundo plano")
                .setSmallIcon(R.drawable.baseline_audiotrack_24)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.baseline_stop_24, "Detener", getActionPendingIntent("STOP"))
                .addAction(R.drawable.baseline_play_arrow_24, "Reproducir", getActionPendingIntent("PLAY"))
                .setProgress(100, progress, false)  // Muestra el progreso
                .build();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PendingIntent getActionPendingIntent(String action) {
        Intent actionIntent = new Intent(this, AudioPlayerService.class);
        actionIntent.setAction(action);
        return PendingIntent.getService(
                this,
                0,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void updateNotification() {
        int progress = (mediaPlayer.isPlaying()) ? mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration() : 0;

        Notification notification = createNotification();
        startForeground(1, notification); // Actualiza la notificación
    }
}
