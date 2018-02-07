package com.juancho.weathermap.api.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.juancho.weathermap.models.City;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Juancho on 02/06/18.
 */

public interface TimezoneServices {

    @GET("json")
    Call<JsonElement> getTimezone(@Query("location") String latLng, @Query("timestamp") int timestamp,
                                  @Query("key") String key);
}
