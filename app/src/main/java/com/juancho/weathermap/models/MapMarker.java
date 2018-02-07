package com.juancho.weathermap.models;

import com.juancho.weathermap.application.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Juancho on 02/06/18.
 */

public class MapMarker extends RealmObject{

    @PrimaryKey
    private int id;
    private float color;

    public MapMarker() {
    }

    public MapMarker(int id, float color) {
        this.id = MyApplication.MarkerID.incrementAndGet();
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getColor() {
        return color;
    }

    public void setColor(float color) {
        this.color = color;
    }
}
