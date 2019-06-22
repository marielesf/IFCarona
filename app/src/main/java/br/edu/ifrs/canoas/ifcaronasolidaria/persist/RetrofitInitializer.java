package br.edu.ifrs.canoas.ifcaronasolidaria.persist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitInitializer {
    private static final String URL = "http://appifcarona.canoas.ifrs.edu.br/";
    private Retrofit retrofit;

    public RetrofitInitializer() {
        Gson gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static String getURL() {
        return URL;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
