package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.utils.Utilidades;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

public class CrearRifaPaso2Fragment extends Fragment {

    private CrearRifaSharedViewModel sharedViewModel;
    private NavController navController;

    // Views
    private TextInputEditText tietTitulo;
    private TextInputEditText tietDescripcion;
    private TextInputEditText tietPremios;
    private TextInputEditText tietNumeros;
    private TextInputEditText tietPrecio;
    private TextInputEditText tietFechaSorteo;
    private TextInputEditText tietHoraSorteo;
    private MaterialButton btnContinuar;

    private Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso2, container, false);
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
        setupDateTimePickers();
        setupContinueButton();
        observeViewModel();
    }

    private void initViews(View view) {
        tietTitulo = view.findViewById(R.id.crear_rifa_paso_2_tiet_titulo);
        tietDescripcion = view.findViewById(R.id.crear_rifa_paso_2_tiet_descripcion);
        tietPremios = view.findViewById(R.id.crear_rifa_paso_2_tiet_premios);
        tietNumeros = view.findViewById(R.id.crear_rifa_paso_2_tiet_numeros);
        tietPrecio = view.findViewById(R.id.crear_rifa_paso_2_tiet_precio);
        tietFechaSorteo = view.findViewById(R.id.crear_rifa_paso_2_tiet_fecha_sorteo);
        tietHoraSorteo = view.findViewById(R.id.crear_rifa_paso_2_tiet_hora_sorteo);
        btnContinuar = view.findViewById(R.id.crear_rifa_paso_2_btn_continuar);
    }

    private void setupDateTimePickers() {
        // Date picker
        tietFechaSorteo.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year1);
                        calendar.set(Calendar.MONTH, month1);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        tietFechaSorteo.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day
            );

            // Fecha mínima: mañana
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.DAY_OF_MONTH, 1);
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

            datePickerDialog.show();
        });

        // Time picker
        tietHoraSorteo.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view12, hourOfDay, minute1) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        tietHoraSorteo.setText(sdf.format(calendar.getTime()));
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });
    }

    private void setupContinueButton() {
        btnContinuar.setOnClickListener(v -> {
            if (validarYContinuar()) {
                navController.navigate(R.id.action_crearRifaPaso2Fragment_to_crearRifaPaso3Fragment);
            }
        });
    }

    private boolean validarYContinuar() {
        String titulo = tietTitulo.getText().toString().trim();
        String descripcion = tietDescripcion.getText().toString().trim();
        String premiosStr = tietPremios.getText().toString().trim();
        String numerosStr = tietNumeros.getText().toString().trim();
        String precioStr = tietPrecio.getText().toString().trim();
        String fechaStr = tietFechaSorteo.getText().toString().trim();
        String horaStr = tietHoraSorteo.getText().toString().trim();

        // Validaciones básicas
        if (titulo.isEmpty()) {
            mostrarError("El título es obligatorio");
            return false;
        }

        if (descripcion.trim().isEmpty()) {
            descripcion = null;
        }

        if (premiosStr.isEmpty()) {
            mostrarError("Debes especificar la cantidad de premios");
            return false;
        }

        if (numerosStr.isEmpty()) {
            mostrarError("Debes especificar la cantidad de números");
            return false;
        }

        if (precioStr.isEmpty()) {
            mostrarError("Debes especificar el precio por número");
            return false;
        }

        if (fechaStr.isEmpty() || horaStr.isEmpty()) {
            mostrarError("Debes seleccionar fecha y hora de sorteo");
            return false;
        }

        try {
            int cantPremios = Integer.parseInt(premiosStr);
            int cantNumeros = Integer.parseInt(numerosStr);
            double precio = Double.parseDouble(precioStr);

            if (cantPremios <= 0) {
                mostrarError("La cantidad de premios debe ser mayor a 0");
                return false;
            }

            if (cantNumeros <= 0 || cantNumeros > 200) {
                mostrarError("La cantidad de números debe estar entre 1 y 200");
                return false;
            }

            if (precio <= 0) {
                mostrarError("El precio debe ser mayor a 0");
                return false;
            }

            // Crear LocalDateTime
            String fechaHoraStr = fechaStr + " " + horaStr;
            LocalDateTime fechaSorteo = Utilidades.parsearFechaHora(fechaHoraStr, "dd-MM-yyyy HH:mm");

            if (fechaSorteo.isBefore(LocalDateTime.now().plusHours(1))) {
                mostrarError("La fecha de sorteo debe ser al menos 1 hora en el futuro");
                return false;
            }

            // Actualizar datos en el ViewModel
            sharedViewModel.actualizarDatosBasicos(titulo, descripcion, cantPremios, cantNumeros, precio, fechaSorteo);
            return true;

        } catch (NumberFormatException e) {
            mostrarError("Los valores numéricos no son válidos");
            return false;
        } catch (Exception e) {
            mostrarError("La fecha y hora no son válidas");
            return false;
        }
    }

    private void mostrarError(String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Error de validación")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void observeViewModel() {
        // Observar si hay datos previos para restaurar el estado
        sharedViewModel.rifaEnConstruccion.observe(getViewLifecycleOwner(), rifa -> {
            if (rifa != null && rifa.getTitulo() != null) {
                // Restaurar datos si el usuario vuelve a este paso
                tietTitulo.setText(rifa.getTitulo());
                tietDescripcion.setText(rifa.getDescripcion());
                if (rifa.getPremios() != null) {
                    tietPremios.setText(String.valueOf(rifa.getPremios().size()));
                }
                tietNumeros.setText(String.valueOf(rifa.getCantNumeros()));
                tietPrecio.setText(String.valueOf(rifa.getPrecioNumero()));

                if (rifa.getFechaSorteo() != null) {
                    tietFechaSorteo.setText(Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "dd-MM-yyyy"));
                    tietHoraSorteo.setText(Utilidades.formatearFechaHora(rifa.getFechaSorteo(), "HH:mm"));
                }
            }
        });
    }
}