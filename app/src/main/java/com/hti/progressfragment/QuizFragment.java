// QuizFragment.java
package com.hti.progressfragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuizFragment extends Fragment {

    private int nomorPertanyaan = 1;
    private int totalPertanyaan = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton tombolKembali = view.findViewById(R.id.backButton);
        TextView teksPertanyaan = view.findViewById(R.id.questionText);
        Button opsi1 = view.findViewById(R.id.option1);
        Button opsi2 = view.findViewById(R.id.option2);
        Button opsi3 = view.findViewById(R.id.option3);
        Button opsi4 = view.findViewById(R.id.option4);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView teksProgres = view.findViewById(R.id.progressText);
        Button tombolGantiFragment = view.findViewById(R.id.tombolGantiFragment);

        perbaruiKonten(teksPertanyaan, teksProgres, progressBar, opsi1, opsi2, opsi3, opsi4);

        tombolKembali.setOnClickListener(v -> getActivity().onBackPressed());

        View.OnClickListener optionClickListener = v -> {
            if (nomorPertanyaan < totalPertanyaan) {
                nomorPertanyaan++;
                perbaruiKonten(teksPertanyaan, teksProgres, progressBar, opsi1, opsi2, opsi3, opsi4);
            } else {
                tampilkanHasil(teksPertanyaan, opsi1, opsi2, opsi3, opsi4, teksProgres, progressBar);
            }
        };

        tombolGantiFragment.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ResultFragment hasilFragment = new ResultFragment();
            fragmentTransaction.replace(R.id.fragment_container, hasilFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        opsi1.setOnClickListener(optionClickListener);
        opsi2.setOnClickListener(optionClickListener);
        opsi3.setOnClickListener(optionClickListener);
        opsi4.setOnClickListener(optionClickListener);
    }

    private void perbaruiKonten(TextView teksPertanyaan, TextView teksProgres, ProgressBar bilahProgres,
                                Button opsi1, Button opsi2, Button opsi3, Button opsi4) {
        teksPertanyaan.setText("Pertanyaan " + nomorPertanyaan + ": Ibukota dari Perancis adalah?");
        opsi1.setText("Paris");
        opsi2.setText("London");
        opsi3.setText("Berlin");
        opsi4.setText("Madrid");

        teksProgres.setText(nomorPertanyaan + " / " + totalPertanyaan);
        bilahProgres.setProgress((nomorPertanyaan * 100) / totalPertanyaan);
    }

    private void tampilkanHasil(TextView teksPertanyaan, Button opsi1, Button opsi2, Button opsi3,
                                Button opsi4, TextView teksProgres, ProgressBar bilahProgres) {
        teksPertanyaan.setText("Selamat! Anda telah menyelesaikan kuis.");
        opsi1.setVisibility(View.GONE);
        opsi2.setVisibility(View.GONE);
        opsi3.setVisibility(View.GONE);
        opsi4.setVisibility(View.GONE);
        teksProgres.setVisibility(View.GONE);
        bilahProgres.setVisibility(View.GONE);
    }
}