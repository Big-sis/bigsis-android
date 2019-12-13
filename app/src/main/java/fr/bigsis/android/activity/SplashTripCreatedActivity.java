package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.bigsis.android.R;

public class SplashTripCreatedActivity extends AppCompatActivity {

    String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_trip_created);
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        event = extras.getString("Event");


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    if (event != null) {
                        Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TripListActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
