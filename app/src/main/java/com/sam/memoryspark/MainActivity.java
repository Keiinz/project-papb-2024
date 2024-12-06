package com.sam.memoryspark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<History> historyList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btBack = findViewById(R.id.btBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList, item -> {
            Toast.makeText(MainActivity.this, "Membuka " + item.getDeckName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, QuizResult.class);
            intent.putExtra("history", item); // Kirim data History (harus Serializable)
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://memory-spark-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("history");

        // Mengambil data dari Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    History history = dataSnapshot.getValue(History.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btBack.setOnClickListener(v -> finish());
    }
}