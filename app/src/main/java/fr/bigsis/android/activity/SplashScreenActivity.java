package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.bigsis.android.R;

public class SplashScreenActivity extends AppCompatActivity {
    protected int _splashTime = 2000;
    FirebaseAuth mFirebaseAuth;
    String userId;
    FirebaseFirestore mFirestore;
    private Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        final SplashScreenActivity sPlashScreen = this;
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(_splashTime);
                    }
                } catch (InterruptedException e) {
                } finally {
                    if (!isFinishing())
                        if (mFirebaseAuth.getCurrentUser() == null) {
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                        } else if (mFirebaseAuth.getCurrentUser() != null) {
                            mFirebaseAuth.getCurrentUser()
                                    .reload();
                            if (!mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                                mFirebaseAuth.signOut();
                                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                finish();
                            } else {
                                userId = mFirebaseAuth.getCurrentUser().getUid();
                                mFirestore.collection("USERS").document(userId).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String groupCampus = documentSnapshot.getString("groupCampus");
                                                if (groupCampus != null) {
                                                    startActivity(new Intent(SplashScreenActivity.this, MapsActivity.class));
                                                    finish();
                                                } else {
                                                    mFirebaseAuth.signOut();
                                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        }
                }
            }
        };
        splashTread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Toast.makeText(this,"exec---",Toast.LENGTH_LONG).show();
            synchronized (splashTread) {
                splashTread.notifyAll();
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (splashTread.getState() == Thread.State.TIMED_WAITING) {
            finish();
        }
    }
}
