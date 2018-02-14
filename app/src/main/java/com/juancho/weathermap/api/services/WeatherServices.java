package com.juancho.weathermap.api.services;

import com.juancho.weathermap.models.City;
import com.juancho.weathermap.models.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Juancho on 01/29/18.
 */

public interface WeatherServices {

    @GET("weather")
    Call<Weather> getWeather_OWM(@Query("lat") double latitude, @Query("lon") double longitude,
                                 @Query("appid") String key, @Query("units") String units,
                                 @Query("lang") String language);

}
