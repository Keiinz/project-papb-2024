package com.sam.memspark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btBack = findViewById(R.id.btBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList, new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(History item) {
                Toast.makeText(MainActivity.this, "Membuka " + item.getDeckName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, QuizResult.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        loadHistoryData();

        btBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadHistoryData() {
        HistoryApi apiService = ApiClient.getClient().create(HistoryApi.class);
        Call<List<History>> call = apiService.getHistory();

        call.enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    historyList.clear();
                    historyList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("MainActivity", "Data berhasil dimuat: " + response.body());
                } else {
                    Log.d("MainActivity", "Respon berhasil tapi kosong atau error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Log.e("MainActivity", "Gagal memuat data", t);
            }
        });
    }
}