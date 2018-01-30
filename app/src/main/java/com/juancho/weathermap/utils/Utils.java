package com.juancho.weathermap.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.api.services.WeatherService;
import com.juancho.weathermap.fragments.MapFragment;
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.models.Weather;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Juancho on 01/30/18.
 */

public class Utils {

    private static String UNITS = "metric";
    private static String LANGUAGE = "en";

    private static Weather currentWeather;

    public static String regexPlace(String address){
        Pattern pattern = Pattern.compile("(.*?),");
        Matcher matcher = pattern.matcher(address);
        matcher.find();
        return matcher.group(1);
    }

    public static void getWeather(final MapFragment mapFragment, LatLng coordinates){

        Call<City> cityCall = MainActivity.weatherService.getCity_OWM(coordinates.latitude, coordinates.longitude,
                mapFragment.getContext().getString(R.string.openWeatherMap_key), UNITS, LANGUAGE);

        cityCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                City city = response.body();
                currentWeather = city.getWeather();
                mapFragment.setMarkerSnippet(currentWeather.getDescription() + " ("
                        + Math.round(currentWeather.getTemp()) + "°C)");
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Toast.makeText(mapFragment.getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
