package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CrearRifaPaso1Fragment extends Fragment {

    private NavController navController;
    private FirebaseFirestore db;
    private boolean tokenCheckCompleted = false;

    public static CrearRifaPaso1Fragment newInstance() {
        return new CrearRifaPaso1Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.crear_rifa_paso_1_btn_continuar);

        // Solo verificar token si no se ha completado antes
        if (!tokenCheckCompleted) {
            checkExistingToken();
        }

        btnContinuar.setOnClickListener(v -> {
            initiateMercadoPagoOAuth();
        });
    }

    private void checkExistingToken() {
        if (tokenCheckCompleted) {
            return; // Ya se verificó antes
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("mercado_pago_tokens").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    tokenCheckCompleted = true;
                    if (documentSnapshot.exists() && isAdded() &&
                            navController.getCurrentDestination() != null &&
                            navController.getCurrentDestination().getId() == R.id.crearRifaPaso1Fragment) {

                        Log.d("Paso1Fragment", "Usuario ya tiene token, navegando automáticamente a paso 2");

                        // Navegar al paso 2 y limpiar el back stack para evitar regresar al paso 1
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.crearRifaPaso1Fragment, true)
                                .build();
                        navController.navigate(R.id.action_crearRifaPaso1Fragment_to_crearRifaPaso2Fragment, null, navOptions);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Paso1Fragment", "Error verificando token", e);
                    tokenCheckCompleted = true;
                });
    }

    private void initiateMercadoPagoOAuth() {
        String clientId = "4389095152560547";
        String redirectUri = "https://us-central1-chancita-rifas.cloudfunctions.net/mpAuthCallback";
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String url = "https://auth.mercadopago.com.ar/authorization" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&platform_id=mp" +
                "&state=" + userId +
                "&redirect_uri=" + redirectUri +
                "&scope=read,write";

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Solo verificar si aún no se ha completado y estamos en el paso 1
        if (!tokenCheckCompleted && isAdded() &&
                navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.crearRifaPaso1Fragment) {
            checkExistingToken();
        }
    }
}