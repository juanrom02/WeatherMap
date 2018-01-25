package com.juancho.weathermap;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{

    private View rootView;
    private MapView mapView;
    private GoogleMap mMap;
    private String locality;
    private Marker marker;
    private MarkerOptions markerOptions;


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

        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng latLng) {
                locality = getLocality(latLng);
                markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(locality)
                        .draggable(false);
                marker.remove();
                marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
        });
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
}
