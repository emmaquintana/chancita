package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.emmanuel.chancita.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CrearRifaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;
    private boolean navigationHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_rifa);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Esperar a que la navegación esté lista antes de procesar intents
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Solo procesar en el primer destino (paso 1)
                if (destination.getId() == R.id.crearRifaPaso1Fragment) {
                    handleNavigationLogic();
                }
            });
        }
    }

    private void handleNavigationLogic() {
        // Primero verificar si es un deep link de OAuth success
        if (handleDeepLink()) {
            return;
        }

        // Luego verificar si es un usuario que ya tiene token
        if (getIntent().getBooleanExtra("startStep2", false)) {
            navigateToStep2();
            return;
        }

        // Finalmente, verificar en Firestore si el usuario ya tiene token
        checkTokenInFirestore();
    }

    private boolean handleDeepLink() {
        Uri data = getIntent().getData();
        if (data != null && data.getHost() != null && data.getHost().equals("oauth-success")) {
            Log.d("DeepLink", "OAuth success detected, navigating to step 2");
            navigateToStep2();
            return true;
        }
        return false;
    }

    private void checkTokenInFirestore() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("mercado_pago_tokens").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("TokenCheck", "Usuario tiene token, navegando a paso 2");
                        navigateToStep2();
                    } else {
                        Log.d("TokenCheck", "Usuario no tiene token, permanece en paso 1");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TokenCheck", "Error al verificar token", e);
                    // En caso de error, el usuario permanece en paso 1
                });
    }

    private void navigateToStep2() {
        if (navController != null &&
                navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.crearRifaPaso1Fragment) {

            navController.navigate(R.id.action_crearRifaPaso1Fragment_to_crearRifaPaso2Fragment);
        }
    }
}