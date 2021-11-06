package com.betterise.maladiecorona.networking.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RESTApiInterface {


    // validate code
    @POST("contact_check.php")
    Call<ResponseBody> validatecode(@Body Map<String,String> obj);



    //send nid
    @POST("fetch-nid.php")
    Call<ResponseBody> sendNID(@Body Map<String,String> obj);




}