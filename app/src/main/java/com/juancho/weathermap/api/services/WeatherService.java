package com.juancho.weathermap.api.services;

import com.juancho.weathermap.models.City;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Juancho on 01/29/18.
 */

public interface WeatherService {

    @GET("weather")
    Call<City> getCity_OWM(@Query("lat") double latitude, @Query("lon") double longitude,
                           @Query("appid") String key, @Query("units") String units,
                           @Query("lang") String language);

}
