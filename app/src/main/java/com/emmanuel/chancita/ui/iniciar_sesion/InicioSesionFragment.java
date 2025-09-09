package com.emmanuel.chancita.ui.iniciar_sesion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.databinding.FragmentIniciarSesionBinding;
import com.emmanuel.chancita.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesionFragment extends Fragment {

    private FragmentIniciarSesionBinding binding;
    private SharedPreferences sharedPreferences;
    private InicioSesionViewModel inicioSesionViewModel;
    private NavController navController;

    public static InicioSesionFragment newInstance() {
        return new InicioSesionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        navController = NavHostFragment.findNavController(this);
        postponeEnterTransition();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentIniciarSesionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Step 3: Remove the listener to avoid it being called multiple times.
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                // Step 4: Now, start the postponed transition.
                startPostponedEnterTransition();
                return true;
            }
        });

        inicioSesionViewModel = new ViewModelProvider(this).get(InicioSesionViewModel.class);

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        binding.loginBtnContinuar.setOnClickListener(v -> {
            String email = binding.loginTietEmail.getText().toString();
            String password = binding.loginTietPassword.getText().toString();
            inicioSesionViewModel.iniciarSesion(email, password);
        });

        binding.loginTxtContraseAOlvidada.setOnClickListener(v -> {
            String email = binding.loginTietEmail.getText().toString();
            inicioSesionViewModel.enviarRestablecimientoDeContraseña(email);
            navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
        });
    }

    private void setupObservers() {
        inicioSesionViewModel.estaIniciandoSesion.observe(getViewLifecycleOwner(), isLoggingIn -> {
            if (isLoggingIn) {
                binding.loginBtnContinuar.setEnabled(false);
                binding.loginBtnContinuar.setText("Iniciando sesión...");
            }
            else {
                binding.loginBtnContinuar.setEnabled(true);
                binding.loginBtnContinuar.setText("Iniciar sesión");
            }

        });

        inicioSesionViewModel.resultadoInicioSesion.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            }
        });

        inicioSesionViewModel.inicioSesionExitoso.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                if (binding.loginCbRecordarme.isChecked()) {
                    sharedPreferences.edit().putBoolean("is_logged_in", true).apply();
                }
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}