package com.emmanuel.chancita.ui.rifa.editar_rifa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuel.chancita.R;

public class EditarRifaFragment extends Fragment {

    private EditarRifaViewModel mViewModel;

    public static EditarRifaFragment newInstance() {
        return new EditarRifaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_rifa, container, false);
    }
}