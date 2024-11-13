// ResultActivity.java
package com.hti.progressfragment;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // Ganti dengan layout activity

        TextView teksHasil = findViewById(R.id.resultText);
        teksHasil.setText("Selamat! Anda telah menyelesaikan kuis.");
    }
}
