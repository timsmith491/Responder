package com.timsmith.responder;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationListener;

/**
 * Created by Tim on 18/01/2017.
 */

public class LocationTracker extends Service implements LocationListener {

    LocationTracker(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
