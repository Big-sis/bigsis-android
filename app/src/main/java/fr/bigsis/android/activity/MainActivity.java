package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import fr.bigsis.android.R;

public class MainActivity extends BigsisActivity {

    Button btSignIn, btSignUp;
    FirebaseAuth mFirebaseAuth;
    private ProgressBar mProgressBarSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSignUp = findViewById(R.id.btSignUp);
        mProgressBarSign = findViewById(R.id.progressBarSign);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();

        btSignIn = findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBarSign.setVisibility(View.VISIBLE);
                if (mFirebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                } else if (mFirebaseAuth.getCurrentUser() != null) {
                    if (!mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    } else {
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    }
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgressBarSign.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
