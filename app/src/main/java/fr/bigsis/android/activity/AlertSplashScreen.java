package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import fr.bigsis.android.R;

public class AlertSplashScreen extends AppCompatActivity {

    TextView tvSplashAlert;
    String alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_splash_screen);

        tvSplashAlert = findViewById(R.id.tvSplashAlert);

        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        alert = extras.getString("ALERT_DESABLE");

        if(!alert.isEmpty()) {
            tvSplashAlert.setText("Alerte désactivée");
        }
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4000);

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
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
