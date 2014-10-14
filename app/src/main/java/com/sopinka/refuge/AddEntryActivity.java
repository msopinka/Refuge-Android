package com.sopinka.refuge;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sopinka.refuge.R;
import com.sopinka.refuge.objects.Restroom;
import com.sopinka.refuge.utilities.HttpHelper;

public class AddEntryActivity extends Activity implements Button.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // setup country autocomplete
        AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.add_country);
        String[] countries = getResources().getStringArray(R.array.all_countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        tv.setAdapter(adapter);

        Button submit = (Button)findViewById(R.id.add_submit);
        submit.setOnClickListener(this);

        AdView mAdView = (AdView)findViewById(R.id.newentry_ads);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        Button submit = (Button)findViewById(R.id.add_submit);
        //submit.setVisibility(Button.GONE);

        ProgressBar pb = (ProgressBar)findViewById(R.id.add_progress);
        pb.setVisibility(ProgressBar.VISIBLE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpHelper.submitRestroom(getSubmissionRestroom());
            }
        });
        t.start();
    }

    private Restroom getSubmissionRestroom() {
        Restroom submission = new Restroom();

        EditText et = (EditText)findViewById(R.id.add_name);
        submission.name = et.getText().toString();

        et = (EditText)findViewById(R.id.add_address);
        submission.street = et.getText().toString();

        et = (EditText)findViewById(R.id.add_city);
        submission.city = et.getText().toString();

        et = (EditText)findViewById(R.id.add_state);
        submission.state = et.getText().toString();

        et = (EditText)findViewById(R.id.add_country);
        submission.country = et.getText().toString();

        Switch s = (Switch)findViewById(R.id.add_accessible);
        submission.accessible = s.isChecked();

        s = (Switch)findViewById(R.id.add_unisex);
        submission.unisex = s.isChecked();

        et = (EditText)findViewById(R.id.add_directions);
        submission.directions = et.getText().toString();

        et = (EditText)findViewById(R.id.add_comment);
        submission.comment = et.getText().toString();

        return submission;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
