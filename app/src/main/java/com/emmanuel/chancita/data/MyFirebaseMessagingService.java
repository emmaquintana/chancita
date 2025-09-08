package com.emmanuel.chancita.data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.time.LocalDateTime;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "rifas_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FCM", "Mensaje recibido de: " + remoteMessage.getFrom());

        // Verificar si hay payload de notificación
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d("FCM", "Título: " + title);
            Log.d("FCM", "Cuerpo: " + body);

            mostrarNotificacion(title, body);
        }

        // Verificar si hay datos adicionales
        if (!remoteMessage.getData().isEmpty()) {
            Log.d("FCM", "Datos del mensaje: " + remoteMessage.getData());
            // Puedes procesar datos adicionales aquí si es necesario
        }
    }

    private void mostrarNotificacion(String title, String body) {
        try {
            // Intent para abrir la app al hacer clic en la notificación
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );

            // Configurar notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification) // Asegúrate de que este icono existe
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{0, 500, 1000})
                    .setLights(Color.BLUE, 3000, 3000);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Crear canal de notificaciones para Android 8+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Notificaciones de Rifas",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notificaciones sobre rifas y sorteos");
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 500, 1000});
                channel.setShowBadge(true);

                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            // Mostrar la notificación
            if (manager != null) {
                int notificationId = (int) System.currentTimeMillis();
                manager.notify(notificationId, builder.build());
                Log.d("FCM", "Notificación mostrada con ID: " + notificationId);
            }

        } catch (Exception e) {
            Log.e("FCM", "Error al mostrar notificación: " + e.getMessage(), e);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "Nuevo token recibido: " + token);

        // Guardar el nuevo token en Firestore
        guardarTokenEnFirestore(token);
    }

    private void guardarTokenEnFirestore(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(user.getUid())
                    .update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FCM", "Token actualizado en Firestore");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM", "Error al actualizar token en Firestore", e);
                    });
        } else {
            Log.w("FCM", "Usuario no autenticado, no se puede guardar el token");
        }
    }
}