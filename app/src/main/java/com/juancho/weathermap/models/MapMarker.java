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
    private String title;
    private double latitude;
    private double longitude;
    private float color;

    public MapMarker() {
    }


    public MapMarker(String title, double latitude, double longitude, float color) {
        this.id = MyApplication.MarkerID.incrementAndGet();
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
