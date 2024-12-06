package com.sam.memoryspark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class QuizResult extends AppCompatActivity {

    private ImageButton homeButton;
    private TextView tvNamaDeck, tvTanggal;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result);

        homeButton = findViewById(R.id.ibHome);
        tvNamaDeck = findViewById(R.id.tvNamaDeckHasil);
        tvTanggal = findViewById(R.id.textView2);

        // Ambil data dari intent
        History history = (History) getIntent().getSerializableExtra("history");

        if (history != null) {
            tvNamaDeck.setText("Deck: " + history.getDeckName());
            tvTanggal.setText(history.getTanggal());

            // Kirim data ke fragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("history", (Serializable) history);

            HasilQuizFragment fragment = new HasilQuizFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .commit();
        }

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResult.this, MainActivity.class);
            startActivity(intent);
        });
    }
}