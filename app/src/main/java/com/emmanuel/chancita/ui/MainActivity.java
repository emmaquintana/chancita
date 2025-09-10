package com.emmanuel.chancita.ui;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.dto.RifaDTO;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.emmanuel.chancita.ui.rifa.RifaOrganizadorViewModel;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.emmanuel.chancita.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private SharedViewModel sharedViewModel;
    private MaterialToolbar toolbar;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si venimos de un deep link
        checkDeepLinkIntent(getIntent());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtiene una referencia al NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        // Conecta el BottomNavigationView con el NavController
        NavigationUI.setupWithNavController(binding.navView, navController);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Obtén el ViewModel compartido entre la Activity y los Fragmentos
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Observa el LiveData del título
        sharedViewModel.getToolbarTitle().observe(this, title -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        });

        /*
        RifaOrganizadorViewModel rifaOrganizadorViewModel = new ViewModelProvider(this).get(RifaOrganizadorViewModel.class);

        List<RifaPremio> premios = new ArrayList<>();
        premios.add(new RifaPremio(Utilidades.generarCodigo(10,10), "Smartphone", "Un celular de última generación", 2));
        premios.add(new RifaPremio(Utilidades.generarCodigo(10,10), "Smartphone", "Un celular de última generación", 2));
        premios.add(new RifaPremio(Utilidades.generarCodigo(10,10), "Smartphone", "Un celular de última generación", 2));


        RifaDTO nuevaRifa = new RifaDTO(
                null, // id (lo genera Firestore en el DAO)
                "Rifa de Tecnología", // titulo
                "Participa para ganar premios tecnológicos", // descripcion
                100, // cantidad de números
                "usuario123", // creadoPor (id del usuario actual)
                RifaEstado.ABIERTO, // estado
                "TECNO2026", // código de rifa
                MetodoEleccionGanador.DETERMINISTA, // método de elección del ganador
                "El número más alto será el ganador", // motivo (si aplica)
                LocalDateTime.now().plusDays(7), // fecha del sorteo
                10.0, // precio del número
                LocalDateTime.now(), // creadoEn
                premios, // lista de premios,
                new ArrayList<Integer>()
        );

        rifaOrganizadorViewModel.crearRifa(nuevaRifa);
*/
// Solicitar permiso de notificaciones si es Android 13+
        // Solicitar permiso de notificaciones si es Android 13+
        solicitarPermisoNotificaciones();

        // Inicializar Firebase Messaging
        inicializarFirebaseMessaging();
    }

    private void solicitarPermisoNotificaciones() {
        // Solo para Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Solicitar el permiso
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void inicializarFirebaseMessaging() {
        // Obtener el token FCM
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Error al obtener token FCM", task.getException());
                            return;
                        }

                        // Obtener el token
                        String token = task.getResult();
                        Log.d("FCM", "Token FCM: " + token);

                        // Guardar el token en Firestore
                        guardarTokenFCM(token);
                    }
                });

        // Crear canal de notificaciones para Android 8+
        crearCanalNotificaciones();
    }

    private void guardarTokenFCM(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(user.getUid())
                    .update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FCM", "Token FCM guardado exitosamente");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM", "Error al guardar token FCM", e);
                    });
        }
    }

    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "rifas_channel",
                    "Notificaciones de Rifas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones sobre rifas y sorteos");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkDeepLinkIntent(intent);
    }

    private void checkDeepLinkIntent(Intent intent) {
        if (intent != null) {
            if (intent.getBooleanExtra("pagoExitoso", false)) {
                String preferenceId = intent.getStringExtra("preferenceId");
                showPagoExitosoDialog(preferenceId);
            } else if (intent.getBooleanExtra("pagoFallido", false)) {
                showPagoFallidoDialog();
            } else if (intent.getBooleanExtra("pagoPendiente", false)) {
                showPagoPendienteDialog();
            }
        }
    }

    private void showPagoExitosoDialog(String preferenceId) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("✅ Pago Exitoso")
                .setMessage("Tu compra se realizó correctamente. Los números han sido reservados para ti.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void showPagoFallidoDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("❌ Pago Fallido")
                .setMessage("El pago no pudo procesarse. Por favor, intenta nuevamente.")
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void showPagoPendienteDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("⏳ Pago Pendiente")
                .setMessage("Estamos procesando tu pago. Te notificaremos cuando se complete.")
                .setPositiveButton("Aceptar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            // Cierra la sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // Borra la preferencia de usuario
            SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();

            // Navega a la actividad de autenticación
            Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}