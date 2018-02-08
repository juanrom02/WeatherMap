package com.juancho.weathermap.fragments;


import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener{

    private View rootView;
    private MapView mapView;

    private GoogleMap mMap;
    private String locality;
    private Marker marker;
    private boolean markerClick = false;
    private MarkerOptions markerOptions;

    private boolean weatherFound;
    private Weather currentWeather;

    private FloatingActionButton saveMarker;
    private FloatingActionButton deleteMarker;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        saveMarker = rootView.findViewById(R.id.saveMarker);
        saveMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Save marker", Toast.LENGTH_SHORT).show();
            }
        });
        deleteMarker = rootView.findViewById(R.id.deleteMarker);
        deleteMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Save marker", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = rootView.findViewById(R.id.mapView);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        markerClick = true;
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        ImageButton showWeatherDetails = ((MainActivity)getActivity()).getShowWeatherDetails();
        if(showWeatherDetails.getVisibility() == View.INVISIBLE) {
            Utils.slideUpIn(getContext(), showWeatherDetails);
        }
        return true;
    }

    private void markerAnim(View view, int animResource){
        Animation animation = AnimationUtils.loadAnimation(getContext(), animResource);
        animation.setDuration(Utils.ANIM_DURATION);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        hideWeatherDetails();
        hideFABs();
        setMarker(latLng);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if(i == REASON_GESTURE) {
            hideWeatherDetails();
            hideFABs();
        }
    }

    @Override
    public void onCameraIdle(){
        if(markerClick){
            markerClick = false;
            markerAnim(saveMarker, R.anim.savemarker_show);
            markerAnim(deleteMarker, R.anim.deletemarker_show);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideWeatherDetails();
        hideFABs();
    }

    private void hideWeatherDetails(){
        ((MainActivity)getActivity()).hideWeatherDetails();
        ImageButton showWeatherDetails = ((MainActivity)getActivity()).getShowWeatherDetails();
        if(showWeatherDetails.getVisibility() == View.VISIBLE) {
            Utils.slideDownOut(getContext(), showWeatherDetails);
            showWeatherDetails.setVisibility(View.INVISIBLE);
        }
    }

    private void hideFABs(){
        saveMarker.setVisibility(View.INVISIBLE);
        deleteMarker.setVisibility(View.INVISIBLE);
    }

    public String getLocality(LatLng latLng){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList = null;

        try{
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        }catch(IOException e){
            e.printStackTrace();
        }

        if (addressList != null && addressList.size() > 0) {
            return addressList.get(0).getLocality();
        }else{
            return "";
        }
    }

    public Marker getMarker(){
        return marker;
    }

    public void setMarker(LatLng latLng){
        locality = getLocality(latLng);
        Utils.getWeather(MapFragment.this, latLng);
        markerOptions = new MarkerOptions()
                .position(latLng)
                .title(locality)
                .draggable(false);
        if(marker != null) marker.remove();
        marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
    }

    public GoogleMap getMap(){
        return mMap;
    }

    public void setWeatherFound(boolean weatherFound){
        this.weatherFound = weatherFound;
    }

    public void setCurrentWeather(Weather currentWeather){
        this.currentWeather = currentWeather;
        fixTimezone(marker.getPosition());
    }

    private void fixTimezone(LatLng latLng){
        Call<JsonElement> timezoneCall = ((MainActivity)getActivity()).getTimezoneServices()
                .getTimezone(latLng.latitude + "," + latLng.longitude, currentWeather.getSunrise(),
                            getString(R.string.google_timezone_key));

        timezoneCall.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                int offset = response.body().getAsJsonObject().get("rawOffset").getAsInt();
                Calendar calendar = Calendar.getInstance();
                int localOffset = (calendar.getTimeZone().getRawOffset())/1000;
                currentWeather.setSunrise(currentWeather.getSunrise() + offset - localOffset);
                currentWeather.setSunset(currentWeather.getSunset() + offset - localOffset);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
            }
        });
    }

    public Weather getCurrentWeather(){
        return currentWeather;
    }
}
