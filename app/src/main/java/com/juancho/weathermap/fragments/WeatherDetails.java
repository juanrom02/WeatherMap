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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import okhttp3.internal.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherDetails extends Fragment {

    private View rootView;
    private ImageView weatherIcon;
    private TextView currentTemp;
    private TextView weatherDescription;
    private TextView humidity;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;

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
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        currentTemp = rootView.findViewById(R.id.currentTemp);
        humidity = rootView.findViewById(R.id.humidity);
        wind = rootView.findViewById(R.id.wind);
        sunrise = rootView.findViewById(R.id.sunriseTime);
        sunset = rootView.findViewById(R.id.sunsetTime);
    }

    public void setWeatherValues(Weather weather){
        weatherIcon.setImageResource(Utils.getWeatherIcon(weather.getIcon()));
        currentTemp.setText(Math.round(weather.getTemp()) + "");
        weatherDescription.setText(Utils.capitalize(weather.getDescription()));
        humidity.setText(Math.round(weather.getHumidity()) + "%");
        String wind_direction = Utils.getWindDirection(weather.getWind_direction());
        wind.setText(Math.round(weather.getWind_speed()) + "m/s - "+ wind_direction);
        sunrise.setText(Utils.getHour(weather.getSunrise()));
        sunset.setText(Utils.getHour(weather.getSunset()));
    }

}
