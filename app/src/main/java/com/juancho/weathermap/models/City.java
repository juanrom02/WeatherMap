package com.juancho.weathermap.models;

import com.juancho.weathermap.application.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Juancho on 01/29/18.
 */

public class City extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String subAdminArea;
    private String adminArea;
    private String postalCode;
    private String country;
    private String countryCode;

    public City(){

    }

    public City(String name, String subAdminArea, String adminArea, String postalCode, String country, String countryCode) {
        this.id = MyApplication.CityID.incrementAndGet();
        this.name = name;
        this.subAdminArea = subAdminArea;
        this.adminArea = adminArea;
        this.postalCode = postalCode;
        this.country = country;
        this.countryCode = countryCode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public void setSubAdminArea(String subAdminArea) {
        this.subAdminArea = subAdminArea;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
