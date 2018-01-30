package com.juancho.weathermap.api.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.models.Weather;

import java.lang.reflect.Type;

/**
 * Created by Juancho on 01/29/18.
 */

public class DeserializerOWM implements JsonDeserializer<City> {

    @Override
    public City deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        int id = json.getAsJsonObject().get("id").getAsInt();
        String name = json.getAsJsonObject().get("name").getAsString();
        String country = json.getAsJsonObject().get("sys")
                .getAsJsonObject().get("country").getAsString();

        JsonObject weather0 = json.getAsJsonObject().get("weather")
                .getAsJsonArray().get(0).getAsJsonObject();
        String icon = weather0.get("icon").getAsString();
        String description = weather0.get("description").getAsString();
        JsonObject main = json.getAsJsonObject().get("main").getAsJsonObject();
        float temp = main.get("temp").getAsFloat();
        float humidity = main.get("humidity").getAsFloat();
        float temp_min = main.get("temp_min").getAsFloat();
        float temp_max = main.get("temp_max").getAsFloat();
        JsonObject windObject = json.getAsJsonObject().get("wind")
                .getAsJsonObject();
        float wind_speed = windObject.get("speed").getAsFloat();
        float wind_direction = windObject.get("deg").getAsFloat();
        JsonObject coord = json.getAsJsonObject().get("coord").getAsJsonObject();
        float latitude = coord.get("lat").getAsFloat();
        float longitude = coord.get("lon").getAsFloat();

        Weather weather = new Weather(description, temp, temp_min, temp_max, humidity, wind_speed, wind_direction);
        City city = new City(id, name, latitude, longitude, weather);
        return city;
    }
}
