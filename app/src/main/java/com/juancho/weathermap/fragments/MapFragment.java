package com.juancho.weathermap.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Layout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.adapters.ColorGridAdapter;
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.models.MapMarker;
import com.juancho.weathermap.models.Weather;
import com.juancho.weathermap.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
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
        GoogleMap.OnCameraIdleListener, View.OnClickListener,
        DialogInterface.OnClickListener{

    private View rootView;
    private MapView mapView;

    private GoogleMap mMap;
    private LatLng defaultLatLng;
    private int defaultZoom;
    private boolean moveDefaultCamera = false;
    private MapMarker mapMarker;
    private City city;
    private Marker marker;
    private List<Marker> markers = new ArrayList<>();
    private boolean showFABs = false;
    private MarkerOptions markerOptions;

    private boolean weatherFound;
    private Weather currentWeather;
    private float currentColor;

    private FloatingActionButton saveMarker;
    private FloatingActionButton deleteMarker;
    private boolean fabsVisible = false;
    private View saveDialogView;
    private GridView colorGridView;
    private ColorGridAdapter colorGridAdapter;
    private AlertDialog saveDialog;
    private String saveDialogTitle;
    private AlertDialog deleteDialog;
    private AlertDialog warningDialog;
    private ImageView dialogMapPin;
    private ImageView mapPinBackground;

    private Realm realm;
    private RealmResults<MapMarker> mapMarkers;

    //Operations in markers list
    public final static int SET = 0;
    public final static int REMOVE = 1;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        realm = ((MainActivity) getActivity()).getRealm();
        mapMarkers = ((MainActivity) getActivity()).getMapMarkers();

        saveMarker = rootView.findViewById(R.id.saveMarker);
        deleteMarker = rootView.findViewById(R.id.deleteMarker);
        saveMarker.setOnClickListener(this);
        deleteMarker.setOnClickListener(this);

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
        addSavedMarkers();
        if(moveDefaultCamera) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoom));
            moveDefaultCamera = false;
            this.marker.showInfoWindow();
            showFABs = true;

        }
    }

    public void setDefaultCamera(LatLng latLng, int zoom) {
        defaultLatLng = latLng;
        defaultZoom = zoom;
        moveDefaultCamera = true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showFABs = true;
        if((this.marker != null)){
            if(this.marker.getId().equals(marker.getId())){
                centerCameraOnMarker();
                return true;
            }else{
                hideWeatherDetails();
                hideFABs();
            }
        }
        this.marker = marker;
        Utils.getWeather(MapFragment.this, marker.getPosition());
        centerCameraOnMarker();
        return true;
    }

    public void centerCameraOnMarker(){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
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
        if(showFABs){
            showFABs = false;
            if(!fabsVisible){
                if(findMapMarker(this.marker).size() > 0){
                    saveMarker.setImageResource(android.R.drawable.ic_menu_edit);
                    saveDialogTitle = "Edit Marker: " + marker.getTitle();
                }else{
                    saveMarker.setImageResource(android.R.drawable.ic_menu_save);
                    saveDialogTitle = "Save Marker: "+ marker.getTitle();
                }
                markerAnim(saveMarker, R.anim.savemarker_show);
                markerAnim(deleteMarker, R.anim.deletemarker_show);
                fabsVisible = true;
                ImageButton showWeatherDetails = ((MainActivity)getActivity()).getShowWeatherDetails();
                Utils.slideUpIn(getContext(), showWeatherDetails);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideWeatherDetails();
        hideFABs();
    }

    //Floating Action Buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveMarker:
                if(saveDialog == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    saveDialogView = inflater.inflate(R.layout.dialog_save_marker, null);

                    setSaveDialogViews();

                    builder.setView(saveDialogView);
                    builder.setMessage("Select a marker color:")
                            .setPositiveButton("Ok", this)
                            .setNegativeButton("Cancel", null);
                    saveDialog = builder.create();
                }
                saveDialog.setTitle(saveDialogTitle);
                saveDialog.show();
                break;
            case R.id.deleteMarker:
                if(deleteDialog == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Marker")
                            .setMessage("Do you really want to delete this marker?")
                            .setPositiveButton("Yes", this)
                            .setNegativeButton("No", null);
                    deleteDialog = builder.create();
                }
                deleteDialog.show();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int button) {
        if(dialogInterface == saveDialog){
            switch (button){
                case DialogInterface.BUTTON_POSITIVE:
                    realm.beginTransaction();
                    RealmResults<MapMarker> markerResults = findMapMarker(marker);
                    if(markerResults.size()>0){
                        markerResults.get(0).setColor(currentColor);
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(currentColor));
                    }else {
                        MapMarker newMapMarker = new MapMarker(marker.getPosition().latitude,
                                marker.getPosition().longitude, currentColor, city, currentWeather);
                        realm.copyToRealmOrUpdate(newMapMarker);
                        marker.remove();
                        putSavedMarker(newMapMarker);
                    }
                    realm.commitTransaction();
                    break;
            }
        }else if (dialogInterface == deleteDialog){
            switch (button){
                case DialogInterface.BUTTON_POSITIVE:
                    realm.beginTransaction();
                    RealmResults<MapMarker> findMarker = findMapMarker(marker);
                    if(findMarker.size()>0){
                        findCityOfMapMarker(findMarker.get(0)).deleteAllFromRealm();
                        findMarkerInList(findMarker.get(0), REMOVE);
                        findMarker.deleteAllFromRealm();
                    }
                    realm.commitTransaction();
                    marker.remove();
                    hideFABs();
                    break;
            }
        }else if (dialogInterface == warningDialog){
            switch (button){
                case DialogInterface.BUTTON_NEUTRAL:
                    showFABs = true;
                    centerCameraOnMarker();
            }
        }
    }

    private void removeMarkerFromList(Marker marker){

    }

    private RealmResults<MapMarker> findMapMarker(Marker marker){
        RealmResults<MapMarker> markerResults = realm.where(MapMarker.class)
                .equalTo("latitude", marker.getPosition().latitude)
                .equalTo("longitude", marker.getPosition().longitude)
                .findAll();
        return markerResults;
    }

    private RealmResults<City> findCityOfMapMarker(MapMarker mapMarker){
        RealmResults<City> cityResults = realm.where(City.class)
                .equalTo("id", mapMarker.getCity().getId())
                .findAll();
        return cityResults;
    }

    private void addSavedMarkers(){
        for(int i=0; i<mapMarkers.size(); i++){
            MapMarker currentMapMarker = mapMarkers.get(i);
            putSavedMarker(currentMapMarker);
        }
    }

    private void putSavedMarker(MapMarker mapMarker){
        LatLng latLng = new LatLng(mapMarker.getLatitude(), mapMarker.getLongitude());
        markerOptions = new MarkerOptions()
                .position(latLng)
                .title(mapMarker.getCity().getName())
                .icon(BitmapDescriptorFactory.defaultMarker(mapMarker.getColor()))
                .draggable(false);
        Marker newMarker = mMap.addMarker(markerOptions);
        newMarker.setTag(mapMarker.getId());
        markers.add(newMarker);
        saveMarker.setImageResource(android.R.drawable.ic_menu_edit);
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
        fabsVisible = false;
    }

    private void getCity(LatLng latLng){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList = null;

        try{
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        }catch(IOException e){
            e.printStackTrace();
        }

        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            mapMarker = findCityInRealm(address);
            if(mapMarker == null) {
                city = new City(address.getLocality(), address.getSubAdminArea(), address.getAdminArea(),
                        address.getPostalCode(), address.getCountryName(), address.getCountryCode());
            }else{
                city = mapMarker.getCity();
                findMarkerInList(mapMarker, SET);
            }
        }
    }

    public void findMarkerInList(MapMarker mapMarker, int op){
        for(Marker marker : markers) {
            if (marker != null  && (marker.getTag().equals(mapMarker.getId()))) {
                if(op == SET) this.marker = marker;
                else if(op == REMOVE) markers.remove(marker);
                break;
            }
        }
    }

    private MapMarker findCityInRealm(Address address){
        RealmResults<City> cityResults = realm.where(City.class)
                .equalTo("name", address.getLocality())
                .equalTo("subAdminArea", address.getSubAdminArea())
                .equalTo("adminArea", address.getAdminArea())
                .equalTo("country", address.getCountryName())
                .findAll();
        if(cityResults.size() > 0){
            RealmResults<MapMarker> mapMarker = realm.where(MapMarker.class)
                    .equalTo("city.id", cityResults.get(0).getId())
                    .findAll();
            return mapMarker.get(0);
        }else{
            return null;
        }
    }

    public Marker getMarker(){
        return marker;
    }

    public void setMarker(LatLng latLng){
        getCity(latLng);
        if(mapMarker != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Warning")
                    .setMessage("There's already a marker for this city.")
                    .setNeutralButton("Take me there", this);
            warningDialog = builder.create();
            warningDialog.show();
        }else{
            markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(city.getName())
                    .draggable(false);
            if(marker != null && (findMapMarker(marker).size() == 0)) marker.remove();
            marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }
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

    private void setSaveDialogViews(){
        dialogMapPin = saveDialogView.findViewById(R.id.mapPin);
        mapPinBackground = saveDialogView.findViewById(R.id.mapPinBackground);
        colorGridView = saveDialogView.findViewById(R.id.colorGrid);
        colorGridAdapter = new ColorGridAdapter(getContext(),
                R.layout.color_grid_item, Utils.getColorList(),
                new ColorGridAdapter.OnItemClickListener() {
                    @Override
                    public void onColorClick(int position) {
                        currentColor = Utils.getColorList().get(position);
                        VectorDrawable shape = (VectorDrawable) dialogMapPin.getDrawable();
                        shape.setTint(Color.HSVToColor(new float[]{currentColor, 1F, 1F}));
                        dialogMapPin.setImageDrawable(shape);
                        GradientDrawable background = (GradientDrawable) mapPinBackground.getDrawable();
                        background.setTint(Color.HSVToColor(new float[]{currentColor, 1F, 0.5F}));
                        mapPinBackground.setImageDrawable(background);
                        Toast.makeText(getContext(), "" + currentColor, Toast.LENGTH_SHORT).show();
                    }
                });
        colorGridView.setAdapter(colorGridAdapter);
    }

    public Weather getCurrentWeather(){
        return currentWeather;
    }
}
