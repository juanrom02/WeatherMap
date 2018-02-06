package com.juancho.weathermap.activities;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.juancho.weathermap.api.services.WeatherService;
import com.juancho.weathermap.fragments.MapFragment;
import com.juancho.weathermap.R;
import com.juancho.weathermap.fragments.WeatherDetails;
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.api.OpenWeatherMapAPI;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener{

    private MapFragment mapFragment;
    private WeatherDetails weatherDetails;
    private NavigationView navigationView;

    private PlaceAutocompleteFragment autocompleteFragment;

    private WeatherService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(new AutocompleteFilter.Builder()
                                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                                        .build());

        navigationView = findViewById(R.id.navView);

        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_content_frame, mapFragment)
                .commit();
        weatherDetails = new WeatherDetails();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_content_frame, weatherDetails)
                .commit();
        weatherService = OpenWeatherMapAPI.getApi().create(WeatherService.class);
    }

    @Override
    public void onPlaceSelected(Place place) {
        hideWeatherDetails();
        mapFragment.setMarker(place.getLatLng());
        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Error finding place: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setToolbar(){
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //For fragment communication
    public ImageButton getShowWeatherDetails(){
        return weatherDetails.getShowWeatherDetails();
    }

    public void hideWeatherDetails(){
        weatherDetails.hide();
    }

    //For fragment communication
    public Weather getCurrentWeather(){
        return mapFragment.getCurrentWeather();
    }

    public WeatherService getWeatherService(){
        return weatherService;
    }
}
