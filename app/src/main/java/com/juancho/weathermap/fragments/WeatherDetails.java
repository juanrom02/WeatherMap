package com.juancho.weathermap.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherDetails extends Fragment implements View.OnClickListener{

    private View rootView;
    private View weatherDetails;
    private ImageButton showWeatherDetails;
    private boolean weatherDetailsUp = false;
    private ImageView weatherIcon;
    private TextView currentTemp;
    private TextView tempUnit;
    private TextView weatherDescription;
    private TextView humidity;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;

    private static String temperature = "°C";
    private static String speed = "m/s";

    public WeatherDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.weather_details, container, false);

        setUI();

        showWeatherDetails.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if(!weatherDetailsUp){
            setWeatherValues(((MainActivity)getActivity()).getWeather());
            showWeatherDetails.setImageResource(R.mipmap.chevron_down);
            rootView.setVisibility(View.INVISIBLE);
            weatherDetails.setVisibility(View.VISIBLE);
            Utils.slideUpIn(getContext(), rootView);
            weatherDetailsUp = true;
        }else{
            hide();
        }
    }

    public void hide(){
        Utils.slideDownOut(getContext(), rootView);
        weatherDetails.postDelayed(new Runnable() {
            @Override
            public void run() {
                weatherDetails.setVisibility(View.GONE);
            }
        }, Utils.ANIM_DURATION);
        showWeatherDetails.setImageResource(R.mipmap.chevron_up);
        weatherDetailsUp = false;
    }

    public ImageButton getShowWeatherDetails() {
        return showWeatherDetails;
    }

    private void setUI(){
        weatherDetails = rootView.findViewById(R.id.weatherDetails);
        showWeatherDetails = rootView.findViewById(R.id.showWeatherDetails);
        weatherDescription = rootView.findViewById(R.id.weatherDescription);
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        currentTemp = rootView.findViewById(R.id.currentTemp);
        tempUnit = rootView.findViewById(R.id.tempUnit);
        humidity = rootView.findViewById(R.id.humidity);
        wind = rootView.findViewById(R.id.wind);
        sunrise = rootView.findViewById(R.id.sunriseTime);
        sunset = rootView.findViewById(R.id.sunsetTime);
    }

    public void setWeatherValues(Weather weather){
        weatherIcon.setImageResource(Utils.getWeatherIcon(weather.getIcon()));
        currentTemp.setText(Math.round(weather.getTemp()) + "");
        tempUnit.setText(temperature);
        weatherDescription.setText(Utils.capitalize(weather.getDescription()));
        humidity.setText(Math.round(weather.getHumidity()) + "%");
        String wind_direction = Utils.getWindDirection(weather.getWind_direction());
        wind.setText(Math.round(weather.getWind_speed()) + speed + " - "+ wind_direction);
        sunrise.setText(Utils.getHour(weather.getSunrise()));
        sunset.setText(Utils.getHour(weather.getSunset()));
    }

    public static void setUnits(String units){
        if(units.equals("metric")){
            temperature = "°C";
            speed = "m/s";
        }else if(units.equals("imperial")){
            temperature = "°F";
            speed = "mph";
        }
    }

}
