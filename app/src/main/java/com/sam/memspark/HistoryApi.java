package com.sam.memspark;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HistoryApi {
    @GET("history.json") // Sesuaikan path dengan endpoint API GitHub
    Call<List<History>> getHistory();
}