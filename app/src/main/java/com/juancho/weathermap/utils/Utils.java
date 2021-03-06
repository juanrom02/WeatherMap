package com.juancho.weathermap.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.fragments.MapFragment;
import com.juancho.weathermap.fragments.WeatherDetails;
import com.juancho.weathermap.models.Weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Juancho on 01/30/18.
 */

public class Utils{

    public static String UNITS;
    private static String LANGUAGE;
    public static int ANIM_DURATION = 500;

    public static void setUnits(Realm realm, String units){
        if(UNITS!=units){
            UNITS = units;
            convertUnits(realm);
            WeatherDetails.setUnits(units);
        }
    }

    private static void convertUnits(Realm realm){
        realm.beginTransaction();
        RealmResults<Weather> results = realm.where(Weather.class).findAll();
        for(int i=0; i<results.size(); i++){
            switch (UNITS){
                case "metric":
                    imperialToMetric(results);
                    break;
                case "imperial":
                    metricToImperial(results);
                    break;
            }
        }
        realm.commitTransaction();
    }

    private static void metricToImperial(RealmResults<Weather> results){
        for(int i=0; i < results.size(); i++){
            float metricWindSpeed = results.get(i).getWind_speed();
            float metricTemp = results.get(i).getTemp();
            results.get(i).setWind_speed((3600f/1609f)*metricWindSpeed);
            results.get(i).setTemp((metricTemp*1.8f)+32);
        }
    }

    private static void imperialToMetric(RealmResults<Weather> results){
        for(int i=0; i < results.size(); i++){
            float imperialWindSpeed = results.get(i).getWind_speed();
            float imperialTemp = results.get(i).getTemp();
            results.get(i).setWind_speed((1609f/3600f)*imperialWindSpeed);
            results.get(i).setTemp((imperialTemp-32)/1.8f);
        }

    }

    public static void setDefaultPreferences(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("units", "metric");
        editor.commit();
    }

    public static List<Float> getColorList(){
        List<Float> colorList = new ArrayList<>();

        colorList.add(BitmapDescriptorFactory.HUE_RED);
        colorList.add(BitmapDescriptorFactory.HUE_ORANGE);
        colorList.add(BitmapDescriptorFactory.HUE_YELLOW);
        colorList.add(BitmapDescriptorFactory.HUE_GREEN);
        colorList.add(BitmapDescriptorFactory.HUE_CYAN);
        colorList.add(BitmapDescriptorFactory.HUE_AZURE);
        colorList.add(BitmapDescriptorFactory.HUE_BLUE);
        colorList.add(BitmapDescriptorFactory.HUE_VIOLET);
        colorList.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colorList.add(BitmapDescriptorFactory.HUE_ROSE);

        return colorList;
    };

    public static String regexPlace(String address){
        Pattern pattern = Pattern.compile("(.*?),");
        Matcher matcher = pattern.matcher(address);
        matcher.find();
        return matcher.group(1);
    }

    public static void getWeather(final MapFragment mapFragment, LatLng coordinates){
        final MainActivity mainActivity = (MainActivity) mapFragment.getActivity();

        Call<Weather> weatherCall = mainActivity.getWeatherServices()
                .getWeather_OWM(coordinates.latitude, coordinates.longitude,
                mapFragment.getContext().getString(R.string.openWeatherMap_key), UNITS, LANGUAGE);

        weatherCall.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                mapFragment.setWeatherFound(true);
                Weather weather = response.body();
                mapFragment.setWeather(weather);
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                mapFragment.setWeatherFound(false);
                Toast.makeText(mapFragment.getContext(), "Weather not found for this place", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static void slideUpIn(Context context,View view){
        Animation slideUpIn = AnimationUtils.loadAnimation(context, R.anim.slide_up_in);
        slideUpIn.setDuration(ANIM_DURATION);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(slideUpIn);
    }

    public static void slideDownOut(Context context,View view){
        Animation slideDownOut = AnimationUtils.loadAnimation(context, R.anim.slide_down_out);
        slideDownOut.setDuration(ANIM_DURATION);
        view.startAnimation(slideDownOut);
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

    //https://openweathermap.org/weather-conditions
    public static int getWeatherIcon(String icon){
        switch (icon){
            case "01d":
                return R.drawable.weathericon_sun;
            case "01n":
                return R.drawable.weathericon_moon;
            case "02d":
            case "03d":
                return R.drawable.weathericon_cloud_sun;
            case "02n":
            case "03n":
                return R.drawable.weathericon_cloud_moon;
            case "04d":
            case "04n":
                return R.drawable.weathericon_cloud;
            case "09d":
                return R.drawable.weathericon_cloud_rain_sun;
            case "09n":
                return R.drawable.weathericon_cloud_rain_moon;
            case "10d":
                return R.drawable.weathericon_cloud_rain_sun_alt;
            case "10n":
                return R.drawable.weathericon_cloud_rain_moon_alt;
            case "11d":
                return R.drawable.weathericon_cloud_lightning_sun;
            case "11n":
                return R.drawable.weathericon_cloud_rain_moon;
            case "13d":
                return R.drawable.weathericon_cloud_snow_sun;
            case "13n":
                return R.drawable.weathericon_cloud_snow_moon;
            case "50d":
                return R.drawable.weathericon_cloud_fog_sun;
            case "50n":
                return R.drawable.weathericon_cloud_fog_moon;
            default:
                return R.drawable.help;

        }
    }

    public static String getHour(int timestamp){
        return new SimpleDateFormat("HH:mm").format(new Date(timestamp * 1000L));
    }

    public static String capitalize(String text){
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
