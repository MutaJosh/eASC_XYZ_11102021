package com.betterise.maladiecorona.networking.singleton;

import com.betterise.maladiecorona.networking.interfaces.RESTApiInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTApiClient {

    private static final String BASE_URL = "https://rbc.gov.rw/community/data-sharing/api/";
    private static RESTApiClient apiClient;
    private static Retrofit retrofit;

    public RESTApiClient() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized RESTApiClient getInstance() {
        if (apiClient == null) {
            apiClient = new RESTApiClient();
        }
        return apiClient;
    }

    public RESTApiInterface getApi() {
        return retrofit.create(RESTApiInterface.class);
    }

}
