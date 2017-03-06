package com.timsmith.responder.GeoFence;


import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.timsmith.responder.GeoFence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 500;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    //    public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 200; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> GEOFENCE_LANDMARKS = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        GEOFENCE_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));

        // Googleplex.
        GEOFENCE_LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));

        //Aungier Street
        GEOFENCE_LANDMARKS.put("DIT", new LatLng(53.338546, -6.267569));

        //Donnybrook
        GEOFENCE_LANDMARKS.put("DONNYBROOK", new LatLng(53.320146, -6.233773));

        GEOFENCE_LANDMARKS.put("DIT Aungier Street", new LatLng(53.338561, -6.267645));

        GEOFENCE_LANDMARKS.put("HOME", new LatLng(53.112951, -6.091830));

        GEOFENCE_LANDMARKS.put("Huntsbury", new LatLng(53.113279, -6.082924));

    }
}
