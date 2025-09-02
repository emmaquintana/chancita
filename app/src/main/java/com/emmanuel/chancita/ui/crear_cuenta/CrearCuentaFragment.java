package com.emmanuel.chancita.ui.crear_cuenta;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.emmanuel.chancita.databinding.FragmentCrearCuentaBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CrearCuentaFragment extends Fragment {

    private FragmentCrearCuentaBinding binding;
    private CrearCuentaViewModel crearCuentaViewModel;
    private LocalDate fechaNacimiento;

    public static CrearCuentaFragment newInstance() {
        return new CrearCuentaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearCuentaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });

        crearCuentaViewModel = new ViewModelProvider(this).get(CrearCuentaViewModel.class);

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        binding.registrarseEtFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        binding.registrarseBtnContinuar.setOnClickListener(v -> {
            String nombre = binding.registrarseEtNombreCompleto.getText().toString();
            String apellido = binding.registrarseEtApellidoCompleto.getText().toString();
            String correo = binding.registrarseTietEmail.getText().toString();
            String nroCelular = binding.registrarseEtNroCelular.getText().toString();
            String contraseña = binding.registarseTietContraseA.getText().toString();
            String confirmarContraseña = binding.registrarseEtConfirmarContraseA.getText().toString();

            crearCuentaViewModel.crearUsuario(nombre, apellido, correo, fechaNacimiento, nroCelular, contraseña, confirmarContraseña);
        });
    }

    private void setupObservers() {
        crearCuentaViewModel.estaRegistrandose.observe(getViewLifecycleOwner(), isRegistering -> {
            if (isRegistering) {
                // Botón cargando y deshabilitado
            }
            else {
                // Botón normal
            }
        });

        crearCuentaViewModel.resultadoRegistro.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDatePicker() {
        // Fecha máxima: hoy - 18 años
        LocalDate hoy = LocalDate.now();
        LocalDate fechaMax = hoy.minusYears(18);
        LocalDate fechaMin = hoy.minusYears(150); // Alguien de >150 años no suele apostar .-.
        long fechaMaxMillis = fechaMax.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(fechaMin.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setEnd(fechaMax.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setValidator(DateValidatorPointBackward.before(
                        fechaMax.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                ));

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu fecha de nacimiento")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(fechaMaxMillis)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Instant instant = Instant.ofEpochMilli(selection);
            fechaNacimiento = instant.atZone(ZoneId.of("UTC")).toLocalDate();

            // Usar un SimpleDateFormat para dar formato al String, también con UTC
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedDate = sdf.format(new Date(selection));

            binding.registrarseEtFechaNacimiento.setText(formattedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}