package com.juancho.weathermap.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juancho.weathermap.R;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

import okhttp3.internal.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherDetails extends Fragment {

    private View rootView;
    private ImageView weatherIcon;
    private TextView weatherDescription;
    private TextView maxTemp;
    private TextView minTemp;
    private TextView humidity;
    private TextView wind;

    public WeatherDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.weather_details, container, false);

        setUI();

        return rootView;
    }

    private void setUI(){
        weatherDescription = rootView.findViewById(R.id.weatherDescription);
        maxTemp = rootView.findViewById(R.id.maxTemp);
        minTemp = rootView.findViewById(R.id.minTemp);
        humidity = rootView.findViewById(R.id.humidity);
        wind = rootView.findViewById(R.id.wind);
    }

    public void setWeatherValues(Weather weather){
        weatherDescription.setText(weather.getDescription());
        maxTemp.setText(weather.getTemp_max() + "");
        minTemp.setText(weather.getTemp_min() + "");
        humidity.setText(weather.getHumidity() + "%");
        String wind_direction = Utils.getWindDirection(weather.getWind_direction());
        wind.setText(weather.getWind_speed() + "m/s - "+ wind_direction);
    }

}
