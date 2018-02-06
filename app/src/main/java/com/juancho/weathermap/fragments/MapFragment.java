package com.juancho.weathermap.fragments;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener{

    private View rootView;
    private MapView mapView;
    private GoogleMap mMap;
    private String locality;
    private Marker marker;
    private MarkerOptions markerOptions;
    private boolean weatherFound;
    private Weather currentWeather;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        ((MainActivity)getActivity()).hideWeatherDetails();
        ImageButton showWeatherDetails = ((MainActivity)getActivity()).getShowWeatherDetails();
        if(showWeatherDetails.getVisibility() == View.VISIBLE) {
            Utils.slideDownOut(getContext(), showWeatherDetails);
            showWeatherDetails.setVisibility(View.INVISIBLE);
        }
        setMarker(latLng);
        if(weatherFound) {
            Utils.slideUpIn(getContext(),((MainActivity)getActivity()).getShowWeatherDetails());
        }
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
    }

    public Weather getCurrentWeather(){
        return currentWeather;
    }
}
