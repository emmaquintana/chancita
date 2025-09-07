package com.emmanuel.chancita.ui.rifa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.SharedViewModel;

public class RifaOrganizadorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rifa_organizador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el rifa_id del Intent
        String rifaId = getIntent().getStringExtra("rifa_id");
        Log.d("RifaParticipanteActivity", "rifaId recibido: " + rifaId);

        if (rifaId == null || rifaId.trim().isEmpty()) {
            Toast.makeText(this, "Error: No se pudo cargar la rifa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el SharedViewModel con el rifaId
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setRifaId(rifaId);

        Log.d("RifaParticipanteActivity", "rifaId configurado en SharedViewModel: " + rifaId);
    }
}