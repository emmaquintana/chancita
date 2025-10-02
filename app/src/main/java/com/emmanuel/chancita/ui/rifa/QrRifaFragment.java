package com.emmanuel.chancita.ui.rifa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.emmanuel.chancita.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

public class QrRifaFragment extends Fragment {
    private static final String ARG_RIFA_ID = "rifa_id";

    public static QrRifaFragment newInstance(String rifaId) {
        QrRifaFragment fragment = new QrRifaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIFA_ID, rifaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_rifa, container, false);

        ImageView imgQr = view.findViewById(R.id.qr_rifa_img);
        Button btnCompartirQr = view.findViewById(R.id.qr_rifa_btn_compartir);

        String rifaId = getArguments().getString(ARG_RIFA_ID);
        String universalLink = "https://emmaquintana.github.io/appchancita/redireccion-rifa/redir.html?rifa_id=" + rifaId;

        Bitmap qr = generarCodigoQR(universalLink);
        if (qr != null) {
            imgQr.setImageBitmap(qr);
        }

        btnCompartirQr.setOnClickListener(v -> compartirQR(qr));

        return view;
    }

    private Bitmap generarCodigoQR(String texto) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(texto, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void compartirQR(Bitmap bitmap) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "qr.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri uri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Compartir QR"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
