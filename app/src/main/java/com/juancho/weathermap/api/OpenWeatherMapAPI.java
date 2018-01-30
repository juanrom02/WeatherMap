package com.juancho.weathermap.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.juancho.weathermap.api.deserializer.DeserializerOWM;
import com.juancho.weathermap.models.City;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Juancho on 01/29/18.
 */

public class OpenWeatherMapAPI {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit = null;

    public static Retrofit getApi(){
        if(retrofit == null){
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(City.class, new DeserializerOWM());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .build();
        }
        return retrofit;
    }
}
