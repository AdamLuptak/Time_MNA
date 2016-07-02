package com.adam.sk.workingtimemanager.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class LocationController {

    public static final String SHARED_PREFERENCES_NAME = "com.osmand.settings";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String THRESHOLD = "threshold";

    //defualt threshol 20 m
    private int thresholdDistance = 20;
    private SharedPreferences prefs;

    public LocationController(Context thisContext) {
        prefs = thisContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
    }

    public void saveLocation(String lon, String lat) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LATITUDE, lat);
        editor.putString(LONGITUDE, lon);
        editor.commit();
    }

    public int getThresholdDistance() {
        return prefs.getInt(THRESHOLD, 20);
    }

    public void setThresholdDistance(int thresholdDistance) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(THRESHOLD, thresholdDistance);
        editor.commit();
        this.thresholdDistance = thresholdDistance;
    }

    public Location loadLocation() {
        double lat = Double.valueOf(prefs.getString(LATITUDE, "0.0"));
        double lon = Double.valueOf(prefs.getString(LONGITUDE, "0.0"));

        Location location = new Location("dummyprovider");
        location.setLatitude(lat);
        location.setLongitude(lon);

        return location;
    }
}
