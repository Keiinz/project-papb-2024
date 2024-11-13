package com.sam.memspark;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;


public class QuizResult extends AppCompatActivity {

    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result);  // Menghubungkan ke layout yang dimodifikasi

        homeButton = findViewById(R.id.ibHome);


        homeButton.setOnClickListener(v -> {

            Intent intent = new Intent(QuizResult.this, MainActivity.class);
            startActivity(intent);
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new HasilQuizFragment())
                    .commit();
        }
    }
}