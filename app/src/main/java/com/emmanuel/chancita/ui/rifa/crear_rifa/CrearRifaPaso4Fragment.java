package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class CrearRifaPaso4Fragment extends Fragment {

    private CrearRifaSharedViewModel sharedViewModel;
    private NavController navController;

    // Views
    private RadioGroup rgMetodoSeleccion;
    private RadioButton rbAleatorio;
    private RadioButton rbDeterminista;
    private MaterialButton btnContinuar;
    private LinearLayout infoMetodos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso4, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(CrearRifaSharedViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupContinueButton();
        observeViewModel();

        infoMetodos.setOnClickListener(v -> showMetodosInfoDialog());
    }

    private void initViews(View view) {
        rgMetodoSeleccion = view.findViewById(R.id.crear_rifa_paso_4_rg_metodo_seleccion);
        rbAleatorio = view.findViewById(R.id.crear_rifa_paso_4_rb_aleatorio);
        rbDeterminista = view.findViewById(R.id.crear_rifa_paso_4_rb_determinista);
        btnContinuar = view.findViewById(R.id.crear_rifa_paso_4_btn_continuar);
        infoMetodos = view.findViewById(R.id.crear_rifa_paso_4_ll_info_metodos);
    }

    private void setupContinueButton() {
        btnContinuar.setOnClickListener(v -> {
            MetodoEleccionGanador metodoSeleccionado = getMetodoSeleccionado();

            if (metodoSeleccionado == null) {
                mostrarError("Debes seleccionar un método de elección");
                return;
            }

            // Actualizar método en el ViewModel
            sharedViewModel.actualizarMetodoEleccion(metodoSeleccionado);

            // Navegar según el método seleccionado
            if (metodoSeleccionado == MetodoEleccionGanador.DETERMINISTA) {
                // Si es determinista, ir al paso 5 para describir el método
                navController.navigate(R.id.action_crearRifaPaso4Fragment_to_crearRifaPaso5Fragment);
            } else {
                // Si es aleatorio, crear la rifa directamente
                crearRifaFinal();
            }
        });
    }

    private MetodoEleccionGanador getMetodoSeleccionado() {
        int selectedId = rgMetodoSeleccion.getCheckedRadioButtonId();

        if (selectedId == R.id.crear_rifa_paso_4_rb_aleatorio) {
            return MetodoEleccionGanador.ALEATORIO;
        } else if (selectedId == R.id.crear_rifa_paso_4_rb_determinista) {
            return MetodoEleccionGanador.DETERMINISTA;
        }

        return null;
    }

    private void crearRifaFinal() {
        // Para método aleatorio, no necesita descripción adicional
        sharedViewModel.actualizarDescripcionMetodo("Selección aleatoria automática");

        // Obtener el usuario actual (necesitarás implementar esto según tu sistema de autenticación)
        String usuarioActual = obtenerUsuarioActual();

        if (usuarioActual == null) {
            mostrarError("Error: No se pudo identificar el usuario actual");
            return;
        }

        // Crear la rifa
        sharedViewModel.crearRifa(usuarioActual);
    }

    private String obtenerUsuarioActual() {
        String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return idUsuarioActual;
    }

    private void mostrarError(String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void observeViewModel() {
        // Observar estado de creación de rifa
        sharedViewModel.creandoRifa.observe(getViewLifecycleOwner(), creando -> {
            btnContinuar.setEnabled(!creando);
            if (creando) {
                btnContinuar.setText("Creando rifa...");
            } else {
                btnContinuar.setText("Continuar");
            }
        });

        // Observar resultado de creación
        sharedViewModel.resultadoCreacion.observe(getViewLifecycleOwner(), resultado -> {
            if (resultado != null) {
                if (resultado.contains("éxito")) {
                    mostrarExitoYSalir(resultado);
                } else {
                    mostrarError(resultado);
                }
            }
        });

        // Restaurar selección previa si existe
        sharedViewModel.rifaEnConstruccion.observe(getViewLifecycleOwner(), rifa -> {
            if (rifa != null && rifa.getMetodoEleccionGanador() != null) {
                if (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.ALEATORIO) {
                    rbAleatorio.setChecked(true);
                } else if (rifa.getMetodoEleccionGanador() == MetodoEleccionGanador.DETERMINISTA) {
                    rbDeterminista.setChecked(true);
                }
            }
        });
    }

    private void mostrarExitoYSalir(String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle("¡Éxito!")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Volver a la pantalla principal o lista de rifas
                    requireActivity().finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showMetodosInfoDialog() {
        String message = "Elige el método que prefieras para determinar los ganadores:\n\n" +
                "ALEATORIO\n" +
                "• El sistema selecciona automáticamente los números ganadores\n" +
                "• Se ejecuta en la fecha y hora programada\n" +
                "• No requiere tu intervención\n\n" +
                "DETERMINISTA\n" +
                "• Tú eliges manualmente los números ganadores\n" +
                "• Tienes control total sobre la selección\n" +
                "• En el siguiente paso, has de indicar qué criterio usarás para escoger uno o varios ganadores";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Métodos de Selección")
                .setMessage(message)
                .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                .setIcon(R.drawable.ic_info_outline)
                .show();
    }
}