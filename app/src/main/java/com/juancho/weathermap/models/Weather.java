package com.juancho.weathermap.models;

/**
 * Created by Juancho on 01/29/18.
 */

public class Weather {

    private String description;
    private String icon;
    private float temp;
    private float humidity;
    private float wind_speed;
    private float wind_direction;
    private int sunrise;
    private int sunset;

    public Weather(String description, String icon, float temp,
                   float humidity, float wind_speed, float wind_direction, int sunrise, int sunset) {
        this.description = description;
        this.icon = icon;
        this.temp = temp;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.wind_direction = wind_direction;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public float getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(float wind_direction) {
        this.wind_direction = wind_direction;
    }

    public int getSunrise() {
        return sunrise;
    }

    public void setSunrise(int sunrise) {
        this.sunrise = sunrise;
    }

    public int getSunset() {
        return sunset;
    }

    public void setSunset(int sunset) {
        this.sunset = sunset;
    }
}
