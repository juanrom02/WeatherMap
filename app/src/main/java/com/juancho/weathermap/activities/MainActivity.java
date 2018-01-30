package com.juancho.weathermap.activities;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.api.OpenWeatherMapAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener{

    private MapFragment mapFragment;
    private NavigationView navigationView;

    private PlaceAutocompleteFragment autocompleteFragment;

    private WeatherService weatherService;
    private String UNITS = "metric";
    private String LANGUAGE = "en";
    private String snippet;

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
        weatherService = OpenWeatherMapAPI.getApi().create(WeatherService.class);
    }

    private void setToolbar(){
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPlaceSelected(Place place) {
        String address = place.getAddress().toString();
        getWeather(place.getLatLng());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(place.getLatLng())
                .title(regexPlace(address))
                .snippet(snippet)
                .draggable(false);
        GoogleMap mMap = mapFragment.getMap();
        mapFragment.setMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
    }

    public void getWeather(LatLng coordinates){

        Call<City> cityCall = weatherService.getCity_OWM(coordinates.latitude, coordinates.longitude,
                getString(R.string.openWeatherMap_key), UNITS, LANGUAGE);

        cityCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                City city = response.body();
                snippet = city.getWeather().getDescription() + " (" +
                        Math.round(city.getWeather().getTemp()) + "°C)";
                mapFragment.setMarkerSnippet(snippet);
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Error finding place: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    private String regexPlace(String address){
        Pattern pattern = Pattern.compile("(.*?),");
        Matcher matcher = pattern.matcher(address);
        matcher.find();
        return matcher.group(1);
    }
}
