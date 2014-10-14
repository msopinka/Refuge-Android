package com.sopinka.refuge.objects;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.sopinka.refuge.MainActivity;
import com.sopinka.refuge.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Mike on 10/12/2014.
 */
public class HomeInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private MainActivity context;

    public HomeInfoWindowAdapter(MainActivity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View root = LayoutInflater.from(context).inflate(R.layout.infowindow, null);
        Restroom restroom = new Gson().fromJson(marker.getSnippet(), Restroom.class);

        ImageView iv = (ImageView) root.findViewById(R.id.infowindow_img);
        if (restroom.unisex && restroom.accessible) {
            iv.setImageResource(R.drawable.accessible_unisex);
            iv.setVisibility(ImageView.VISIBLE);
        }
        else if (restroom.unisex) {
            iv.setImageResource(R.drawable.unisex);
            iv.setVisibility(ImageView.VISIBLE);
        }
        else if (restroom.accessible) {
            iv.setImageResource(R.drawable.accessible);
            iv.setVisibility(ImageView.VISIBLE);
        }

        TextView tv = (TextView)root.findViewById(R.id.infowindow_title);
        tv.setText(restroom.name);

        tv = (TextView) root.findViewById(R.id.infowindow_desc);
        tv.setText(String.format("%s\r\n%s, %s", restroom.street, restroom.city, restroom.state));

        return root;
    }

}
