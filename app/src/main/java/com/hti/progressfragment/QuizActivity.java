// QuizActivity.java
package com.hti.progressfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuizActivity extends AppCompatActivity {

    private int nomorPertanyaan = 1;
    private int totalPertanyaan = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz); // Ganti dengan layout activity

        ImageButton tombolKembali = findViewById(R.id.backButton);
        TextView teksPertanyaan = findViewById(R.id.questionText);
        Button opsi1 = findViewById(R.id.option1);
        Button opsi2 = findViewById(R.id.option2);
        Button opsi3 = findViewById(R.id.option3);
        Button opsi4 = findViewById(R.id.option4);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView teksProgres = findViewById(R.id.progressText);
        Button tombolGantiActivity = findViewById(R.id.tombolGantiFragment); // Diganti tombolGantiActivity

        perbaruiKonten(teksPertanyaan, teksProgres, progressBar, opsi1, opsi2, opsi3, opsi4);

        tombolKembali.setOnClickListener(v -> onBackPressed());

        View.OnClickListener optionClickListener = v -> {
            if (nomorPertanyaan < totalPertanyaan) {
                nomorPertanyaan++;
                perbaruiKonten(teksPertanyaan, teksProgres, progressBar, opsi1, opsi2, opsi3, opsi4);
            } else {
                tampilkanHasil(teksPertanyaan, opsi1, opsi2, opsi3, opsi4, teksProgres, progressBar);
            }
        };

        tombolGantiActivity.setOnClickListener(v -> {
            // Start ResultActivity instead of switching Fragment
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            startActivity(intent);
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
