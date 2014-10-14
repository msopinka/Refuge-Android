package com.sopinka.refuge.objects;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sopinka.refuge.MainActivity;
import com.sopinka.refuge.R;
import com.sopinka.refuge.utilities.HttpHelper;

/**
 * Created by Mike on 10/12/2014.
 */
public class MapSearchTask extends AsyncTask<LatLng, Void, Restroom[]> {
    private Activity context;
    private GoogleMap map;

    public MapSearchTask(Activity context, GoogleMap map) {
        this.context = context;
        this.map = map;
    }

    @Override
    protected Restroom[] doInBackground(LatLng... params) {
        LatLng user = params[0];
        return HttpHelper.getMapResults(user.latitude, user.longitude);
    }

    protected void onPostExecute(Restroom[] results) {
        if (results != null) {
            for (Restroom restroom : results) {
                map.addMarker(new MarkerOptions().position(new LatLng(restroom.latitude, restroom.longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pushpin))
                        .snippet(new Gson().toJson(restroom)));
            }
        }

        // hide progress bar
        ProgressBar pb = (ProgressBar)context.findViewById(R.id.progress);
        pb.setVisibility(ProgressBar.GONE);
    }
}
