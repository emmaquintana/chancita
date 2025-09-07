package com.emmanuel.chancita.ui.rifa.crear_rifa;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
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

public class CrearRifaPaso1Fragment extends Fragment {

    private CrearRifaPaso1ViewModel mViewModel;
    private NavController navController;

    public static CrearRifaPaso1Fragment newInstance() {
        return new CrearRifaPaso1Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_rifa_paso1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnContinuar = view.findViewById(R.id.crear_rifa_paso_1_btn_continuar);

        btnContinuar.setOnClickListener(v -> {
            // (En realidad, debería ser un deep link)
            navController.navigate(R.id.action_crearRifaPaso1Fragment_to_crearRifaPaso2Fragment);
        });
    }
}