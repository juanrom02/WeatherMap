package com.juancho.weathermap.application;

import android.app.Application;

import com.juancho.weathermap.models.MapMarker;
import com.juancho.weathermap.models.Weather;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Juancho on 02/06/18.
 */

public class MyApplication extends Application{

    public static AtomicInteger MarkerID = new AtomicInteger();
    public static AtomicInteger WeatherID = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        MarkerID = getIdByTable(realm, MapMarker.class);
        WeatherID = getIdByTable(realm, Weather.class);

        realm.close();
    }

    private void setUpRealmConfig(){

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0 ) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();

    }

}
