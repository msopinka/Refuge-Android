package com.sopinka.refuge;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.sopinka.refuge.utilities.LocationHelper;


public class SplashScreenActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private Handler handler;
    private LocationHelper locHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                Location userloc = (Location)msg.obj;
                i.putExtra("userlat", userloc.getLatitude());
                i.putExtra("userlng", userloc.getLongitude());
                startActivity(i);
                SplashScreenActivity.this.finish();
            }
        };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                locHelper = new LocationHelper(SplashScreenActivity.this);
                locHelper.connect();
            }
        });
        t.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locHelper != null) {
            locHelper.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Location user = locHelper.getCurrentLocation();
                try {
                    Thread.sleep(2000);
                }
                catch(Exception ex) {
                }
                Message msg = new Message();
                msg.obj = user;
                handler.sendMessage(msg);
            }
        });
        t.start();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
