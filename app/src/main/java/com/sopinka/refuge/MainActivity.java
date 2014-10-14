package com.sopinka.refuge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.sopinka.refuge.R;
import com.sopinka.refuge.objects.HomeInfoWindowAdapter;
import com.sopinka.refuge.objects.MapSearchTask;
import com.sopinka.refuge.objects.Restroom;

import org.w3c.dom.Text;

public class MainActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setTitle("Refuge");

        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.main_map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setInfoWindowAdapter(new HomeInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Restroom restroom = new Gson().fromJson(marker.getSnippet(), Restroom.class);
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup);
                dialog.setCanceledOnTouchOutside(true);

                ImageView iv = (ImageView) dialog.findViewById(R.id.popup_img);
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

                TextView tv = (TextView)dialog.findViewById(R.id.popup_title);
                tv.setText(restroom.name);

                tv = (TextView)dialog.findViewById(R.id.popup_address);
                tv.setText(String.format("%s\r\n%s, %s", restroom.street, restroom.city, restroom.state));

                if (restroom.directions != null && !restroom.directions.equals("")) {
                    tv = (TextView) dialog.findViewById(R.id.popup_desc);
                    tv.setText("Directions:\r\n" + restroom.directions);
                }

                if (restroom.comment != null && !restroom.comment.equals("")) {
                    tv = (TextView) dialog.findViewById(R.id.popup_comment);
                    tv.setText("Comments:\r\n" + restroom.comment);
                }

                tv = (TextView) dialog.findViewById(R.id.popup_updated);
                tv.setText("Last Updated: " + restroom.updated_at.substring(0, 10));

                dialog.show();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("userlat") && extras.containsKey("userlng")) {
            setupMap(extras.getDouble("userlat"), extras.getDouble("userlng"));
        }

        AdView mAdView = (AdView)findViewById(R.id.main_ads);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_new) {
//            Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
//            startActivity(i);
//        }
        if (id == R.id.action_refresh) {
            doSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupMap(double userlat, double userlng) {
        // pan to user loc
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlat, userlng), 12), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                doSearch();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void doSearch() {
        // show progress bar
        ProgressBar pb = (ProgressBar)findViewById(R.id.progress);
        pb.setVisibility(ProgressBar.VISIBLE);

        // clear map
        mMap.clear();

        // thread
        MapSearchTask task = new MapSearchTask(this, mMap);
        task.execute(mMap.getCameraPosition().target);
    }
}
