package com.emmanuel.chancita.ui.rifa;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emmanuel.chancita.R;
import com.google.android.material.card.MaterialCardView;

public class RifaOrganizadorFragment extends Fragment {

    private RifaOrganizadorViewModel mViewModel;
    private NavController navController;

    public static RifaOrganizadorFragment newInstance() {
        return new RifaOrganizadorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rifa_organizador, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Debe permitirse navegar hacia "Editar rifa" y para ello se debe tener el FAB

        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_rifaOrganizadorFragment_to_editarRifaFragment);
            }
        });
        */
    }

}