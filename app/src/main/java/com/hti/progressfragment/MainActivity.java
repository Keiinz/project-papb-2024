package com.hti.progressfragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance("https://memspark-6e5de-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("questions");

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Inisialisasi Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        // Listener Delete
        adapter.setDeleteClickListener(this::deleteQuestion);

        // Tambahkan data dummy ke database
        insertDummyData();

        // Muat data dari Firebase
        loadQuestions();
    }

    private void insertDummyData() {
        List<Question> dummyQuestions = new ArrayList<>();
        dummyQuestions.add(new Question("What is your name?", "Option 1", "Option 2", "Option 3", "Option 4"));
        dummyQuestions.add(new Question("Where do you live?", "Option A", "Option B", "Option C", "Option D"));
        dummyQuestions.add(new Question("What is your favorite color?", "Red", "Blue", "Green", "Yellow"));

        for (Question question : dummyQuestions) {
            String id = databaseReference.push().getKey();
            question.setId(id);
            databaseReference.child(id).setValue(question);
        }
    }

    private void loadQuestions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questions = new ArrayList<>();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    questions.add(question);
                }
                adapter.updateData(questions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load questions", error.toException());
            }
        });
    }

    private void deleteQuestion(Question question) {
        databaseReference.child(question.getId()).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Question deleted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete question!", Toast.LENGTH_SHORT).show());
    }
}
