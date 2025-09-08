package com.emmanuel.chancita.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.rifa.crear_rifa.CrearRifaActivity;

public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data != null && data.getHost() != null) {
            String host = data.getHost();

            Log.d("DeepLinkActivity", "Deep link recibido: " + data.toString());

            if (host.equals("oauth-success")) {
                // Obtener el userId del parámetro step2 si existe
                String userId = data.getQueryParameter("userId");

                Intent intent = new Intent(this, CrearRifaActivity.class);
                intent.setData(data); // Pass the deep link data
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                // Si tenemos userId, podemos agregarlo como extra también
                if (userId != null) {
                    intent.putExtra("userId", userId);
                    intent.putExtra("fromOAuth", true);
                }

                startActivity(intent);

            } else if (host.equals("oauth-failure")) {
                // Manejar error de OAuth
                String error = data.getQueryParameter("error");
                Log.e("DeepLinkActivity", "OAuth failed: " + error);

                // Podrías mostrar un Toast o navegar a una pantalla de error
                Toast.makeText(this, "Error en la vinculación: " + error, Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }
}