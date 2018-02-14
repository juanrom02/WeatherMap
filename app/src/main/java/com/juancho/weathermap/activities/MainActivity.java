package com.juancho.weathermap.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.juancho.weathermap.api.GoogleTimezoneAPI;
import com.juancho.weathermap.api.services.TimezoneServices;
import com.juancho.weathermap.api.services.WeatherServices;
import com.juancho.weathermap.fragments.MapFragment;
import com.juancho.weathermap.R;
import com.juancho.weathermap.fragments.WeatherDetails;
import com.juancho.weathermap.api.OpenWeatherMapAPI;
import com.juancho.weathermap.models.MapMarker;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

import java.sql.Time;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener,
        RealmChangeListener<RealmResults<MapMarker>>, NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener{

    private MapFragment mapFragment;
    private WeatherDetails weatherDetails;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private PlaceAutocompleteFragment autocompleteFragment;

    private WeatherServices weatherServices;
    private TimezoneServices timezoneServices;

    private Realm realm;
    private RealmResults<MapMarker> mapMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        mapMarkers = realm.where(MapMarker.class).findAll();
        mapMarkers.addChangeListener(this);

        setToolbar();
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(new AutocompleteFilter.Builder()
                                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                                        .build());

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(this);

        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_content_frame, mapFragment)
                .commit();
        weatherDetails = new WeatherDetails();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_content_frame, weatherDetails)
                .commit();
        weatherServices = OpenWeatherMapAPI.getApi().create(WeatherServices.class);
        timezoneServices = GoogleTimezoneAPI.getApi().create(TimezoneServices.class);
    }

    @Override
    public void onPlaceSelected(Place place) {
        hideWeatherDetails();
        mapFragment.setMarker(place.getLatLng());
        Utils.getWeather(mapFragment, place.getLatLng());
        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Error finding place: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChange(RealmResults<MapMarker> mapMarkers) {
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

    public WeatherServices getWeatherServices(){
        return weatherServices;
    }

    public TimezoneServices getTimezoneServices(){ return timezoneServices;}

    public Realm getRealm(){
        return realm;
    }

    public RealmResults<MapMarker> getMapMarkers(){
        return mapMarkers;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.drawerMap:
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawerCityList:
                Toast.makeText(this, "List", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawerSettings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawerAbout:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
