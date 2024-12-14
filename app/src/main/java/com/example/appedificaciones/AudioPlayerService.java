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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AudioPlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "AudioPlayerChannel";
    private boolean isForeground = false;
    private Handler handler = new Handler();
    private Runnable updateProgressRunnable;
    private String audio;
    private int lastPosition = 0; // Posición actual del audio
    private boolean isPause =false;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa el MediaPlayer con un archivo vacío (se cargará dinámicamente más tarde)
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false); // No repetir por defecto

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        audio = intent.getStringExtra("audio");  // Obtener el nombre del archivo de audio desde el Intent

        // Verificamos si 'audio' no es null antes de proceder
        if (audio != null) {
            String nombreArchivo = audio.split("\\.")[0];  // Eliminar la extensión .mp3 si está presente
            int resourceId = getResources().getIdentifier(nombreArchivo.toLowerCase().replace(" ", "_"), "raw", getPackageName());

            if ("PLAY".equals(action)) {
                if (!mediaPlayer.isPlaying() && resourceId != 0) {
                    //mediaPlayer.reset();
                    mediaPlayer = MediaPlayer.create(this, resourceId);

                    if (lastPosition > 0) {
                        mediaPlayer.seekTo(lastPosition); // Reanudar desde la última posición
                    }
                    mediaPlayer.start();
                    // Establecer el OnCompletionListener
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            lastPosition = 0; // Reiniciar la posición cuando el audio termine
                        }
                    });

                    startProgressUpdate();
                    isPause = false;
                } else if (resourceId == 0) {
                    Log.e("AudioPlayerService", "Archivo de audio no encontrado: " + audio);
                }
            }else if ("PAUSE".equals(action)) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    lastPosition = mediaPlayer.getCurrentPosition(); // Guardar posición actual
                    stopProgressUpdate();
                    isPause = true;
                }
            } else if ("STOP".equals(action)) {
                mediaPlayer.stop();
                stopSelf(); // Detener el servicio
                stopProgressUpdate();
                lastPosition = 0; // Reiniciar la posición
                isPause = false;
            } else if ("START_FOREGROUND".equals(action)) {
                startForegroundService();
            } else if ("STOP_FOREGROUND".equals(action)) {
                stopForegroundService();
            } else if ("SEEK".equals(action)) {

                int seekPosition = intent.getIntExtra("seek_position", 0);
                Log.d("SEEK", "cambio de posicion "+ seekPosition);
                lastPosition = seekPosition * mediaPlayer.getDuration() / 100;
                mediaPlayer.seekTo(seekPosition * mediaPlayer.getDuration() / 100); // Calcular la posición proporcional

            }

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
        Intent notificationIntent = new Intent(this, HomeActivity.class);
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
                .addAction(R.drawable.baseline_play_arrow_24, "Play", getActionPendingIntent("PLAY"))
                .addAction(R.drawable.baseline_pause_24, "Pause", getActionPendingIntent("PAUSE"))
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
        actionIntent.putExtra("audio", audio);

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

    private void startProgressUpdate() {
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    // Progreso del audio como porcentaje
                    int progress = mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration();

                    // Calcular tiempo transcurrido (passTime)
                    int currentPosition = mediaPlayer.getCurrentPosition(); // En milisegundos
                    String passTime = formatTime(currentPosition); // Convertir a formato "mm:ss"

                    // Calcular duración total (dueTime)
                    int totalDuration = mediaPlayer.getDuration(); // En milisegundos
                    String dueTime = formatTime(totalDuration); // Convertir a formato "mm:ss"

                    // Enviar los tiempos al MainActivity
                    sendProgressToActivity(progress, passTime, dueTime);
                    if(isForeground){
                        updateNotification();
                    }

                    // Actualizar cada segundo
                    handler.postDelayed(this, 250);
                }
            }
        };
        handler.post(updateProgressRunnable);
    }

    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void sendProgressToActivity(int progress, String passTime, String dueTime) {
        Intent intent = new Intent("AUDIO_PROGRESS");
        intent.putExtra("progress", progress);
        intent.putExtra("passTime", passTime);
        intent.putExtra("dueTime", dueTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void stopProgressUpdate() {
        if (updateProgressRunnable != null) {
            handler.removeCallbacks(updateProgressRunnable);
        }
    }
}
