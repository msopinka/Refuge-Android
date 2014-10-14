package com.sopinka.refuge.utilities;

import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.sopinka.refuge.SplashScreenActivity;

/**
 * Created by Mike on 10/12/2014.
 */
public class LocationHelper {
    private LocationClient mLocationClient;

    public LocationHelper(SplashScreenActivity context) {
        mLocationClient = new LocationClient(context, context, context);
    }

    public Location getCurrentLocation() {
        return mLocationClient.getLastLocation();
    }

    public void connect() {
        mLocationClient.connect();
    }

    public void disconnect() {
        mLocationClient.disconnect();
    }
}
