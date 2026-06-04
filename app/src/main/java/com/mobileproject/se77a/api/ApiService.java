package com.mobileproject.se77a.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    @GET("v1/exercises")
    Call<List<HealthTip>> getExercises(
        @Header("X-Api-Key") String apiKey,
        @Query("type") String type
    );

    @GET("https://api.mymemory.translated.net/get")
    Call<TranslationResponse> translate(
        @Query("q") String text,
        @Query("langpair") String langPair
    );
}
