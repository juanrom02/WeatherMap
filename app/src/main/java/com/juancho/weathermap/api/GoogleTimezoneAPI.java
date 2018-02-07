package com.juancho.weathermap.api;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Juancho on 02/06/18.
 */

public class GoogleTimezoneAPI {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/timezone/";
    private static Retrofit retrofit = null;

    public static Retrofit getApi(){
        if(retrofit == null){
            GsonBuilder builder = new GsonBuilder();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .build();
        }
        return retrofit;
    }
}
