package com.emmanuel.chancita.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.repository.RifaRepository;
import com.emmanuel.chancita.ui.rifa.RifaParticipanteActivity;
import com.emmanuel.chancita.ui.rifa.crear_rifa.CrearRifaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data != null && data.getHost() != null) {
            String host = data.getHost();

            Log.d("DeepLinkActivity", "Deep link recibido: " + data.toString());

            if (host.equals("oauth-success")) {
                // Manejar éxito de OAuth
                handleOAuthSuccess(data);
                finish();

            } else if (host.equals("oauth-failure")) {
                // Manejar error de OAuth
                handleOAuthFailure(data);
                finish();

            } else if (host.equals("pago-exitoso")) {
                // Pago exitoso
                handlePagoExitoso(data);
                finish();

            } else if (host.equals("pago-fallido")) {
                // Pago fallido
                handlePagoFallido(data);
                finish();

            } else if (host.equals("pago-pendiente")) {
                // Pago pendiente
                handlePagoPendiente(data);
                finish();
            } else if (host.equals("rifa")) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String idRifa = data.getQueryParameter("rifa_id");
                    if (idRifa != null) {
                        RifaRepository rifaRepository = new RifaRepository();
                        rifaRepository.obtenerRifa(idRifa, task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    String codigoRifa = snapshot.getString("codigo");

                                    // Llamas tu función con el código
                                    unirseARifa(codigoRifa);

                                    Intent intent = new Intent(this, RifaParticipanteActivity.class);
                                    intent.putExtra("rifa_id", idRifa);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                // Manejar error
                                Log.e("Rifa", "No se pudo obtener la rifa", task.getException());
                                finish();
                            }
                        });
                    }
                    else {
                        finish();
                    }
                }
                else {
                    Intent intent = new Intent(this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }
        else {
            finish();
        }
    }

    private void handleOAuthSuccess(Uri data) {
        String userId = data.getQueryParameter("userId");

        Intent intent = new Intent(this, CrearRifaActivity.class);
        intent.setData(data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (userId != null) {
            intent.putExtra("userId", userId);
            intent.putExtra("fromOAuth", true);
        }

        startActivity(intent);
        Toast.makeText(this, "✅ Cuenta vinculada exitosamente", Toast.LENGTH_LONG).show();
    }

    private void handleOAuthFailure(Uri data) {
        String error = data.getQueryParameter("error");
        Log.e("DeepLinkActivity", "OAuth failed: " + error);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        Toast.makeText(this, "❌ Error en vinculación: " + error, Toast.LENGTH_LONG).show();
    }

    private void handlePagoExitoso(Uri data) {
        Log.d("DeepLinkActivity", "Pago exitoso");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("pagoExitoso", true);

        String preferenceId = data.getQueryParameter("preference_id");
        String paymentId = data.getQueryParameter("payment_id");

        if (preferenceId != null) {
            intent.putExtra("preferenceId", preferenceId);
        }
        if (paymentId != null) {
            intent.putExtra("paymentId", paymentId);
        }

        startActivity(intent);

        // Mostrar notificación
        showPaymentNotification("✅ Pago exitoso", "Tu compra se realizó correctamente");
    }

    private void handlePagoFallido(Uri data) {
        Log.e("DeepLinkActivity", "Pago fallido");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("pagoFallido", true);
        startActivity(intent);

        showPaymentNotification("❌ Pago fallido", "El pago no pudo procesarse");
    }

    private void handlePagoPendiente(Uri data) {
        Log.d("DeepLinkActivity", "Pago pendiente");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("pagoPendiente", true);
        startActivity(intent);

        showPaymentNotification("⏳ Pago pendiente", "Estamos procesando tu pago");
    }

    private void showPaymentNotification(String title, String message) {
        // Crear notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "pago_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "pago_channel",
                    "Pagos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void unirseARifa(String codigo) {
        RifaRepository rifaRepository = new RifaRepository();
        rifaRepository.unirseARifa(codigo, null);
    }
}