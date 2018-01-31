package com.juancho.weathermap.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class Utils{

    private static String UNITS = "metric";
    private static String LANGUAGE = "en";

    private static Weather currentWeather = null;


    public static String regexPlace(String address){
        Pattern pattern = Pattern.compile("(.*?),");
        Matcher matcher = pattern.matcher(address);
        matcher.find();
        return matcher.group(1);
    }

    public static void getWeather(final MapFragment mapFragment, LatLng coordinates){

        Call<City> cityCall = ((MainActivity) mapFragment.getActivity()).getWeatherService()
                .getCity_OWM(coordinates.latitude, coordinates.longitude,
                mapFragment.getContext().getString(R.string.openWeatherMap_key), UNITS, LANGUAGE);

        cityCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                mapFragment.setWeatherFound(true);
                City city = response.body();
                currentWeather = city.getWeather();
                mapFragment.setMarkerSnippet(currentWeather.getDescription() + " ("
                        + Math.round(currentWeather.getTemp()) + "°C)");
                mapFragment.setCurrentWeather(currentWeather);
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                mapFragment.setWeatherFound(false);
                Toast.makeText(mapFragment.getContext(), "Weather not found for this place", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static void slideUpIn(Context context,View view){
        Animation slideUpIn = AnimationUtils.loadAnimation(context, R.anim.slide_up_in);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(slideUpIn);
    }

    public static String getWindDirection(double degrees){

        double j = 22.5;
        int i = 0;

        if(!(348.75 < degrees || degrees <= 11.25)){
            for(i = 1; i < 16; i++){
                if((j-11.25) < degrees && degrees <= (j+11.25)){
                    break;
                }else{
                    j = j + 22.5;
                }
            }
        }

        switch (i){
            case 0: return "N";
            case 1: return "NNE";
            case 2: return "NE";
            case 3: return "ENE";
            case 4: return "E";
            case 5: return "ESE";
            case 6: return "SE";
            case 7: return "SSE";
            case 8: return "S";
            case 9: return "SSW";
            case 10: return "SW";
            case 11: return "WSW";
            case 12: return "W";
            case 13: return "WNW";
            case 14: return "NW";
            case 15: return "NNW";
            default: return "";
        }
    }
}
