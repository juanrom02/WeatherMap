package com.juancho.weathermap.models;

import com.google.android.gms.maps.model.Marker;
import com.juancho.weathermap.application.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Juancho on 02/06/18.
 */

public class MapMarker extends RealmObject{

    @PrimaryKey
    private int id;
    private double latitude;
    private double longitude;
    private float color;
    private City city;
    private Weather weather;

    public MapMarker() {
    }


    public MapMarker(double latitude, double longitude, float color, City city, Weather weather) {
        this.id = MyApplication.MarkerID.incrementAndGet();
        this.latitude = latitude;
        this.longitude = longitude;
        this.color = color;
        this.weather = weather;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getColor() {
        return color;
    }

    public void setColor(float color) {
        this.color = color;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
