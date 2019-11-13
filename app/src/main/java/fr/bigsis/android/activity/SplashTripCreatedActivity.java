package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import fr.bigsis.android.R;

public class SplashTripCreatedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_trip_created);
        Intent i = getIntent();
        String addEvent = i.getStringExtra("ADD_EVENT");
        if(addEvent != null) {
            Thread myThread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        Intent intent = new Intent(getApplicationContext(),EventListActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
        } else {
            Thread myThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        Intent intent = new Intent(getApplicationContext(), TripListActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
        }
    }
}
