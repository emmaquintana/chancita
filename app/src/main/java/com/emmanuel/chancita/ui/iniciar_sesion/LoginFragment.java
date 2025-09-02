package com.emmanuel.chancita.ui.iniciar_sesion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.emmanuel.chancita.R;
import com.emmanuel.chancita.ui.MainActivity;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private NavController navController;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();

        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_iniciar_sesion, container, false);
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

        TextView txtRestablecerContraseña = view.findViewById(R.id.login_txt_contraseña_olvidada);
        Button btnIniciarSesion = view.findViewById(R.id.login_btn_continuar);

        txtRestablecerContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}