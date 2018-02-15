package com.juancho.weathermap.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.juancho.weathermap.adapters.CitiesAdapter;
import com.juancho.weathermap.api.GoogleTimezoneAPI;
import com.juancho.weathermap.api.services.TimezoneServices;
import com.juancho.weathermap.api.services.WeatherServices;
import com.juancho.weathermap.fragments.AboutFragment;
import com.juancho.weathermap.fragments.AlertsFragment;
import com.juancho.weathermap.fragments.CitiesFragment;
import com.juancho.weathermap.fragments.MapFragment;
import com.juancho.weathermap.R;
import com.juancho.weathermap.fragments.MyPlaceAutocompleteFragment;
import com.juancho.weathermap.fragments.SettingsFragment;
import com.juancho.weathermap.fragments.WeatherDetails;
import com.juancho.weathermap.api.OpenWeatherMapAPI;
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.models.MapMarker;
import com.juancho.weathermap.models.Weather;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener{

    private MapFragment mapFragment;
    private WeatherDetails weatherDetails;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem previousMenuItem;

    private CitiesAdapter.OnPinClickListener onPinClickListener;

    private MyPlaceAutocompleteFragment autocompleteFragment;

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

        realm.beginTransaction();
        realm.where(MapMarker.class).findAll().deleteAllFromRealm();
        realm.where(City.class).findAll().deleteAllFromRealm();
        realm.where(Weather.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();

        setToolbar();
        autocompleteFragment = (MyPlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(new AutocompleteFilter.Builder()
                                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                                        .build());

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(this);

        setDefaultFragment();
        setOnPinClickListener();
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
        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Error finding place: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setCheckable(true);

        previousMenuItem.setChecked(false);

        boolean fragmentChange = false;
        Fragment fragment = null;

        switch (item.getItemId()){
            case R.id.drawerMap:
                fragment = mapFragment;
                fragmentChange = true;
                break;
            case R.id.drawerCityList:
                fragment = new CitiesFragment();
                fragmentChange = true;
                break;
            case R.id.drawerAlerts:
                fragment = new AlertsFragment();
                fragmentChange = true;
                break;
            case R.id.drawerSettings:
                fragment = new SettingsFragment();
                fragmentChange = true;
                break;
            case R.id.drawerAbout:
                fragment = new AboutFragment();
                fragmentChange = true;
                break;
        }

        if(fragmentChange){
            changeFragment(fragment, item);
            drawerLayout.closeDrawers();
        }

        return true;
    }

    private void changeFragment(Fragment fragment, MenuItem item){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_frame, fragment)
                .commit();
        item.setChecked(true);
        if(fragment instanceof MapFragment){
            autocompleteFragment.setLayoutVisibilityMode(autocompleteFragment.NORMAL);
        }else{
            autocompleteFragment.setLayoutVisibilityMode(autocompleteFragment.HIDDEN);
        }
        autocompleteFragment.setTitle(item.getTitle());
    }

    private void setDefaultFragment(){
        MenuItem item = navigationView.getMenu().getItem(0);
        previousMenuItem = item;
        item.setCheckable(true);
        mapFragment = new MapFragment();
        changeFragment(mapFragment, item);
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

    private void setOnPinClickListener(){
        onPinClickListener = new CitiesAdapter.OnPinClickListener(){
            @Override
            public void onPinClick(MapMarker mapMarker) {
                mapFragment.findMarkerInList(mapMarker, MapFragment.SET);
                mapFragment.setDefaultCamera(new LatLng(mapMarker.getLatitude(), mapMarker.getLongitude()), 10);
                changeFragment(mapFragment, navigationView.getMenu().getItem(0));
            }
        };
    }

    public CitiesAdapter.OnPinClickListener getOnPinClickListener(){
        return onPinClickListener;
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
    public Weather getWeather(){
        return mapFragment.getWeather();
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

}
