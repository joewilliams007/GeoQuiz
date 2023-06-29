package com.dev.geoquizworld;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsWiki {
    @GET
    Call<ModelWiki> getAllData(
            @Url String url
    );
}
